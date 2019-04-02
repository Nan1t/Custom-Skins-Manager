package ru.csm.bukkit.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.csm.api.player.Head;

import java.util.Map;
import java.util.UUID;

public class SkinMenu implements InventoryHolder {

    private static final int ITEMS_ON_PAGE = 21;

    private String title = "Menu";
    private ItemStack pane;
    private ItemStack leftArrow;
    private ItemStack rightArrow;
    private ItemStack resetButton;

    private Inventory inv;
    private Map<UUID, Head> heads;

    private int itemsOnPage = ITEMS_ON_PAGE;
    private int menuSize;
    private int pagesCount;
    private int page;

    public SkinMenu(int menuSize, int page, Map<UUID, Head> heads){
        this.menuSize = menuSize;
        this.page = page;
        this.heads = heads;

        pagesCount = menuSize/itemsOnPage;

        int remain = menuSize%itemsOnPage;

        if(remain > 0){
            pagesCount += 1;
        }
    }

    public SkinMenu(int itemsOnPage, int menuSize, int page, Map<UUID, Head> heads){
        this.itemsOnPage = itemsOnPage;
        this.menuSize = menuSize;
        this.page = page;
        this.heads = heads;

        pagesCount = menuSize/itemsOnPage;

        int remain = menuSize%itemsOnPage;

        if(remain > 0){
            pagesCount += 1;
        }
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public Head getHead(UUID uuid){
        return heads.get(uuid);
    }

    public int getMenuSize(){
        return menuSize;
    }

    public int getPagesCount(){
        return pagesCount;
    }

    public int getPage(){
        return page;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setPane(ItemStack pane){
        this.pane = pane;
    }

    public void setLeftArrow(ItemStack leftArrow) {
        this.leftArrow = leftArrow;
    }

    public void setRightArrow(ItemStack rightArrow) {
        this.rightArrow = rightArrow;
    }

    public void setResetButton(ItemStack resetButton) {
        this.resetButton = resetButton;
    }

    public void open(Player player){
        inv = Bukkit.createInventory(this, 54, title + " | " + page);

        inv.setItem(0, pane);
        inv.setItem(1, pane);
        inv.setItem(2, pane);
        inv.setItem(3, pane);
        inv.setItem(4, pane);
        inv.setItem(5, pane);
        inv.setItem(6, pane);
        inv.setItem(7, pane);
        inv.setItem(8, pane);
        inv.setItem(9, pane);
        inv.setItem(18, pane);
        inv.setItem(27, pane);
        inv.setItem(36, pane);
        inv.setItem(17, pane);
        inv.setItem(26, pane);
        inv.setItem(35, pane);
        inv.setItem(44, pane);

        for(Head head : heads.values()){
            inv.addItem(Heads.toItemStack(head));
        }

        inv.setItem(45, leftArrow);
        inv.setItem(49, resetButton);
        inv.setItem(53, rightArrow);

        player.openInventory(inv);
    }
}
