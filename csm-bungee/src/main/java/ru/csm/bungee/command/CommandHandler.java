package ru.csm.bungee.command;

import net.md_5.bungee.api.CommandSender;

import java.util.Set;

public interface CommandHandler {

    Set<String> getRegisteredSubKeys();

    String getPermission();

    void setPermission(String permission);

    void addSub(SubCommand command, String arg, String... aliases);

    CommandHandler getSub(String arg);

    void exec(CommandSender sender, String[] args);

    default boolean checkPermission(CommandSender sender, CommandHandler command){
        return (command.getPermission() == null) || sender.hasPermission(command.getPermission());
    }

}
