package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.CurrencySQL;
import com.spillhuset.Utils.Enum.Currency;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.UUID;

public class CurrencyManager {
    private final HashMap<UUID, Double> playerBank = new HashMap<>();
    private final HashMap<UUID, Double> guildBank = new HashMap<>();
    private final HashMap<UUID, Double> pocket = new HashMap<>();

    public void setBankBalance(UUID uuid, double amount, Currency account) {
        if (account.equals(Currency.bank_guild)) {
            guildBank.put(uuid, amount);
        } else {
            playerBank.put(uuid, amount);
        }
        CurrencySQL.setBalance(uuid, amount, account);
    }

    public void setPocketBalance(UUID uuid, double amount) {
        pocket.put(uuid, amount);
        //OddJob.getInstance().getMySQLManager().econSet(uuid,amount, Econ.POCKET);
    }

    public double getBankBalance(UUID uuid, Currency account) {
        if (account.equals(Currency.bank_guild)) {
            if (!hasBankAccount(uuid, Currency.bank_guild))
                createAccounts(uuid, ConfigManager.getCurrencyInitialGuild(), account);
            return guildBank.get(uuid);
        } else {
            return playerBank.get(uuid);
        }
    }

    public double getPocketBalance(UUID player) {
        return pocket.get(player);
    }

    public boolean hasBankAccount(UUID uuid, Currency account) {
        if (account.equals(Currency.bank_guild)) {
            return guildBank.containsKey(uuid);
        } else {
            return playerBank.containsKey(uuid);
        }
    }

    public boolean hasPocket(UUID player) {
        return pocket.containsKey(player);
    }

    public boolean subtractBankBalance(UUID uuid, double cost, boolean negative, Currency account) {
        if (!negative || getBankBalance(uuid, account) > cost) {
            setBankBalance(uuid, getBankBalance(uuid, account) - cost, account);
            return true;
        }
        return false;
    }

    public boolean subtractPocketBalance(UUID uuid, double cost, boolean negative, CommandSender sender) {
        if (!negative || getPocketBalance(uuid) > cost) {
            setPocketBalance(uuid, getPocketBalance(uuid) - cost);
            return true;
        }
        return false;
    }

    public Double cost(String name) {
        return OddJob.getInstance().getConfig().getDouble("econ.cost." + name, 0.0);
    }

    public void createAccounts(UUID uuid, double startValue, Currency account) {
        createBankAccount(uuid, startValue, account);
        if (!account.equals(Currency.bank_guild)) createPocket(uuid, startValue);
        CurrencySQL.createAccount(uuid, startValue, account);
    }

    public void createPocket(UUID uuid, double startValue) {
        setPocketBalance(uuid, startValue);
    }

    public void createBankAccount(UUID uuid, double startValue, Currency guild) {
        setBankBalance(uuid, startValue, guild);
    }

    public void addBankBalance(UUID uuid, Double cost, CommandSender sender, Currency account) {
        double bankBalance = getBankBalance(uuid, account);
        setBankBalance(uuid, bankBalance + cost, account);
        String name = (account.equals(Currency.bank_guild)) ? OddJob.getInstance().getGuildManager().getGuildNameByUUID(uuid) : OddJob.getInstance().getPlayerManager().getName(uuid);
        OddJob.getInstance().getMessageManager().currencySuccessAdded(name, String.valueOf(cost), bankBalance, sender, account);

    }

    public void addPocketBalance(UUID player, double cost) {
        setPocketBalance(player, getPocketBalance(player) + cost);
    }

    public void load() {
        HashMap<String, HashMap<UUID, Double>> values = CurrencySQL.load();
        if (values.containsKey("pocket") && values.get("pocket").size() > 0) {
            pocket.putAll(values.get("pocket"));
        }
        if (values.containsKey("guild") && values.get("guild").size() > 0) {
            guildBank.putAll(values.get("guild"));
        }
        if (values.containsKey("bank") && values.get("bank").size() > 0) {
            playerBank.putAll(values.get("bank"));
        }
    }

    public void save() {
        CurrencySQL.save();
    }
}
