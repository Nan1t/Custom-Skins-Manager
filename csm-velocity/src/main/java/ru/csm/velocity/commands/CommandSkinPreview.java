package ru.csm.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.TextComponent;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinsAPI;
import ru.csm.velocity.command.SubCommand;

import java.util.Optional;

public class CommandSkinPreview extends SubCommand {

    private final SkinsAPI<Player> api;
    private final ProxyServer server;
    private final TextComponent usage;

    public CommandSkinPreview(SkinsAPI<Player> api, ProxyServer server){
        this.api = api;
        this.server = server;
        this.usage = TextComponent.of(String.format(api.getLang().of("command.usage"),
                "/skin preview <player> <texture> <signature> [permission]"));
    }

    @Override
    public void exec(CommandSource sender, String[] args) {
        if (args.length >= 3){
            Optional<Player> player = server.getPlayer(args[0]);
            String texture = args[1];
            String signature = args[2];
            String permission = null;

            if (!player.isPresent() || !player.get().isActive()){
                sender.sendMessage(TextComponent.of("Player is offline"));
                return;
            }

            if (args.length == 4) permission = args[3];

            api.showPreview(player.get(), new Skin(texture, signature), false, permission);
            return;
        }

        sender.sendMessage(usage);
    }
}
