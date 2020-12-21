package ru.csm.bungee.cmd;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.services.SkinsAPI;

public class CmdSkinMenu implements CommandExecutor {

    private final SkinsAPI<ProxiedPlayer> api;

    public CmdSkinMenu(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        if (sender.getSender() instanceof ProxiedPlayer){
            api.openSkinsMenu(sender.getSender());
        }
    }

}
