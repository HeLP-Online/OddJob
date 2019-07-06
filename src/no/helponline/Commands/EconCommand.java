package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EconCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        UUID uuid;
        Double amount;

        if (args.length == 0 && sender instanceof Player) {
            sender.sendMessage(ChatColor.GREEN + "Hey! Your balance is " + OddJob.getInstance().getEconManager().getBalance(((Player) sender).getUniqueId()));
            return true;
        }

        if (args.length == 1 && sender.hasPermission("econ.other")) {
            uuid = OddJob.getInstance().getPlayerManager().getUUID(args[0]);

            if (uuid == null) {
                sender.sendMessage(ChatColor.RED + "We could not find any player with that name");
                return true;
            }

            sender.sendMessage(ChatColor.GREEN + "Balance of " + args[0] + " is " + OddJob.getInstance().getEconManager().getBalance(uuid));
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Hey! You cannot use that command like that!");
            return false;
        }

        if (!sender.hasPermission("econ.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have the right permission to use this command!");
            return true;
        }

        uuid = OddJob.getInstance().getPlayerManager().getUUID(args[1]);

        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + "We could not find any player with that name");
            return true;
        }

        try {
            amount = Double.parseDouble(args[2]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount");
            return true;
        }

        if (OddJob.getInstance().getEconManager().hasAccount(uuid)) {
            sender.sendMessage(ChatColor.RED + args[1] + " does not own an account");
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            OddJob.getInstance().getEconManager().setBalance(uuid, OddJob.getInstance().getEconManager().getBalance(uuid) + amount);
            OddJob.getInstance().log("Added balance to " + args[1] + ", " + args[2] + ", sum: " + OddJob.getInstance().getEconManager().getBalance(uuid));
        } else if (args[0].equalsIgnoreCase("set")) {
            OddJob.getInstance().getEconManager().setBalance(uuid, amount);
            OddJob.getInstance().log("Balance for " + args[1] + " set to " + args[2]);
        } else if (args[0].equalsIgnoreCase("remove")) {
            OddJob.getInstance().getEconManager().setBalance(uuid, OddJob.getInstance().getEconManager().getBalance(uuid) - amount);
            OddJob.getInstance().log("Subtracted balance for " + args[1] + ", " + args[2] + ", sum: " + OddJob.getInstance().getEconManager().getBalance(uuid));
        } else {
            sender.sendMessage(ChatColor.RED + "Incorrect argument");
            OddJob.getInstance().log("Incorrect argument!");
        }
        return true;
    }
}
