package ru.csm.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.csm.bukkit.npc.ClickAction;
import ru.csm.bukkit.npc.NPC;

public class NpcClickEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final NPC npc;
    private final ClickAction action;

    public NpcClickEvent(Player player, NPC npc, ClickAction action){
        this.player = player;
        this.npc = npc;
        this.action = action;
    }

    public Player getPlayer() {
        return player;
    }

    public NPC getNpc() {
        return npc;
    }

    public ClickAction getAction() {
        return action;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
