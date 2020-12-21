package ru.csm.bukkit.commands;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.csm.bukkit.menu.item.Skull;

public class CmdSkullToUrl implements CommandExecutor {

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        Player player = ctx.<Player>get("target").get();
        String url = ctx.getString("url");

        ItemStack item = Skull.getCustomSkull(url);
        player.getInventory().addItem(item);
    }

}
