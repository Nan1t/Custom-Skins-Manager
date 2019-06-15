package ru.csm.api.services;

import ru.csm.api.WhiteListElement;
import ru.csm.api.player.*;
import ru.csm.api.storage.Language;
import ru.csm.api.threads.ThreadWorker;
import ru.csm.api.upload.QueueLicense;
import ru.csm.api.upload.QueueMineSkin;
import ru.csm.api.upload.QueueMojang;
import ru.csm.api.upload.QueueService;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Tables;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.Row;
import ru.csm.api.upload.entity.RequestImage;
import ru.csm.api.upload.entity.RequestLicense;
import ru.csm.api.upload.entity.SkinRequest;
import ru.csm.bukkit.player.CitizensSkinPlayer;

import java.util.*;

public class SkinsAPI {

    private static final Skin emptySkin = new Skin(
            "eyJ0aW1lc3RhbXAiOjE1NTQyMDQ0MTYzOTQsInByb2ZpbGVJZCI6Ijg2NjdiYTcxYjg1YTQwMDRhZjU0NDU3YTk3MzRlZWQ3IiwicHJvZmlsZU5hbWUiOiJTdGV2ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGMxYzc3Y2U4ZTU0OTI1YWI1ODEyNTQ0NmVjNTNiMGNkZDNkMGNhM2RiMjczZWI5MDhkNTQ4Mjc4N2VmNDAxNiJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTUzY2FjOGI3NzlmZTQxMzgzZTY3NWVlMmI4NjA3MWE3MTY1OGYyMTgwZjU2ZmJjZThhYTMxNWVhNzBlMmVkNiJ9fX0=",
            "KObC7VWtO4ixdgW65ZqPlprCq3/CKYJFMP/aUsOPhU/UjDgQF1uQDftpv7sfyKQmabOWkGFrQM52QKk2+cCP9gC3MaIIuHmVz2S09LIscNn5UepXEgSkugnOqMCqw1vvQevTwFQudpgVhvPhUhib/GERSwZCX+qZR0zoFmnOWdZgeCaf9BFR73Z5Gk9ni9lXI/49gfvzjEmeA1HEUdOvk7zO9RBpUQlXskdooztZCO/parc8KB8y4kyM1Q4+cFm0rCHzZcl+VGe17VxJqQgkLQ7jZkJzyp9HEb/fn1eEV65und+RKIypQnU/2qaxCfNq5vFs7C/c4L9Uw3mcliPNaFFxUGufmkCfTQBlKlTEyQspXh6i1yUCAuYPa/jI3eVcImRzHKWV+oVXhqe9mHRHCLWPxFrKczn3cmRwyVA2wB/CW94fchQ7CmkPuhN5DYsCtlWpJXywta3WhYnoJb5vAkL2AaFUOPYyQbamLQiRXoykftptITDSstC3FliDkj0rC4ybGUz3nRhh3Hpm8XeYeWDN2oJU4dfuxAPXdBNEYpTpzE0z6WtkXqT7PUbkZeg1ICRfffwls9D99EuzQr+j/zkEttNK0sD3/4fgKLccDBzhoMTde2bYD24y7juony42ipjpJVuR7PbPyQ/h5Umi2wthUci5rU5JhGAaFm+1mPI="
    );

    private TreeMap<UUID, SkinPlayer> playersByUUID = new TreeMap<>();
    private TreeMap<String, SkinPlayer> playersByName = new TreeMap<>();
    private TreeMap<String, HashedSkin> namedSkinHash = new TreeMap<>();

    private List<String> blacklist = new ArrayList<>();
    private Map<String, WhiteListElement> nicknamesWhitelist = new TreeMap<>();
    private Map<String, WhiteListElement> urlWhitelist = new TreeMap<>();

    private Configuration conf;
    private Database database;
    private Language lang;

    private List<Skin> defaultSkin = new ArrayList<>();

    private QueueService licenseSkinsQueue;
    private QueueService imageSkinsQueue;

    private Random random = new Random();
    private Timer cleanTimer = new Timer();

    public SkinsAPI(Database database, Configuration conf, Language lang) {
        this.database = database;
        this.conf = conf;
        this.lang = lang;

        parseDefaultSkins();
        parseConstraints();

        boolean enableMojang = conf.get().getNode("skins", "mojang", "enable").getBoolean();
        long licensePeriod = conf.get().getNode("skins", "license", "period").getInt()*1000;

        licenseSkinsQueue = new QueueLicense(this, lang, licensePeriod);

        if(enableMojang){
            long mojangPeriod = conf.get().getNode("skins", "mojang", "period").getInt()*1000;
            imageSkinsQueue = new QueueMojang(this, database, conf, lang, mojangPeriod);
        } else {
            long mineskinPeriod = conf.get().getNode("skins", "mineskin", "period").getInt()*1000;
            imageSkinsQueue = new QueueMineSkin(this, lang, mineskinPeriod);
        }

        licenseSkinsQueue.start();
        imageSkinsQueue.start();

        startCleaner();
    }

    public Configuration getConfiguration(){
        return conf;
    }

    public Language getLang(){
        return lang;
    }

    public List<String> getBlacklist(){
        return blacklist;
    }

    public Map<String, WhiteListElement> getNicknamesWhitelist(){
        return nicknamesWhitelist;
    }

    public Map<String, WhiteListElement> getUrlWhitelist(){
        return urlWhitelist;
    }

    public QueueService getLicenseSkinsQueue(){
        return licenseSkinsQueue;
    }

    public QueueService getImageSkinsQueue(){
        return imageSkinsQueue;
    }

    private void startCleaner(){
        cleanTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                namedSkinHash.values().forEach((skin)->{
                    if(System.currentTimeMillis() > skin.getExpiryTime()){
                        namedSkinHash.remove(skin.getName());
                    }
                });
            }
        }, 0, 1000);
    }

    public void stopCleaner(){
        cleanTimer.cancel();
    }

    private void parseDefaultSkins(){
        List<LinkedHashMap> list = (ArrayList<LinkedHashMap>)conf.get().getNode("skins", "default").getValue();

        if(list == null || list.isEmpty()){
            defaultSkin.add(emptySkin);
            return;
        }

        for(LinkedHashMap map : list){
            String value = map.get("value").toString();
            String signature = map.get("signature").toString();
            defaultSkin.add(new Skin(value, signature));
        }
    }

    private void parseConstraints() {
        boolean enableBlacklist = conf.get().getNode("skins", "blacklist", "enable").getBoolean();
        boolean enableNicknamesWhiteList = conf.get().getNode("skins", "whitelist", "nicknames", "enable").getBoolean();
        boolean enableUrlWhiteList = conf.get().getNode("skins", "whitelist", "url", "enable").getBoolean();

        if(enableNicknamesWhiteList){
            List<String> list = (ArrayList<String>) conf.get().getNode("skins", "whitelist", "nicknames", "list").getValue();

            for(String str : list){
                WhiteListElement elem = null;
                String[] arr = str.split("::");

                if(arr.length == 1){
                    elem = new WhiteListElement(arr[0].toLowerCase());
                }
                if(arr.length == 2){
                    elem = new WhiteListElement(arr[0].toLowerCase(), arr[1]);
                }

                if(elem != null){
                    nicknamesWhitelist.put(elem.getValue(), elem);
                }
            }
        } else {
            if(enableBlacklist){
                blacklist = (ArrayList<String>) conf.get().getNode("skins", "blacklist", "list").getValue();
                blacklist = toLowerCase(blacklist);
            }
        }

        if(enableUrlWhiteList){
            List<String> list = (ArrayList<String>)conf.get().getNode("skins", "whitelist", "url", "list").getValue();

            for(String str : list){
                WhiteListElement elem = null;
                String[] arr = str.split("::");

                if(arr.length == 1){
                    elem = new WhiteListElement(arr[0].toLowerCase());
                }
                if(arr.length == 2){
                    elem = new WhiteListElement(arr[0].toLowerCase(), arr[1]);
                }

                if(elem != null){
                    urlWhitelist.put(elem.getValue(), elem);
                }
            }
        }
    }

    private List<String> toLowerCase(List<String> list){
        for (int i = 0; i < list.size(); i++){
            list.set(i, list.get(i).toLowerCase());
        }
        return list;
    }

    /**
     * Check is premium nickname exist in blacklist
     * @param nickname Required premium nickname
     * @return true if exists and false otherwise
     */
    public boolean hasBlacklist(String nickname){
        return blacklist.contains(nickname.toLowerCase());
    }

    /**
     * Check is premium nickname exist in whitelist
     * @param nickname Required premium nickname
     * @return true if exists and false otherwise
     */
    public boolean hasNicknameWhileList(String nickname){
        return nicknamesWhitelist.containsKey(nickname.toLowerCase());
    }

    /**
     * Check is image url exist in whitelist
     * @param url Required url to image
     * @return true if exists and false otherwise
     */
    public boolean hasUrlWhiteList(String url){
        return urlWhitelist.containsKey(url.toLowerCase());
    }

    /**
     * Get skin saved in hash
     * @param name Name of the premium player
     * @return Skin object if it exist and null otherwise
     */
    public Skin getHashedSkin(String name){
        return namedSkinHash.get(name.toLowerCase());
    }

    /**
     * @return Default skin, defined in config as skins->default
     * */
    public Skin getDefaultSkin(){
        int size = defaultSkin.size();
        int index = 0;

        if(size > 1){
            index = random.nextInt(size-1);
        }

        return defaultSkin.get(index);
    }

    /**
     * Get skinned player by UUID
     * @param uuid - UUID of the player
     * */
    public SkinPlayer getPlayer(UUID uuid){
        return playersByUUID.get(uuid);
    }

    /**
     * Get skinned player by name
     * @param name - Name of the player
     * */
    public SkinPlayer getPlayer(String name){
        return playersByName.get(name.toLowerCase());
    }

    /**
     * Add player to hash. This method used by plugin and not recommended use as API
     * @param player - SkinPlayer object
     * */
    public void addPlayer(SkinPlayer player){
        playersByUUID.put(player.getUUID(), player);
        playersByName.put(player.getName().toLowerCase(), player);
    }

    /**
     * Remove player from hash
     * @param uuid - UUID of the player
     * */
    public void removePlayer(UUID uuid){
        SkinPlayer target = playersByUUID.get(uuid);

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
        SkinPlayer target = playersByName.get(name.toLowerCase());

        if(target != null){
            playersByName.remove(target.getName().toLowerCase());
            playersByUUID.remove(target.getUUID());
        }
    }

    /**
     * Save skin in hash for optimizing future requests
     * @param name - Name of the premium player
     * @param skin - Skin object
     */
    public void hashSkin(String name, HashedSkin skin){
        namedSkinHash.put(name.toLowerCase(), skin);
    }

    /**
     * Set custom skin for player
     * @param player SkinPlayer object
     * @param skin Skin object
     */
    public void setCustomSkin(SkinPlayer player, Skin skin){
        player.setCustomSkin(skin);
        player.applySkin();
        player.refreshSkin();
        savePlayer(player);
        player.sendMessage(lang.of("skin.success"));
    }

    /**
     * Set skin from image url
     * @param player SkinPlayer object
     * @param imageUrl URL to .png image
     * @param model Model of the skin
     */
    public void setSkinFromImage(SkinPlayer player, String imageUrl, SkinModel model) {
        RequestImage request = new RequestImage(player, imageUrl, model);
        imageSkinsQueue.addRequest(request);

        long seconds = imageSkinsQueue.getWaitSeconds();
        player.sendMessage(String.format(lang.of("skin.process"), seconds));
    }

    /**
     * Set skin from premium account
     * @param player SkinPlayer object
     * @param name Name of the target premium account
     */
    public void setSkinFromName(SkinPlayer player, String name) {
        SkinRequest request = new RequestLicense(player, name);
        licenseSkinsQueue.addRequest(request);

        long seconds = licenseSkinsQueue.getWaitSeconds();
        player.sendMessage(String.format(lang.of("skin.process"), seconds));
    }

    /**
     * Reset player skin to default
     * @param player SkinPlayer object
     */
    public void resetSkin(SkinPlayer player) {
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
    public Head getPlayerHead(SkinPlayer player){
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
    public void savePlayer(SkinPlayer player){
        if(player instanceof CitizensSkinPlayer){
            return;
        }

        ThreadWorker.execute(()->{
            Row row = new Row();

            row.addField("name", player.getName());
            row.addField("default_value", player.getDefaultSkin().getValue());
            row.addField("default_signature", player.getDefaultSkin().getSignature());
            row.addField("custom_value", null);
            row.addField("custom_signature", null);

            if(player.hasCustomSkin()){
                row.addField("custom_value", player.getCustomSkin().getValue());
                row.addField("custom_signature", player.getCustomSkin().getSignature());
            }

            database.updateRow(Tables.SKINS, "uuid", player.getUUID().toString(), row);
        });
    }

    public void createPlayer(SkinPlayer player){
        ThreadWorker.execute(()->{
            Row row = new Row();

            row.addField("uuid", player.getUUID());
            row.addField("name", player.getName());
            row.addField("default_value", player.getDefaultSkin().getValue());
            row.addField("default_signature", player.getDefaultSkin().getSignature());

            database.createRow(Tables.SKINS, row);
        });
    }
}