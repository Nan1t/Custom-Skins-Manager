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

import napi.configurate.Configuration;
import napi.configurate.serializing.NodeSerializers;
import napi.configurate.source.ConfigSources;
import napi.configurate.yaml.YamlConfiguration;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import ru.csm.api.logging.JULHandler;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinHash;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.*;
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
import ru.csm.bungee.util.BungeeTasks;

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
            BungeeTasks.init(this);

            registerSerializers();

            Path pluginFolder = this.getDataFolder().toPath();
            Configuration configurationFile = YamlConfiguration.builder()
                    .source(ConfigSources.resource("bukkit/config.yml", this).copyTo(pluginFolder))
                    .build();

            SkinsConfig config = new SkinsConfig(this, configurationFile);

            configurationFile.reload();
            config.load(getDataFolder().toPath());

            try{
                setupDatabase(config);
            } catch (SQLException e){
                getLogger().severe("Cannot connect to SQL database: " + e.getMessage());
                return;
            }

            MessageSender<ProxiedPlayer> sender = new PluginMessageSender();
            PluginMessageReceiver receiver = new PluginMessageReceiver();

            api = new BungeeSkinsAPI(database, config, sender);

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
        NodeSerializers.register(Profile.class, new Profile.Serializer());
        NodeSerializers.register(Skin.class, new Skin.Serializer());
    }

    private void registerListeners(){
        getProxy().getPluginManager().registerListener(this, new PlayerListeners(api));
        getProxy().getPluginManager().registerListener(this, new PluginMessageReceiver());
    }

    private void setupDatabase(SkinsConfig conf) throws SQLException {
        String type = conf.getDbType().toLowerCase();

        switch (type) {
            case "h2": {
                Path path = Paths.get(getDataFolder().getAbsolutePath(), "skins");
                this.database = new H2Database(path, conf.getDbUser(), conf.getDbPassword());
                break;
            }
            case "mysql": {
                String host = conf.getDbHost();
                int port = conf.getDbPort();
                String dbname = conf.getDbName();
                String user = conf.getDbUser();
                String password = conf.getDbPassword();
                this.database = new MySQLDatabase(host, port, dbname, user, password);
                break;
            }
            default:
                throw new SQLException("Undefined database type: " + type);
        }

        this.database.executeSQL(FileUtil.readResourceContent("/tables/" + type + "/skins.sql"));
    }
}
