package ru.csm.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import ru.csm.api.network.Channels;
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
import ru.csm.bukkit.commands.CommandSkull;
import ru.csm.bukkit.gui.managers.BungeeMenuManager;
import ru.csm.bukkit.gui.managers.CustomMenuManager;
import ru.csm.bukkit.gui.managers.MenuManager;
import ru.csm.bukkit.listeners.InventoryClickListener;
import ru.csm.bukkit.listeners.PlayerJoinListener;
import ru.csm.bukkit.network.PluginMessageService;
import ru.csm.bukkit.network.executors.SkinsCitizensExecutor;
import ru.csm.bukkit.network.executors.SkinsMenuExecutor;
import ru.csm.bukkit.network.executors.SkinsRefreshExecutor;
import ru.csm.bukkit.protocol.NPCService;
import ru.csm.bukkit.protocol.listeners.NpcClickListener;
import ru.csm.bukkit.util.BukkitTasks;

import java.nio.file.Paths;
import java.sql.SQLException;

public class Skins extends JavaPlugin {

    private Database database;
    private SkinsAPI api;
    private MenuManager menuManager;
    private NPCService npcService;

    private static int subVersion;
    private static boolean isBungeeCord = false;
    private static boolean isEnabledCitizens = false;

    @Override
    public void onEnable(){
        try{
            if(!checkDependencies()){
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            BukkitTasks.setPlugin(this);

            Configuration configuration = new Configuration("configuration/bukkit/config.conf", getDataFolder().toPath(), this);
            Configuration menuConf = new Configuration("configuration/bukkit/menu.conf", getDataFolder().toPath(), this);
            Language lang = new Language(this, Paths.get(getDataFolder().toPath().toString(), "lang"), "lang/"+configuration.get().getNode("language").getString());
            PluginMessageService pmService = new PluginMessageService(this);

            isBungeeCord = configuration.get().getNode("bungeecord").getBoolean();
            isEnabledCitizens = getServer().getPluginManager().isPluginEnabled("Citizens");

            String packageName = getServer().getClass().getPackage().getName();
            String ver = packageName.substring(packageName.lastIndexOf('.') + 1);
            subVersion = Integer.parseInt(ver.split("_")[1]);

            registerSerializers();

            boolean isCustomMenu = menuConf.get().getNode("custom", "enable").getBoolean();

            npcService = new NPCService(subVersion);

            if(!isBungeeCord){
                try{
                    setupDatabase(configuration);
                } catch (SQLException e){
                    getLogger().severe("Cannot connect to SQL database: " + e.getMessage());
                    getServer().getPluginManager().disablePlugin(this);
                    return;
                }

                api = new SkinsAPI(database, configuration, lang);
                menuManager = isCustomMenu ? new CustomMenuManager(menuConf, lang, api) : new MenuManager(menuConf, lang, api);
                getCommand("csm").setExecutor(new CommandSkin(this, api, menuConf, lang, menuManager, npcService));
                getCommand("csmskull").setExecutor(new CommandSkull(api, lang));
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

        api.stopCleaner();
    }

    private boolean checkDependencies(){
        if(!getServer().getPluginManager().isPluginEnabled("ProtocolLib")){
            getLogger().severe("ProtocolLib is not enabled! CSM disabled");
            return false;
        }

        return true;
    }

    public static int getSubVersion() {
        return subVersion;
    }

    public static boolean isEnabledCitizens(){
        return isEnabledCitizens;
    }

    private void registerListeners(){
        if(!isBungeeCord){
            getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, database, api), this);
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

        getServer().getMessenger().registerIncomingPluginChannel(this, Channels.SKINS_MENU, new SkinsMenuExecutor(menuManager));
        getServer().getMessenger().registerIncomingPluginChannel(this, Channels.SKINS_REFRESH, new SkinsRefreshExecutor());

        getServer().getMessenger().registerIncomingPluginChannel(this, Channels.SKINS_CITIZENS, new SkinsCitizensExecutor(this));
    }

    private void setupDatabase(Configuration configuration) throws SQLException {
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
                        "\t`name` varchar(16) NOT NULL,\n" +
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
        createSQLiteDatabase(getDataFolder().toPath().toString(), Tables.SKINS, "user", "");
    }

    private void createSQLiteDatabase(String path, String dbName, String user, String password) throws SQLException{
        database = new SQLiteDatabase(path, dbName, user, password);

        database.executeSQL("CREATE TABLE IF NOT EXISTS `"+Tables.SKINS+"` (\n" +
                "\t`id` IDENTITY PRIMARY KEY,\n" +
                "\t`uuid` varchar(38) NOT NULL,\n" +
                "\t`name` varchar(16) NOT NULL,\n" +
                "\t`default_value` varchar(512) NOT NULL,\n" +
                "\t`default_signature` varchar(1024) NOT NULL,\n" +
                "\t`custom_value` varchar(1024),\n" +
                "\t`custom_signature` varchar(1024));");
    }
}
