package com.spillhuset.Commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpawnMobCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player target = Bukkit.getPlayer(strings[0]);
        World world = target.getWorld();
        EntityType entityType = EntityType.valueOf(strings[1]);
        Entity entity = world.spawnEntity(target.getLocation(), entityType);

        return true;
    }
}
