package com.spillhuset.Commands.Currency.Bank;
import com.spillhuset.Commands.Currency.Pocket.PocketAddCommand;
import com.spillhuset.Commands.Currency.Pocket.PocketSetCommand;
import com.spillhuset.Commands.Currency.Pocket.PocketSubCommand;
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

    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
