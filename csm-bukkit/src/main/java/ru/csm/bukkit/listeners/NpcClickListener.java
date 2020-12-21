/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.csm.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.event.NpcClickEvent;
import ru.csm.bukkit.npc.ClickAction;
import ru.csm.bukkit.npc.NPC;
import ru.csm.bukkit.npc.inject.HandlerInjector;
import ru.csm.bukkit.services.MenuManager;
import ru.csm.bukkit.services.NpcManager;

public class NpcClickListener implements Listener {

    private final SkinsAPI<Player> skinsAPI;
    private final MenuManager menuManager;

    public NpcClickListener(SkinsAPI<Player> skinsAPI, MenuManager menuManager){
        this.skinsAPI = skinsAPI;
        this.menuManager = menuManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        HandlerInjector.inject(event.getPlayer());
    }

    @EventHandler
    public void onNpcClick(NpcClickEvent event){
        NPC npc = event.getNpc();

        if (event.getAction().equals(ClickAction.ATTACK)){
            NpcManager.removeNpc(event.getPlayer());
            if (npc.isOpenMenu()){
                menuManager.getOpenedMenu(event.getPlayer()).ifPresent((menu)->{
                    menu.open(event.getPlayer());
                });
                return;
            }
        }

        if (event.getAction().equals(ClickAction.INTERACT)){
            if (npc.getPermission() != null && !event.getPlayer().hasPermission(npc.getPermission())) return;
            skinsAPI.setCustomSkin(event.getPlayer(), npc.getSkin());
            npc.destroy(event.getPlayer());
        }
    }

}
