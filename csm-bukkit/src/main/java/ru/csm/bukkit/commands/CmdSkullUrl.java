package ru.csm.bukkit.commands;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.menu.item.Skull;

public class CmdSkullUrl implements CommandExecutor {

    private final SkinsAPI<Player> api;

    public CmdSkullUrl(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        if (!(sender.getSender() instanceof Player)) return;

        Player player = sender.getSender();
        String url = ctx.getString("url");
        ItemStack item = Skull.getCustomSkull(url);

        player.getInventory().addItem(item);
        player.sendMessage(api.getLang().of("player.skull.received"));
    }

}
