package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class TpCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length > 0 && command.getName().equalsIgnoreCase("tp")) {
            // COMMAND TP
            if (strings.length == 2) {
                Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[1]));
                if (target == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender);
                    return true;
                }
                Player destination = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
                if (destination == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                    return true;
                }

                target.teleport(destination.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                OddJob.getInstance().getMessageManager().success("You have been teleported to " + destination.getName(), target.getUniqueId());
            } else if (strings.length == 4 || strings.length == 5) {
                Player player = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
                if (player == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                    return true;
                }
                int x = Integer.parseInt(strings[1]);
                int y = Integer.parseInt(strings[2]);
                int z = Integer.parseInt(strings[3]);
                World world = (strings.length == 5) ? Bukkit.getWorld(strings[4]) : player.getWorld();

                player.teleport(new Location(world, x, y, z), PlayerTeleportEvent.TeleportCause.COMMAND);
                OddJob.getInstance().getMessageManager().success("You have been teleported to a specific location", player.getUniqueId());
            } else if (commandSender instanceof Player && strings.length == 1) {
                Player target = (Player) commandSender;
                Player destination = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
                if (destination == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                    return true;
                }
                target.teleport(destination.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                OddJob.getInstance().getMessageManager().success("You have been teleported to " + destination.getName(), target.getUniqueId());
            }
            //TODO permissions
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        //TODO
        return null;
    }
}
