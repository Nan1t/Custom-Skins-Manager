package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.menu.item.Skull;

import java.util.List;

public class CommandSkullUrl extends Command {

    private final SkinsAPI<Player> api;
    private final String usage;

    public CommandSkullUrl(SkinsAPI<Player> api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skull url <url>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1){
            if (sender instanceof Player){
                Player player = (Player) sender;
                ItemStack item = Skull.getCustomSkull(args[0]);
                player.getInventory().addItem(item);
                player.sendMessage(api.getLang().of("player.skull.received"));
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
