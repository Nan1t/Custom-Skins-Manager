package ru.csm.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.text.TextComponent;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.velocity.command.SubCommand;

public class CommandSkinReset extends SubCommand {

    private final SkinsAPI<Player> api;
    private final TextComponent usage;

    public CommandSkinReset(SkinsAPI<Player> api){
        this.api = api;
        this.usage = TextComponent.of(String.format(api.getLang().of("command.usage"), "/skin reset"));
    }

    @Override
    public void exec(CommandSource sender, String[] args) {
        if (sender instanceof Player){
            if (args.length > 0){
                sender.sendMessage(usage);
                return;
            }

            SkinPlayer player = api.getPlayer(((Player)sender).getUsername());

            if(player != null){
                api.resetSkin(player);
            }
        }
    }
}
