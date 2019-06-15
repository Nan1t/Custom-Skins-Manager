package ru.csm.bukkit.player;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.bukkit.Skins;
import ru.csm.bukkit.events.SkinChangedEvent;
import ru.csm.bukkit.events.SkinResetEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BukkitSkinPlayer implements SkinPlayer<Player> {

    private Player player;
    private WrappedGameProfile profile;

    private Skin defaultSkin;
    private Skin customSkin;

    public BukkitSkinPlayer(Player player){
        this.player = player;
        this.profile = WrappedGameProfile.fromPlayer(player);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public UUID getUUID() {
        return player.getUniqueId();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public Skin getDefaultSkin() {
        return defaultSkin;
    }

    @Override
    public Skin getCustomSkin() {
        return customSkin;
    }

    @Override
    public void setPlayer(Player player){
        this.player = player;
    }

    @Override
    public void setDefaultSkin(Skin skin) {
        this.defaultSkin = skin;
    }

    @Override
    public void setCustomSkin(Skin skin) {
        this.customSkin = skin;
        Bukkit.getPluginManager().callEvent(new SkinChangedEvent(this, skin));
    }

    @Override
    public void applySkin() {
        Skin currentSkin = defaultSkin;

        if(hasCustomSkin()){
            currentSkin = customSkin;
        }

        WrappedSignedProperty property = new WrappedSignedProperty("textures",
                currentSkin.getValue(),
                currentSkin.getSignature());
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", property);
    }

    @Override
    public void refreshSkin() {
        try{
            WrapperPlayServerPlayerInfo removeInfo = new WrapperPlayServerPlayerInfo();
            removeInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            removeInfo.setData(getInfoData());

            WrapperPlayServerPlayerInfo addInfo = new WrapperPlayServerPlayerInfo();
            addInfo.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            addInfo.setData(getInfoData());

            WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
            destroy.setEntityIds(new int[]{this.player.getEntityId()});

            WrapperPlayServerNamedEntitySpawn spawn = new WrapperPlayServerNamedEntitySpawn();
            spawn.setPlayerUUID(this.player.getUniqueId());
            spawn.setEntityID(player.getEntityId());

            PacketContainer respawnPacket = new PacketContainer(PacketType.Play.Server.RESPAWN);
            respawnPacket.getGameModes().write(0, EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()));
            respawnPacket.getWorldTypeModifier().write(0, player.getWorld().getWorldType());

            switch (player.getWorld().getEnvironment()){
                case NETHER:
                    respawnPacket.getDimensions().write(0, -1);
                    break;
                case NORMAL:
                    respawnPacket.getDimensions().write(0, 0);
                    break;
                case THE_END:
                    respawnPacket.getDimensions().write(0, 1);
                    break;
            }

            // For version < 1.14
            if(Skins.getSubVersion() < 14){
                respawnPacket.getDifficulties().write(0, EnumWrappers.Difficulty.PEACEFUL);
            }

            // For version > 1.8
            if(Skins.getSubVersion() > 8){
                spawn.setMetadata(WrappedDataWatcher.getEntityWatcher(player));
                spawn.setPosition(player.getLocation().toVector());
                spawn.setYaw(player.getLocation().getYaw());
                spawn.setPitch(player.getLocation().getPitch());
            }

            WrapperPlayServerPosition position = new WrapperPlayServerPosition();
            position.setX(player.getLocation().getX());
            position.setY(player.getLocation().getY());
            position.setZ(player.getLocation().getZ());
            position.setYaw(player.getLocation().getYaw());
            position.setPitch(player.getLocation().getPitch());

            WrapperPlayServerHeldItemSlot slot = new WrapperPlayServerHeldItemSlot();
            slot.setSlot(player.getInventory().getHeldItemSlot());

            for(Player player : Bukkit.getOnlinePlayers()){
                if(player.getName().equals(this.player.getName())){
                    removeInfo.sendPacket(player);
                    addInfo.sendPacket(player);
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, respawnPacket);
                    position.sendPacket(player);
                    slot.sendPacket(player);
                    continue;
                }

                if(player.getWorld().equals(this.player.getWorld()) && player.canSee(this.player)){
                    destroy.sendPacket(player);
                    removeInfo.sendPacket(player);
                    addInfo.sendPacket(player);
                    spawn.sendPacket(player);
                    continue;
                }

                removeInfo.sendPacket(player);
                addInfo.sendPacket(player);
            }

            player.updateInventory();
        } catch (Exception e){
            System.out.println("Error while skin refreshing: " + e.getMessage());
        }
    }

    private List<PlayerInfoData> getInfoData(){
        List<PlayerInfoData> dataList = new ArrayList<>();
        dataList.add(new PlayerInfoData(profile, 0,
                EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()),
                WrappedChatComponent.fromText(player.getDisplayName())));

        return dataList;
    }

    @Override
    public void resetSkin() {
        this.customSkin = null;
        Bukkit.getPluginManager().callEvent(new SkinResetEvent(this));
    }

    @Override
    public void sendMessage(String... message){
        player.sendMessage(message);
    }

    @Override
    public boolean isOnline(){
        return this.player.isOnline();
    }

    @Override
    public boolean hasCustomSkin() {
        if(customSkin != null){
            return customSkin.getValue() != null && customSkin.getSignature() != null;
        }

        return false;
    }

    @Override
    public boolean hasDefaultSkin() {
        if(defaultSkin != null){
            return defaultSkin.getValue() != null && defaultSkin.getSignature() != null;
        }

        return false;
    }
}
