package ru.csm.bungee.network;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class JsonMessage {

    private String channel;
    private ProxiedPlayer receiver;
    private JsonObject json;

    public JsonMessage(String channel, ProxiedPlayer receiver, JsonObject json){
        this.channel = channel;
        this.receiver = receiver;
        this.json = json;
    }

    public String getChannel(){
        return channel;
    }

    public ProxiedPlayer getReceiver() {
        return receiver;
    }

    public JsonObject getJson() {
        return json;
    }
}
