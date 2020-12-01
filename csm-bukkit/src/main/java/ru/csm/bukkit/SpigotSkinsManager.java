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

package ru.csm.bukkit;

import napi.configurate.Configuration;
import napi.configurate.serializing.NodeSerializers;
import napi.configurate.source.ConfigSources;
import napi.configurate.yaml.YamlConfiguration;
import napi.util.LibLoader;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import ru.csm.api.Dependencies;
import ru.csm.api.logging.JULHandler;
import ru.csm.api.network.Channels;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinHash;
import ru.csm.api.storage.*;
import ru.csm.api.logging.Logger;
import ru.csm.api.upload.Profile;
import ru.csm.bukkit.commands.*;
import ru.csm.bukkit.nms.handler.SkinHandlers;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.nms.hologram.Holograms;
import ru.csm.bukkit.listeners.InventoryListener;
import ru.csm.bukkit.listeners.NpcClickListener;
import ru.csm.bukkit.listeners.PlayerListener;
import ru.csm.bukkit.listeners.RespawnListener;
import ru.csm.bukkit.menu.item.Item;
import ru.csm.bukkit.messages.PluginMessageReceiver;
import ru.csm.bukkit.messages.PluginMessageSender;
import ru.csm.bukkit.messages.handlers.HandlerMenu;
import ru.csm.bukkit.messages.handlers.HandlerPreview;
import ru.csm.bukkit.messages.handlers.HandlerSkin;
import ru.csm.bukkit.messages.handlers.HandlerSkull;
import ru.csm.bukkit.nms.npc.NpcPacketHandler;
import ru.csm.bukkit.nms.npc.Npcs;
import ru.csm.bukkit.placeholders.Placeholders;
import ru.csm.bukkit.services.ProxySkinsAPI;
import ru.csm.bukkit.services.SpigotSkinsAPI;
import ru.csm.bukkit.services.MenuManager;
import ru.csm.bukkit.util.BukkitTasks;
import ru.csm.api.utils.FileUtil;
import ru.csm.bukkit.util.ProxyUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class SpigotSkinsManager extends JavaPlugin {

    private Database database;
    private SkinsAPI<Player> api;

    @Override
    public void onLoad() {
        Logger.set(new JULHandler(getLogger()));

        LibLoader libLoader = new LibLoader(this, Paths.get(getDataFolder().toString(), "libs"));

        try {
            libLoader.loadJar(libLoader.download(Dependencies.H2.getName(), Dependencies.H2.getUrl()));
            libLoader.loadJar(libLoader.download(Dependencies.DBCP.getName(), Dependencies.DBCP.getUrl()));
        } catch (Exception e){
            Logger.severe("Cannot load library: " + e.getMessage());
        }
    }

    @Override
    public void onEnable(){
        try{
            new Metrics(this, 7375);

            registerSerializers();

            String packageName = getServer().getClass().getPackage().getName();
            String version = packageName.substring(packageName.lastIndexOf('.') + 1);

            SkinHandlers.init(version);
            Npcs.init(version);
            Holograms.init(version);
            NpcPacketHandler.init(version);
            BukkitTasks.setPlugin(this);

            Configuration configurationFile = YamlConfiguration.builder()
                    .source(ConfigSources.resource("/bukkit/config.yml", this).copyTo(getDataFolder().toPath()))
                    .build();

            SkinsConfig config = new SkinsConfig(this, configurationFile);

            configurationFile.reload();
            config.load(getDataFolder().toPath());

            MenuManager menuManager = new MenuManager(config.getLanguage());

            if(!ProxyUtil.isUseProxy()){
                try{
                    setupDatabase(config);
                } catch (SQLException e){
                    Logger.severe("Cannot connect to SQL database: %s", e.getMessage());
                    getPluginLoader().disablePlugin(this);
                    return;
                }

                api = new SpigotSkinsAPI(database, config, config.getLanguage(), menuManager);

                BukkitTasks.runTaskTimerAsync(SkinHash::clean, 0, 900); // 30 sec

                getServer().getPluginManager().registerEvents(new PlayerListener(api), this);
                getServer().getServicesManager().register(SkinsAPI.class, api, this, ServicePriority.Normal);
            } else {
                getLogger().info("Using proxy server as skin manager");

                PluginMessageSender sender = new PluginMessageSender(this);
                PluginMessageReceiver receiver = new PluginMessageReceiver();

                api = new ProxySkinsAPI(config.getLanguage(), sender);

                receiver.registerHandler(Channels.SKINS, new HandlerSkin());
                receiver.registerHandler(Channels.SKULLS, new HandlerSkull());
                receiver.registerHandler(Channels.MENU, new HandlerMenu(api, menuManager));
                receiver.registerHandler(Channels.PREVIEW, new HandlerPreview(api));

                getServer().getMessenger().registerIncomingPluginChannel(this, Channels.SKINS, receiver);
                getServer().getMessenger().registerIncomingPluginChannel(this, Channels.SKULLS, receiver);
                getServer().getMessenger().registerIncomingPluginChannel(this, Channels.MENU, receiver);
                getServer().getMessenger().registerIncomingPluginChannel(this, Channels.PREVIEW, receiver);

                getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS);
                getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKULLS);
                getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.MENU);
                getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.PREVIEW);
            }

            getServer().getPluginManager().registerEvents(new InventoryListener(), this);
            getServer().getPluginManager().registerEvents(new RespawnListener(api), this);
            getServer().getPluginManager().registerEvents(new NpcClickListener(api, menuManager), this);

            getServer().getServicesManager().register(SkinsAPI.class, api, this, ServicePriority.Normal);

            if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
                Placeholders.init();
            }

            registerCommands();
        } catch (Exception e){
            Logger.severe("Cannot enable plugin: " + e.getMessage());
        }
    }

    @Override
    public void onDisable(){
        if(database != null){
            database.closeConnection();
        }
    }

    private void registerCommands(){
        Command commandSkin = new CommandSkin(api.getLang());
        Command commandSkull = new CommandSkull(api.getLang());

        commandSkin.addSub(new CommandSkinPlayer(api).setPermission("csm.skin.player"), "player");
        commandSkin.addSub(new CommandSkinUrl(api).setPermission("csm.skin.url"), "url");
        commandSkin.addSub(new CommandSkinReset(api).setPermission("csm.skin.reset"), "reset");
        commandSkin.addSub(new CommandSkinMenu(api).setPermission("csm.skin.menu"), "menu");
        commandSkin.addSub(new CommandSkinTo(api).setPermission("csm.skin.to"), "to");
        commandSkin.addSub(new CommandSkinPreview(api).setPermission("csm.skin.preview"), "preview");

        commandSkull.addSub(new CommandSkullPlayer(api).setPermission("csm.skull.player"), "player");
        commandSkull.addSub(new CommandSkullUrl(api).setPermission("csm.skull.url"), "url");
        commandSkull.addSub(new CommandSkullTo(api).setPermission("csm.skull.to"), "to");

        getCommand("csm").setExecutor(commandSkin);
        getCommand("csmskull").setExecutor(commandSkull);
    }

    private void registerSerializers(){
        NodeSerializers.register(Profile.class, new Profile.Serializer());
        NodeSerializers.register(Skin.class, new Skin.Serializer());
        NodeSerializers.register(Item.class, new Item.Serializer());
    }

    private void setupDatabase(SkinsConfig conf) throws SQLException {
        String type = conf.getDbType().toLowerCase();

        switch (type) {
            case "h2": {
                Path path = Paths.get(getDataFolder().getAbsolutePath(), "skins");
                this.database = new H2Database(path, conf.getDbUser(), conf.getDbPassword());
                break;
            }
            case "sqlite": {
                String path = getDataFolder().getAbsolutePath();
                this.database = new SQLiteDatabase(path, conf.getDbName(), conf.getDbUser(), conf.getDbPassword());
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
