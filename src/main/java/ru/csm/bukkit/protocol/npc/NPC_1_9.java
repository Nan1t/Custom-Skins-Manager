package ru.csm.bukkit.protocol.npc;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityHeadRotation;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;
import ru.csm.bukkit.protocol.hologram.Holo_1_9;
import ru.csm.bukkit.protocol.hologram.Hologram;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NPC_1_9 implements NPC {

    private WrapperPlayServerNamedEntitySpawn wrapper;
    private WrapperPlayServerPlayerInfo info;
    private WrappedGameProfile profile;
    private WrapperPlayServerEntityHeadRotation rotation;

    private String name;
    private UUID uuid;
    private Location location;
    private Skin skin = new Skin();
    private List<Hologram> customName = new ArrayList<>();
    private Player viewer;

    private int entityId;

    @Override
    public Location getLocation(){
        return location;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public UUID getUUID(){
        return uuid;
    }

    @Override
    public Skin getSkin(){
        return skin;
    }

    @Override
    public int getEntityId(){
        return entityId;
    }

    public NPC_1_9(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;

        entityId = new Random().nextInt(9999999);
        profile = new WrappedGameProfile(uuid, name);

        info = new WrapperPlayServerPlayerInfo();
        info.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        info.setData(getInfoData());

        wrapper = new WrapperPlayServerNamedEntitySpawn();
        wrapper.setEntityID(entityId);
        wrapper.setPlayerUUID(uuid);

        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(12, byteSerializer), (byte) 0xFF);

        wrapper.setMetadata(watcher);
    }

    @Override
    public void setSkin(Skin skin){
        this.skin = skin;
    }

    @Override
    public void setLocation(Location location){
        this.location = location;

        wrapper.setX(location.getX());
        wrapper.setY(location.getY());
        wrapper.setZ(location.getZ());
        wrapper.setYaw(location.getYaw());
        wrapper.setPitch(location.getPitch());
        wrapper.setPosition(location.toVector());

        rotation = new WrapperPlayServerEntityHeadRotation();
        rotation.setEntityID(entityId);
        rotation.setHeadYaw(getCompressedAngle(location.getYaw()));
    }

    @Override
    public void setCustomName(List<String> lines){
        int count = lines.size();
        double holoHeight = 0.25d;

        for(int i = count; i > 0; i--){
            double height = 1.75d + holoHeight*i;
            Hologram hologram = new Holo_1_9(getLocation().clone().add(0, height,0));
            hologram.setText(lines.get(i-1));
            customName.add(hologram);
        }
    }

    @Override
    public void spawn(Player player){
        this.viewer = player;

        WrappedSignedProperty property = new WrappedSignedProperty("textures", skin.getValue(), skin.getSignature());
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", property);

        info.sendPacket(player);
        wrapper.sendPacket(player);
        rotation.sendPacket(player);

        for(Hologram holo : customName){
            holo.show(player);
        }
    }

    @Override
    public void destroy(){
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[]{entityId});

        WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
        info.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        info.setData(getInfoData());

        destroy.sendPacket(viewer);
        info.sendPacket(viewer);

        for(Hologram holo : customName){
            holo.hide(viewer);
        }
    }

    private List<PlayerInfoData> getInfoData(){
        PlayerInfoData data = new PlayerInfoData(profile, 1, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(""));
        List<PlayerInfoData> dataList = new ArrayList<>();
        dataList.add(data);

        return dataList;
    }

    private byte getCompressedAngle(float value) {
        return (byte) (value * 256.0F / 360.0F);
    }
}
