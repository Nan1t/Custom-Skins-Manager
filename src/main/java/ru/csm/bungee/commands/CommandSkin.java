package ru.csm.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.bungee.commands.subcommands.*;

import java.util.HashMap;
import java.util.Map;

public class CommandSkin extends Command {

    private Map<String, SubCommand> playerSubCommands = new HashMap<>();
    private Map<String, SubCommand> adminSubCommands = new HashMap<>();

    private Plugin plugin;
    private SkinsAPI api;
    private Language lang;

    public CommandSkin(Plugin plugin, SkinsAPI api, Language lang) {
        super("csm", null, "skin", "skins");
        this.plugin = plugin;
        this.api = api;
        this.lang = lang;
        registerSubCommands();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            String subCommand = args[0].toLowerCase();
            SubCommand executor = playerSubCommands.get(subCommand);

            if (executor != null) {
                if (!sender.hasPermission(executor.getPermission())) {
                    sender.sendMessage(TextComponent.fromLegacyText(lang.of("permission.deny")));
                    return;
                }

                if (!executor.execute(sender, args)) {
                    sendUsage(sender, executor);
                }

                return;
            }

            if(adminSubCommands.containsKey(subCommand)){
                if(sender.hasPermission("csm.admin")){
                    sendAdminHelp(sender);
                    return;
                }
            }
        }

        sendHelp(sender);
    }

    private void sendHelp(CommandSender sender){
        ComponentBuilder builder = new ComponentBuilder("");

        for(String s : lang.ofArray("help")){
            builder.append(s);
        }

        sender.sendMessage(builder.create());

        if(sender.hasPermission("csm.admin")){
            sendAdminHelp(sender);
        }
    }

    private void sendAdminHelp(CommandSender sender){
        ComponentBuilder builder = new ComponentBuilder("");

        for(String s : lang.ofArray("admin")){
            builder.append(s);
        }

        sender.sendMessage(builder.create());
    }

    private void sendUsage(CommandSender sender, SubCommand cmd){
        sender.sendMessage(TextComponent.fromLegacyText(lang.of("command.usage")));

        ComponentBuilder builder = new ComponentBuilder("");

        for(String s : cmd.getUsage()){
            builder.append(s);
        }

        sender.sendMessage(builder.create());
    }

    private void registerSubCommands(){
        // Player commands
        playerSubCommands.put("player", new CommandPlayer(api, lang));
        playerSubCommands.put("url", new CommandUrl(api, lang));
        playerSubCommands.put("reset", new CommandReset(api, lang));
        playerSubCommands.put("npc", new CommandNPC(plugin, api));
        playerSubCommands.put("menu", new CommandMenu(lang, api));

        // Admin or console commands
        SubCommand commandTo = new CommandTo(api, lang);

        playerSubCommands.put("to", commandTo);
        adminSubCommands.put("to", commandTo);
    }
}
