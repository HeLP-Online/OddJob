package com.spillhuset.Commands.Currency.Pocket;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Account;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PocketSubCommand extends SubCommand {
    @Override
    public String getName() {
        return "sub";
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
        return "currency.pocket.sub";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length != 4) {
            OddJob.getInstance().getMessageManager().errorMissingArgs(Plugin.currency,sender);
            OddJob.getInstance().getMessageManager().sendSyntax(Plugin.warp, getSyntax(), sender);
            sender.sendMessage(ChatColor.GOLD + "args: " + ChatColor.RESET + "<player> <amount>");
            return;
        }
        UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[2]);
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.ban, args[2], sender);
            return;
        }
        double amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (Exception e) {
            OddJob.getInstance().getMessageManager().invalidNumber(Plugin.currency,args[3],sender);
            return;
        }
        if (OddJob.getInstance().getCurrencyManager().getPocketBalance(target) <= amount) {
            OddJob.getInstance().getCurrencyManager().setPocketBalance(target, 0);
        } else {
            OddJob.getInstance().getCurrencyManager().subtractPocketBalance(target, amount);
        }
        double balance = OddJob.getInstance().getCurrencyManager().getPocketBalance(target);
        OddJob.getInstance().getMessageManager().currencySuccessSubtracted(args[2], args[3], balance, sender, Account.POCKET);
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
