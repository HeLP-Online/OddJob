package com.spillhuset.Utils.Account;

import com.spillhuset.Utils.Enum.Types.AccountType;

import java.util.UUID;

public class Account {
    UUID uuid = null;
    double bank = 0d;
    double pocket = 0d;

    boolean guild = false;

    public Account(UUID uuid,double bank,double pocket, boolean guild) {
        this.uuid = uuid;
        this.bank = bank;
        this.pocket = pocket;
        this.guild = guild;
    }

    public void set(AccountType account, double amount) {
        switch (account) {
            case pocket -> pocket = amount;
            case bank -> bank = amount;
        }
    }

    public double get(AccountType account) {
        return switch (account) {
            case pocket -> pocket;
            case bank -> bank;
        };
    }

    public boolean isGuild() {
        return guild;
    }

    public UUID getUuid() {
        return uuid;
    }
}
