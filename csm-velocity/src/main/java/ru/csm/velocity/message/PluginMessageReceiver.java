package ru.csm.velocity.message;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import ru.csm.api.network.MessageReceiver;

public class PluginMessageReceiver extends MessageReceiver {

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event){
        receive(event.getIdentifier().getId(), event.getData());
    }

}
