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
