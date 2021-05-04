package com.spillhuset.Commands;

import com.spillhuset.Utils.Rollback;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RollbackCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("backup")) {
            World world;
            if (strings.length == 0 && commandSender instanceof Player) {
                Player player = (Player) commandSender;
                world = player.getWorld();
            } else {
                world = Bukkit.getWorld(strings[0]);
            }
            if (world != null) {
                Rollback.backup(world);
            }
        } else if (command.getName().equalsIgnoreCase("rollback")) {
            World world;
            if (strings.length == 0 && commandSender instanceof Player) {
                Player player = (Player) commandSender;
                world = player.getWorld();
            } else {
                world = Bukkit.getWorld(strings[0]);
            }
            if (world != null) {
                Rollback.rollback(world);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length == 0 || strings.length == 1) {
            for (World world : Bukkit.getWorlds()) {
                if (strings.length == 1 && world.getName().toLowerCase().startsWith(strings[0].toLowerCase())) {
                    list.add(world.getName());
                }
            }
        }
        return null;
    }
}
