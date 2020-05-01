package ru.csm.bungee.message;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import ru.csm.api.network.MessageReceiver;

public class PluginMessageReceiver extends MessageReceiver implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event){
        receive(event.getTag(), event.getData());
    }

}
