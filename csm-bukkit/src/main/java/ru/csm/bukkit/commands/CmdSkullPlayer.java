package ru.csm.bukkit.commands;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.csm.api.player.Head;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.menu.item.Skull;

public class CmdSkullPlayer implements CommandExecutor {

    private final SkinsAPI<Player> api;

    public CmdSkullPlayer(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        if (!(sender.getSender() instanceof Player)) return;

        Player player = sender.getSender();
        String username = ctx.getString("username");
        Head head = api.getPlayerHead(username);

        if (head != null){
            ItemStack item = Skull.getCustomSkull(head.getUrl());
            player.getInventory().addItem(item);
            player.sendMessage(api.getLang().of("player.skull.received"));
            return;
        }

        player.sendMessage(String.format(api.getLang().of("player.missing"), username));
    }

}
