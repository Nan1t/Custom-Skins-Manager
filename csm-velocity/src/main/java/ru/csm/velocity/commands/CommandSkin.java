package ru.csm.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.text.TextComponent;
import ru.csm.api.storage.Language;
import ru.csm.velocity.command.CommandExecutor;

public class CommandSkin extends CommandExecutor {

    private final TextComponent usage;

    public CommandSkin(Language lang){
        this.usage = TextComponent.of(String.join("\n", lang.ofArray("help")));
    }

    @Override
    public void exec(CommandSource sender, String[] args) {
        sender.sendMessage(usage);
    }
}
