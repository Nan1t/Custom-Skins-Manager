package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class Command implements TabExecutor {

    private String permission;
    private final Map<String, Command> subCommands = new HashMap<>();

    public Command(){ }

    public Command(String permission){
        this.permission = permission;
    }

    public Set<String> getRegisteredSubKeys(){
        return subCommands.keySet();
    }

    public String getPermission(){
        return permission;
    }

    public Command setPermission(String permission){
        this.permission = permission;
        return this;
    }

    public Command addSub(Command command, String arg, String... aliases){
        subCommands.put(arg, command);
        for (String a : aliases){
            subCommands.put(a, command);
        }
        return this;
    }

    public Command getSub(String arg){
        return subCommands.get(arg);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        executeSubCommands(sender, this, args, cmd.getPermissionMessage());
        return true;
    }

    private void executeSubCommands(CommandSender sender, Command parent, String[] args, String noPermMessage){
        for (String arg : args){
            Command cmd = parent.getSub(arg);
            if (cmd != null){
                executeSubCommands(sender, cmd, Arrays.copyOfRange(args, 1, args.length), noPermMessage);
                return;
            }
        }

        if(!checkPermission(sender, parent)){
            sender.sendMessage(noPermMessage);
            return;
        }

        parent.execute(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if(!checkPermission(sender, this)) return null;
        return getTooltips(sender, this, args);
    }

    private List<String> getTooltips(CommandSender sender, Command command, String[] args){
        for (String arg : args){
            Command cmd = command.getSub(arg);
            if (cmd != null){
                return getTooltips(sender, cmd, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        if (checkPermission(sender, command)){
            if (args.length > 0){
                Set<String> keys = command.getRegisteredSubKeys();

                if (keys != null && !keys.isEmpty()){
                    List<String> result = new ArrayList<>();
                    String lastArg = args[args.length-1];

                    for (String str : keys){
                        if (str.startsWith(lastArg)) result.add(str);
                    }

                    return result;
                }
            }

            return command.onTab(sender, args);
        }

        return null;
    }

    private boolean checkPermission(CommandSender sender, Command command){
        return (command.getPermission() == null) || sender.hasPermission(command.getPermission());
    }

    public abstract void execute(CommandSender sender, String[] args);

    public abstract List<String> onTab(CommandSender sender, String[] args);
}
