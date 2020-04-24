package ru.csm.bungee.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class PluginMessageService implements Listener {

    private Map<String, MessageExecutor> executors = new HashMap<>();

    public void registerExecutor(String channel, MessageExecutor executor){
        BungeeCord.getInstance().registerChannel(channel);
        executors.put(channel, executor);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e){
        try{
            String channel = e.getTag();
            if(!executors.containsKey(channel)){
                return;
            }

            String str = IOUtils.toString(e.getData(), "UTF-8");
            JsonObject json = new JsonParser().parse(str).getAsJsonObject();

            MessageExecutor executor = executors.get(channel);

            if(executor != null){
                JsonMessage response = executor.execute(json);
                if(response != null){
                    sendMessage(response);
                }
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public static void sendMessage(JsonMessage message){
        message.getReceiver().getServer().sendData(message.getChannel(), message.getJson().toString().getBytes());
    }

}
