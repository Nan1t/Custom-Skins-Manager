package ru.csm.bukkit.cmd;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.services.SkinsAPI;

public class CmdSkinMenu implements CommandExecutor {

    private final SkinsAPI<Player> api;

    public CmdSkinMenu(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        if (sender.getSender() instanceof Player){
            api.openSkinsMenu(sender.getSender());
        }
    }

}
