package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WorldCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("world")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (strings[0].equalsIgnoreCase("set")) {
                    if (strings[1].equalsIgnoreCase("spawn")) {
                        OddJob.getInstance().getWorldManager().setSpawn(player);
                        player.sendMessage("Spawn point set!");
                        return true;
                    } else if (strings[1].equalsIgnoreCase("gamemode")) {
                        OddJob.getInstance().getWorldManager().setGameMode(player, strings[2]);
                        player.sendMessage("Gamemode for " + player.getWorld().getName() + " set to " + strings[2]);
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("warp") || command.getName().equalsIgnoreCase("delwarp")) {
            if (strings.length == 0 || strings.length == 1) {
                List<String> l = OddJob.getInstance().getWarpManager().listWarps();
                if (strings.length == 1) {
                    for (String st : l) {
                        if (st.startsWith(strings[0])) {
                            list.add(st);
                        }
                    }
                }
            }
        }
        return list;
    }
}
