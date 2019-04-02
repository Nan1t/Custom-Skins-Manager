package ru.csm.bungee.network;

import com.google.gson.JsonObject;

public interface MessageExecutor {

    JsonMessage execute(JsonObject json);

}
