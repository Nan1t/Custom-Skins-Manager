package ru.csm.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import ru.csm.api.storage.Language;
import ru.csm.bungee.command.CommandExecutor;

public class CommandSkin extends CommandExecutor {

    private final String[] usage;

    public CommandSkin(Language lang){
        super("csm", null, "skin", "skins");
        this.usage = lang.ofArray("help");
    }

    @Override
    public void exec(CommandSender sender, String[] args) {
        sender.sendMessages(usage);
    }
}
