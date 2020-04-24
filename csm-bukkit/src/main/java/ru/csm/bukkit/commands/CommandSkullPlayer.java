package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;
import ru.csm.api.services.SkinsAPI;

import java.util.List;

public class CommandSkullPlayer extends Command {

    private final SkinsAPI api;

    public CommandSkullPlayer(SkinsAPI api){
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
