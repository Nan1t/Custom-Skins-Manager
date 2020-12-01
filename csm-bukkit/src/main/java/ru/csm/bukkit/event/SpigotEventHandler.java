package ru.csm.bukkit.event;

import org.bukkit.Bukkit;
import ru.csm.api.event.EventHandler;
import ru.csm.api.event.EventSkinChange;
import ru.csm.api.event.EventSkinChanged;
import ru.csm.api.event.EventSkinReset;

import java.util.function.Consumer;

public class SpigotEventHandler implements EventHandler {

    @Override
    public void fireSkinChange(EventSkinChange event, Consumer<EventSkinChange> callback) {
        Bukkit.getServer().getPluginManager().callEvent(new SkinChangeEvent(event));
        callback.accept(event);
    }

    @Override
    public void fireSkinChanged(EventSkinChanged event) {
        Bukkit.getServer().getPluginManager().callEvent(new SkinChangedEvent(event));
    }

    @Override
    public void fireSkinReset(EventSkinReset event) {
        Bukkit.getServer().getPluginManager().callEvent(new SkinResetEvent(event));
    }
}
