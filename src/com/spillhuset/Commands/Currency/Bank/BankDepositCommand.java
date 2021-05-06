package com.spillhuset.Commands.Currency.Bank;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Currency;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankDepositCommand extends com.spillhuset.Utils.SubCommand {
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
        return null;
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

        Currency account = null;
        double value = 0d;
        UUID target = null;

        try {
            account = Currency.valueOf(args[2]);
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
            case bank_player:
                target = OddJob.getInstance().getPlayerManager().getUUID(args[3]);
                if (target == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(Plugin.player,args[3],sender);
                    return;
                }


                break;
            case bank_guild:
                target = OddJob.getInstance().getGuildManager().getGuildUUIDByName(args[3]);
                if (target == null) {
                    OddJob.getInstance().getMessageManager().errorGuild(args[3],sender);
                    return;
                }
                break;
        }

        OddJob.getInstance().getCurrencyManager().subtractPocketBalance(target,value,sender.hasPermission("currency.negative"),sender);
        OddJob.getInstance().getCurrencyManager().addBankBalance(target,value,sender,account);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        List<Currency> accounts = new ArrayList<>();
        accounts.add(Currency.bank_guild);
        accounts.add(Currency.bank_player);
        if (args.length == 3) {
            for (Currency account : accounts) {
                if (account.name().startsWith(args[2])) {
                    list.add(account.name());
                }
            }
        } else if(args.length == 4) {
            Currency account = Currency.valueOf(args[2]);
            switch (account) {
                case pocket:
                case bank_player:
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                        if (offlinePlayer.getName().startsWith(args[3])) {
                            list.add(offlinePlayer.getName());
                        }
                    }
                    break;
                case bank_guild:
                    for (UUID guild : OddJob.getInstance().getGuildManager().getGuilds().keySet()) {
                        if (OddJob.getInstance().getGuildManager().getGuild(guild).getName().startsWith(args[3])) {
                            list.add(OddJob.getInstance().getGuildManager().getGuild(guild).getName());
                        }
                    }
                    break;
            }
        }
        return list;
    }
}
