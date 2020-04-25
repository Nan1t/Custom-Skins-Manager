package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

import java.util.List;

public class CommandSkinSet extends Command {

    private final SkinsAPI<Player> api;
    private final String usage;

    public CommandSkinSet(SkinsAPI<Player> api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skin set <player> <texture> <signature>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 3){
            SkinPlayer<Player> player = api.getPlayer(args[0]);
            if(player != null){
                Skin skin = new Skin();
                skin.setValue(args[1]);
                skin.setSignature(args[2]);
                api.setCustomSkin(player, skin);
                return;
            }
            sender.sendMessage(String.format("Player %s does not exist", args[0]));
            return;
        }

        sender.sendMessage(usage);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return null;
    }
}
