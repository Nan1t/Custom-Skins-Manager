package ru.csm.bukkit.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.storage.Language;
import ru.csm.bukkit.commands.SubCommand;
import ru.csm.bukkit.gui.managers.MenuManager;

public class CommandMenu extends SubCommand {

    private MenuManager manager;
    private Language lang;

    public CommandMenu(MenuManager manager, Language lang){
        super("csm.skin.menu", "/skin menu [page]");
        this.manager = manager;
        this.lang = lang;
    }

    // skin menu [page]

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Command only for players");
            return true;
        }

        Player player = (Player)sender;
        int page = 1;

        if(args.length == 2){
            try{
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                sender.sendMessage(lang.of("menu.page.invalid"));
                return true;
            }
        }

        manager.openMenu(player, page);
        return true;
    }

}
