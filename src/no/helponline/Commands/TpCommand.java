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

public class TpCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 2) {
            OddJob.getInstance().getTeleportManager().teleport(commandSender, strings[0], strings[1]);
        } else if (strings.length == 1) {
            OddJob.getInstance().getTeleportManager().teleport(commandSender, strings[0]);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length <= 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (strings.length == 1) {
                    if (player.getName().startsWith(strings[0])) {
                        list.add(player.getName());
                    }
                } else {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}
