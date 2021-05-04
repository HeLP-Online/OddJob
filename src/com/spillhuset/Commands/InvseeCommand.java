package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class InvseeCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("invsee")) {
            // COMMAND INVSEE
            if (!(commandSender instanceof Player)) return true;
            Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(Plugin.invsee,strings[0],commandSender);
                return true;
            }
            Player player = (Player) commandSender;
            player.openInventory(target.getInventory());
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
