package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OpCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    @Override
    public boolean allowOp() {
        return false;
    }

    @Override
    public boolean allowConsole() {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.op;
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (checkArgs(1, 1, args, sender, getPlugin())) {
            return true;
        }

        boolean self = false;
        Player player = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(args[0]));
        if (player == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[0], sender);
            return true;
        }
        if (sender.getName().equals(player.getName())) {
            self = true;
        }
        if (can(sender, false)) {
            player.setOp(true);
            OddJob.getInstance().getMessageManager().opSet(player, sender, self);
        } else {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) continue;
            if (player.equals(sender)) continue;
            list.add(player.getName());
        }
        return list;
    }
}
