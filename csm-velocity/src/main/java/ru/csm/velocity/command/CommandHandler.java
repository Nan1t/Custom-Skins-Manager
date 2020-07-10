package ru.csm.velocity.command;

import com.velocitypowered.api.command.CommandSource;

import java.util.Set;

public interface CommandHandler {

    Set<String> getRegisteredSubKeys();

    String getPermission();

    void setPermission(String permission);

    void addSub(SubCommand command, String arg, String... aliases);

    CommandHandler getSub(String arg);

    void exec(CommandSource sender, String[] args);

    default boolean checkPermission(CommandSource sender, CommandHandler command){
        return (command.getPermission() == null) || sender.hasPermission(command.getPermission());
    }

}
