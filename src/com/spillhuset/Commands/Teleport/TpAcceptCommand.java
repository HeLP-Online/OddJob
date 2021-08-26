package com.spillhuset.Commands.Teleport;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAcceptCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            if (args.length != 1) {
                Bukkit.dispatchCommand(sender, "teleport accept");
            } else {
                Bukkit.dispatchCommand(sender, "teleport accept " + args[0]);
            }
        } else {
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.teleport);
        }
        return true;
    }
}
