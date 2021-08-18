package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.spillhuset.Utils.Enum.Plugin;

public class TpaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            if (args.length != 1) {
                OddJob.getInstance().getMessageManager().sendSyntax(Plugin.teleport,"/tp request <player>",sender);
                return true;
            }
            Bukkit.dispatchCommand(sender,"/tp request "+args[0]);
        } else {
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.teleport);
        }
        return true;
    }
}
