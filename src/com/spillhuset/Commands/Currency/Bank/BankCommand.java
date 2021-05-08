package com.spillhuset.Commands.Currency.Bank;
import com.spillhuset.Commands.Currency.Pocket.PocketAddCommand;
import com.spillhuset.Commands.Currency.Pocket.PocketSetCommand;
import com.spillhuset.Commands.Currency.Pocket.PocketSubCommand;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class BankCommand extends SubCommand {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public BankCommand() {
        subCommands.add(new BankAddCommand());
        subCommands.add(new BankDelCommand());
        subCommands.add(new BankSetCommand());
        subCommands.add(new BankInvestCommand());
        subCommands.add(new BankDepositCommand());
        subCommands.add(new BankWithdrawCommand());
    }

    @Override
    public boolean allowConsole() {
        return false;
    }

    @Override
    public boolean allowOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.currency;
    }

    @Override
    public String getName() {
        return "bank";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args.length >= 2) {
                if (name.equalsIgnoreCase(args[1])) {
                    subCommand.perform(sender, args);
                    return;
                }
            }
            nameBuilder.append(name).append(",");
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args[1].isEmpty()) {
                list.add(name);
            } else if (name.equalsIgnoreCase(args[1]) && args.length > 2) {
                return subCommand.getTab(sender, args);
            } else if (name.startsWith(args[1])) {
                list.add(name);
            }
        }
        return list;
    }
}
