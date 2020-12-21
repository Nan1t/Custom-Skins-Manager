package ru.csm.bukkit.cmd;

import napi.commands.Arguments;
import napi.commands.Command;
import napi.commands.bukkit.BukkitArgs;
import napi.commands.bukkit.BukkitCommandManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.cmd.cmdto.CmdToFrom;
import ru.csm.bukkit.cmd.cmdto.CmdToReset;
import ru.csm.bukkit.cmd.cmdto.CmdToSet;
import ru.csm.bukkit.cmd.cmdto.CmdToUrl;

public final class Commands {

    private Commands(){}

    public static void init(Plugin plugin, SkinsAPI<Player> api) {
        BukkitCommandManager manager = new BukkitCommandManager(plugin);

        Command cmdSkinPlayer = Command.builder()
                .permission("csm.skin.player")
                .args(
                        Arguments.string("username")
                )
                .executor(new CmdSkinPlayer(api))
                .build();

        Command cmdSkinUrl = Command.builder()
                .permission("csm.skin.url")
                .args(
                        Arguments.string("url"),
                        Arguments.optional(Arguments.string("slim"))
                )
                .executor(new CmdSkinUrl(api))
                .build();

        Command cmdSkinReset = Command.builder()
                .permission("csm.skin.reset")
                .executor(new CmdSkinReset(api))
                .build();

        Command cmdSkinToFrom = Command.builder()
                .args(
                        BukkitArgs.player("target"),
                        Arguments.string("username")
                )
                .executor(new CmdToFrom(api))
                .build();

        Command cmdSkinToUrl = Command.builder()
                .args(
                        BukkitArgs.player("target"),
                        Arguments.string("url"),
                        Arguments.optional(Arguments.string("slim"))
                )
                .executor(new CmdToUrl(api))
                .build();

        Command cmdSkinToSet = Command.builder()
                .args(
                        BukkitArgs.player("target"),
                        Arguments.string("texture"),
                        Arguments.string("signature")
                )
                .executor(new CmdToSet(api))
                .build();

        Command cmdSkinToReset = Command.builder()
                .args(
                        BukkitArgs.player("target")
                )
                .executor(new CmdToReset(api))
                .build();

        Command cmdSkinTo = Command.builder()
                .permission("csm.skin.to")
                .child(cmdSkinToFrom, "from")
                .child(cmdSkinToUrl, "url")
                .child(cmdSkinToSet, "set")
                .child(cmdSkinToReset, "reset")
                .build();

        Command cmdSkinPreview = Command.builder()
                .permission("csm.skin.preview")
                .args(
                        BukkitArgs.player("player"),
                        Arguments.string("texture"),
                        Arguments.string("signature"),
                        Arguments.optional(Arguments.string("permission"))
                )
                .executor(new CmdSkinPreview(api))
                .build();

        Command cmdSkinMenu = Command.builder()
                .permission("csm.skin.menu")
                .executor(new CmdSkinMenu(api))
                .build();

        Command cmdSkin = Command.builder()
                .help(api.getLang().ofArr("help"))
                .child(cmdSkinPlayer, "player")
                .child(cmdSkinUrl, "url")
                .child(cmdSkinReset, "reset")
                .child(cmdSkinTo, "to")
                .child(cmdSkinPreview, "preview")
                .child(cmdSkinMenu, "menu")
                .build();

        manager.register(cmdSkin, "csm", "skin", "skins");
    }

}
