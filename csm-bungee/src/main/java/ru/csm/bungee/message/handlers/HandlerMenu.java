package ru.csm.bungee.message.handlers;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.network.MessageHandler;
import ru.csm.api.services.SkinsAPI;

public class HandlerMenu implements MessageHandler {

    private final SkinsAPI<ProxiedPlayer> api;

    public HandlerMenu(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
    }

    @Override
    public void execute(JsonObject json) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(json.get("player").getAsString());
        int page = json.get("page").getAsInt();
        api.openSkinsMenu(player, page);
    }

}
