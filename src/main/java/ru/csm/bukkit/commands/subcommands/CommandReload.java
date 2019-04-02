package ru.csm.bukkit.commands.subcommands;

import org.bukkit.command.CommandSender;
import ru.csm.api.utils.text.Colors;
import ru.csm.bukkit.Skins;
import ru.csm.bukkit.commands.SubCommand;

import java.io.IOException;

public class CommandReload extends SubCommand {

    public CommandReload(){
        super("csm.admin", "/csm reload");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        try {
            Skins.reloadConfiguration();
            sender.sendMessage(Colors.of("&aCustomSkinsManager successfully reloaded"));
        } catch (IOException e){
            System.out.println("Error while reloading CustomSkinsManager: " + e.getMessage());
            sender.sendMessage(Colors.of("&cError while reloading CustomSkinsManager. Check the console to see an error"));
        }
        return true;
    }

}
