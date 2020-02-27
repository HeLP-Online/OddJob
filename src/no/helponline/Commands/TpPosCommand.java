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

public class TpPosCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 4 || strings.length == 5) {
            Player player = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (player == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(strings[0], commandSender);
                return true;
            }
            // tp <player> <x> <y> <z> <world>
            World world;
            if (strings.length == 5) {
                world = Bukkit.getWorld(strings[4]);
                if (world == null) {
                    OddJob.getInstance().getMessageManager().errorWorld(strings[4],commandSender);
                    return true;
                }
            } else {
                world = player.getWorld();
            }
            OddJob.getInstance().getTeleportManager().teleport(commandSender, strings[0], strings[1], strings[2], strings[3], world);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
