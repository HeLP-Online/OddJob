package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class KickCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("kick")) {
            // COMMAND KICK
            String message = OddJob.getInstance().getConfig().getString("default.kick_message");
            if (strings.length >= 2) {
                int length = strings.length;
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= length - 1; i++) {
                    sb.append(strings[i]);
                }
                message = sb.toString();
            }
            Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target == null || !target.isOnline()) {
                OddJob.getInstance().getMessageManager().errorPlayer(Plugin.kick,strings[0],commandSender);
                return true;
            }
            OddJob.getInstance().getBanManager().kick(target, message);
            OddJob.getInstance().getMessageManager().kickPlayer(target.getName(),message,commandSender);
            // todo permission
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        // TODO
        return null;
    }
}
