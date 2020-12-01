package ru.csm.api.storage;

import napi.configurate.Configuration;
import napi.configurate.Language;
import napi.configurate.source.ConfigSources;
import napi.configurate.yaml.YamlLanguage;
import ru.csm.api.player.Skin;
import ru.csm.api.upload.Profile;

import java.nio.file.Path;
import java.util.List;

public class SkinsConfig {

    private final Object plugin;
    private final Configuration conf;

    private Language language;

    // Database credentials
    private String dbType;
    private String dbHost;
    private int dbPort;
    private String dbDatabase;
    private String dbUser;
    private String dbPassword;
    // End database credentials

    private boolean restoreSkins;
    private boolean updateDefaultSkin;
    private List<Skin> defaultSkins;
    private boolean enableMojangAccounts;
    private int mojangQueryPeriod;
    private List<Profile> mojangProfiles;
    private boolean enableBlackList;
    private List<String> skinsBlackList;
    private boolean enableWhitelist;
    private List<String> skinsWhiteList;

    public SkinsConfig(Object plugin ,Configuration conf){
        this.plugin = plugin;
        this.conf = conf;
    }

    public Configuration getConf() {
        return conf;
    }

    public void load(Path dataFolder) throws Exception {
        String langPath = "lang/" + conf.getNode("language").getString() + ".yml";

        this.language = YamlLanguage.builder()
                .source(ConfigSources.resource(langPath, plugin).copyTo(dataFolder))
                .build();

        this.language.reload();

        this.dbType = conf.getNode("database", "type").getString();
        this.dbHost = conf.getNode("database", "host").getString();
        this.dbPort = conf.getNode("database", "port").getInt();
        this.dbDatabase = conf.getNode("database", "database").getString();
        this.dbUser = conf.getNode("database", "user").getString();
        this.dbPassword = conf.getNode("database", "password").getString();

        this.restoreSkins = conf.getNode("restoreSkins").getBoolean();
        this.updateDefaultSkin = conf.getNode("updateDefaultSkin").getBoolean();
        this.defaultSkins = conf.getNode("defaultSkins").getList(Skin.class);
        this.enableMojangAccounts = conf.getNode("mojang", "enable").getBoolean();
        this.mojangQueryPeriod = conf.getNode("mojang", "period").getInt();
        this.mojangProfiles = conf.getNode("mojang", "accounts").getList(Profile.class);
        this.enableBlackList = conf.getNode("enableBlacklist").getBoolean();
        this.skinsBlackList = conf.getNode("blacklist").getList(String.class);
        this.enableWhitelist = conf.getNode("enableWhitelist").getBoolean();
        this.skinsWhiteList = conf.getNode("whitelist").getList(String.class);
    }

    public Language getLanguage() {
        return language;
    }

    public String getDbType() {
        return dbType;
    }

    public String getDbHost() {
        return dbHost;
    }

    public int getDbPort() {
        return dbPort;
    }

    public String getDbName() {
        return dbDatabase;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public boolean isRestoreSkins() {
        return restoreSkins;
    }

    public boolean isUpdateDefaultSkin() {
        return updateDefaultSkin;
    }

    public List<Skin> getDefaultSkins() {
        return defaultSkins;
    }

    public boolean isEnableMojangAccounts() {
        return enableMojangAccounts;
    }

    public int getMojangQueryPeriod() {
        return mojangQueryPeriod;
    }

    public List<Profile> getMojangProfiles() {
        return mojangProfiles;
    }

    public boolean isEnableBlackList() {
        return enableBlackList;
    }

    public List<String> getSkinsBlackList() {
        return skinsBlackList;
    }

    public boolean isEnableWhitelist() {
        return enableWhitelist;
    }

    public List<String> getSkinsWhiteList() {
        return skinsWhiteList;
    }
}
