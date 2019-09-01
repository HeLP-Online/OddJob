package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class TpAllCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("tpall") && commandSender.hasPermission("essentials.tpall")) {
            // COMMAND TPALL
            if (!(commandSender instanceof Player)) return true;
            Player sender = (Player) commandSender;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.equals(sender)) {
                    OddJob.getInstance().getTeleportManager().teleport(player, sender, 0, PlayerTeleportEvent.TeleportCause.COMMAND);
                    //player.teleport(sender);
                }
                player.sendMessage("Everyone was teleported!");
            }
            //todo permission
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        //TODO
        return null;
    }
}
