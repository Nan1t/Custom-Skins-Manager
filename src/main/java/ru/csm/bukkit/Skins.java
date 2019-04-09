package ru.csm.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import ru.csm.api.network.Channels;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.serializers.ItemStackSerializer;
import ru.csm.api.serializers.ProfileSerializer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.Tables;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.MySQLDatabase;
import ru.csm.api.storage.database.SQLiteDatabase;
import ru.csm.api.upload.entity.Profile;
import ru.csm.bukkit.commands.CommandSkin;
import ru.csm.bukkit.gui.managers.BungeeMenuManager;
import ru.csm.bukkit.gui.managers.CustomMenuManager;
import ru.csm.bukkit.gui.managers.MenuManager;
import ru.csm.bukkit.listeners.InventoryClickListener;
import ru.csm.bukkit.listeners.PlayerJoinListener;
import ru.csm.bukkit.network.SkinsCitizensListener;
import ru.csm.bukkit.network.SkinsMenuListener;
import ru.csm.bukkit.network.SkinsRefreshListener;
import ru.csm.bukkit.protocol.NPCService;
import ru.csm.bukkit.protocol.listeners.NpcClickListener;
import ru.csm.bukkit.services.BungeeSkinsAPI;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class Skins extends JavaPlugin {

    private static Plugin plugin;
    private static Path pluginFolder;

    private static Configuration configuration, menuConf;
    private static Language lang;
    private static Database database;

    private static SkinsAPI api;
    private static MenuManager menuManager;
    private static NPCService npcService;

    private static boolean isBungeeCord = false;

    @Override
    public void onEnable(){
        try{
            checkDependencies();

            plugin = this;
            pluginFolder = this.getDataFolder().toPath();
            configuration = new Configuration("configuration/bukkit/config.conf", pluginFolder, plugin);
            menuConf = new Configuration("configuration/bukkit/menu.conf", pluginFolder, plugin);
            lang = new Language(plugin, Paths.get(pluginFolder.toString(), "lang"), "lang/"+configuration.get().getNode("language").getString());
            isBungeeCord = configuration.get().getNode("bungeecord").getBoolean();

            registerSerializers();

            if(isBungeeCord){
                getLogger().info("Using BungeeCord as skin applier");
                api = new BungeeSkinsAPI(database, configuration, lang);
                menuManager = new BungeeMenuManager(menuConf, lang, api);
            } else {
                setupDatabase();
                api = new SkinsAPI(database, configuration, lang);
                menuManager = new MenuManager(menuConf, lang, api);
            }

            if(menuConf.get().getNode("custom", "enable").getBoolean()){
                menuManager = new CustomMenuManager(menuConf, lang, api);
            }

            npcService = new NPCService(getSubVersion());

            registerMessageListeners();
            registerEvents();

            getServer().getServicesManager().register(SkinsAPI.class, api, plugin, ServicePriority.Normal);

            getServer().getServicesManager().getRegistration(SkinsAPI.class).getProvider();
            getCommand("csm").setExecutor(new CommandSkin(api, configuration, lang, menuManager));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable(){
        if(database != null){
            database.closeConnection();
        }
    }

    private void checkDependencies(){
        if(!getServer().getPluginManager().isPluginEnabled("ProtocolLib")){
            getLogger().severe("ProtocolLib is not enabled! CSM disabled");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public static int getSubVersion() {
        final String packageName = plugin.getServer().getClass().getPackage().getName();
        String ver = packageName.substring(packageName.lastIndexOf('.') + 1);
        String[] arr = ver.split("_");
        return Integer.parseInt(arr[1]);
    }

    public static boolean isEnabledCitizens(){
        return plugin.getServer().getPluginManager().isPluginEnabled("Citizens");
    }

    private void registerEvents(){
        if(!isBungeeCord){
            getServer().getPluginManager().registerEvents(new PlayerJoinListener(database, api), this);
        }
        getServer().getPluginManager().registerEvents(new InventoryClickListener(menuManager, npcService, api), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new NpcClickListener(this, PacketType.Play.Client.USE_ENTITY, npcService, menuManager, api));
    }

    private void registerSerializers(){
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Profile.class), new ProfileSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(ItemStack.class), new ItemStackSerializer());
    }

    private void registerMessageListeners(){
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS_PLAYER);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS_URL);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS_RESET);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS_MENU);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS_APPLY);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS_REFRESH);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.SKINS_CITIZENS);

        getServer().getMessenger().registerIncomingPluginChannel(this, Channels.SKINS_MENU, new SkinsMenuListener(menuManager));
        getServer().getMessenger().registerIncomingPluginChannel(this, Channels.SKINS_REFRESH, new SkinsRefreshListener());

        getServer().getMessenger().registerIncomingPluginChannel(this, Channels.SKINS_CITIZENS, new SkinsCitizensListener());
    }

    public static void reloadConfiguration() throws IOException {
        configuration = new Configuration("configuration/bukkit/config.conf", pluginFolder, plugin);
        lang = new Language(plugin, Paths.get(pluginFolder.toString(), "lang"), "lang/"+configuration.get().getNode("language").getString());
    }

    private void setupDatabase() throws SQLException {
        boolean useRemote = configuration.get().getNode("remoteDatabase").getBoolean();

        if(useRemote){
            String type = configuration.get().getNode("database", "type").getString();
            String host = configuration.get().getNode("database", "host").getString();
            String name = configuration.get().getNode("database", "database").getString();
            String user = configuration.get().getNode("database", "user").getString();
            String password = configuration.get().getNode("database", "password").getString();
            int port = configuration.get().getNode("database", "port").getInt();

            if(type.equalsIgnoreCase("mysql")){
                database = new MySQLDatabase(host, port, name, user, password);

                database.executeSQL("CREATE TABLE IF NOT EXISTS `"+Tables.SKINS+"` (\n" +
                        "\t`id` INT NOT NULL AUTO_INCREMENT,\n" +
                        "\t`uuid` varchar(38) NOT NULL,\n" +
                        "\t`name` varchar(18) NOT NULL,\n" +
                        "\t`default_value` varchar(512) NOT NULL,\n" +
                        "\t`default_signature` varchar(1024) NOT NULL,\n" +
                        "\t`custom_value` varchar(1024),\n" +
                        "\t`custom_signature` varchar(1024),\n" +
                        "\tPRIMARY KEY (`id`));");
                return;
            }

            if(type.equalsIgnoreCase("sqlite")){
                createSQLiteDatabase(host, name, user, password);
                return;
            }

            getLogger().warning("Not supported database type '" + type + "'");
            return;
        }

        // Setup local SQLite database
        createSQLiteDatabase(pluginFolder.toString(), Tables.SKINS, "user", "");
    }

    private void createSQLiteDatabase(String path, String dbname, String user, String password) throws SQLException{
        database = new SQLiteDatabase(path, dbname, user, password);

        database.executeSQL("CREATE TABLE IF NOT EXISTS `"+Tables.SKINS+"` (\n" +
                "\t`id` IDENTITY PRIMARY KEY,\n" +
                "\t`uuid` varchar(38) NOT NULL,\n" +
                "\t`name` varchar(18) NOT NULL,\n" +
                "\t`default_value` varchar(512) NOT NULL,\n" +
                "\t`default_signature` varchar(1024) NOT NULL,\n" +
                "\t`custom_value` varchar(1024),\n" +
                "\t`custom_signature` varchar(1024));");
    }

    public static Plugin getPlugin(){
        return plugin;
    }

    public static Path getPluginFolder(){
        return pluginFolder;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static Configuration getMenuConf() {
        return menuConf;
    }

    public static Language getLang() {
        return lang;
    }

    public static Database getDatabase() {
        return database;
    }

    public static SkinsAPI getSkinsAPI() {
        return api;
    }

    public static MenuManager getMenuManager(){
        return menuManager;
    }
}
