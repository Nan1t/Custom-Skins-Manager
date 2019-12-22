package ru.csm.bukkit.gui.managers;

import com.google.common.reflect.TypeToken;
import de.tr7zw.changeme.nbtapi.NBTItem;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.api.player.Head;
import ru.csm.bukkit.gui.SkinMenu;

import java.util.*;

public class MenuManager {

    private String title;
    private ItemStack pane;
    private ItemStack leftArrow;
    private ItemStack rightArrow;
    private ItemStack resetButton;

    private SkinsAPI api;
    private Language lang;

    private Map<UUID, SkinMenu> currentMenus = new TreeMap<>();

    public SkinMenu getPlayerCurrentMenu(UUID playerUuid){
        return currentMenus.get(playerUuid);
    }

    public void setCurrentMenu(UUID uuid, SkinMenu menu){
        currentMenus.put(uuid, menu);
    }

    protected SkinsAPI getApi() {
        return api;
    }

    protected Language getLang() {
        return lang;
    }

    public String getTitle() {
        return title;
    }

    public ItemStack getPane() {
        return pane;
    }

    public ItemStack getLeftArrow() {
        return leftArrow;
    }

    public ItemStack getRightArrow() {
        return rightArrow;
    }

    public ItemStack getResetButton() {
        return resetButton;
    }

    public MenuManager(Configuration menuConf, Language lang, SkinsAPI api) throws ObjectMappingException {
        this.api = api;
        this.lang = lang;

        ConfigurationNode gui = menuConf.get().getNode("toolbar");

        pane = gui.getNode("pane").getValue(TypeToken.of(ItemStack.class));
        leftArrow = gui.getNode("leftArrow").getValue(TypeToken.of(ItemStack.class));
        rightArrow = gui.getNode("rightArrow").getValue(TypeToken.of(ItemStack.class));
        resetButton = gui.getNode("resetButton").getValue(TypeToken.of(ItemStack.class));
        title = lang.of("menu.title");

        NBTItem leftArrowNBT = new NBTItem(leftArrow);
        leftArrowNBT.setString("InventoryAction", "back");
        leftArrow = leftArrowNBT.getItem();

        NBTItem rightArrowNBT = new NBTItem(rightArrow);
        rightArrowNBT.setString("InventoryAction", "next");
        rightArrow = rightArrowNBT.getItem();

        NBTItem resetBtnNBT = new NBTItem(resetButton);
        resetBtnNBT.setString("InventoryAction", "reset");
        resetButton = resetBtnNBT.getItem();
    }

    public void openMenu(Player player, int page){
        int menuSize = api.getMenuSize();
        Map<UUID, Head> heads = api.getHeads(menuSize, page);
        SkinMenu menu = new SkinMenu(menuSize, page, heads);

        if(page > menu.getPagesCount()){
            player.sendMessage(lang.of("menu.page.invalid"));
            return;
        }

        menu.setTitle(title);
        menu.setPane(pane);
        menu.setLeftArrow(leftArrow);
        menu.setRightArrow(rightArrow);
        menu.setResetButton(resetButton);
        menu.open(player);

        setCurrentMenu(player.getUniqueId(), menu);
    }

    public void openMenu(Player player, SkinMenu menu){
        menu.setTitle(title);
        menu.setPane(pane);
        menu.setLeftArrow(leftArrow);
        menu.setRightArrow(rightArrow);
        menu.setResetButton(resetButton);
        menu.open(player);

        setCurrentMenu(player.getUniqueId(), menu);
    }
}
