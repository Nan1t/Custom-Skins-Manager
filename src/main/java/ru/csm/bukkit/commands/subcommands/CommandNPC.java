package ru.csm.bukkit.commands.subcommands;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.csm.api.player.SkinModel;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.api.utils.text.Colors;
import ru.csm.bukkit.Skins;
import ru.csm.bukkit.commands.SubCommand;
import ru.csm.bukkit.player.CitizensSkinPlayer;

public class CommandNPC extends SubCommand {

    private Plugin plugin;
    private SkinsAPI api;
    private Language lang;

    public CommandNPC(Plugin plugin, SkinsAPI api){
        super("csm.skin.npc","/skin npc <npc> <url> [slim]");
        this.plugin = plugin;
        this.api = api;
        this.lang = api.getLang();
    }

    // skin npc <url> [slim]

    @Override
    public boolean execute(CommandSender sender, String[] args){
        if(!Skins.isEnabledCitizens()){
            sender.sendMessage(Colors.of("&cCitizens is not enabled"));
            return true;
        }

        if(!(sender instanceof Player)){
            sender.sendMessage("This command only for players");
            return true;
        }

        NPC selectedNPC = CitizensAPI.getDefaultNPCSelector().getSelected(sender);

        if(selectedNPC == null){
            sender.sendMessage(lang.of("npc.not_selected"));
            return true;
        }

        if(!(selectedNPC.getEntity() instanceof SkinnableEntity)){
            sender.sendMessage(lang.of("npc.skin.impossible"));
            return true;
        }

        if(args.length >= 2){
            String url = args[1];
            SkinModel model = SkinModel.STEVE;

            if(args.length == 3){
                if(args[2].equalsIgnoreCase("slim")){
                    model = SkinModel.ALEX;
                }
            }

            CitizensSkinPlayer skinPlayer = new CitizensSkinPlayer(plugin, (Player)sender, selectedNPC);
            api.setSkinFromImage(skinPlayer, url, model);
        }

        return true;
    }

}
