package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class HealCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player target = null;
        if (strings.length == 1) {
            target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target == null || !target.isOnline()) {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                return true;
            }
        }

        if (!(commandSender instanceof Player) && strings.length == 0) target = (Player) commandSender;

        if (target != null) {
            target.setHealth(20);
            if (commandSender instanceof Player && !commandSender.equals(target)) {
                OddJob.getInstance().getMessageManager().success(target.getName() + " has been healed.", commandSender);
            }
            OddJob.getInstance().getMessageManager().success("You have been healed", target.getUniqueId());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
