package ru.csm.bungee;

import com.google.common.reflect.TypeToken;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.leaping.modded.configurate.objectmapping.serialize.TypeSerializers;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.H2Database;
import ru.csm.api.storage.database.MySQLDatabase;
import ru.csm.api.upload.Profile;
import ru.csm.api.utils.FileUtil;
import ru.csm.bungee.command.CommandExecutor;
import ru.csm.bungee.command.SubCommand;
import ru.csm.bungee.commands.*;
import ru.csm.bungee.listeners.PlayerListeners;

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
        CommandExecutor skinCommand = new CommandSkin(api.getLang());
        CommandExecutor skullCommand = new CommandSkin(api.getLang());

        SubCommand skinPlayer = new CommandSkinPlayer(api);
        SubCommand skinUrl = new CommandSkinUrl(api);
        SubCommand skinReset = new CommandSkinReset(api);
        SubCommand skinMenu = new CommandSkinMenu(api);
        SubCommand skinTo = new CommandSkinTo(api);
        SubCommand skinPreview = new CommandSkinPreview(api);
        SubCommand skullPlayer = new CommandSkullPlayer(api);
        SubCommand skullUrl = new CommandSkullUrl(api);
        SubCommand skullTo = new CommandSkullTo(api);

        skinPlayer.setPermission("csm.skin.player");
        skinUrl.setPermission("csm.skin.url");
        skinReset.setPermission("csm.skin.reset");
        skinMenu.setPermission("csm.skin.menu");
        skinTo.setPermission("csm.skin.to");
        skinPreview.setPermission("csm.skin.preview");
        skullPlayer.setPermission("csm.skull.player");
        skullUrl.setPermission("csm.skull.player");
        skullTo.setPermission("csm.skull.player");

        skinCommand.addSub(skinPlayer, "player");
        skinCommand.addSub(skinUrl, "url");
        skinCommand.addSub(skinReset, "reset");
        skinCommand.addSub(skinMenu, "menu");
        skinCommand.addSub(skinTo, "to");
        skinCommand.addSub(skinPreview, "preview");

        skullCommand.addSub(skullPlayer, "player");
        skullCommand.addSub(skullUrl, "url");
        skullCommand.addSub(skullTo, "to");
    }

    private void registerSerializers(){
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Profile.class), new Profile.Serializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Skin.class), new Skin.Serializer());
    }

    private void registerListeners(){
        getProxy().getPluginManager().registerListener(this, new PlayerListeners(api));
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
