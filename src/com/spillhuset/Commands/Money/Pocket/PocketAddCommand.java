package com.spillhuset.Commands.Money.Pocket;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Types;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PocketAddCommand extends SubCommand {
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
        return "Adds an amount to the players pocket";
    }

    @Override
    public String getSyntax() {
        return "/currency pocket add <player> <amount>";
    }

    public String getPermission() {
        return "currency.pocket.add";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(4, 4, args, sender, getPlugin())) {
            return;
        }

        UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[2]);
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[2], sender);
            return;
        }
        double amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (Exception e) {
            OddJob.getInstance().getMessageManager().invalidNumber(getPlugin(), args[3], sender);
            return;
        }
        OddJob.getInstance().getCurrencyManager().add(target, amount, Types.AccountType.pocket);
        double balance = OddJob.getInstance().getCurrencyManager().get(target).get(Types.AccountType.pocket);
        OddJob.getInstance().getMessageManager().currencySuccessAdded(args[2], args[3], balance, sender, Types.AccountType.pocket);
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
