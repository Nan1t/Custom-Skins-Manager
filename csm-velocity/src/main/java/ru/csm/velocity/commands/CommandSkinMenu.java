package ru.csm.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import ru.csm.api.services.SkinsAPI;
import ru.csm.velocity.command.SubCommand;

public class CommandSkinMenu extends SubCommand {

    private final SkinsAPI<Player> api;

    public CommandSkinMenu(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void exec(CommandSource sender, String[] args) {
        if (sender instanceof Player){
            api.openSkinsMenu((Player)sender);
        }
    }
}
