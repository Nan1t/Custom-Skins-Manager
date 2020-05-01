package ru.csm.bukkit;

import com.google.common.reflect.TypeToken;
import me.clip.placeholderapi.PlaceholderAPI;
import ninja.leaping.modded.configurate.objectmapping.serialize.TypeSerializers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import org.spigotmc.SpigotConfig;
import ru.csm.api.network.Channels;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinHash;
import ru.csm.api.storage.database.H2Database;
import ru.csm.api.utils.Logger;
import ru.csm.bukkit.commands.*;
import ru.csm.bukkit.handler.SkinHandlers;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.MySQLDatabase;
import ru.csm.api.storage.database.SQLiteDatabase;
import ru.csm.api.upload.Profile;
import ru.csm.bukkit.hologram.Holograms;
import ru.csm.bukkit.listeners.InventoryListener;
import ru.csm.bukkit.listeners.NpcClickListener;
import ru.csm.bukkit.listeners.PlayerJoinListener;
import ru.csm.bukkit.listeners.PlayerListener;
import ru.csm.bukkit.menu.item.Item;
import ru.csm.bukkit.messages.PluginMessageReceiver;
import ru.csm.bukkit.messages.PluginMessageSender;
import ru.csm.bukkit.messages.handlers.HandlerMenu;
import ru.csm.bukkit.messages.handlers.HandlerPreview;
import ru.csm.bukkit.messages.handlers.HandlerSkin;
import ru.csm.bukkit.messages.handlers.HandlerSkull;
import ru.csm.bukkit.npc.NpcPacketHandler;
import ru.csm.bukkit.npc.Npcs;
import ru.csm.bukkit.placeholders.Placeholders;
import ru.csm.bukkit.services.BukkitBungeeSkinsAPI;
import ru.csm.bukkit.services.BukkitSkinsAPI;
import ru.csm.bukkit.services.MenuManager;
import ru.csm.bukkit.util.BukkitTasks;
import ru.csm.api.utils.FileUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class SpigotSkinsManager extends JavaPlugin {

    private Metrics metrics;

    private Database database;
    private SkinsAPI<Player> api;

    public Metrics getMetrics(){
        return metrics;
    }

    @Override
    public void onEnable(){
        try{
            Logger.set(getLogger());

            metrics = new Metrics(this, 7375);

            registerSerializers();

            String packageName = getServer().getClass().getPackage().getName();
            String version = packageName.substring(packageName.lastIndexOf('.') + 1);

            SkinHandlers.init(version);
            Npcs.init(version);
            Holograms.init(version);
            NpcPacketHandler.init(version);
            BukkitTasks.setPlugin(this);

            Configuration configuration = new Configuration("bukkit/config.conf", getDataFolder().toPath(), this);
            Language lang = new Language(this, Paths.get(getDataFolder().toPath().toString(), "lang"), "lang/"+configuration.get().getNode("language").getString());
            MenuManager menuManager = new MenuManager(lang);

            if(!SpigotConfig.bungee){
                try{
                    setupDatabase(configuration);
                } catch (SQLException e){
                    Logger.severe("Cannot connect to SQL database: %s", e.getMessage());
                    getPluginLoader().disablePlugin(this);
                    return;
                }

                api = new BukkitSkinsAPI(database, configuration, lang, menuManager);
                SkinHash.startCleaner();
                registerCommands();

                getServer().getPluginManager().registerEvents(new PlayerListener(api), this);
                getServer().getServicesManager().register(SkinsAPI.class, api, this, ServicePriority.Normal);
            } else {
                getLogger().info("Using BungeeCord as skin manager");

                PluginMessageSender sender = new PluginMessageSender(this);
                PluginMessageReceiver receiver = new PluginMessageReceiver();

                api = new BukkitBungeeSkinsAPI(lang, sender);

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

            getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
            getServer().getPluginManager().registerEvents(new InventoryListener(), this);
            getServer().getPluginManager().registerEvents(new NpcClickListener(api, menuManager), this);

            getServer().getServicesManager().register(SkinsAPI.class, api, this, ServicePriority.Normal);

            if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
                Placeholders.init();
            }
        } catch (Exception e){
            Logger.severe("Cannot enable plugin: " + e.getMessage());
        }
    }

    @Override
    public void onDisable(){
        if (api != null){
            if (api.getImageQueue() != null) api.getImageQueue().stop();
            if (api.getNameQueue() != null) api.getNameQueue().stop();
        }

        if(database != null){
            database.closeConnection();
        }

        SkinHash.stopCleaner();
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
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Profile.class), new Profile.Serializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Skin.class), new Skin.Serializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Item.class), new Item.Serializer());
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
            case "sqlite": {
                String path = getDataFolder().getAbsolutePath();
                String dbname = conf.get().getNode("database", "database").getString();
                String user = conf.get().getNode("database", "user").getString();
                String password = conf.get().getNode("database", "password").getString();
                this.database = new SQLiteDatabase(path, dbname, user, password);
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
