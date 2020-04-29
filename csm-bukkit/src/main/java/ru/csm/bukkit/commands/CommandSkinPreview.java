package ru.csm.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinsAPI;

import java.util.Collections;
import java.util.List;

public class CommandSkinPreview extends Command {

    private final SkinsAPI<Player> api;
    private final String usage;

    public CommandSkinPreview(SkinsAPI<Player> api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skin preview <player> <texture> <signature> [permission]");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 3){
            Player player = Bukkit.getPlayer(args[0]);
            String texture = args[1];
            String signature = args[2];
            String permission = null;

            if (player == null || !player.isOnline()){
                sender.sendMessage("Player is offline");
                return;
            }

            if (args.length == 4) permission = args[3];

            api.showPreview(player, new Skin(texture, signature), false, permission);
            return;
        }

        sender.sendMessage(usage);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 1){
            return Collections.singletonList("<player>");
        } else if (args.length == 2){
            return Collections.singletonList("<texture>");
        } else if (args.length == 3){
            return Collections.singletonList("<signature>");
        } else if (args.length == 4){
            return Collections.singletonList("[permission]");
        }
        return null;
    }
}
