package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MapCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage("You'll find our map at "+ OddJob.getInstance().getConfig().getString("Map.Address"));
        return true;
    }
}
