package ru.csm.api.services;

import com.google.common.reflect.TypeToken;
import ninja.leaping.modded.configurate.objectmapping.ObjectMappingException;
import ru.csm.api.player.*;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Tables;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.Row;
import ru.csm.api.upload.*;
import ru.csm.api.utils.Logger;
import ru.csm.api.utils.UuidUtil;
import ru.csm.api.utils.Validator;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class SkinsAPI {

    private final Configuration conf;
    private final Database database;
    private final Language lang;

    private final Map<UUID, SkinPlayer<?>> playersByUUID = new TreeMap<>();
    private final Map<String, SkinPlayer<?>> playersByName = new TreeMap<>();

    private Map<String, String> blacklist;
    private Map<String, String> whitelist;

    private Skin[] defaultSkins;

    private NameQueue nameQueue;
    private ImageQueue imageQueue;

    public SkinsAPI(Database database, Configuration conf, Language lang) {
        this.database = database;
        this.conf = conf;
        this.lang = lang;

        loadDefaultSkins();
        loadBlacklist();
        loadWhitelist();
        loadQueues();
    }

    public Configuration getConfiguration(){
        return conf;
    }

    public Language getLang(){
        return lang;
    }

    public NameQueue getNameQueue() {
        return nameQueue;
    }

    public ImageQueue getImageQueue() {
        return imageQueue;
    }

    /**
     * Check is premium nickname exist in blacklist
     * @param nickname Required premium nickname
     * @return true if exists and false otherwise
     */
    public boolean isBlackList(String nickname, SkinPlayer<?> player){
        if (blacklist == null) return false;

        if (blacklist.containsKey(nickname.toLowerCase())){
            String perm = blacklist.get(nickname.toLowerCase());
            return perm == null || player.hasPermission(perm);
        }

        return false;
    }

    /**
     * Check is premium nickname exist in whitelist
     * @param nickname Required premium nickname
     * @return true if exists and false otherwise
     */
    public boolean isWhitelist(String nickname, SkinPlayer<?> player){
        if (whitelist == null) return true;

        if (whitelist.containsKey(nickname.toLowerCase())){
            String perm = whitelist.get(nickname.toLowerCase());
            return perm == null || player.hasPermission(perm);
        }

        return false;
    }

    /**
     * @return Get random default skin, defined in config
     * */
    public Skin getDefaultSkin(){
        return defaultSkins[ThreadLocalRandom.current().nextInt(defaultSkins.length)];
    }

    /**
     * Get skinned player by UUID
     * @param uuid - UUID of the player
     * */
    public SkinPlayer<?> getPlayer(UUID uuid){
        return playersByUUID.get(uuid);
    }

    /**
     * Get skinned player by name
     * @param name - Name of the player
     * */
    public SkinPlayer<?> getPlayer(String name){
        return playersByName.get(name.toLowerCase());
    }

    /**
     * Add player to hash. This method used by plugin and not recommended use as API
     * @param player - SkinPlayer object
     * */
    public void addPlayer(SkinPlayer<?> player){
        playersByUUID.put(player.getUUID(), player);
        playersByName.put(player.getName().toLowerCase(), player);
    }

    /**
     * Remove player from hash
     * @param uuid - UUID of the player
     * */
    public void removePlayer(UUID uuid){
        SkinPlayer<?> target = playersByUUID.get(uuid);

        if(target != null){
            playersByUUID.remove(target.getUUID());
            playersByName.remove(target.getName().toLowerCase());
        }
    }

    /**
     * Remove player from hash
     * @param name - Name of the player
     * */
    public void removePlayer(String name){
        SkinPlayer<?> target = playersByName.get(name.toLowerCase());

        if(target != null){
            playersByName.remove(target.getName().toLowerCase());
            playersByUUID.remove(target.getUUID());
        }
    }

    /**
     * Set custom skin for player
     * @param player SkinPlayer object
     * @param skin Skin object
     */
    public void setCustomSkin(SkinPlayer<?> player, Skin skin){
        player.setCustomSkin(skin);
        player.applySkin();
        player.refreshSkin();
        savePlayer(player);
        player.sendMessage(lang.of("skin.success"));
    }

    /**
     * Set skin from image link
     * @param player SkinPlayer object
     * @param link Link to *.png image
     * @param model Model of the skin
     */
    public void setSkinFromImage(SkinPlayer<?> player, String link, SkinModel model) {
        if(!Validator.validateURL(link)){
            player.sendMessage(lang.of("skin.image.invalid"));
            return;
        }

        imageQueue.push(player, link, model);
        long seconds = imageQueue.getWaitSeconds();
        player.sendMessage(String.format(lang.of("skin.process"), seconds));
    }

    /**
     * Set skin from premium account
     * @param player SkinPlayer object
     * @param name Name of the target premium account
     */
    public void setSkinFromName(SkinPlayer<?> player, String name) {
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

    /**
     * Reset player skin to default
     * @param player SkinPlayer object
     */
    public void resetSkin(SkinPlayer<?> player) {
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

    /**
     * Get player head with her current skin (custom or default)
     * @param player Object of SkinPlayer
     * @return Head object if player exist or null otherwise
     */
    public Head getPlayerHead(SkinPlayer<?> player){
        if(player != null) {
            Skin skin = player.getDefaultSkin();
            if(player.hasCustomSkin()){
                skin = player.getCustomSkin();
            }
            return new Head(player.getUUID(), player.getName(), skin);
        }
        return null;
    }

    /**
     * Get player head by name with her current skin (custom or default)
     * @param playerName Name of the player
     * @return Head object if player exist or null otherwise
     */
    public Head getPlayerHead(String playerName){
        SkinPlayer<?> player = getPlayer(playerName);

        if(player != null){
            Skin skin = player.getCustomSkin();

            if(skin == null){
                skin = player.getDefaultSkin();
            }

            return new Head(player.getUUID(), player.getName(), skin);
        }

        Row row = database.getRow(Tables.SKINS, "name", playerName);

        if(row != null){
            UUID uuid = UuidUtil.getUUID(row.getField("uuid").toString());
            Skin skin = new Skin();

            if(row.getField("custom_value") != null){
                skin.setValue(row.getField("custom_value").toString());
                skin.setSignature(row.getField("custom_signature").toString());
            } else{
                skin.setValue(row.getField("default_value").toString());
                skin.setSignature(row.getField("default_signature").toString());
            }

            return new Head(uuid, playerName, skin);
        }

        return null;
    }

    /**
     * Get pages count of the skins menu
     * @return Count of the menu pages
     */
    public int getMenuSize(){
        Row[] rows = database.getRowsWithRequest("SELECT * FROM " + Tables.SKINS + " WHERE custom_value IS NOT NULL");
        return rows.length;
    }

    /**
     * Get list of heads with player skins for menu
     * @param page Number of page in menu. Current menu size you can get use getMenuSize()
     * @return List of players heads or empty list if required page not exist. Maximum list size - 44
     */
    public Map<UUID, Head> getHeads(int menuSize, int page){
        int count = 21;

        int remain = menuSize%count;
        int pages = menuSize/count;
        int startPoint = (page-1)*count;

        if(menuSize <= count){
            remain = 0;
            pages = 1;
            startPoint = 0;
        }

        if(remain > 0){
            pages += 1;
            remain = 0;
        }

        if(page > pages && remain != 0){
            return null;
        }

        Row[] rows = database.getRowsWithRequest("SELECT * FROM " + Tables.SKINS + " WHERE custom_value IS NOT NULL ORDER BY -id LIMIT " + startPoint + "," + count);
        Map<UUID, Head> heads = new HashMap<>();

        for(Row row : rows){
            UUID uuid = UUID.fromString(row.getField("uuid").toString());
            String name = row.getField("name").toString();
            String value = row.getField("custom_value").toString();
            String signature = row.getField("custom_signature").toString();
            Skin skin = new Skin(value, signature);
            heads.put(uuid, new Head(uuid, name, skin));
        }

        return heads;
    }

    /**
     * Save the player data into current storage (local or remote database)
     * @param player - Object of a player
     * */
    public void savePlayer(SkinPlayer<?> player){
        CompletableFuture.runAsync(()->{
            Row row = new Row();

            row.addField("name", player.getName());
            row.addField("default_value", player.getDefaultSkin().getValue());
            row.addField("default_signature", player.getDefaultSkin().getSignature());

            if(player.hasCustomSkin()){
                row.addField("custom_value", player.getCustomSkin().getValue());
                row.addField("custom_signature", player.getCustomSkin().getSignature());
            }

            database.updateRow(Tables.SKINS, "uuid", player.getUUID().toString(), row);
        });
    }

    public void createPlayer(SkinPlayer<?> player){
        CompletableFuture.runAsync(()->{
            Row row = new Row();

            row.addField("uuid", player.getUUID());
            row.addField("name", player.getName());
            row.addField("default_value", player.getDefaultSkin().getValue());
            row.addField("default_signature", player.getDefaultSkin().getSignature());

            database.createRow(Tables.SKINS, row);
        });
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