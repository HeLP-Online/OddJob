package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageManager {
    private static OddJob plugin;

    public MessageManager(OddJob plugin) {
        MessageManager.plugin = plugin;
    }


    public static void success(String message, CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + message);
    }

    public static void success(String message, UUID sender) {
        Bukkit.getPlayer(sender).sendMessage(ChatColor.GREEN + message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + message);
    }

    public static void warning(String message, CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + message);
    }

    public static void warning(String message, UUID sender) {
        Bukkit.getPlayer(sender).sendMessage(ChatColor.YELLOW + message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + message);
    }

    public static void danger(String message, CommandSender sender) {
        sender.sendMessage(ChatColor.RED + message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + message);
    }

    public static void danger(String message, UUID sender) {
        Bukkit.getPlayer(sender).sendMessage(ChatColor.RED + message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + message);
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(message);
    }


    public static void sendMessage(UUID player, String message) {
        Bukkit.getPlayer(player).sendMessage(message);
    }


    public static void console(String s) {
        Bukkit.getConsoleSender().sendMessage(s);
    }
}
