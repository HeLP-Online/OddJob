package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = null;
        Location spawn = null;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;

            if (strings.length == 1) {
                try {
                    spawn = Bukkit.getWorld(strings[0]).getSpawnLocation();
                } catch (NullPointerException ex) {
                    OddJob.getInstance().getMessageManager().errorWorld(strings[0], commandSender);
                    return true;
                }
            } else {
                spawn = Bukkit.getWorld("world").getSpawnLocation();
            }
            OddJob.getInstance().getTeleportManager().spawn(player, spawn);
        } else {
            if (strings.length == 2) {
                try {
                    spawn = Bukkit.getWorld(strings[1]).getSpawnLocation();
                } catch (Exception ex) {
                    OddJob.getInstance().getMessageManager().errorWorld(strings[1], commandSender);
                    return true;
                }
            } else {
                spawn = Bukkit.getWorld("world").getSpawnLocation();
            }
            if (strings.length >= 1) {
                player = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
                if (player == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(strings[0], commandSender);
                    return true;
                }

                OddJob.getInstance().getTeleportManager().spawn(player, spawn);
            }
        }
        return true;
    }

}
