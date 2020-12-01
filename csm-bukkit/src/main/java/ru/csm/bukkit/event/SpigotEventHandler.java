package ru.csm.bukkit.event;

import org.bukkit.Bukkit;
import ru.csm.api.event.EventHandler;
import ru.csm.api.event.EventSkinChange;
import ru.csm.api.event.EventSkinReset;

public class SpigotEventHandler implements EventHandler {

    @Override
    public void fireSkinChange(EventSkinChange event) {
        Bukkit.getServer().getPluginManager().callEvent(new SkinChangeEvent(event));
    }

    @Override
    public void fireSkinReset(EventSkinReset event) {
        Bukkit.getServer().getPluginManager().callEvent(new SkinResetEvent(event));
    }
}
