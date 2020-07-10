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

package ru.csm.api.upload;

import com.google.common.reflect.TypeToken;
import ninja.leaping.modded.configurate.ConfigurationNode;
import ninja.leaping.modded.configurate.objectmapping.serialize.TypeSerializer;
import ru.csm.api.utils.UuidUtil;

import java.util.UUID;

public class Profile {

    private final String username;
    private final String password;
    private String accessToken;
    private String clientToken;
    private UUID uuid;

    public Profile(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getAccessToken(){
        return accessToken;
    }

    public String getClientToken(){
        return clientToken;
    }

    public UUID getUUID(){
        return uuid;
    }

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    public void setClientToken(String clientToken){
        this.clientToken = clientToken;
    }

    public void setUUID(String uuid){
        this.uuid = UuidUtil.getUUID(uuid);
    }

    public void setUUID(UUID uuid){
        this.uuid = uuid;
    }

    @Override
    public String toString(){
        return username;
    }

    public static class Serializer implements TypeSerializer<Profile> {

        @Override
        public Profile deserialize(TypeToken<?> type, ConfigurationNode node) {
            String login = node.getNode("login").getString();
            String password = node.getNode("password").getString();
            return new Profile(login, password);
        }

        @Override
        public void serialize(TypeToken<?> type, Profile profile, ConfigurationNode node) {

        }
    }
}
