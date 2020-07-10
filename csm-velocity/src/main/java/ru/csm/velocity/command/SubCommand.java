package ru.csm.velocity.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class SubCommand implements CommandHandler {

    private String permission;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    @Override
    public Set<String> getRegisteredSubKeys(){
        return subCommands.keySet();
    }

    @Override
    public String getPermission(){
        return permission;
    }

    @Override
    public void setPermission(String permission){
        this.permission = permission;
    }

    @Override
    public void addSub(SubCommand command, String arg, String... aliases){
        subCommands.put(arg, command);
        for (String a : aliases){
            subCommands.put(a, command);
        }
    }

    @Override
    public SubCommand getSub(String arg){
        return subCommands.get(arg);
    }
}
