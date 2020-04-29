package ru.csm.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.csm.api.player.Head;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.menu.item.Skull;

import java.util.List;

public class CommandSkullTo extends Command {

    private final SkinsAPI<Player> api;

    public CommandSkullTo(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 3){
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null || !target.isOnline()){
                sender.sendMessage(String.format("Player %s does not exist or offline", args[0]));
                return;
            }

            if (args[1].equalsIgnoreCase("from")){
                Head head = api.getPlayerHead(args[2]);

                if (head != null){
                    ItemStack item = Skull.getCustomSkull(head.getUrl());
                    target.getInventory().addItem(item);
                }
                return;
            } else if (args[1].equalsIgnoreCase("url")){
                ItemStack item = Skull.getCustomSkull(args[2]);
                target.getInventory().addItem(item);
                return;
            }
        }

        sender.sendMessage("Wrong admin command format! Check the manual on SpigotMC plugin page");
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return null;
    }
}
