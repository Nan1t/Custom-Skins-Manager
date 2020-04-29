package ru.csm.api.network;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface MessageHandler {

    void execute(JsonObject json);

}
