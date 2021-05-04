package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("back")) {
            if (commandSender instanceof Player) {
                if (commandSender.hasPermission("back")) {
                    Player player = (Player) commandSender;
                    OddJob.getInstance().getTeleportManager().back(player);
                }
            }
        }
        return true;
    }
}
