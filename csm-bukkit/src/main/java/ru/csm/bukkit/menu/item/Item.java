package ru.csm.bukkit.menu.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Item {

    private ItemStack itemStack;
    private List<ClickAction> actions;

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
        private List<String> lore;

        private List<ClickAction> actions;

        public Builder material(Material material){
            this.material = material;
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
            ItemStack itemStack = new ItemStack(material, data);
            ItemMeta meta = itemStack.getItemMeta();

            itemStack.setAmount(amount);

            if (meta != null){
                meta.setDisplayName(displayName);
                meta.setLore(lore);
            }

            Item item = new Item();

            item.itemStack = itemStack;
            item.actions = actions;

            return item;
        }

    }

}
