package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class KillCommand implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1 && command.getName().equalsIgnoreCase("kill")) {
            // COMMAND KILL
            Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target != null) {
                target.setHealth(0D);
                OddJob.getInstance().getMessageManager().success("Killed "+ ChatColor.AQUA + strings[0],commandSender,true);
            } else {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender,false);
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
