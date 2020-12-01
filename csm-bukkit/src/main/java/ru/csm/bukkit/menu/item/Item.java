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

import napi.configurate.data.ConfigNode;
import napi.configurate.serializing.NodeSerializer;
import napi.configurate.serializing.NodeSerializingException;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Item {

    private ItemStack itemStack;
    private String texture;
    private List<ClickAction> actions;

    public Material getMaterial(){
        return itemStack.getType();
    }

    public String getName(){
        return itemStack.getItemMeta().getDisplayName();
    }

    public String getTexture(){
        return texture;
    }

    public List<String> getLore(){
        return itemStack.getItemMeta().getLore();
    }

    public void doClick(Player player){
        if (actions != null){
            for (ClickAction action : actions){
                action.onClick(player, this);
            }
        }
    }

    public ItemStack toItemStack(){
        return itemStack;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {

        private Material material;
        private byte data = (byte) 0;
        private int amount = 1;
        private String displayName;
        private String texture;
        private List<String> lore;

        private List<ClickAction> actions;

        public Builder material(Material material){
            this.material = material;
            return this;
        }

        public Builder texture(String texture){
            this.texture = texture;
            return this;
        }

        public Builder data(byte data){
            this.data = data;
            return this;
        }

        public Builder amount(int amount){
            this.amount = amount;
            return this;
        }

        public Builder displayName(String displayName){
            this.displayName = displayName;
            return this;
        }

        public Builder lore(List<String> lore){
            this.lore = lore;
            return this;
        }

        public Builder action(ClickAction action){
            if (actions == null) actions = new ArrayList<>();
            actions.add(action);
            return this;
        }

        public Item build(){
            ItemStack itemStack;

            if (texture != null){
                itemStack = Skull.getCustomSkull(texture);
            } else {
                itemStack = new ItemStack(material, data);
            }

            amount = Math.max(amount, 1);
            amount = Math.min(amount, 64);

            itemStack.setAmount(amount);

            ItemMeta meta = itemStack.getItemMeta();

            if (meta != null){
                meta.setDisplayName(displayName);
                meta.setLore(lore);
            }

            itemStack.setItemMeta(meta);

            Item item = new Item();

            item.itemStack = itemStack;
            item.actions = actions;
            item.texture = texture;

            return item;
        }

    }

    public static class Serializer implements NodeSerializer<Item> {

        @Override
        public Item deserialize(ConfigNode node) throws NodeSerializingException {
            Material material = Material.getMaterial(node.getNode("material").getString());
            String name = node.getNode("name").getString();
            List<String> lore = node.getNode("lore").getList(String.class);
            String texture = node.getNode("texture").getString();

            return Item.builder()
                    .material(material)
                    .displayName(name)
                    .lore(lore)
                    .texture(texture)
                    .build();
        }

        @Override
        public void serialize(Item obj, ConfigNode value) {

        }
    }

}
