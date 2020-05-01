package ru.csm.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import ru.csm.api.storage.Language;
import ru.csm.bungee.command.CommandExecutor;

public class CommandSkull extends CommandExecutor {

    private final String[] usage;

    public CommandSkull(Language lang){
        super("csmskull", null, "skull");
        this.usage = lang.ofArray("help");
    }

    @Override
    public void exec(CommandSender sender, String[] args) {
        sender.sendMessages(usage);
    }
}
