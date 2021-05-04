package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class KillCommand implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1 && command.getName().equalsIgnoreCase("kill")) {
            // COMMAND KILL
            String name = args[0];
            Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(name));
            if (target == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(Plugin.ban, name,sender);
                return true;
            }
            target.setHealth(0D);
            OddJob.getInstance().getMessageManager().killed(name,sender);
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
