package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

import java.util.Collections;
import java.util.List;

public class CommandSkinPlayer extends Command {

    private final SkinsAPI api;
    private final String usage;

    public CommandSkinPlayer(SkinsAPI api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skin player <player>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        if (args.length == 1){
            SkinPlayer<?> player = api.getPlayer(sender.getName());
            if(player != null){
                api.setSkinFromName(player, args[0]);
            }
            return;
        }

        sender.sendMessage(usage);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return Collections.singletonList("<player>");
    }
}
