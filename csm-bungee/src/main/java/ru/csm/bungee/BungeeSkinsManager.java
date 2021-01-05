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
import napi.util.LibLoader;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import ru.csm.api.Dependency;
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
import ru.csm.bungee.cmd.Commands;
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

    private Database database;
    private SkinsAPI<ProxiedPlayer> api;

    public SkinsAPI<ProxiedPlayer> getApi(){
        return api;
    }

    @Override
    public void onLoad() {
        Logger.set(new JULHandler(getLogger()));

        Path libsFolder = Paths.get(getDataFolder().toString(), "libs");
        LibLoader libLoader = new LibLoader(this, libsFolder);

        try {
            libLoader.download(Dependency.COMMONS_LOGGING.getName(), Dependency.COMMONS_LOGGING.getUrl());
            libLoader.download(Dependency.COMMONS_LANG3.getName(), Dependency.COMMONS_LANG3.getUrl());
            libLoader.download(Dependency.COMMONS_POOL.getName(), Dependency.COMMONS_POOL.getUrl());
            libLoader.download(Dependency.DBCP.getName(), Dependency.DBCP.getUrl());
            libLoader.download(Dependency.H2.getName(), Dependency.H2.getUrl());

            libLoader.load(libsFolder);
        } catch (Exception e){
            Logger.severe("Cannot load library: " + e.getMessage());
        }
    }

    @Override
    public void onEnable(){
        try {
            new Metrics(this, 7375);

            BungeeTasks.init(this);

            registerSerializers();

            Path pluginFolder = this.getDataFolder().toPath();
            Configuration configurationFile = YamlConfiguration.builder()
                    .source(ConfigSources.resource("/bungee/config.yml", this).copyTo(pluginFolder))
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

            Commands.init(this, api, sender);

            BungeeTasks.runRepeatTask(SkinHash::clean, 0, 30000); // 30 sec
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable(){
        if (database != null){
            database.closeConnection();
        }
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
