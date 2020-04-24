package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;
import ru.csm.api.services.SkinsAPI;

import java.util.List;

public class CommandSkullUrl extends Command {

    private final SkinsAPI api;

    public CommandSkullUrl(SkinsAPI api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return null;
    }
}
