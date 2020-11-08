/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.csm.velocity;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import ninja.leaping.modded.configurate.objectmapping.serialize.TypeSerializers;
import ru.csm.api.logging.Logger;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinHash;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.H2Database;
import ru.csm.api.storage.database.MySQLDatabase;
import ru.csm.api.upload.Profile;
import ru.csm.api.utils.FileUtil;
import ru.csm.velocity.command.CommandExecutor;
import ru.csm.velocity.command.SubCommand;
import ru.csm.velocity.commands.*;
import ru.csm.velocity.listeners.PlayerListeners;
import ru.csm.velocity.message.PluginMessageReceiver;
import ru.csm.velocity.message.PluginMessageSender;
import ru.csm.velocity.message.handlers.HandlerMenu;
import ru.csm.velocity.message.handlers.HandlerPreview;
import ru.csm.velocity.message.handlers.HandlerSkin;
import ru.csm.velocity.message.handlers.HandlerSkull;
import ru.csm.velocity.services.VelocitySkinsAPI;
import ru.csm.velocity.util.VelocityTasks;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

@Plugin(id = "custom_skins_manager", name = "Custom Skins Manager", version = "3.5", authors = {"Nanit"})
public class VelocitySkinsManager {

    private final ProxyServer server;
    private final org.slf4j.Logger logger;

    private Database database;
    private SkinsAPI<Player> api;

    public SkinsAPI<Player> getApi(){
        return api;
    }

    @Inject
    public VelocitySkinsManager(ProxyServer server, org.slf4j.Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event){
        try{
            Logger.set(new Slf4jHandler(logger));
            VelocityTasks.init(this, server);

            registerSerializers();

            Path pluginFolder = getPluginFolder();
            Configuration configuration = new Configuration("velocity/config.conf", pluginFolder, this);
            Language lang = new Language(this, Paths.get(pluginFolder.toString(), "lang"), "lang/"+configuration.get().getNode("language").getString());

            try{
                setupDatabase(configuration);
            } catch (SQLException e){
                Logger.severe("Cannot connect to SQL database: " + e.getMessage());
                return;
            }

            MessageSender<Player> sender = new PluginMessageSender();
            PluginMessageReceiver receiver = new PluginMessageReceiver();

            api = new VelocitySkinsAPI(database, configuration, lang, sender, server);

            receiver.registerHandler(Channels.SKINS, new HandlerSkin(api));
            receiver.registerHandler(Channels.SKULLS, new HandlerSkull());
            receiver.registerHandler(Channels.MENU, new HandlerMenu(api, server));
            receiver.registerHandler(Channels.PREVIEW, new HandlerPreview());

            server.getChannelRegistrar().register(new LegacyChannelIdentifier(Channels.SKINS));
            server.getChannelRegistrar().register(new LegacyChannelIdentifier(Channels.SKULLS));
            server.getChannelRegistrar().register(new LegacyChannelIdentifier(Channels.PREVIEW));
            server.getChannelRegistrar().register(new LegacyChannelIdentifier(Channels.MENU));

            server.getEventManager().register(this, receiver);

            registerListeners();
            registerCommands(sender);

            SkinHash.startCleaner();

            Logger.info("Plugin enabled!");
        } catch (Exception e){
            Logger.severe("Cannot enable plugin: " + e.getMessage());
        }
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event){
        if (api != null){
            api.getNameQueue().stop();
            api.getImageQueue().stop();
        }

        if (database != null){
            database.closeConnection();
        }

        SkinHash.stopCleaner();
    }

    private void registerCommands(MessageSender<Player> sender){
        CommandExecutor skinCommand = new CommandSkin(api.getLang());
        CommandExecutor skullCommand = new CommandSkull(api.getLang());

        SubCommand skinPlayer = new CommandSkinPlayer(api);
        SubCommand skinUrl = new CommandSkinUrl(api);
        SubCommand skinReset = new CommandSkinReset(api);
        SubCommand skinMenu = new CommandSkinMenu(api);
        SubCommand skinTo = new CommandSkinTo(api);
        SubCommand skinPreview = new CommandSkinPreview(api, server);
        SubCommand skullPlayer = new CommandSkullPlayer(api, sender);
        SubCommand skullUrl = new CommandSkullUrl(api, sender);
        SubCommand skullTo = new CommandSkullTo(api, server, sender);

        skinPlayer.setPermission("csm.skin.player");
        skinUrl.setPermission("csm.skin.url");
        skinReset.setPermission("csm.skin.reset");
        skinMenu.setPermission("csm.skin.menu");
        skinTo.setPermission("csm.skin.to");
        skinPreview.setPermission("csm.skin.preview");
        skullPlayer.setPermission("csm.skull.player");
        skullUrl.setPermission("csm.skull.url");
        skullTo.setPermission("csm.skull.to");

        skinCommand.addSub(skinPlayer, "player");
        skinCommand.addSub(skinUrl, "url");
        skinCommand.addSub(skinReset, "reset");
        skinCommand.addSub(skinMenu, "menu");
        skinCommand.addSub(skinTo, "to");
        skinCommand.addSub(skinPreview, "preview");

        skullCommand.addSub(skullPlayer, "player");
        skullCommand.addSub(skullUrl, "url");
        skullCommand.addSub(skullTo, "to");

        server.getCommandManager().register(skinCommand, "csm", "skin", "skins");
        server.getCommandManager().register(skullCommand, "csmskull", "skull");
    }

    private void registerSerializers(){
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Profile.class), new Profile.Serializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Skin.class), new Skin.Serializer());
    }

    private void registerListeners(){
        server.getEventManager().register(this, new PlayerListeners(api));
        server.getEventManager().register(this, new PluginMessageReceiver());
    }

    private void setupDatabase(Configuration conf) throws SQLException {
        String type = conf.get().getNode("database", "type").getString("").toLowerCase();

        switch (type) {
            case "h2": {
                Path path = Paths.get(getPluginFolder().toString(), "skins");
                String user = conf.get().getNode("database", "user").getString();
                String password = conf.get().getNode("database", "password").getString();
                this.database = new H2Database(path, user, password);
                break;
            }
            case "mysql": {
                String host = conf.get().getNode("database", "host").getString();
                int port = conf.get().getNode("database", "port").getInt(3306);
                String dbname = conf.get().getNode("database", "database").getString();
                String user = conf.get().getNode("database", "user").getString();
                String password = conf.get().getNode("database", "password").getString();
                this.database = new MySQLDatabase(host, port, dbname, user, password);
                break;
            }
            default:
                throw new SQLException("Undefined database type: " + type);
        }

        this.database.executeSQL(FileUtil.readResourceContent("/tables/" + type + "/skins.sql"));
    }

    public static Path getPluginFolder(){
        return Paths.get(getPluginsFolder().toString(), "CustomSkinsManager");
    }

    public static Path getPluginsFolder(){
        try{
            return Paths.get(VelocitySkinsManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (URISyntaxException e){
            return null;
        }
    }

}
