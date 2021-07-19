package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.CurrencySQL;
import com.spillhuset.Utils.Account.Account;
import com.spillhuset.Utils.Enum.Types;

import java.util.HashMap;
import java.util.UUID;

public class MoneyManager {
    private HashMap<UUID, Account> accounts = new HashMap<>();

    /**
     * @param uuid    UUID of Player or Guild
     * @param amount  Balance to be set
     * @param account Account to set
     */
    public void set(UUID uuid, double amount, Types.AccountType account) {
        accounts.get(uuid).set(account, amount);
        CurrencySQL.setBalance(uuid, amount, account);
    }

    /**
     * @param uuid  UUID of Player of Guild
     * @param guild Is a guild?
     * @return Double balance
     */
    public Account get(UUID uuid, boolean guild) {
        if (accounts.containsKey(uuid)) {
            return accounts.get(uuid);
        } else {
            if (guild) {
                create(uuid, ConfigManager.getCurrencyInitialGuild());
            } else {
                create(uuid, ConfigManager.getCurrencyInitialBank(), ConfigManager.getCurrencyInitialPocket());
            }
            return get(uuid, guild);
        }
    }

    /**
     *
     * @param uuid UUID of target account
     * @param value Double Transaction value
     * @param negative Boolean value can be negative
     * @param type Type of transaction
     * @return Boolean if success
     */
    public boolean subtract(UUID uuid, double value, boolean negative, Types.AccountType type) {
        // Check if negative value is ok?
        if (!negative || get(uuid).get(type) > value) {
            // Set amount
            set(uuid, get(uuid).get(type) - value, type);
            return true;
        }
        return false;
    }

    /**
     *
     * @param name String of transaction
     * @return Double value
     */
    public Double cost(String name) {
        return OddJob.getInstance().getConfig().getDouble("econ.cost." + name, 0.0d);
    }

    public void create(UUID uuid, double bank, double pocket) {
        accounts.put(uuid, new Account(uuid, bank, pocket, false));
        CurrencySQL.createAccount(uuid, bank, pocket, false);
    }

    public void create(UUID uuid, double bank) {
        accounts.put(uuid, new Account(uuid, bank, 0, true));
        CurrencySQL.createAccount(uuid, bank, 0, true);
    }

    /**
     *
     * @param uuid UUID of target account
     * @param value Double Transaction value
     * @param type Type of transaction
     */
    public void add(UUID uuid, double value, Types.AccountType type) {
        set(uuid, get(uuid).get(type) + value, type);
    }

    public void load() {
        accounts = CurrencySQL.load();
    }

    public void save() {
        CurrencySQL.save(accounts);
    }

    public Account get(UUID uuid) {
        return accounts.get(uuid);
    }
}
