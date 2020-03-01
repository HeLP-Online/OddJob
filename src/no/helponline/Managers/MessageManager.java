package no.helponline.Managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageManager {
    public void success(String message, CommandSender sender, boolean console) {
        sender.sendMessage(ChatColor.GREEN + message);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.GREEN + message);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + message);
        }
    }

    public void success(String message, UUID sender, boolean console) {
        Player player = Bukkit.getPlayer(sender);
        if (player != null) {
            player.sendMessage(ChatColor.GREEN + message);
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.GREEN + message);
        }
    }

    public void warning(String message, CommandSender sender, boolean console) {
        sender.sendMessage(ChatColor.YELLOW + message);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.YELLOW + message);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + message);
        }
    }

    public void warning(String message, UUID sender, boolean console) {
        Player player = Bukkit.getPlayer(sender);
        if (player != null) {
            player.sendMessage(ChatColor.YELLOW + message);
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.YELLOW + message);
        }
    }

    public void danger(String message, CommandSender sender, boolean console) {
        sender.sendMessage(ChatColor.RED + message);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.RED + message);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + message);
        }
    }

    public void danger(String message, UUID sender, boolean console) {
        Player player = Bukkit.getPlayer(sender);
        if (player != null) {
            player.sendMessage(ChatColor.RED + message);
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.RED + message);
        }
    }

    public void console(String s) {
        Bukkit.getConsoleSender().sendMessage(s);
    }

    public void info(String message, CommandSender sender, boolean console) {
        sender.sendMessage(ChatColor.DARK_AQUA + message);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.DARK_AQUA + message);
        } else {
            if (console) Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + message);
        }
    }

    public void info(String message, UUID sender, boolean console) {
        Player player = Bukkit.getPlayer(sender);
        if (player != null) {
            player.sendMessage(ChatColor.DARK_AQUA + message);
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.DARK_AQUA + message);
        }
    }

    public void errorPlayer(String string, CommandSender commandSender) {
        warning("Sorry we can't find the player: " + string, commandSender, false);
    }

    public void errorWorld(String string, CommandSender commandSender) {
        warning("Sorry we can't find the world: " + string, commandSender, false);
    }

    public void broadcastAchievement(String string) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            pl.sendMessage(string);
        }
    }
}
