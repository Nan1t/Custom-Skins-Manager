package ru.csm.bukkit.serializers;

import com.google.common.reflect.TypeToken;
import ninja.leaping.modded.configurate.ConfigurationNode;
import ninja.leaping.modded.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.csm.api.utils.text.Colors;

public class ItemStackSerializer implements TypeSerializer<ItemStack> {

    @Override
    public ItemStack deserialize(TypeToken<?> typeToken, ConfigurationNode node) {
        try{
            Material mat = Material.getMaterial(node.getNode("item").getString());
            short data = (short) node.getNode("data").getInt();
            String displayName = Colors.of(node.getNode("text").getString());

            ItemStack itemStack = new ItemStack(mat, 1, data);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(displayName);
            itemStack.setItemMeta(meta);
            return itemStack;
        } catch (Exception e){
            System.out.println("[CustomSkinsManager] Error while parse menu item "+node.getKey().toString()+". Check item params");
        }

        return new ItemStack(Material.AIR);
    }

    @Override
    public void serialize(TypeToken<?> typeToken, ItemStack item, ConfigurationNode node) {
        return;
    }
}
