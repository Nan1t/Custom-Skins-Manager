package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.npc.NPC;
import ru.csm.bukkit.npc.Npcs;
import ru.csm.bukkit.util.BukkitTasks;

import java.util.Arrays;
import java.util.List;

public class CommandSkinTo extends Command {

    private final SkinsAPI<Player> api;

    public CommandSkinTo(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            SkinPlayer<Player> skinPlayer = api.getPlayer(player.getUniqueId());
            NPC npc = Npcs.create();

            if (npc != null){
                npc.setLocation(player.getLocation());
                npc.setName(" ");
                npc.setDisplayName(Arrays.asList(
                        "&aRMB - Deny",
                        "&aLMB - Apply"
                ));
                npc.setSkin(skinPlayer.getCustomSkin());
                npc.spawn(player);

                BukkitTasks.runTaskLater(()->npc.destroy(player), 60);
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return null;
    }
}
