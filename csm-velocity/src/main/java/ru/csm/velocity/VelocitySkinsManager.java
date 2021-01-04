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

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import napi.configurate.Configuration;
import napi.configurate.serializing.NodeSerializers;
import napi.configurate.source.ConfigSources;
import napi.configurate.yaml.YamlConfiguration;
import napi.util.LibLoader;
import ru.csm.api.Dependency;
import ru.csm.api.logging.Logger;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinHash;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Database;
import ru.csm.api.storage.H2Database;
import ru.csm.api.storage.MySQLDatabase;
import ru.csm.api.storage.SkinsConfig;
import ru.csm.api.upload.Profile;
import ru.csm.api.utils.FileUtil;
import ru.csm.velocity.cmd.Commands;
import ru.csm.velocity.listeners.PlayerListeners;
import ru.csm.velocity.message.PluginMessageReceiver;
import ru.csm.velocity.message.PluginMessageSender;
import ru.csm.velocity.message.handlers.HandlerMenu;
import ru.csm.velocity.message.handlers.HandlerPreview;
import ru.csm.velocity.message.handlers.HandlerSkin;
import ru.csm.velocity.message.handlers.HandlerSkull;
import ru.csm.velocity.services.VelocitySkinsAPI;
import ru.csm.velocity.util.VelocityTasks;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

@Plugin(id = "custom_skins_manager", name = "Custom Skins Manager", version = "3.6.1", authors = {"Nanit"})
public class VelocitySkinsManager {

    private final ProxyServer server;
    private final Path dataFolder;

    private Database database;
    private SkinsAPI<Player> api;

    public SkinsAPI<Player> getApi(){
        return api;
    }

    @Inject
    public VelocitySkinsManager(ProxyServer server, org.slf4j.Logger logger, @DataDirectory Path dataFolder) {
        this.server = server;
        this.dataFolder = dataFolder.toAbsolutePath();

        Logger.set(new Slf4jHandler(logger));

        Path libsFolder = Paths.get(dataFolder.toString(), "libs");
        LibLoader libLoader = new LibLoader(this, libsFolder);

        try {
            libLoader.download(Dependency.COMMONS_LOGGING.getName(), Dependency.COMMONS_LOGGING.getUrl());
            libLoader.download(Dependency.COMMONS_POOL.getName(), Dependency.COMMONS_POOL.getUrl());
            libLoader.download(Dependency.DBCP.getName(), Dependency.DBCP.getUrl());
            libLoader.download(Dependency.H2.getName(), Dependency.H2.getUrl());

            libLoader.load(libsFolder);
        } catch (Exception e){
            Logger.severe("Cannot load library: " + e.getMessage());
        }
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event){
        try{
            VelocityTasks.init(this, server);

            registerSerializers();

            Configuration configurationFile = YamlConfiguration.builder()
                    .source(ConfigSources.resource("/velocity/config.yml", this).copyTo(dataFolder))
                    .build();

            SkinsConfig config = new SkinsConfig(this, configurationFile);

            configurationFile.reload();
            config.load(dataFolder);

            try{
                setupDatabase(config);
            } catch (SQLException e){
                Logger.severe("Cannot connect to SQL database: " + e.getMessage());
                return;
            }

            MessageSender<Player> sender = new PluginMessageSender();
            PluginMessageReceiver receiver = new PluginMessageReceiver();

            api = new VelocitySkinsAPI(database, config, sender, server);

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
            Commands.init(server, api, sender);

            VelocityTasks.runRepeat(SkinHash::clean, 30000); // 30 sec

            Logger.info("Plugin enabled!");
        } catch (Exception e){
            Logger.severe("Cannot enable plugin: " + e.getMessage());
        }
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event){
        if (database != null){
            database.closeConnection();
        }
    }

    private void registerSerializers(){
        NodeSerializers.register(Profile.class, new Profile.Serializer());
        NodeSerializers.register(Skin.class, new Skin.Serializer());
    }

    private void registerListeners(){
        server.getEventManager().register(this, new PlayerListeners(api));
        server.getEventManager().register(this, new PluginMessageReceiver());
    }

    private void setupDatabase(SkinsConfig conf) throws SQLException {
        String type = conf.getDbType().toLowerCase();

        switch (type) {
            case "h2": {
                Path path = Paths.get(dataFolder.toString(), "skins");
                String user = conf.getDbUser();
                String password = conf.getDbPassword();
                this.database = new H2Database(path, user, password);
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
