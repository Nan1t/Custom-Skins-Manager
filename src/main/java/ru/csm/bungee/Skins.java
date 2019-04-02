package ru.csm.bungee;

import com.google.common.reflect.TypeToken;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import ru.csm.api.network.Channels;
import ru.csm.api.serializers.ProfileSerializer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.Tables;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.MySQLDatabase;
import ru.csm.api.storage.database.SQLiteDatabase;
import ru.csm.api.upload.entity.Profile;
import ru.csm.bungee.listeners.PostLoginListener;
import ru.csm.bungee.network.PluginMessageService;
import ru.csm.bungee.network.executors.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class Skins extends Plugin {

    private static Plugin plugin;
    private static Path pluginFolder;
    private static PluginMessageService pluginMessageService;

    private static Configuration configuration;
    private static Language lang;
    private static Database database;

    private static SkinsAPI api;

    @Override
    public void onEnable(){
        try {
            plugin = this;
            pluginFolder = this.getDataFolder().toPath();
            configuration = new Configuration("configuration/bungee/config.conf", pluginFolder, plugin);
            lang = new Language(plugin, Paths.get(pluginFolder.toString(), "lang"), "lang/"+configuration.get().getNode("language").getString());

            setupDatabase();

            pluginMessageService = new PluginMessageService();
            api = new SkinsAPI(database, configuration, lang);

            registerMessageExecutors();
            registerSerializers();
            registerListeners();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable(){
        database.closeConnection();
    }

    private void registerSerializers(){
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Profile.class), new ProfileSerializer());
    }

    private void registerListeners(){
        getProxy().getPluginManager().registerListener(plugin, pluginMessageService);
        getProxy().getPluginManager().registerListener(plugin, new PostLoginListener(database, api));
    }

    private void registerMessageExecutors(){
        pluginMessageService.registerExecutor(Channels.SKINS_PLAYER, new ExecutorCommandPlayer(api));
        pluginMessageService.registerExecutor(Channels.SKINS_URL, new ExecutorCommandUrl(api));
        pluginMessageService.registerExecutor(Channels.SKINS_RESET, new ExecutorCommandReset(api));
        pluginMessageService.registerExecutor(Channels.SKINS_MENU, new ExecutorSkinsMenu(api));
        pluginMessageService.registerExecutor(Channels.SKINS_APPLY, new ExecutorSkinsApply(api));
        pluginMessageService.registerExecutor(Channels.SKINS_CITIZENS, new ExecutorSkinsCitizens(api));
    }

    private void setupDatabase() throws SQLException {
        String type = configuration.get().getNode("database", "type").getString();
        String host = configuration.get().getNode("database", "host").getString();
        String name = configuration.get().getNode("database", "database").getString();
        String user = configuration.get().getNode("database", "user").getString();
        String password = configuration.get().getNode("database", "password").getString();
        int port = configuration.get().getNode("database", "port").getInt();

        if(type.equalsIgnoreCase("mysql")){
            database = new MySQLDatabase(host, port, name, user, password);

            database.executeSQL("CREATE TABLE IF NOT EXISTS `"+ Tables.SKINS+"` (\n" +
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
    }

    public static Plugin getPlugin(){
        return plugin;
    }

    public static PluginMessageService getPluginMessageService(){
        return pluginMessageService;
    }

    public static Path getPluginFolder(){
        return pluginFolder;
    }

    public static Configuration getConfiguration() {
        return configuration;
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
}
