package ru.csm.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.Head;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.bukkit.gui.Heads;

public class CommandSkull implements CommandExecutor {

    private SkinsAPI api;
    private Language lang;

    public CommandSkull(SkinsAPI api, Language lang) {
        this.api = api;
        this.lang = lang;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Command only for players");
            return false;
        }

        Player player = (Player) sender;

        if(args.length == 1){
            String name = args[0];
            Head head = api.getPlayerHead(name);

            if(head != null){
                player.getInventory().addItem(Heads.toItemStack(head));
                sender.sendMessage(String.format(lang.of("player.skull.gived"), name));
                return true;
            }

            sender.sendMessage(String.format(lang.of("player.missing"), name));
            return false;
        }

        if(args.length == 2){
            if(!sender.hasPermission("csm.skulls.admin")){
                sender.sendMessage(lang.of("permission.deny"));
                return false;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if(target == null || !target.isOnline()){
                sender.sendMessage(String.format(lang.of("player.missing"), args[1]));
                return false;
            }

            String name = args[0];
            Head head = api.getPlayerHead(name);

            if(head != null){
                target.getInventory().addItem(Heads.toItemStack(head));
                sender.sendMessage(String.format(lang.of("player.skull.gived"), name));
                return true;
            }

            sender.sendMessage(String.format(lang.of("player.missing"), name));
            return false;
        }

        sendHelp(sender);
        return false;
    }

    private void sendHelp(CommandSender sender){
        sender.sendMessage(lang.of("player.skull.help"));
    }
}
