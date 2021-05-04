package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FreezeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("freeze")) {
            if (strings.length == 1) {
                Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
                if (target == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(Plugin.freeze, strings[0], commandSender);
                    return true;
                }
                if (OddJob.getInstance().getFreezeManager().get(target.getUniqueId()) != null) {
                    OddJob.getInstance().getFreezeManager().del(target.getUniqueId());
                    OddJob.getInstance().getMessageManager().frozenPlayer(target.getName(), commandSender);
                    OddJob.getInstance().getMessageManager().frozenTarget(target.getUniqueId());
                    return true;
                }
                OddJob.getInstance().getFreezeManager().add(target.getUniqueId(), target.getLocation());
                OddJob.getInstance().getMessageManager().unfreezePlayer(target.getName(), commandSender);
                OddJob.getInstance().getMessageManager().unfreezeTarget(target.getUniqueId());
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getName().equals(commandSender.getName()) && !player.isOp()) {
                if (strings.length == 1) {
                    if (player.getName().toLowerCase().startsWith(strings[0].toLowerCase())) {
                        list.add(player.getName());
                    }
                } else {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}
