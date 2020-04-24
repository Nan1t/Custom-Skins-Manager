package ru.csm.bukkit.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.api.utils.Validator;
import ru.csm.bukkit.commands.SubCommand;
import ru.csm.bukkit.player.BukkitSkinPlayer;

public class CommandPlayer extends SubCommand {

    private SkinsAPI api;
    private Language lang;

    public CommandPlayer(SkinsAPI api, Language lang){
        super("csm.skin.player", "/skin player <player>");
        this.api = api;
        this.lang = lang;
    }

    // skin player <player name>

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

        if(args.length == 2){
            String target = args[1];

            if(!Validator.validateName(target)){
                sender.sendMessage(lang.of("skin.name.invalid"));
                return true;
            }

            if(!api.getBlacklist().isEmpty()){
                if(api.hasBlacklist(target)){
                    sender.sendMessage(lang.of("skin.name.blacklist.exist"));
                    return true;
                }
            }

            if(!api.getNicknamesWhitelist().isEmpty()){
                WhiteListElement elem = api.getNicknamesWhitelist().get(target.toLowerCase());
                if(elem != null){
                    if(elem.hasPermission()){
                        if(!sender.hasPermission(elem.getPermission())){
                            sender.sendMessage(lang.of("permission.deny"));
                            return true;
                        }
                    }
                }
            }

            api.setSkinFromName(player, target);
            return true;
        }

        return false;
    }
}
