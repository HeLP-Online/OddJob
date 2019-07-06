package no.helponline.Managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageManager {
    public void success(String message, CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + message);
    }

    public void success(String message, UUID sender) {
        Bukkit.getPlayer(sender).sendMessage(ChatColor.GREEN + message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + message);
    }

    public void warning(String message, CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + message);
    }

    public void warning(String message, UUID sender) {
        Bukkit.getPlayer(sender).sendMessage(ChatColor.YELLOW + message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + message);
    }

    public void danger(String message, CommandSender sender) {
        sender.sendMessage(ChatColor.RED + message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + message);
    }

    public void danger(String message, UUID sender) {
        Bukkit.getPlayer(sender).sendMessage(ChatColor.RED + message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + message);
    }

    public void sendMessage(Player player, String message) {
        player.sendMessage(message);
    }


    public void sendMessage(UUID player, String message) {
        Bukkit.getPlayer(player).sendMessage(message);
    }


    public void console(String s) {
        Bukkit.getConsoleSender().sendMessage(s);
    }
}
