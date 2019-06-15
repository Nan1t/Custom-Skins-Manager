package ru.csm.bukkit.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.bukkit.commands.SubCommand;
import ru.csm.bukkit.player.BukkitSkinPlayer;

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
        if(!(sender instanceof Player)){
            sender.sendMessage("Command only for players");
            return true;
        }

        SkinPlayer player = api.getPlayer(sender.getName());

        if(player == null){
            player = new BukkitSkinPlayer((Player)sender);
        }

        api.resetSkin(player);
        return true;
    }

}
