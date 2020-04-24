package ru.csm.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.WhiteListElement;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.api.utils.text.Validator;
import ru.csm.bungee.commands.SubCommand;

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
        if(!(sender instanceof ProxiedPlayer)){
            sender.sendMessage("Command only for players");
            return true;
        }

        SkinPlayer player = api.getPlayer(sender.getName());

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
