package ru.csm.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.bungee.commands.SubCommand;
import ru.csm.bungee.player.BungeeSkinPlayer;

public class CommandReset extends SubCommand {

    private SkinsAPI api;
    private Language lang;

    public CommandReset(SkinsAPI api, Language lang){
        super("csm.skin.reset", "/skin reset");
        this.api = api;
        this.lang = lang;
    }

    // skin reset

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            sender.sendMessage("Command only for players");
            return true;
        }

        SkinPlayer player = api.getPlayer(sender.getName());

        if(player == null){
            player = new BungeeSkinPlayer((ProxiedPlayer)sender);
        }

        api.resetSkin(player);
        return true;
    }

}
