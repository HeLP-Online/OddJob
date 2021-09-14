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

public class InvseeCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {


    @Override
    public boolean denyConsole() {
        return true;
    }

    @Override
    public boolean onlyConsole() {
        return false;
    }

    @Override
    public boolean denyOp() {
        return false;
    }

    @Override
    public boolean onlyOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.players;
    }

    @Override
    public String getPermission() {
        return "invsee";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return true;
        }

        if (checkArgs(1, 1, args, sender, getPlugin())) {
            return true;
        }


        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[0], sender);
            return true;
        }

        Player player = (Player) sender;
        player.openInventory(target.getInventory());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOp() || !player.hasPermission("op")) {
                list.add(player.getName());
            }
        }

        return list;
    }
}
