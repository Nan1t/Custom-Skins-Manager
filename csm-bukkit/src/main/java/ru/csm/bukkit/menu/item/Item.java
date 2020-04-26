package ru.csm.bukkit.menu.item;

import com.google.common.reflect.TypeToken;
import ninja.leaping.modded.configurate.ConfigurationNode;
import ninja.leaping.modded.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.modded.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Item {

    public static final TypeToken<Item> TOKEN = TypeToken.of(Item.class);

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

    public static class Serializer implements TypeSerializer<Item> {

        @Override
        public Item deserialize(TypeToken<?> type, ConfigurationNode node) throws ObjectMappingException {
            Material material = Material.getMaterial(node.getNode("material").getString());
            String name = node.getNode("name").getString();
            List<String> lore = node.getNode("lore").getList(TypeToken.of(String.class));
            String texture = node.getNode("texture").getString();

            return Item.builder()
                    .material(material)
                    .displayName(name)
                    .lore(lore)
                    .texture(texture)
                    .build();
        }

        @Override
        public void serialize(TypeToken<?> type, Item obj, ConfigurationNode value) throws ObjectMappingException {

        }
    }

}
