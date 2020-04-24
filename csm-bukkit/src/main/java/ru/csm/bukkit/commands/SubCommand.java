package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {

    private String permission;
    private String[] usage;

    public SubCommand(String permission, String... usage){
        this.permission = permission;
        this.usage = usage;
    }

    public SubCommand(String permission){
        this.permission = permission;
        this.usage = new String[]{"nope"};
    }

    public String getPermission(){
        return permission;
    }

    public String[] getUsage(){
        return usage;
    }

    public abstract boolean execute(CommandSender sender, String[] args);

}
