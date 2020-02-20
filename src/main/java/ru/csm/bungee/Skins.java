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
import ru.csm.api.storage.database.SimpleMySQLDatabase;
import ru.csm.api.upload.entity.Profile;
import ru.csm.bungee.commands.CommandSkin;
import ru.csm.bungee.listeners.PostLoginListener;
import ru.csm.bungee.network.PluginMessageService;
import ru.csm.bungee.network.executors.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class Skins extends Plugin {

    private PluginMessageService pmService;
    private Database database;
    private SkinsAPI api;

    @Override
    public void onEnable(){
        try {
            registerSerializers();

            Path pluginFolder = this.getDataFolder().toPath();
            Configuration configuration = new Configuration("configuration/bungee/config.conf", pluginFolder, this);
            Language lang = new Language(this, Paths.get(pluginFolder.toString(), "lang"), "lang/"+configuration.get().getNode("language").getString());

            try{
                setupDatabase(configuration);
            } catch (SQLException e){
                getLogger().severe("Cannot connect to SQL database: " + e.getMessage());
                return;
            }

            pmService = new PluginMessageService();
            api = new SkinsAPI(database, configuration, lang);

            registerMessageExecutors();
            registerListeners();

            getProxy().getPluginManager().registerCommand(this, new CommandSkin(this, api, lang));
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
        getProxy().getPluginManager().registerListener(this, pmService);
        getProxy().getPluginManager().registerListener(this, new PostLoginListener(database, api, pmService));
    }

    private void registerMessageExecutors(){
        pmService.registerExecutor(Channels.SKINS_PLAYER, new ExecutorCommandPlayer(api));
        pmService.registerExecutor(Channels.SKINS_URL, new ExecutorCommandUrl(api));
        pmService.registerExecutor(Channels.SKINS_RESET, new ExecutorCommandReset(api));
        pmService.registerExecutor(Channels.SKINS_MENU, new ExecutorSkinsMenu(api));
        pmService.registerExecutor(Channels.SKINS_APPLY, new ExecutorSkinsApply(api));
        pmService.registerExecutor(Channels.SKINS_CITIZENS, new ExecutorSkinsCitizens(api));
    }


    private void setupDatabase(Configuration configuration) throws SQLException {
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
                    "\t`name` varchar(16) NOT NULL,\n" +
                    "\t`default_value` varchar(512) NOT NULL,\n" +
                    "\t`default_signature` varchar(1024) NOT NULL,\n" +
                    "\t`custom_value` varchar(1024),\n" +
                    "\t`custom_signature` varchar(1024),\n" +
                    "\tPRIMARY KEY (`id`));");
        }
    }
}
