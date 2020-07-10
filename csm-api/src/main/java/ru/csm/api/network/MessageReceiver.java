/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.csm.api.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.csm.api.utils.NumUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MessageReceiver {

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

            System.arraycopy(data, PART_HEADER_SIZE, clearData, 0, clearData.length);

            if (partsCount == 1){
                JsonObject result = JSON_PARSER.parse(new String(clearData)).getAsJsonObject();
                handler.execute(result);
                return;
            }

            Packet packet = packets.computeIfAbsent(packetId, (id)->new Packet(id, partsCount));

            packet.addPart(partId, new String(clearData));

            if (packet.isAllReceived()){
                packets.remove(packetId);
                JsonObject result = JSON_PARSER.parse(packet.buildParts()).getAsJsonObject();
                handler.execute(result);
            }
        }
    }

}
