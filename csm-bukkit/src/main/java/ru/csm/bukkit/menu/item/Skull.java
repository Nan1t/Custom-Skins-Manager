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

package ru.csm.bukkit.menu.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import ru.csm.bukkit.util.Materials;

import java.lang.reflect.Field;
import java.util.UUID;

public final class Skull {

    private Skull(){ }

	public static ItemStack getCustomSkull(String url) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        if (profile.getProperties() == null) {
            throw new IllegalStateException("Profile doesn't contain a property map");
        }

        PropertyMap propertyMap = profile.getProperties();
        ItemStack head = new ItemStack(Materials.HEAD, 1, (short) 3);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        byte[] encodedData = Base64Coder.encodeString(String.format("{textures:{SKIN:{url:\"%s\"}}}", url)).getBytes();

        propertyMap.put("textures", new Property("textures", new String(encodedData)));

        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchMethodError | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e1) {
            e1.printStackTrace();
        }

        head.setItemMeta(headMeta);

        return head;
    }

}
