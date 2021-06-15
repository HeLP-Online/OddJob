package com.spillhuset.Commands.Money.Bank;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Currency;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class BankSubCommand extends SubCommand {
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
        return "sub";
    }

    @Override
    public String getDescription() {
        return "Subtracts an amount from player/guild bank";
    }

    @Override
    public String getSyntax() {
        return "/currency bank subtract <player/guild> <name> <amount>";
    }

    @Override
    public String getPermission() {
        return "bank.sub";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(5, 5, args, sender, getPlugin())) {
            return;
        }

        UUID target;
        Currency account = Currency.valueOf(args[2]);
        switch (account) {
            case bank_player -> target = OddJob.getInstance().getPlayerManager().getUUID(args[3]);
            case bank_guild -> target = OddJob.getInstance().getGuildManager().getGuildUUIDByName(args[3]);
            default -> {
                OddJob.getInstance().getMessageManager().cannotIdentify(args[3], args[2], getPlugin(), sender);
                return;
            }
        }

        double amount;
        try {
            amount = Double.parseDouble(args[4]);
        } catch (NumberFormatException e) {
            OddJob.getInstance().getMessageManager().invalidNumber(getPlugin(), args[4], sender);
            return;
        }

        if (OddJob.getInstance().getCurrencyManager().subtractBankBalance(target, amount, sender.hasPermission("currency.negative"), account)) {
            OddJob.getInstance().getMessageManager().currencyChanged(account, amount, OddJob.getInstance().getCurrencyManager().getPocketBalance(target), target, sender);
        } else {
            OddJob.getInstance().getMessageManager().insufficientFunds(target,sender);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
