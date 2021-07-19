package com.spillhuset.Commands.Money.Bank;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Types.BankType;
import com.spillhuset.Utils.Enum.Types.AccountType;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankDepositCommand extends com.spillhuset.Utils.SubCommand {
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
        return "deposit";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/currency bank deposit <guild/player> <amount>";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(5,5,args,sender, Plugin.currency)) {
            return;
        }

        BankType account = null;
        double value = 0d;
        UUID target = null;

        try {
            account = BankType.valueOf(args[2]);
            value = Double.parseDouble(args[4]);
        } catch (NumberFormatException e){
            OddJob.getInstance().getMessageManager().invalidNumber(Plugin.currency,args[4],sender);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (account == null) {
            return;
        }

        switch (account) {
            case player -> {
                target = OddJob.getInstance().getPlayerManager().getUUID(args[3]);
                if (target == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(Plugin.player, args[3], sender);
                    return;
                }
            }
            case guild -> {
                target = OddJob.getInstance().getGuildManager().getGuildUUIDByName(args[3]);
                if (target == null) {
                    OddJob.getInstance().getMessageManager().errorGuild(args[3], sender);
                    return;
                }
            }
        }

        OddJob.getInstance().getCurrencyManager().subtract(target,value,sender.hasPermission("currency.negative"),AccountType.pocket);
        OddJob.getInstance().getCurrencyManager().add(target,value,AccountType.bank);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 3) {
            for (BankType account : BankType.values()) {
                if (args[2].isEmpty() || account.name().startsWith(args[2])) {
                    list.add(account.name());
                }
            }
        } else if(args.length == 4) {
            list.add("<amount>");
        }
        return list;
    }
}
