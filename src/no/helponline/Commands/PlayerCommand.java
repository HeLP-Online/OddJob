package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class PlayerCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("player")) {
            if (strings.length == 0) {
                Bukkit.dispatchCommand(commandSender, command.getName() + " help");
            }
            if (strings[0].equalsIgnoreCase("help")) {
                OddJob.getInstance().log("HELP");
            } else if (strings[0].equalsIgnoreCase("list")) {
                List<String> list = OddJob.getInstance().getPlayerManager().listPlayers();
                OddJob.getInstance().getMessageManager().console(Arrays.toString(list.toArray()));
            } else if (strings[0].equalsIgnoreCase("info")) {

            } else if (strings[0].equalsIgnoreCase("set")) {

            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
