package ru.csm.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.text.TextComponent;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.velocity.command.SubCommand;

public class CommandSkinUrl extends SubCommand {

    private final SkinsAPI<Player> api;
    private final TextComponent usage;

    public CommandSkinUrl(SkinsAPI<Player> api){
        this.api = api;
        this.usage = TextComponent.of(String.format(api.getLang().of("command.usage"),
                "/skin url <url> [slim]"));
    }

    @Override
    public void exec(CommandSource sender, String[] args) {
        if (!(sender instanceof Player)) return;

        SkinPlayer player = api.getPlayer(((Player)sender).getUsername());
        SkinModel model = SkinModel.STEVE;

        if(player != null){
            if (args.length > 0){
                if (args.length == 2 && args[1].equals("slim")) model = SkinModel.ALEX;
                api.setSkinFromImage(player, args[0], model);
                return;
            }
        }

        sender.sendMessage(usage);
    }
}
