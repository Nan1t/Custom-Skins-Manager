package ru.csm.bukkit.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.csm.api.player.Skin;
import ru.csm.api.storage.Language;
import ru.csm.api.utils.Colors;
import ru.csm.bukkit.commands.SubCommand;
import ru.csm.bukkit.protocol.NPCService;
import ru.csm.bukkit.protocol.npc.NPC;

import java.util.List;
import java.util.UUID;

public class CommandPreview extends SubCommand {

    private NPCService npcService;
    private Language lang;

    public CommandPreview(NPCService npcService, Language lang){
        super("csm.admin", "/skin preview <player> <texture> <signature>");
        this.npcService = npcService;
        this.lang = lang;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length == 4){
            Player player = Bukkit.getPlayer(args[1]);

            if(player == null || !player.isOnline()){
                sender.sendMessage(Colors.of("&cPlayer is not online"));
                return true;
            }

            NPC prevNPC = npcService.getNPC(player.getUniqueId());

            if(prevNPC != null) prevNPC.destroy();

            Skin skin = new Skin(args[2], args[3]);
            NPC npc = npcService.createNPC(UUID.randomUUID(), "");
            npc.setSkin(skin);
            npc.setOpenMenu(false);

            Location loc = player.getLocation().clone();
            Vector modify = player.getLocation().getDirection().normalize().multiply(2);
            loc.add(modify);
            loc.setY(player.getLocation().getY());
            loc.setPitch(0);
            loc.setYaw(player.getLocation().getYaw()+180);

            npc.setLocation(loc);

            List<String> customName = Colors.ofArr(lang.ofArray("npc.name"));
            npc.setCustomName(customName);

            player.closeInventory();
            npc.spawn(player);

            npcService.addNPC(player.getUniqueId(), npc);
            return true;
        }
        return false;
    }

}
