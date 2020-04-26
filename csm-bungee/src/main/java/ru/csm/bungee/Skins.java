package ru.csm.bungee;

import com.google.common.reflect.TypeToken;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.leaping.modded.configurate.objectmapping.serialize.TypeSerializers;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.Tables;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.MySQLDatabase;
import ru.csm.api.upload.Profile;
import ru.csm.bungee.listeners.PostLoginListener;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class Skins extends Plugin {

    private Database database;
    private SkinsAPI<ProxiedPlayer> api;

    @Override
    public void onEnable(){
        try {
            registerSerializers();

            Path pluginFolder = this.getDataFolder().toPath();
            Configuration configuration = new Configuration("bungee/config.conf", pluginFolder, this);
            Language lang = new Language(this, Paths.get(pluginFolder.toString(), "lang"), "lang/"+configuration.get().getNode("language").getString());

            try{
                setupDatabase(configuration);
            } catch (SQLException e){
                getLogger().severe("Cannot connect to SQL database: " + e.getMessage());
                return;
            }

            api = new BungeeSkinsAPI(database, configuration, lang);

            registerListeners();
            registerCommands();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable(){
        database.closeConnection();
    }

    private void registerCommands(){
        // TODO register commands
    }

    private void registerSerializers(){
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Profile.class), new Profile.Serializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Skin.class), new Skin.Serializer());
    }

    private void registerListeners(){
        getProxy().getPluginManager().registerListener(this, new PostLoginListener(api));
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
