package com.spillhuset.Commands.Currency;

import com.spillhuset.Commands.Currency.Bank.BankCommand;
import com.spillhuset.Commands.Currency.Pocket.PocketCommand;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Currency;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CurrencyCommand implements CommandExecutor, TabCompleter, SubCommandInterface {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public CurrencyCommand() {
        subCommands.add(new PocketCommand());
        subCommands.add(new BankCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                OddJob.getInstance().getMessageManager().errorMissingArgs(Plugin.currency, sender);
                return true;
            } else if (args[0].equalsIgnoreCase("save")) {
                OddJob.getInstance().getCurrencyManager().save();
                return true;
            } else if (args[0].equalsIgnoreCase("load")) {
                OddJob.getInstance().getCurrencyManager().load();
                return true;
            }
        }
        UUID uuid;
        double pocket, bank;
        if (args.length == 0) {
            uuid = ((Player) sender).getUniqueId();
            pocket = OddJob.getInstance().getCurrencyManager().getPocketBalance(uuid);
            bank = OddJob.getInstance().getCurrencyManager().getBankBalance(uuid, Currency.bank_player);
            OddJob.getInstance().getMessageManager().infoCurrencyBalance(uuid, pocket, bank);
            return true;
        }


        StringBuilder nameBuilder = new StringBuilder();
        // Listing SubCommands

        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (name.equalsIgnoreCase(args[0])) {
                subCommand.perform(sender, args);
                return true;
            }
            nameBuilder.append(name).append(",");
        }
        if (sender.hasPermission("currency.others")) {
            nameBuilder.append("[name]");
        } else {
            nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));
        }
        // Fallback
        sender.sendMessage(ChatColor.GOLD + "args: " + ChatColor.RESET + nameBuilder.toString());
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args[0].isEmpty()) {
                list.add(name);
            } else if (name.equalsIgnoreCase(args[0]) && args.length > 1) {
                return subCommand.getTab(sender, args);
            } else if (name.startsWith(args[0])) {
                list.add(name);
            }
        }
        return list;
    }
}
