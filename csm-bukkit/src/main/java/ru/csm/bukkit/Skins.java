package ru.csm.bukkit;

import com.google.common.reflect.TypeToken;
import ninja.leaping.modded.configurate.objectmapping.serialize.TypeSerializers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import org.spigotmc.SpigotConfig;
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
import ru.csm.bukkit.menu.item.Item;
import ru.csm.bukkit.npc.NpcPacketHandler;
import ru.csm.bukkit.npc.Npcs;
import ru.csm.bukkit.services.BukkitSkinsAPI;
import ru.csm.bukkit.services.MenuManager;
import ru.csm.bukkit.util.BukkitTasks;
import ru.csm.bukkit.util.FileUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class Skins extends JavaPlugin {

    private Database database;
    private SkinsAPI<Player> api;

    @Override
    public void onEnable(){
        try{
            Logger.set(getLogger());

            String packageName = getServer().getClass().getPackage().getName();
            String version = packageName.substring(packageName.lastIndexOf('.') + 1);

            registerSerializers();

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

                getServer().getPluginManager().registerEvents(new PlayerJoinListener(api), this);
                getServer().getServicesManager().register(SkinsAPI.class, api, this, ServicePriority.Normal);
            } else {
                getLogger().info("Using BungeeCord as skin manager");
            }

            getServer().getPluginManager().registerEvents(new InventoryListener(), this);
            getServer().getPluginManager().registerEvents(new NpcClickListener(api, menuManager), this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable(){
        if(database != null){
            database.closeConnection();
        }

        SkinHash.stopCleaner();
    }

    private void registerCommands(){
        Command commandSkin = new CommandSkin(api.getLang());
        Command commandSkull = new CommandSkull();

        commandSkin.addSub(new CommandSkinPlayer(api), "player");
        commandSkin.addSub(new CommandSkinUrl(api), "url");
        commandSkin.addSub(new CommandSkinReset(api), "reset");
        commandSkin.addSub(new CommandSkinMenu(api), "menu");
        commandSkin.addSub(new CommandSkinTo(api), "to");
        commandSkin.addSub(new CommandSkinPreview(api), "preview");
        commandSkin.addSub(new CommandSkinSet(api), "set");

        commandSkull.addSub(new CommandSkullPlayer(api), "player");
        commandSkull.addSub(new CommandSkullUrl(api), "url");

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
                Logger.severe("Undefined database type: %s", type);
                return;
        }

        this.database.executeSQL(FileUtil.readResourceContent("/tables/" + type + "/skins.sql"));
    }
}
