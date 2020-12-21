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
import ru.csm.api.utils.NumUtil;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MessageSender<Player> {

    private static final int PACKET_SIZE = 32767;
    private static final int MAX_PACKET_SIZE = PACKET_SIZE * Short.MAX_VALUE;
    private static final int PART_HEADER_SIZE = 8;
    private static final int PART_DATA_SIZE = PACKET_SIZE - PART_HEADER_SIZE;

    private static final AtomicInteger PACKET_ID = new AtomicInteger(Integer.MIN_VALUE);

    public void sendMessage(Player player, String channel, JsonObject json){
        byte[] data = json.toString().getBytes(StandardCharsets.UTF_8);

        if (data.length > MAX_PACKET_SIZE){
            throw new IllegalArgumentException(String.format("Data too long. Max - %s bytes", MAX_PACKET_SIZE));
        }

        short parts = (short) Math.ceil((float)data.length / PART_DATA_SIZE);

        byte[] partsArr = NumUtil.toByteArray(parts);
        byte[] messageId = NumUtil.toByteArray(PACKET_ID.incrementAndGet());
        byte[] part;
        byte[] partId;

        int size;

        for (short i = 0; i < parts; i++){
            partId = NumUtil.toByteArray(i);
            size = (i != parts-1) ? PART_DATA_SIZE + PART_HEADER_SIZE : (data.length % PART_DATA_SIZE) + PART_HEADER_SIZE;
            part = new byte[size];

            part[0] = messageId[0];
            part[1] = messageId[1];
            part[2] = messageId[2];
            part[3] = messageId[3];

            part[4] = partsArr[0];
            part[5] = partsArr[1];

            part[6] = partId[0];
            part[7] = partId[1];

            System.arraycopy(data, i * PART_DATA_SIZE, part, PART_HEADER_SIZE, part.length - PART_HEADER_SIZE);

            send(player, channel, part);
        }
    }

    public abstract void send(Player player, String channel, byte[] data);
}
