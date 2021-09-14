package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnCommand extends SubCommandInterface implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = null;
        Location spawn = null;
        if (sender instanceof Player) {
            player = (Player) sender;

            if (can(sender, true) && args.length == 1) {
                try {
                    spawn = Bukkit.getWorld(args[0]).getSpawnLocation();
                } catch (NullPointerException ex) {
                    OddJob.getInstance().getMessageManager().errorWorld(args[0], sender, getPlugin());
                    return true;
                }
            } else {
                spawn = Bukkit.getWorld("world").getSpawnLocation();
            }
            OddJob.getInstance().getTeleportManager().spawn(player, spawn);
        } else {
            if (can(sender, true) && args.length == 2) {
                try {
                    spawn = Bukkit.getWorld(args[1]).getSpawnLocation();
                } catch (Exception ex) {
                    OddJob.getInstance().getMessageManager().errorWorld(args[1], sender, getPlugin());
                    return true;
                }
            } else {
                spawn = Bukkit.getWorld("world").getSpawnLocation();
            }
            if (args.length >= 1) {
                player = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(args[0]));
                if (player == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[0], sender);
                    return true;
                }

                OddJob.getInstance().getTeleportManager().spawn(player, spawn);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return null;
    }

    @Override
    public boolean denyConsole() {
        return false;
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
        return "spawn";
    }

}
