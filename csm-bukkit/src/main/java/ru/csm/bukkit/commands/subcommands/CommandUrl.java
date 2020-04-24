package ru.csm.bukkit.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.api.upload.entity.RequestImage;
import ru.csm.api.utils.Validator;
import ru.csm.bukkit.commands.SubCommand;
import ru.csm.bukkit.player.BukkitSkinPlayer;

public class CommandUrl extends SubCommand {

    private SkinsAPI api;
    private Language lang;

    public CommandUrl(SkinsAPI api, Language lang){
        super("csm.skin.url", "/skin url <URL> [slim]");
        this.api = api;
        this.lang = lang;
    }

    // skin url <url> [slim]

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Command only for players");
            return true;
        }

        SkinPlayer player = api.getPlayer(sender.getName());

        if(player == null){
            player = new BukkitSkinPlayer((Player)sender);
        }

        if(args.length < 2){
            return false;
        }

        String url = args[1];
        SkinModel model = SkinModel.STEVE;

        if(!Validator.validateURL(url)){
            sender.sendMessage(lang.of("skin.image.invalid"));
            return true;
        }

        if(!api.getUrlWhitelist().isEmpty()){
            WhiteListElement elem = api.getUrlWhitelist().get(url.toLowerCase());
            if(elem != null){
                if(elem.hasPermission()){
                    if(!sender.hasPermission(elem.getPermission())){
                        sender.sendMessage(lang.of("permission.deny"));
                        return true;
                    }
                }
            }
        }

        if(args.length == 3) {
            if(!args[2].equalsIgnoreCase("slim")){
                return false;
            }
            model = SkinModel.ALEX;
        }

        api.setSkinFromImage(player, url, model);
        return true;
    }
}
