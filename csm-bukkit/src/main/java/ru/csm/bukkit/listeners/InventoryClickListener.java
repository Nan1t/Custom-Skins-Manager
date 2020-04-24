package ru.csm.bukkit.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.api.utils.Colors;
import ru.csm.api.player.Head;
import ru.csm.bukkit.gui.SkinMenu;
import ru.csm.bukkit.gui.managers.MenuManager;
import ru.csm.bukkit.player.BukkitSkinPlayer;
import ru.csm.bukkit.protocol.npc.NPC;
import ru.csm.bukkit.protocol.NPCService;

import java.util.List;
import java.util.UUID;

public class InventoryClickListener implements Listener {

    private NPCService npcService;
    private MenuManager menuManager;
    private SkinsAPI api;
    private Language lang;

    public InventoryClickListener(MenuManager menuManager, NPCService npcService, SkinsAPI api){
        this.menuManager = menuManager;
        this.npcService = npcService;
        this.api = api;
        this.lang = api.getLang();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getInventory().getHolder() != null && (e.getInventory().getHolder() instanceof SkinMenu)){
            e.setCancelled(true);

            Player player = (Player) e.getWhoClicked();
            SkinPlayer skinPlayer = api.getPlayer(player.getUniqueId());
            SkinMenu menu = (SkinMenu) e.getInventory().getHolder();
            ItemStack clicked = e.getCurrentItem();

            if(skinPlayer == null){
                skinPlayer = new BukkitSkinPlayer(player);
            }

            if(clicked != null && !clicked.getType().equals(Material.AIR) && clicked.hasItemMeta()){
                NBTItem nbt = new NBTItem(clicked);
                String action = nbt.getString("InventoryAction");

                if(nbt.hasKey("SkinPermission")){
                    String perm = nbt.getString("SkinPermission");
                    if(!player.hasPermission(perm)){
                        player.sendMessage(lang.of("permission.deny"));
                        return;
                    }
                }

                if(action != null){
                    if(action.equals("back")){
                        int currentPage = menu.getPage();
                        if(currentPage > 1){
                            menuManager.openMenu(player, currentPage-1);
                        }
                        return;
                    }
                    if(action.equals("next")){
                        int currentPage = menu.getPage();
                        if(currentPage < menu.getPagesCount()){
                            menuManager.openMenu(player, currentPage+1);
                        }
                        return;
                    }
                    if(action.equals("reset")){
                        api.resetSkin(skinPlayer);
                        player.closeInventory();
                        return;
                    }
                    if(action.equals("spawnNPC")){
                        NPC prevNPC = npcService.getNPC(player.getUniqueId());
                        if(prevNPC != null){
                            prevNPC.destroy();
                        }

                        UUID ownerUUID = UUID.fromString(nbt.getString("SkinOwnerUUID"));
                        Head head = menu.getHead(ownerUUID);
                        NPC npc = npcService.createNPC(UUID.randomUUID(), "");
                        npc.setSkin(head.getSkin());

                        Location loc = player.getLocation().clone();
                        Vector modify = player.getLocation().getDirection().normalize().multiply(2);
                        loc.add(modify);
                        loc.setY(player.getLocation().getY());
                        loc.setPitch(0);
                        loc.setYaw(player.getLocation().getYaw()+180);

                        npc.setLocation(loc);

                        List<String> customName = Colors.ofArr(lang.ofArray("npc.name"));
                        npc.setCustomName(customName);

                        player.closeInventory();
                        npc.spawn(player);

                        npcService.addNPC(player.getUniqueId(), npc);
                    }
                }
            }
        }
    }

}
