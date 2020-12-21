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

import java.util.concurrent.atomic.AtomicInteger;

public class Packet {

    private final int id;
    private final String[] parts;
    private final AtomicInteger received;

    public Packet(int id, int parts){
        this.id = id;
        this.parts = new String[parts];
        this.received = new AtomicInteger(0);
    }

    public int getId() {
        return id;
    }

    public boolean isAllReceived(){
        return received.get() == parts.length;
    }

    public void addPart(int part, String data) {
        this.parts[part] = data;
        this.received.incrementAndGet();
    }

    public String buildParts(){
        StringBuilder builder = new StringBuilder();
        for (String part : parts) builder.append(part);
        return builder.toString();
    }
}
