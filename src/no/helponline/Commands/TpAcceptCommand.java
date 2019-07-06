package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAcceptCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("tpaccept")) {
            if (!(commandSender instanceof Player)) return true;
            Player player = (Player) commandSender;
            OddJob.getInstance().getTeleportManager().accept(player.getUniqueId());
        }
        return true;
    }
}
