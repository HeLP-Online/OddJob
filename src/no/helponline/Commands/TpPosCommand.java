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
        if (command.getName().equalsIgnoreCase("tppos")) {
            if (!(commandSender instanceof Player)) return true;
            World world = null;
            Player player = (Player) commandSender;
            if (strings.length == 4) {
                world = Bukkit.getWorld(strings[3]);
            }
            if (world == null) {
                world = player.getWorld();
            }

            try {
                int x = Integer.parseInt(strings[0]);
                int y = Integer.parseInt(strings[1]);
                int z = Integer.parseInt(strings[2]);

                OddJob.getInstance().getTeleportManager().teleport(player, new Location(world, x, y, z), 0, PlayerTeleportEvent.TeleportCause.COMMAND);
            } catch (Exception e) {
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
