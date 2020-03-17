package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

    public void broadcast(String s) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(s);
        }
    }

    public void guild(String s, UUID guild) {
        for (UUID uuid : OddJob.getInstance().getGuildManager().getGuildMembers(guild)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.sendMessage(s);
            }
        }
    }

    public void errorGuild(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.YELLOW+"Sorry, you are not associated with any guild yet.");
    }

    public void insufficientItems(Player player) {
        player.sendMessage(ChatColor.YELLOW+"Insufficient number of items.");
    }

    public void insufficientFunds(Player player) {
        player.sendMessage(ChatColor.YELLOW+"Insufficient funds.");
    }

    public void errorConsole() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Onl usable as a player");
    }

    public void errorMaterial(String string, Player player) {
        player.sendMessage(ChatColor.RED+"Unknown material '"+ChatColor.YELLOW+string+ChatColor.RED+"'");
    }

    public void errorNumber(String string, Player player) {
        player.sendMessage(ChatColor.RED+"Invalid number '"+ChatColor.YELLOW+string+ChatColor.RED+"'");
    }

    public void errorHome(String name, CommandSender player) {
        player.sendMessage(ChatColor.RED+"Unknown home '"+ChatColor.YELLOW+name+ChatColor.RED+"'");
    }

    public void infoListPlayers(String string, Collection<UUID> list, CommandSender commandSender) {
        StringBuilder builder = new StringBuilder();
        builder.append(string).append("\n");
        builder.append("---------------------\n");
        int i = 0;
        for (UUID uuid : list) {
            builder.append(i).append(".) ").append(OddJob.getInstance().getPlayerManager().getName(uuid)).append("\n");
        }
        commandSender.sendMessage(builder.toString());
    }

    public void infoHashmap(String string, HashMap<String, String> info, CommandSender commandSender) {
        StringBuilder builder = new StringBuilder();
        builder.append(string).append("\n");
        builder.append("---------------------\n");
        for (String st : info.keySet()) {
            builder.append(st).append(" ").append(info.get(st)).append("\n");
        }
        commandSender.sendMessage(builder.toString());
    }
}
