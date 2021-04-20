package no.helponline.Commands.Ban;

import no.helponline.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnbanCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("unban")) {

            UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[0]);
            if (target == null) {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender,false);
                return true;
            }
            OddJob.getInstance().getBanManager().unban(target);
            OddJob.getInstance().getMessageManager().success("Unbanned "+ ChatColor.AQUA+OddJob.getInstance().getPlayerManager().getName(target),commandSender,true);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length == 1) {
            OddJob.getInstance().log("count: " + OddJob.getInstance().getBanManager().getBans().size());
            for (UUID uuid : OddJob.getInstance().getBanManager().getBans()) {
                if (OddJob.getInstance().getPlayerManager().getName(uuid).startsWith(strings[0])) {
                    list.add(OddJob.getInstance().getPlayerManager().getName(uuid));
                }
            }
        }
        return list;
    }
}
