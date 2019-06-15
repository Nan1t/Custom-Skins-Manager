package ru.csm.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.bungee.commands.SubCommand;

public class CommandNPC extends SubCommand {

    private Plugin plugin;
    private SkinsAPI api;
    private Language lang;

    public CommandNPC(Plugin plugin, SkinsAPI api){
        super("csm.skin.npc","/skin npc <npc> <url> [slim]");
        this.plugin = plugin;
        this.api = api;
        this.lang = api.getLang();
    }

    // skin npc <url> [slim]

    @Override
    public boolean execute(CommandSender sender, String[] args){
        return true;
    }

}
