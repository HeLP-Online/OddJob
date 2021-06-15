package com.spillhuset.Commands.Money.Pocket;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Currency;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PocketSubCommand extends SubCommand {
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
        return "Subtract an amount from players pocket";
    }

    @Override
    public String getSyntax() {
        return "/currency pocket subtract <name> <value>";
    }

    @Override
    public String getPermission() {
        return "currency.pocket.sub";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(4,4,args,sender,Plugin.currency)){
            return;
        }

        UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[2]);
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.currency, args[2], sender);
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            OddJob.getInstance().getMessageManager().invalidNumber(Plugin.currency, args[3], sender);
            return;
        }
        if (OddJob.getInstance().getCurrencyManager().subtractPocketBalance(target, amount, sender.hasPermission("currency.negative"))) {
            OddJob.getInstance().getMessageManager().currencyChanged(Currency.pocket, amount, OddJob.getInstance().getCurrencyManager().getPocketBalance(target), target,sender);
        } else {
            OddJob.getInstance().getMessageManager().insufficientFunds(target, sender);
        }


        double balance = OddJob.getInstance().getCurrencyManager().getPocketBalance(target);
        OddJob.getInstance().getMessageManager().currencySuccessSubtracted(args[2], args[3], balance, sender, Currency.pocket);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 3) {
            for (String name : OddJob.getInstance().getPlayerManager().getNames()) {
                if (args[2].isEmpty()) {
                    list.add(name);
                } else if (name.startsWith(args[2])) {
                    list.add(name);
                }
            }
        }
        return list;
    }
}
