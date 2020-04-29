package ru.csm.api.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.csm.api.utils.NumUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class MessageReceiver {

    private static final int PART_HEADER_SIZE = 8;
    private static final JsonParser JSON_PARSER = new JsonParser();

    private final Map<String, MessageHandler> handlers = new ConcurrentHashMap<>();
    private final Map<Integer, Packet> packets = Collections.synchronizedMap(new HashMap<>());

    public void registerHandler(String channel , MessageHandler handler){
        handlers.put(channel, handler);
    }

    public void receive(String channel, byte[] data){
        MessageHandler handler = handlers.get(channel);

        if (handler != null){
            int packetId = NumUtil.intFromBytes(data[0], data[1], data[2], data[3]);
            short partsCount = NumUtil.shortFromBytes(data[4], data[5]);
            short partId = NumUtil.shortFromBytes(data[6], data[7]);
            byte[] clearData = new byte[data.length - PART_HEADER_SIZE];
            Packet packet = packets.computeIfAbsent(packetId, (id)->new Packet(id, partsCount));

            System.arraycopy(data, PART_HEADER_SIZE, clearData, 0, clearData.length);

            packet.addPart(partId, new String(clearData));

            if (packet.isAllReceived()){
                JsonObject result = JSON_PARSER.parse(packet.buildParts()).getAsJsonObject();
                handler.execute(result);
            }
        }
    }

}
