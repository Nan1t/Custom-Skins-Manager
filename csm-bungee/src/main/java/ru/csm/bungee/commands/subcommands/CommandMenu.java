package ru.csm.bungee.commands.subcommands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.network.Channels;
import ru.csm.api.player.Head;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.bungee.commands.SubCommand;
import ru.csm.bungee.network.JsonMessage;
import ru.csm.bungee.network.PluginMessageService;

import java.util.Map;
import java.util.UUID;

public class CommandMenu extends SubCommand {

    private Language lang;
    private SkinsAPI api;
    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public CommandMenu(Language lang, SkinsAPI api){
        super("csm.skin.menu", "/skin menu [page]");
        this.lang = lang;
        this.api = api;
    }

    // skin menu [page]

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            sender.sendMessage("Command only for players");
            return true;
        }

        ProxiedPlayer player = (ProxiedPlayer)sender;
        int page = 1;

        if(args.length == 2){
            try{
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                sender.sendMessage(lang.of("menu.page.invalid"));
                return true;
            }
        }

        int menuSize = api.getMenuSize();
        Map<UUID, Head> heads = api.getHeads(menuSize, page);

        String jsonStr = gson.toJson(heads);
        JsonObject headsJson = new JsonParser().parse(jsonStr).getAsJsonObject();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("player", player.getUniqueId().toString());
        responseJson.addProperty("page", page);
        responseJson.addProperty("size", menuSize);
        responseJson.add("heads", headsJson);

        PluginMessageService.sendMessage(new JsonMessage(Channels.SKINS_MENU, player, responseJson));

        return true;
    }

}
