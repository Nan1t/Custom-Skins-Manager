/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.csm.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.text.TextComponent;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.velocity.command.SubCommand;

public class CommandSkinTo extends SubCommand {

    private final SkinsAPI<Player> api;

    public CommandSkinTo(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void exec(CommandSource sender, String[] args) {
        if (args.length >= 2){
            SkinPlayer target = api.getPlayer(args[0]);

            if (target == null){
                sender.sendMessage(TextComponent.of(String.format("Player %s does not exist or offline", args[0])));
                return;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("reset")){
                api.resetSkin(target);
                return;
            } else if (args.length == 3){
                if (args[1].equalsIgnoreCase("from")){
                    api.setSkinFromName(target, args[2]);
                    sender.sendMessage(TextComponent.of(String.format("Processing skin from name for %s...", target.getName())));
                    return;
                } else if (args[1].equalsIgnoreCase("url")){
                    api.setSkinFromImage(target, args[2], SkinModel.STEVE);
                    sender.sendMessage(TextComponent.of(String.format("Processing skin from image (steve model) for %s...", target.getName())));
                    return;
                }
            } else if (args.length == 4){
                if (args[1].equalsIgnoreCase("url")){
                    if (args[3].equalsIgnoreCase("slim")){
                        api.setSkinFromImage(target, args[2], SkinModel.ALEX);
                        sender.sendMessage(TextComponent.of(String.format("Processing skin from image (alex model) for %s...", target.getName())));
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

        sender.sendMessage(TextComponent.of("Wrong admin command format! Check the manual on SpigotMC plugin page"));
    }
}
