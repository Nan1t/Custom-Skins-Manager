package ru.csm.bukkit.commands;

import napi.commands.Arguments;
import napi.commands.Command;
import napi.commands.bukkit.BukkitArgs;
import napi.commands.bukkit.BukkitCommandManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.csm.api.services.SkinsAPI;

public final class Commands {

    private Commands(){}

    public static void init(Plugin plugin, SkinsAPI<Player> api) {
        BukkitCommandManager manager = new BukkitCommandManager(plugin);

        /*  /skin commands  */

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
                .executor(new CmdSkinToFrom(api))
                .build();

        Command cmdSkinToUrl = Command.builder()
                .args(
                        BukkitArgs.player("target"),
                        Arguments.string("url"),
                        Arguments.optional(Arguments.string("slim"))
                )
                .executor(new CmdSkinToUrl(api))
                .build();

        Command cmdSkinToSet = Command.builder()
                .args(
                        BukkitArgs.player("target"),
                        Arguments.string("texture"),
                        Arguments.string("signature")
                )
                .executor(new CmdSkinToSet(api))
                .build();

        Command cmdSkinToReset = Command.builder()
                .args(
                        BukkitArgs.player("target")
                )
                .executor(new CmdSkinToReset(api))
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
                .description("CustomSkinsManager skin command")
                .help(api.getLang().ofArr("help"))
                .child(cmdSkinPlayer, "player")
                .child(cmdSkinUrl, "url")
                .child(cmdSkinReset, "reset")
                .child(cmdSkinTo, "to")
                .child(cmdSkinPreview, "preview")
                .child(cmdSkinMenu, "menu")
                .build();

        /*  /skull commands  */

        Command cmdSkullPlayer = Command.builder()
                .permission("csm.skull.player")
                .args(
                        Arguments.string("username")
                )
                .executor(new CmdSkullPlayer(api))
                .build();

        Command cmdSkullUrl = Command.builder()
                .permission("csm.skull.url")
                .args(
                        Arguments.string("url")
                )
                .executor(new CmdSkullUrl(api))
                .build();

        Command cmdSkullToPlayer = Command.builder()
                .args(
                        BukkitArgs.player("target"),
                        Arguments.string("username")
                )
                .executor(new CmdSkullToFrom(api))
                .build();

        Command cmdSkullToUrl = Command.builder()
                .args(
                        BukkitArgs.player("target"),
                        Arguments.string("url")
                )
                .executor(new CmdSkullToUrl())
                .build();

        Command cmdSkullTo = Command.builder()
                .permission("csm.skull.to")
                .child(cmdSkullToPlayer, "player")
                .child(cmdSkullToUrl, "url")
                .build();

        Command cmdSkull = Command.builder()
                .permission("csm.skulls")
                .description("CustomSkinsManager skull command")
                .help(api.getLang().ofArr("help"))
                .child(cmdSkullPlayer, "player")
                .child(cmdSkullUrl, "url")
                .child(cmdSkullTo, "to")
                .build();

        manager.register(cmdSkin, "csm", "skin", "skins");
        manager.register(cmdSkull, "csmskull", "skull");
    }

}
