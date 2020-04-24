package ru.csm.bukkit.protocol.hologram;

import ru.csm.bukkit.wrappers.OldWrapperPlayServerSpawnEntityLiving;
import ru.csm.bukkit.wrappers.WrapperPlayServerEntityDestroy;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Random;

public class Holo_1_8 implements Hologram {

    private final int id;
    private final Location location;
    private String text;

    private final OldWrapperPlayServerSpawnEntityLiving entity;
    private final WrappedDataWatcher watcher;

    public int getEntityID(){
        return id;
    }

    public Location getLocation(){
        return location;
    }

    public String getText() {
        return text;
    }

    public Holo_1_8(Location location){
        this.location = location;
        this.id = new Random().nextInt(Integer.MAX_VALUE);

        entity = new OldWrapperPlayServerSpawnEntityLiving();
        entity.setEntityID(id);
        entity.setType(EntityType.ARMOR_STAND);

        entity.setX(location.getX());
        entity.setY(location.getY()-2.2);
        entity.setZ(location.getZ());
        entity.setYaw(0);
        entity.setHeadPitch(0);

        watcher = new WrappedDataWatcher();

        watcher.setObject(0, (byte) 0x20);
        watcher.setObject(3, (byte) 1);
        watcher.setObject(10, (byte) 0x02);
    }

    public void setText(String text){
        this.text = text;

        watcher.setObject(2, text);
    }

    public void show(Player player){
        entity.setMetadata(watcher);
        entity.sendPacket(player);
    }

    public void hide(Player player){
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[]{id});
        destroy.sendPacket(player);
    }

    public void destroy(){
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[]{id});

        for(Player player : Bukkit.getOnlinePlayers()){
            destroy.sendPacket(player);
        }
    }
}
