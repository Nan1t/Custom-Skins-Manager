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

package ru.csm.bungee;

import com.google.common.reflect.TypeToken;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.leaping.modded.configurate.objectmapping.serialize.TypeSerializers;
import ru.csm.api.logging.JULHandler;
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
import ru.csm.api.logging.Logger;
import ru.csm.bungee.command.CommandExecutor;
import ru.csm.bungee.command.SubCommand;
import ru.csm.bungee.commands.*;
import ru.csm.bungee.listeners.PlayerListeners;
import ru.csm.bungee.message.PluginMessageReceiver;
import ru.csm.bungee.message.PluginMessageSender;
import ru.csm.bungee.message.handlers.HandlerMenu;
import ru.csm.bungee.message.handlers.HandlerPreview;
import ru.csm.bungee.message.handlers.HandlerSkin;
import ru.csm.bungee.message.handlers.HandlerSkull;
import ru.csm.bungee.services.BungeeSkinsAPI;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class BungeeSkinsManager extends Plugin {

    private Metrics metrics;
    private Database database;
    private SkinsAPI<ProxiedPlayer> api;

    public SkinsAPI<ProxiedPlayer> getApi(){
        return api;
    }

    public Metrics getMetrics(){
        return metrics;
    }

    @Override
    public void onEnable(){
        try {
            metrics = new Metrics(this, 7375);

            Logger.set(new JULHandler(getLogger()));

            registerSerializers();

            Path pluginFolder = this.getDataFolder().toPath();
            Configuration configuration = new Configuration("bungee/config.conf", pluginFolder, this);
            Language lang = new Language(this, Paths.get(pluginFolder.toString(), "lang"), "lang/"+configuration.get().getNode("language").getString());

            try{
                setupDatabase(configuration);
            } catch (SQLException e){
                getLogger().severe("Cannot connect to SQL database: " + e.getMessage());
                return;
            }

            MessageSender<ProxiedPlayer> sender = new PluginMessageSender();
            PluginMessageReceiver receiver = new PluginMessageReceiver();

            api = new BungeeSkinsAPI(database, configuration, lang, sender);

            receiver.registerHandler(Channels.SKINS, new HandlerSkin(api));
            receiver.registerHandler(Channels.SKULLS, new HandlerSkull());
            receiver.registerHandler(Channels.MENU, new HandlerMenu(api));
            receiver.registerHandler(Channels.PREVIEW, new HandlerPreview());

            getProxy().registerChannel(Channels.SKINS);
            getProxy().registerChannel(Channels.SKULLS);
            getProxy().registerChannel(Channels.PREVIEW);
            getProxy().registerChannel(Channels.MENU);

            getProxy().getPluginManager().registerListener(this, receiver);

            registerListeners();
            registerCommands(sender);

            SkinHash.startCleaner();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable(){
        if (api != null){
            api.getNameQueue().stop();
            api.getImageQueue().stop();
        }

        if (database != null){
            database.closeConnection();
        }

        SkinHash.stopCleaner();
    }

    private void registerCommands(MessageSender<ProxiedPlayer> sender){
        CommandExecutor skinCommand = new CommandSkin(api.getLang());
        CommandExecutor skullCommand = new CommandSkull(api.getLang());

        SubCommand skinPlayer = new CommandSkinPlayer(api);
        SubCommand skinUrl = new CommandSkinUrl(api);
        SubCommand skinReset = new CommandSkinReset(api);
        SubCommand skinMenu = new CommandSkinMenu(api);
        SubCommand skinTo = new CommandSkinTo(api);
        SubCommand skinPreview = new CommandSkinPreview(api);
        SubCommand skullPlayer = new CommandSkullPlayer(api, sender);
        SubCommand skullUrl = new CommandSkullUrl(api, sender);
        SubCommand skullTo = new CommandSkullTo(api, sender);

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

        getProxy().getPluginManager().registerCommand(this, skinCommand);
        getProxy().getPluginManager().registerCommand(this, skullCommand);
    }

    private void registerSerializers(){
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Profile.class), new Profile.Serializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Skin.class), new Skin.Serializer());
    }

    private void registerListeners(){
        getProxy().getPluginManager().registerListener(this, new PlayerListeners(api));
        getProxy().getPluginManager().registerListener(this, new PluginMessageReceiver());
    }

    private void setupDatabase(Configuration conf) throws SQLException {
        String type = conf.get().getNode("database", "type").getString("").toLowerCase();

        switch (type) {
            case "h2": {
                Path path = Paths.get(getDataFolder().getAbsolutePath(), "skins");
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
}
