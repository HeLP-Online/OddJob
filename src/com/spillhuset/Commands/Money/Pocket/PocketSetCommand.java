package com.spillhuset.Commands.Money.Pocket;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Currency;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PocketSetCommand extends SubCommand {
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
        return "set";
    }

    @Override
    public String getDescription() {
        return "Sets a value to a players pocket";
    }

    @Override
    public String getSyntax() {
        return "/currency pocket set <player> <amount>";
    }

    @Override
    public String getPermission() {
        return "currency.pocket.set";
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

        OddJob.getInstance().getCurrencyManager().setPocketBalance(target, amount);
        OddJob.getInstance().getMessageManager().currencySuccessSet(args[2], args[3], sender, Currency.pocket);
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
