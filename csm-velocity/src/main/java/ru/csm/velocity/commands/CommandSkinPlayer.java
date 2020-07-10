package ru.csm.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.text.TextComponent;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.velocity.command.SubCommand;

public class CommandSkinPlayer extends SubCommand {

    private final SkinsAPI<Player> api;
    private final TextComponent usage;

    public CommandSkinPlayer(SkinsAPI<Player> api){
        this.api = api;
        this.usage = TextComponent.of(String.format(api.getLang().of("command.usage"),
                "/skin player <player>"));
    }

    @Override
    public void exec(CommandSource sender, String[] args) {
        if (!(sender instanceof Player)) return;

        if (args.length == 1){
            SkinPlayer player = api.getPlayer(((Player)sender).getUsername());
            if(player != null){
                api.setSkinFromName(player, args[0]);
            }
            return;
        }

        sender.sendMessage(usage);
    }
}
