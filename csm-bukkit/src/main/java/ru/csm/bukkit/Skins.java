package ru.csm.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.reflect.TypeToken;
import ninja.leaping.modded.configurate.objectmapping.serialize.TypeSerializers;

import org.bukkit.inventory.ItemStack;
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
import ru.csm.bukkit.serializers.ItemStackSerializer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.MySQLDatabase;
import ru.csm.api.storage.database.SQLiteDatabase;
import ru.csm.api.upload.Profile;
import ru.csm.bukkit.gui.managers.BungeeMenuManager;
import ru.csm.bukkit.gui.managers.CustomMenuManager;
import ru.csm.bukkit.gui.managers.MenuManager;
import ru.csm.bukkit.listeners.InventoryClickListener;
import ru.csm.bukkit.listeners.PlayerJoinListener;
import ru.csm.bukkit.network.PluginMessageService;
import ru.csm.bukkit.network.executors.SkinsMenuExecutor;
import ru.csm.bukkit.network.executors.SkinsRefreshExecutor;
import ru.csm.bukkit.protocol.NPCService;
import ru.csm.bukkit.protocol.listeners.NpcClickListener;
import ru.csm.bukkit.util.BukkitTasks;
import ru.csm.bukkit.util.FileUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class Skins extends JavaPlugin {

    private Database database;
    private SkinsAPI api;
    private MenuManager menuManager;
    private NPCService npcService;

    @Override
    public void onEnable(){
        try{
            Logger.set(getLogger());

            if(!checkDependencies()){
                getPluginLoader().disablePlugin(this);
                return;
            }

            String packageName = getServer().getClass().getPackage().getName();
            String version = packageName.substring(packageName.lastIndexOf('.') + 1);

            BukkitTasks.setPlugin(this);
            SkinHandlers.init(version);

            Configuration configuration = new Configuration("bukkit/config.conf", getDataFolder().toPath(), this);
            Configuration menuConf = new Configuration("bukkit/menu.conf", getDataFolder().toPath(), this);
            Language lang = new Language(this, Paths.get(getDataFolder().toPath().toString(), "lang"), "lang/"+configuration.get().getNode("language").getString());
            PluginMessageService pmService = new PluginMessageService(this);

            registerSerializers();

            boolean isCustomMenu = menuConf.get().getNode("custom", "enable").getBoolean();

            npcService = new NPCService();

            if(!SpigotConfig.bungee){
                try{
                    setupDatabase(configuration);
                } catch (SQLException e){
                    getLogger().severe("Cannot connect to SQL database: " + e.getMessage());
                    getPluginLoader().disablePlugin(this);
                    return;
                }

                SkinHash.startCleaner();

                api = new SkinsAPI(database, configuration, lang);
                menuManager = isCustomMenu ? new CustomMenuManager(menuConf, lang, api) : new MenuManager(menuConf, lang, api);
                registerCommands();
            } else {
                api = new BungeeSkinsAPI(database, configuration, lang, pmService);
                menuManager = new BungeeMenuManager(menuConf, lang, api, pmService);
                getLogger().info("Using BungeeCord as skin applier");
            }

            registerMessageListeners();
            registerListeners();

            getServer().getServicesManager().register(SkinsAPI.class, api, this, ServicePriority.Normal);
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

    private boolean checkDependencies(){
        if(!getServer().getPluginManager().isPluginEnabled("ProtocolLib")){
            getLogger().severe("ProtocolLib is not enabled! CSM disabled");
            return false;
        }
        return true;
    }

    private void registerCommands(){
        Command commandSkin = new CommandSkin(api.getLang());
        Command commandSkull = new CommandSkull();

        commandSkin.addSub(new CommandSkinPlayer(api), "player");
        commandSkin.addSub(new CommandSkinUrl(api), "url");
        commandSkin.addSub(new CommandSkinReset(api), "reset");
        commandSkin.addSub(new CommandSkinTo(api), "to");
        commandSkin.addSub(new CommandSkinPreview(api), "preview");

        commandSkull.addSub(new CommandSkullPlayer(api), "player");
        commandSkull.addSub(new CommandSkullUrl(api), "url");

        getCommand("csm").setExecutor(commandSkin);
        getCommand("csmskull").setExecutor(commandSkull);
    }

    private void registerListeners(){
        if(!SpigotConfig.bungee){
            getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, database, api), this);
        }

        getServer().getPluginManager().registerEvents(new InventoryClickListener(menuManager, npcService, api), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new NpcClickListener(this, PacketType.Play.Client.USE_ENTITY, npcService, menuManager, api));
    }

    private void registerSerializers(){
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Profile.class), new Profile.Serializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Skin.class), new Skin.Serializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(ItemStack.class), new ItemStackSerializer());
    }

    private void registerMessageListeners(){
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS_PLAYER);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS_URL);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS_RESET);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS_MENU);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS_APPLY);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS_REFRESH);

        getServer().getMessenger().registerIncomingPluginChannel(this, Channels.SKINS_MENU, new SkinsMenuExecutor(menuManager));
        getServer().getMessenger().registerIncomingPluginChannel(this, Channels.SKINS_REFRESH, new SkinsRefreshExecutor());
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
