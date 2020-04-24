package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;
import ru.csm.api.storage.Language;

import java.util.List;

public class CommandSkin extends Command {

    private final String[] usage;

    public CommandSkin(Language lang){
        this.usage = lang.ofArray("help");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(usage);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return null;
    }
}
