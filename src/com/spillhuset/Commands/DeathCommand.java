package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeathCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        World world = null;
        if (strings.length == 1) {
            try {
                world = Bukkit.getWorld(strings[0]);
            } catch (NullPointerException ex) {
                OddJob.getInstance().getMessageManager().errorWorld(strings[0],commandSender, Plugin.tp);
            }
        }
        int i = OddJob.getInstance().getDeathManager().cleanUp(world);
        OddJob.getInstance().getMessageManager().console("Replaced " + i + " chests");
        return true;
    }
}
