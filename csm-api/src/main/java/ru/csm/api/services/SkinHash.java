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

package ru.csm.api.services;

import ru.csm.api.player.Skin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class SkinHash {

    private static final Map<String, Hash> HASH = new ConcurrentHashMap<>();

    private SkinHash(){}

    public static Optional<Skin> get(String key){
        return Optional.ofNullable(HASH.get(key.toLowerCase()));
    }

    public static void add(String key, Skin skin){
        HASH.put(key.toLowerCase(), new Hash(skin));
    }

    public static void clean(){
        HASH.entrySet().removeIf((entry)->System.currentTimeMillis() >= entry.getValue().getExpiryTime());
    }

    private static class Hash extends Skin {

        private final long expiryTime;

        public Hash(Skin skin){
            super(skin);
            this.expiryTime = System.currentTimeMillis() + 60000;
        }

        public long getExpiryTime() {
            return expiryTime;
        }
    }
}
