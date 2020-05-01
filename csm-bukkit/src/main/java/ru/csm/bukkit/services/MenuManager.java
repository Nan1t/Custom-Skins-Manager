package ru.csm.bukkit.services;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.bukkit.menu.GeneratedSkinsMenu;
import ru.csm.bukkit.menu.item.HeadItem;
import ru.csm.bukkit.menu.SkinsMenu;
import ru.csm.bukkit.menu.item.Item;

import java.util.*;

public final class MenuManager {

    private static final String ARROW_LEFT = "http://textures.minecraft.net/texture/bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9";
    private static final String ARROW_RIGHT = "http://textures.minecraft.net/texture/19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf";

    private final Map<UUID, SkinsMenu> openedMenus = new HashMap<>();

    private final Language lang;

    public MenuManager(Language lang){
        this.lang = lang;
    }

    public Optional<SkinsMenu> getOpenedMenu(Player player){
        return Optional.ofNullable(openedMenus.get(player.getUniqueId()));
    }

    public void openMenu(Player player, SkinsMenu menu){
        menu.open(player);
        openedMenus.put(player.getUniqueId(), menu);
    }

    private void closeMenu(Player player){
        openedMenus.remove(player.getUniqueId());
    }

    public SkinsMenu createMenu(SkinsAPI<Player> api, List<HeadItem> heads, int page){
        SkinsMenu menu = new GeneratedSkinsMenu(lang.of("menu.title"));
        List<Item> headItems = new ArrayList<>();

        for (HeadItem head : heads){
            Item item = Item.builder()
                    .texture(head.getSkin().getURL())
                    .displayName(lang.of("menu.btn.head.name").replace("%player%", head.getName()))
                    .lore(lang.ofList("menu.btn.head.lore"))
                    .action((clicker, clicked)->{
                        clicker.closeInventory();
                        api.showPreview(clicker, head.getSkin());
                    })
                    .build();

            headItems.add(item);
        }

        Item resetBtn = Item.builder()
                .material(Material.BARRIER)
                .displayName(lang.of("menu.btn.reset.name"))
                .action((player, item)->{
                    player.closeInventory();
                    api.resetSkin(player.getUniqueId());
                })
                .build();

        Item prevBtn = Item.builder()
                .texture(ARROW_LEFT)
                .amount(page-1)
                .displayName(lang.of("menu.btn.prev.name"))
                .action((player, item)->api.openSkinsMenu(player, page-1))
                .build();

        Item nextBtn = Item.builder()
                .texture(ARROW_RIGHT)
                .amount(page+1)
                .displayName(lang.of("menu.btn.next.name"))
                .action((player, item)->api.openSkinsMenu(player, page+1))
                .build();

        menu.setItems(headItems);
        menu.setItem(49, resetBtn);
        menu.setItem(45, prevBtn);
        menu.setItem(53, nextBtn);

        return menu;
    }
}
