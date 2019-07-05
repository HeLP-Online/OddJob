package no.helponline.Commands;

import no.helponline.Managers.MessageManager;
import no.helponline.Managers.PlayerManager;
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

public class EssentialsCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0 && command.getName().equalsIgnoreCase("suicide")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                player.setHealth(0D);
            }
            //TODO permissions
        } else if (strings.length == 1 && command.getName().equalsIgnoreCase("kill")) {
            Player target = PlayerManager.getPlayer(PlayerManager.getUUID(strings[0]));
            if (target != null) {
                target.setHealth(0D);
                OddJob.getInstance().log(commandSender.getName() + " killed " + strings[0]);
            } else {
                MessageManager.warning("Sorry, we can't find " + strings[0], commandSender);
            }
            //TODO permissions
        } else if (strings.length > 0 && command.getName().equalsIgnoreCase("tp")) {
            if (strings.length == 2) {
                Player target = PlayerManager.getPlayer(PlayerManager.getUUID(strings[1]));
                if (target == null) {
                    MessageManager.warning("Sorry, we can't find " + strings[1], commandSender);
                    return true;
                }
                Player destination = PlayerManager.getPlayer(PlayerManager.getUUID(strings[0]));
                if (destination == null) {
                    MessageManager.warning("Sorry, we can't find " + strings[0], commandSender);
                    return true;
                }

                target.teleport(destination.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                MessageManager.success("You have been teleported to " + destination.getName(), target.getUniqueId());
            } else if (strings.length == 4 || strings.length == 5) {
                Player player = PlayerManager.getPlayer(PlayerManager.getUUID(strings[0]));
                if (player == null) {
                    MessageManager.warning("Sorry, we can't find " + strings[0], commandSender);
                    return true;
                }
                int x = Integer.parseInt(strings[1]);
                int y = Integer.parseInt(strings[2]);
                int z = Integer.parseInt(strings[3]);
                World world = (strings.length == 5) ? Bukkit.getWorld(strings[4]) : player.getWorld();

                player.teleport(new Location(world, x, y, z), PlayerTeleportEvent.TeleportCause.COMMAND);
                MessageManager.success("You have been teleported to a specific location", player.getUniqueId());
            } else if (commandSender instanceof Player && strings.length == 1) {
                Player target = (Player) commandSender;
                Player destination = PlayerManager.getPlayer(PlayerManager.getUUID(strings[0]));
                if (destination == null) {
                    MessageManager.warning("Sorry, we can't find " + strings[0], commandSender);
                    return true;
                }
                target.teleport(destination.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                MessageManager.success("You have been teleported to " + destination.getName(), target.getUniqueId());
            }
            //TODO permissions
        } else if (command.getName().equalsIgnoreCase("clear")) {
            Player target = null;
            if (strings.length == 1) {
                target = PlayerManager.getPlayer(PlayerManager.getUUID(strings[0]));
                if (target == null) {
                    MessageManager.warning("Sorry, we can't find " + strings[0], commandSender);
                    return true;
                }
            } else if (strings.length == 0 && commandSender instanceof Player) {
                target = (Player) commandSender;
            }
            if (target != null) {
                target.getInventory().clear();
            }
        }
        //TODO
        //tpa,tphere,tpall,clear,invsee,kick,ban,unban
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
