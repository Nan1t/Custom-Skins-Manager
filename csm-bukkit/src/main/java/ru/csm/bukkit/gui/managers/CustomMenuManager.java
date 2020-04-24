package ru.csm.bukkit.gui.managers;

import ninja.leaping.modded.configurate.objectmapping.ObjectMappingException;
import org.bukkit.entity.Player;
import ru.csm.api.player.Head;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.api.utils.text.Colors;
import ru.csm.bukkit.gui.SkinMenu;

import java.util.*;

public class CustomMenuManager extends MenuManager {

    private List<Map<UUID, Head>> pages = new LinkedList<>();
    private int menuSize;

    private static final int HEADS_ON_PAGE = 28;

    public CustomMenuManager(Configuration conf, Language lang, SkinsAPI api) throws ObjectMappingException {
        super(conf, lang, api);

        List<LinkedHashMap> list = (List<LinkedHashMap>) conf.get().getNode("custom", "list").getValue();

        menuSize = list.size();

        int pagesCount = menuSize/HEADS_ON_PAGE;
        int remain = menuSize%HEADS_ON_PAGE;
        if(remain > 0){
            pagesCount += 1;
        }

        int lastIndex = 0;

        for(int i = 1; i < pagesCount+1; i++){
            Map<UUID, Head> page = new LinkedHashMap<>();

            int bound = (HEADS_ON_PAGE*i);

            if(bound > menuSize){
                bound = menuSize;
            }

            for(int j = lastIndex; j < bound; j++) {
                LinkedHashMap map = list.get(j);

                String displayName = map.get("name").toString();
                String value = map.get("value").toString();
                String signature = map.get("signature").toString();
                String permission = null;

                if(map.get("permission") != null){
                    permission = map.get("permission").toString();
                }

                List<String> lore = null;

                if (map.get("lore") != null) {
                    lore = Colors.ofArr((ArrayList<String>) map.get("lore"));
                }

                Skin skin = new Skin(value, signature);
                Head head = new Head(UUID.randomUUID(), displayName, skin);
                head.setLore(lore);
                head.setPermission(permission);

                page.put(head.getOwnerUuid(), head);

                lastIndex = j;
            }

            pages.add(page);
        }
    }

    public void openMenu(Player player, int page){
        try{
            pages.get(page-1);
        } catch (IndexOutOfBoundsException e){
            player.sendMessage(getLang().of("menu.page.invalid"));
            return;
        }

        SkinMenu menu = new SkinMenu(HEADS_ON_PAGE, menuSize, page, pages.get(page-1));

        menu.setTitle(getTitle());
        menu.setPane(getPane());
        menu.setLeftArrow(getLeftArrow());
        menu.setRightArrow(getRightArrow());
        menu.setResetButton(getResetButton());
        menu.open(player);

        setCurrentMenu(player.getUniqueId(), menu);
    }

}
