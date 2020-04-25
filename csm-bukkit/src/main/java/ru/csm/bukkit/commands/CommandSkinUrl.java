package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CommandSkinUrl extends Command {

    private final SkinsAPI<Player> api;
    private final String usage;

    public CommandSkinUrl(SkinsAPI<Player> api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skin url <player> [model]");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        SkinPlayer<Player> player = api.getPlayer(sender.getName());
        SkinModel model = SkinModel.STEVE;

        if(player != null){
            if (args.length > 0){
                if (args.length == 2 && args[1].equals("slim")) model = SkinModel.ALEX;
                api.setSkinFromImage(player, args[0], model);
                return;
            }
        }

        sender.sendMessage(usage);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 1){
            return Collections.singletonList("<url>");
        } else if (args.length == 2){
            return Collections.singletonList("slim");
        }

        return null;
    }
}
