package ru.csm.velocity.message.handlers;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import ru.csm.api.network.MessageHandler;
import ru.csm.api.services.SkinsAPI;

public class HandlerMenu implements MessageHandler {

    private final SkinsAPI<Player> api;
    private final ProxyServer server;

    public HandlerMenu(SkinsAPI<Player> api, ProxyServer server){
        this.api = api;
        this.server = server;
    }

    @Override
    public void execute(JsonObject json) {
        server.getPlayer(json.get("player").getAsString()).ifPresent((player)->{
            int page = json.get("page").getAsInt();
            api.openSkinsMenu(player, page);
        });
    }

}
