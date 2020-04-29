package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.csm.api.player.Head;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.menu.item.Skull;

import java.util.List;

public class CommandSkullPlayer extends Command {

    private final SkinsAPI<Player> api;
    private final String usage;

    public CommandSkullPlayer(SkinsAPI<Player> api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skull player <player>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1){
            if (sender instanceof Player){
                Player player = (Player) sender;
                Head head = api.getPlayerHead(args[0]);

                if (head != null){
                    ItemStack item = Skull.getCustomSkull(head.getUrl());
                    player.getInventory().addItem(item);player.sendMessage(api.getLang().of("player.skull.received"));

                }
            }

            return;
        }

        sender.sendMessage(usage);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return null;
    }
}
