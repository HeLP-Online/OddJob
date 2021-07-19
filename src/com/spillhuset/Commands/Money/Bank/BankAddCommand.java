package com.spillhuset.Commands.Money.Bank;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Types.AccountType;
import com.spillhuset.Utils.Enum.Types.BankType;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
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
        return "/currency bank add [guild/player] [name] <amount>";
    }

    @Override
    public String getPermission() {
        return "currency.bank.add";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(5, 5, args, sender, getPlugin())) {
            return;
        }

        String name = args[3];
        BankType bankType = null;
        for (BankType type : BankType.values()) {
            if (type.name().equalsIgnoreCase(args[2])) {
                bankType = type;
            }
        }
        if (bankType == null) {
            OddJob.getInstance().getMessageManager().errorCurrencyBankType(getPlugin(), args[2], sender);
            return;
        }
        double value = 0.0d;
        try {
            value = Double.parseDouble(args[4]);
        } catch (NumberFormatException e) {
            OddJob.getInstance().getMessageManager().invalidNumber(getPlugin(), args[4], sender);
            return;
        }

        UUID uuid = null;
        switch (bankType) {
            case player -> {
                uuid = OddJob.getInstance().getPlayerManager().getUUID(name);
                if (uuid == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(),args[3],sender);
                    return;
                }
            }
            case guild -> {
                uuid = OddJob.getInstance().getGuildManager().getGuildUUIDByName(name);
                if (uuid == null) {
                    OddJob.getInstance().getMessageManager().errorGuild(args[3],sender);
                    return;
                }
            }
            default -> {
                OddJob.getInstance().getMessageManager().errorCurrencyBankType(getPlugin(), args[2], sender);
                return;
            }
        }

        OddJob.getInstance().getCurrencyManager().add(uuid, value, AccountType.bank);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 3) {
            for (BankType type : BankType.values()) {
                if (args[2].isEmpty() || type.name().startsWith(args[2])) {
                    list.add(type.name());
                }
            }
        } else if (args.length == 4) {
            for (BankType type : BankType.values()) {
                if (type.name().equals(args[2])) {
                    switch (type) {
                        case player -> {
                            for (String name : OddJob.getInstance().getPlayerManager().getNames()) {
                                if (args[3].isEmpty() || name.equalsIgnoreCase(args[3])) {
                                    list.add(name);
                                }
                            }
                        }
                        case guild -> {
                            for (String name : OddJob.getInstance().getGuildManager().getNames()) {
                                if ((args[3].isEmpty() || name.equalsIgnoreCase(args[3])) && !name.endsWith("Zone")) {
                                    list.add(name);
                                }
                            }
                        }
                    }
                }
            }
        }

        return list;
    }
}
