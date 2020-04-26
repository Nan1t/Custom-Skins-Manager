package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.services.SkinsAPI;

import java.util.List;

public class CommandSkinMenu extends Command {

    private final SkinsAPI<Player> api;

    public CommandSkinMenu(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player){
            api.openSkinsMenu((Player)sender);
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return null;
    }
}
