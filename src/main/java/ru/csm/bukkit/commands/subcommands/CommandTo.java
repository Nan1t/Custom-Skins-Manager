package ru.csm.bukkit.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.database.Database;
import ru.csm.api.upload.entity.RequestImage;
import ru.csm.api.upload.entity.RequestLicense;
import ru.csm.api.upload.entity.SkinRequest;
import ru.csm.api.utils.text.Colors;
import ru.csm.api.utils.text.Validator;
import ru.csm.bukkit.commands.SubCommand;
import ru.csm.bukkit.player.BukkitSkinPlayer;

public class CommandTo extends SubCommand {

    private SkinsAPI api;
    private Database db;
    private Language lang;

    public CommandTo(SkinsAPI api, Language lang){
        super("csm.admin",
                "/csm to <player> url <image url> [slim] - Set custom skin",
                "/skin to <player> from <premium player> - Set premium skin",
                "/skin to <player> reset - Reset skin");

        this.api = api;
        this.lang = lang;
    }

    // skin to <player> url <url> [slim]
    // skin to <player> from <premium player>
    // skin to <player> reset

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length < 3){
            return false;
        }

        String targetName = args[1];
        String cmd = args[2];
        Player defPlayer = Bukkit.getPlayer(targetName);

        if(defPlayer == null || !defPlayer.isOnline()){
            sender.sendMessage(Colors.of("&cPlayer must be online"));
            return true;
        }

        SkinPlayer player = api.getPlayer(targetName);

        if(player == null){
            player = new BukkitSkinPlayer(defPlayer);
        }

        if(cmd.equalsIgnoreCase("reset")){
            if(!player.hasCustomSkin()){
                sender.sendMessage("Player " + player.getName() + " not have custom skin");
                return true;
            }

            api.resetSkin(player);
            sender.sendMessage("Success reset skin for player " + player.getName());
            return true;
        }

        if(cmd.equalsIgnoreCase("from")){
            if(args.length == 4){
                String name = args[3];

                if(!Validator.validateName(name)){
                    sender.sendMessage("Premium name invalid");
                    return true;
                }

                api.setSkinFromName(player, name);
                sender.sendMessage("Request for change player skin added to queue");
                return true;
            }
            return false;
        }

        if(cmd.equalsIgnoreCase("url")){
            if(args.length > 3){
                String url = args[3];
                SkinModel model = SkinModel.STEVE;

                if(args.length == 5){
                    if(!args[4].equalsIgnoreCase("slim")){
                        return false;
                    }
                    model = SkinModel.ALEX;
                }

                if(!Validator.validateURL(url)){
                    sender.sendMessage(lang.of("skin.image.invalid"));
                    return true;
                }

                api.setSkinFromImage(player, url, model);
                sender.sendMessage("Request for change player skin added to queue");
                return true;
            }

            return false;
        }

        return false;
    }

}
