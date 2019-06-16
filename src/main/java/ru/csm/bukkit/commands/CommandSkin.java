package ru.csm.bukkit.commands;

import ninja.leaping.configurate.ConfigurationNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.plugin.Plugin;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.bukkit.commands.subcommands.*;
import ru.csm.bukkit.gui.managers.MenuManager;

import java.util.HashMap;
import java.util.Map;

public class CommandSkin implements CommandExecutor {

    private Map<String, SubCommand> playerSubCommands = new HashMap<>();
    private Map<String, SubCommand> adminSubCommands = new HashMap<>();

    private Plugin plugin;
    private SkinsAPI api;
    private Configuration menuConf;
    private Language lang;
    private MenuManager menuManager;

    public CommandSkin(Plugin plugin, SkinsAPI api, Configuration menuConf, Language lang, MenuManager menuManager) {
        this.plugin = plugin;
        this.api = api;
        this.menuConf = menuConf;
        this.lang = lang;
        this.menuManager = menuManager;

        registerSubCommands();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String subCommand = args[0].toLowerCase();
            SubCommand executor = playerSubCommands.get(subCommand);

            if (executor != null) {
                if (!sender.hasPermission(executor.getPermission())) {
                    sender.sendMessage(lang.of("permission.deny"));
                    return false;
                }

                if (!executor.execute(sender, args)) {
                    sendUsage(sender, executor);
                }

                return true;
            }

            if(adminSubCommands.containsKey(subCommand)){
                if(sender.hasPermission("csm.admin")){
                    sendAdminHelp(sender);
                    return true;
                }
            }
        }

        sendHelp(sender);
        return true;
    }

    private void sendHelp(CommandSender sender){
        sender.sendMessage(lang.ofArray("help"));

        if(sender.hasPermission("csm.admin")){
            sendAdminHelp(sender);
        }
    }

    private void sendAdminHelp(CommandSender sender){
        sender.sendMessage(lang.ofArray("admin"));
    }

    private void sendUsage(CommandSender sender, SubCommand cmd){
        sender.sendMessage(String.format(lang.of("command.usage"), ""));
        sender.sendMessage(cmd.getUsage());
    }

    private void registerSubCommands(){
        // Player commands
        playerSubCommands.put("player", new CommandPlayer(api, lang));
        playerSubCommands.put("url", new CommandUrl(api, lang));
        playerSubCommands.put("reset", new CommandReset(api, lang));
        playerSubCommands.put("npc", new CommandNPC(plugin, api));

        ConfigurationNode node = menuConf.get();

        if(node.getNode("auto", "enable").getBoolean() || node.getNode("custom", "enable").getBoolean()){
            playerSubCommands.put("menu", new CommandMenu(menuManager, lang));
        }

        // Admin or console commands
        SubCommand commandTo = new CommandTo(api, lang);

        playerSubCommands.put("to", commandTo);
        adminSubCommands.put("to", commandTo);
    }

}
