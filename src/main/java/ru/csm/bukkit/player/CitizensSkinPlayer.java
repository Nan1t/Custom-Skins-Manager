package ru.csm.bukkit.player;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

import java.util.UUID;

public class CitizensSkinPlayer implements SkinPlayer<Player> {

    private Plugin plugin;
    private NPC npc;
    private SkinnableEntity skinnableEntity;
    private Player sender;

    private Skin skin;

    public CitizensSkinPlayer(Plugin plugin, Player sender, NPC npc){
        this.plugin = plugin;
        this.sender = sender;
        this.npc = npc;
        this.skinnableEntity = (SkinnableEntity)npc.getEntity();
    }

    public Player getCitizensNPC(){
        return skinnableEntity.getBukkitEntity();
    }

    @Override
    public Player getPlayer() {
        return sender;
    }

    @Override
    public UUID getUUID() {
        return npc.getUniqueId();
    }

    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public Skin getDefaultSkin() {
        return null;
    }

    @Override
    public Skin getCustomSkin() {
        return null;
    }

    @Override
    public void setPlayer(Player player) {
        this.sender = player;
    }

    @Override
    public void setDefaultSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    public void setCustomSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    public void applySkin() {
        new BukkitRunnable(){
            public void run(){
                skinnableEntity.setSkinPersistent(skin.getOwnerUUID(), skin.getSignature(), skin.getValue());
            }
        }.runTask(plugin);
    }

    @Override
    public void refreshSkin() {
        new BukkitRunnable(){
            public void run(){
                Location loc = npc.getStoredLocation();
                npc.despawn();
                npc.spawn(loc);
            }
        }.runTask(plugin);
    }

    @Override
    public void resetSkin() {

    }

    @Override
    public void sendMessage(String... message) {
        if(sender != null){
            sender.sendMessage(message);
        }
    }

    @Override
    public boolean isOnline() {
        return npc.isSpawned();
    }

    @Override
    public boolean hasCustomSkin() {
        return true;
    }

    @Override
    public boolean hasDefaultSkin() {
        return true;
    }
}
