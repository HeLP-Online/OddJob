package com.spillhuset.Commands.Money.Bank;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Currency;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class BankAddCommand extends SubCommand {
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
        return "add";
    }

    @Override
    public String getDescription() {
        return "Adds an amount to the given bank";
    }

    @Override
    public String getSyntax() {
        return "/currency bank add <bank_guild,bank_player> [name] <amount>";
    }

    @Override
    public String getPermission() {
        return "currency.bank.add";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(4, 5, args, sender, getPlugin())) {
            return;
        }

        Currency account = Currency.valueOf(args[2]);

        String name = "";
        String i = "";
        double amount = 0;
        try {
            if (args.length == 5) {
                name = args[3];
                i = args[4];
                amount = Double.parseDouble(i);
            } else {
                name = sender.getName();
                i = args[3];
                amount = Double.parseDouble(i);
            }
        } catch (NumberFormatException e) {
            OddJob.getInstance().getMessageManager().errorNumber(getPlugin(), i, sender);
        }
        UUID uuid = null;
        switch (account) {
            case bank_player:
                uuid = OddJob.getInstance().getPlayerManager().getUUID(name);
                break;
            case bank_guild:
                uuid = OddJob.getInstance().getGuildManager().getGuildUUIDByName(name);
                break;
            default:
        }

        OddJob.getInstance().getCurrencyManager().addBankBalance(uuid, amount, sender, account);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
