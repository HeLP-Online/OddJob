package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpDenyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("tpdeny")) {
            if (!(commandSender instanceof Player)) return true;
            Player player = (Player) commandSender;
            OddJob.getInstance().getTeleportManager().deny(player.getUniqueId());
        }
        return true;
    }
}
