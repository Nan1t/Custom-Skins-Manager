package ru.csm.bukkit.gui.managers;

import com.google.gson.JsonObject;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.entity.Player;
import ru.csm.api.network.Channels;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.bukkit.network.PluginMessageService;

public class BungeeMenuManager extends MenuManager {

    private PluginMessageService pmService;

    public BungeeMenuManager(Configuration conf, Language lang, SkinsAPI api, PluginMessageService pmService) throws ObjectMappingException {
        super(conf, lang, api);
        this.pmService = pmService;
    }

    @Override
    public void openMenu(Player player, int page){
        JsonObject json = new JsonObject();
        json.addProperty("player", player.getUniqueId().toString());
        json.addProperty("page", page);
        pmService.sendMessage(player, Channels.SKINS_MENU, json);
    }

}
