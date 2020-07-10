package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

import java.util.List;

public class CommandSkinReset extends Command {

    private final SkinsAPI<Player> api;
    private final String usage;

    public CommandSkinReset(SkinsAPI<Player> api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skin reset");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0){
            sender.sendMessage(usage);
            return;
        }

        SkinPlayer player = api.getPlayer(sender.getName());

        if(player != null){
            api.resetSkin(player);
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return null;
    }
}
