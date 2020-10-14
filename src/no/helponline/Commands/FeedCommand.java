package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FeedCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player target = null;
        if (strings.length == 1) {
            target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target == null || !target.isOnline()) {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender, false);
                return true;
            }
        }

        if ((commandSender instanceof Player) && strings.length == 0) {
            target = (Player) commandSender;
        }

        if (target != null) {
            target.setFoodLevel(20);
            if (commandSender instanceof Player && !commandSender.equals(target)) {
                OddJob.getInstance().getMessageManager().success(target.getName() + " has been feed.", commandSender, true);
            }
            OddJob.getInstance().getMessageManager().success("You have been feed", target.getUniqueId(), false);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (strings.length == 1) {
                if (player.getName().startsWith(strings[0])) {
                    list.add(player.getName());
                }
            } else if (strings.length == 0) {
                list.add(player.getName());
            }
        }
        return list;
    }
}
