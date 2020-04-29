package ru.csm.bungee;

import com.google.common.reflect.TypeToken;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ninja.leaping.modded.configurate.objectmapping.ObjectMappingException;
import ru.csm.api.player.Head;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.Tables;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.Row;
import ru.csm.api.upload.*;
import ru.csm.api.utils.Logger;
import ru.csm.api.utils.Validator;
import ru.csm.bungee.player.BungeeSkinPlayer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BungeeSkinsAPI implements SkinsAPI<ProxiedPlayer> {

    private final Configuration conf;
    private final Database database;
    private final Language lang;

    private final Map<UUID, SkinPlayer<ProxiedPlayer>> playersByUUID = new TreeMap<>();
    private final Map<String, SkinPlayer<ProxiedPlayer>> playersByName = new TreeMap<>();

    private Map<String, String> blacklist;
    private Map<String, String> whitelist;

    private Skin[] defaultSkins;

    private NameQueue nameQueue;
    private ImageQueue imageQueue;

    public BungeeSkinsAPI(Database database, Configuration conf, Language lang) {
        this.database = database;
        this.conf = conf;
        this.lang = lang;

        loadDefaultSkins();
        loadBlacklist();
        loadWhitelist();
        loadQueues();
    }

    @Override
    public Configuration getConfiguration(){
        return conf;
    }

    @Override
    public Language getLang(){
        return lang;
    }

    @Override
    public Database getDatabase() {
        return database;
    }

    @Override
    public NameQueue getNameQueue() {
        return nameQueue;
    }

    @Override
    public ImageQueue getImageQueue() {
        return imageQueue;
    }

    @Override
    public boolean isBlackList(String nickname, SkinPlayer<ProxiedPlayer> player){
        if (blacklist == null) return false;

        if (blacklist.containsKey(nickname.toLowerCase())){
            String perm = blacklist.get(nickname.toLowerCase());
            return perm == null || player.hasPermission(perm);
        }

        return false;
    }

    @Override
    public boolean isWhitelist(String nickname, SkinPlayer<ProxiedPlayer> player){
        if (whitelist == null) return true;

        if (whitelist.containsKey(nickname.toLowerCase())){
            String perm = whitelist.get(nickname.toLowerCase());
            return perm == null || player.hasPermission(perm);
        }

        return false;
    }

    @Override
    public Skin getDefaultSkin(){
        return defaultSkins[ThreadLocalRandom.current().nextInt(defaultSkins.length)];
    }

    @Override
    public SkinPlayer<ProxiedPlayer> getPlayer(UUID uuid){
        return playersByUUID.get(uuid);
    }

    @Override
    public SkinPlayer<ProxiedPlayer> getPlayer(String name){
        return playersByName.get(name.toLowerCase());
    }

    @Override
    public Head getPlayerHead(String name) {
        Row row = database.getRow(Tables.SKINS, "name", name);

        if (row != null){
            String owner = row.getField("name").toString();
            Skin skin = new Skin();

            if (row.hasField("custom_value")){
                skin.setValue(row.getField("custom_value").toString());
            } else {
                skin.setValue(row.getField("default_value").toString());
            }

            return new Head(owner, skin.getURL());
        }

        return null;
    }

    @Override
    public void showPreview(ProxiedPlayer player, Skin skin, boolean openMenu, String permission) {
        // TODO send data to spigot
    }

    @Override
    public void setCustomSkin(SkinPlayer<ProxiedPlayer> player, Skin skin){
        player.setCustomSkin(skin);
        player.applySkin();
        player.refreshSkin();
        savePlayer(player);
        player.sendMessage(lang.of("skin.success"));
    }

    @Override
    public void setCustomSkin(ProxiedPlayer p, Skin skin) {
        SkinPlayer<ProxiedPlayer> player = getPlayer(p.getUniqueId());
        if (player != null) setCustomSkin(player, skin);
    }

    @Override
    public void setSkinFromImage(SkinPlayer<ProxiedPlayer> player, String link, SkinModel model) {
        if(!Validator.validateURL(link)){
            player.sendMessage(lang.of("skin.image.invalid"));
            return;
        }

        imageQueue.push(player, link, model);
        long seconds = imageQueue.getWaitSeconds();
        player.sendMessage(String.format(lang.of("skin.process"), seconds));
    }

    @Override
    public void setSkinFromName(SkinPlayer<ProxiedPlayer> player, String name) {
        if (!Validator.validateName(name)){
            player.sendMessage(lang.of("skin.name.invalid"));
            return;
        }

        if (isBlackList(name, player) || !isWhitelist(name, player)){
            player.sendMessage(lang.of("skin.unallowed"));
            return;
        }

        nameQueue.push(player, name);
        long seconds = nameQueue.getWaitSeconds();
        player.sendMessage(String.format(lang.of("skin.process"), seconds));
    }

    @Override
    public void resetSkin(SkinPlayer<ProxiedPlayer> player) {
        if(!player.hasCustomSkin()){
            player.sendMessage(lang.of("skin.reset.empty"));
            return;
        }

        player.resetSkin();
        player.applySkin();
        player.refreshSkin();
        savePlayer(player);
        player.sendMessage(lang.of("skin.reset.success"));
    }

    @Override
    public void openSkinsMenu(ProxiedPlayer player, int page) {
        // TODO send data to spigot
    }

    @Override
    public SkinPlayer<ProxiedPlayer> buildPlayer(ProxiedPlayer player) {
        return new BungeeSkinPlayer(player);
    }

    @Override
    public void addPlayer(SkinPlayer<ProxiedPlayer> player) {
        playersByName.put(player.getName().toLowerCase(), player);
        playersByUUID.put(player.getUUID(), player);
    }

    @Override
    public void removePlayer(UUID uuid) {
        SkinPlayer<ProxiedPlayer> player = getPlayer(uuid);
        if (player != null){
            playersByName.remove(player.getName().toLowerCase());
            playersByUUID.remove(uuid);
        }
    }

    private void loadDefaultSkins(){
        try{
            List<Skin> skins = conf.get().getNode("defaultSkins").getList(TypeToken.of(Skin.class));
            defaultSkins = skins.toArray(new Skin[0]);
        } catch (ObjectMappingException e){
            Logger.severe("Cannot load default skins: %s", e.getMessage());
        }
    }

    private void loadBlacklist(){
        boolean enable = conf.get().getNode("enableBlacklist").getBoolean();

        if (enable){
            blacklist = new HashMap<>();
            try{
                List<String> list = conf.get().getNode("blacklist").getList(TypeToken.of(String.class));

                for (String elem : list){
                    String[] arr = elem.split(":");
                    blacklist.put(arr[0].toLowerCase(), (arr.length == 2) ? arr[1] : null);
                }
            } catch (ObjectMappingException e){
                Logger.severe("Cannot load skins blacklist: %s", e.getMessage());
            }
        }
    }

    private void loadWhitelist(){
        boolean enable = conf.get().getNode("enableWhitelist").getBoolean();

        if (enable){
            blacklist = null;
            whitelist = new HashMap<>();
            try{
                List<String> list = conf.get().getNode("whitelist").getList(TypeToken.of(String.class));

                for (String elem : list){
                    String[] arr = elem.split(":");
                    whitelist.put(arr[0].toLowerCase(), (arr.length == 2) ? arr[1] : null);
                }
            } catch (ObjectMappingException e){
                Logger.severe("Cannot load skins whitelist: %s", e.getMessage());
            }
        }
    }

    private void loadQueues() {
        try{
            boolean enableMojang = conf.get().getNode("mojang", "enable").getBoolean();
            int imagePeriod = 6;

            if (enableMojang){
                imagePeriod = conf.get().getNode("mojang", "period").getInt();
                List<Profile> profiles = conf.get().getNode("mojang", "accounts").getList(TypeToken.of(Profile.class));
                imageQueue = new MojangQueue(this, profiles);
            } else {
                imageQueue = new MineskinQueue(this);
            }

            nameQueue = new NameQueue(this);
            nameQueue.start(1);
            imageQueue.start(imagePeriod);
        } catch (ObjectMappingException e){
            Logger.severe("Cannot load skin queue service: %s" + e.getMessage());
        }
    }

}
