package ru.csm.bukkit.protocol.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.gui.SkinMenu;
import ru.csm.bukkit.gui.managers.MenuManager;
import ru.csm.bukkit.player.BukkitSkinPlayer;
import ru.csm.bukkit.protocol.npc.NPC;
import ru.csm.bukkit.protocol.NPCService;

public class NpcClickListener extends PacketAdapter {

    private NPCService service;
    private MenuManager menuManager;
    private SkinsAPI api;

    public NpcClickListener(Plugin plugin, PacketType type, NPCService npcService, MenuManager menuManager, SkinsAPI api) {
        super(plugin, type);
        this.service = npcService;
        this.menuManager = menuManager;
        this.api = api;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        SkinPlayer skinPlayer = api.getPlayer(player.getUniqueId());

        if(skinPlayer == null){
            skinPlayer = new BukkitSkinPlayer(player);
        }

        NPC clicked = service.getNPC(packet.getIntegers().read(0));

        if(clicked == null){
            return;
        }

        if(packet.getEntityUseActions().getValues().get(0).equals(EnumWrappers.EntityUseAction.ATTACK)) {
            // Open menu again
            clicked.destroy();

            SkinMenu menu = menuManager.getPlayerCurrentMenu(player.getUniqueId());

            if(menu != null){
                menuManager.openMenu(player, menu);
                return;
            }

            menuManager.openMenu(player, 1);
            return;
        }

        if(packet.getHands().getValues().size() > 0){
            if(packet.getHands().getValues().get(0).equals(EnumWrappers.Hand.OFF_HAND)) {
                event.setCancelled(true);
                return;
            }
        }

        if(packet.getEntityUseActions().getValues().get(0).equals(EnumWrappers.EntityUseAction.INTERACT)) {
            // Apply skin
            api.setCustomSkin(skinPlayer, clicked.getSkin());
            clicked.destroy();
            return;
        }

    }
}
