package no.helponline.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class SuicideCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0 && command.getName().equalsIgnoreCase("suicide")) {
            // COMMAND SUICIDE
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                player.setHealth(0D);
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
