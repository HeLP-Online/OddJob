package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = null;
        Location spawn = null;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;

            if (can(commandSender,true) && strings.length == 1) {
                try {
                    spawn = Bukkit.getWorld(strings[0]).getSpawnLocation();
                } catch (NullPointerException ex) {
                    OddJob.getInstance().getMessageManager().errorWorld(strings[0], commandSender, Plugin.teleport);
                    return true;
                }
            } else {
                spawn = Bukkit.getWorld("world").getSpawnLocation();
            }
            OddJob.getInstance().getTeleportManager().spawn(player, spawn);
        } else {
            if (can(commandSender,true) && strings.length == 2) {
                try {
                    spawn = Bukkit.getWorld(strings[1]).getSpawnLocation();
                } catch (Exception ex) {
                    OddJob.getInstance().getMessageManager().errorWorld(strings[1], commandSender, Plugin.teleport);
                    return true;
                }
            } else {
                spawn = Bukkit.getWorld("world").getSpawnLocation();
            }
            if (strings.length >= 1) {
                player = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
                if (player == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(Plugin.ban, strings[0], commandSender);
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
        return null;
    }



    @Override
    public String getPermission() {
        return null;
    }

}
