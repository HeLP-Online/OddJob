package com.spillhuset.Commands.Ban;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnbanCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("unban")) {

            String name = args[0];
            UUID target = OddJob.getInstance().getPlayerManager().getUUID(name);
            if (target == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(Plugin.ban,name,sender);
                return true;
            }
            OddJob.getInstance().getBanManager().unban(target);
            OddJob.getInstance().getMessageManager().unbanned(OddJob.getInstance().getPlayerManager().getName(target),sender,true);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length == 1) {
            OddJob.getInstance().log("count: " + OddJob.getInstance().getBanManager().getBans().size());
            for (UUID uuid : OddJob.getInstance().getBanManager().getBans()) {
                if (OddJob.getInstance().getPlayerManager().getName(uuid).startsWith(strings[0])) {
                    list.add(OddJob.getInstance().getPlayerManager().getName(uuid));
                }
            }
        }
        return list;
    }
}
