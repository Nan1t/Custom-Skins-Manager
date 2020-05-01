package ru.csm.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.command.SubCommand;

public class CommandSkinTo extends SubCommand {

    private final SkinsAPI<ProxiedPlayer> api;

    public CommandSkinTo(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
    }

    @Override
    public void exec(CommandSender sender, String[] args) {
        if (args.length >= 2){
            SkinPlayer<ProxiedPlayer> target = api.getPlayer(args[0]);

            if (target == null){
                sender.sendMessage(String.format("Player %s does not exist or offline", args[0]));
                return;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("reset")){
                api.resetSkin(target);
                return;
            } else if (args.length == 3){
                if (args[1].equalsIgnoreCase("from")){
                    api.setSkinFromName(target, args[2]);
                    sender.sendMessage(String.format("Processing skin from name for %s...", target.getName()));
                    return;
                } else if (args[1].equalsIgnoreCase("url")){
                    api.setSkinFromImage(target, args[2], SkinModel.STEVE);
                    sender.sendMessage(String.format("Processing skin from image (steve model) for %s...", target.getName()));
                    return;
                }
            } else if (args.length == 4){
                if (args[1].equalsIgnoreCase("url")){
                    if (args[3].equalsIgnoreCase("slim")){
                        api.setSkinFromImage(target, args[2], SkinModel.ALEX);
                        sender.sendMessage(String.format("Processing skin from image (alex model) for %s...", target.getName()));
                        return;
                    }
                } else if (args[1].equalsIgnoreCase("set")){
                    Skin skin = new Skin();
                    skin.setValue(args[2]);
                    skin.setSignature(args[3]);
                    api.setCustomSkin(target, skin);
                    return;
                }
            }
        }

        sender.sendMessage("Wrong admin command format! Check the manual on SpigotMC plugin page");
    }
}
