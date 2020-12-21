package ru.csm.velocity.cmd;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import napi.commands.Arguments;
import napi.commands.Command;
import napi.commands.manager.CommandManager;
import napi.commands.velocity.VelocityArgs;
import napi.commands.velocity.VelocityCommandManager;
import ru.csm.api.network.MessageSender;
import ru.csm.api.services.SkinsAPI;

public final class Commands {

    private Commands(){}

    public static void init(ProxyServer server, SkinsAPI<Player> api, MessageSender<Player> sender) {
        CommandManager manager = new VelocityCommandManager(server);

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
                        VelocityArgs.player("target"),
                        Arguments.string("username")
                )
                .executor(new CmdSkinToFrom(api))
                .build();

        Command cmdSkinToUrl = Command.builder()
                .args(
                        VelocityArgs.player("target"),
                        Arguments.string("url"),
                        Arguments.optional(Arguments.string("slim"))
                )
                .executor(new CmdSkinToUrl(api))
                .build();

        Command cmdSkinToSet = Command.builder()
                .args(
                        VelocityArgs.player("target"),
                        Arguments.string("texture"),
                        Arguments.string("signature")
                )
                .executor(new CmdSkinToSet(api))
                .build();

        Command cmdSkinToReset = Command.builder()
                .args(
                        VelocityArgs.player("target")
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
                        VelocityArgs.player("player"),
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
                        VelocityArgs.player("player")
                )
                .executor(new CmdSkullPlayer(api, sender))
                .build();

        Command cmdSkullUrl = Command.builder()
                .permission("csm.skull.url")
                .args(
                        Arguments.string("url")
                )
                .executor(new CmdSkullUrl(api))
                .build();

        Command cmdSkullToFrom = Command.builder()
                .args(
                        VelocityArgs.player("target"),
                        VelocityArgs.player("username")
                )
                .executor(new CmdSkullToFrom(api, sender))
                .build();

        Command cmdSkullToUrl = Command.builder()
                .args(
                        VelocityArgs.player("target"),
                        Arguments.string("url")
                )
                .executor(new CmdSkullToUrl(sender))
                .build();

        Command cmdSkullTo = Command.builder()
                .permission("csm.skull.to")
                .child(cmdSkullToFrom, "from")
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
