package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnbanCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("unban")) {

            Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target == null) {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                return true;
            }

            OddJob.getInstance().getBanManager().unban(target.getUniqueId());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length <= 1)
            for (UUID uuid : OddJob.getInstance().getBanManager().getBans().keySet()) {
                if (strings.length == 1) {
                    if (OddJob.getInstance().getPlayerManager().getPlayer(uuid).getName().startsWith(strings[0])) {
                        list.add(OddJob.getInstance().getPlayerManager().getPlayer(uuid).getName());
                    }
                } else {
                    list.add(OddJob.getInstance().getPlayerManager().getPlayer(uuid).getName());
                }
            }
        return list;
    }
}
