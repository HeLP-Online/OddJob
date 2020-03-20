package no.helponline.Managers;

import no.helponline.OddJob;

import java.util.HashMap;
import java.util.UUID;

public class EconManager {
    private final HashMap<UUID, Double> playerBank = new HashMap<>();
    private final HashMap<UUID, Double> guildBank = new HashMap<>();
    private final HashMap<UUID, Double> pocket = new HashMap<>();

    public void setBankBalance(UUID uuid, double amount, boolean guild) {
        if (guild) {
            guildBank.put(uuid, amount);
        } else {
            playerBank.put(uuid, amount);
        }
        //OddJob.getInstance().getMySQLManager().econSet(uuid,amount, Econ.BANK);
    }

    public void setPocketBalance(UUID uuid, double amount) {
        pocket.put(uuid, amount);
        //OddJob.getInstance().getMySQLManager().econSet(uuid,amount, Econ.POCKET);
    }

    public double getBankBalance(UUID uuid, boolean guild) {
        if (guild) {
            if (!hasBankAccount(uuid,true)) createAccounts(uuid,200D,true);
            return guildBank.get(uuid);
        } else {
            return playerBank.get(uuid);
        }
    }

    public double getPocketBalance(UUID player) {
        return pocket.get(player);
    }

    public boolean hasBankAccount(UUID uuid, boolean guild) {
        if (guild) {
            return guildBank.containsKey(uuid);
        } else {
            return playerBank.containsKey(uuid);
        }
    }

    public boolean hasPocket(UUID player) {
        return pocket.containsKey(player);
    }

    public void subtractBankBalance(UUID uuid, double cost, boolean guild) {
        setBankBalance(uuid, getBankBalance(uuid, guild) - cost, guild);
    }
    public void subtractPocketBalance(UUID uuid, double cost) {
        setPocketBalance(uuid, getPocketBalance(uuid) - cost);
    }

    public Double cost(String name) {
        return OddJob.getInstance().getConfig().getDouble("econ.cost." + name, 0.0);
    }

    public void createAccounts(UUID uuid, double startValue, boolean guild) {
        createBankAccount(uuid,startValue,guild);
        if (!guild) createPocket(uuid,startValue);
        OddJob.getInstance().getMySQLManager().createEconAccount(uuid, startValue, guild);
    }
    public void createPocket(UUID uuid, double startValue) {
        setPocketBalance(uuid,startValue);
    }
    public void createBankAccount(UUID uuid, double startValue, boolean guild) {
        setBankBalance(uuid, startValue, guild);
    }

    public void addBankBalance(UUID uuid, Double cost, boolean guild) {
        setBankBalance(uuid, getBankBalance(uuid, guild) + cost, guild);
    }
    public void addPocketBalance(UUID player, double cost) {
        setPocketBalance(player,getPocketBalance(player)+cost);
    }

    public void load() {
        HashMap<String,HashMap<UUID,Double>> values = OddJob.getInstance().getMySQLManager().loadEcon();
        pocket.putAll(values.get("pocket"));
        guildBank.putAll(values.get("guild"));
        playerBank.putAll(values.get("bank"));
    }

    public void save() {
        OddJob.getInstance().getMySQLManager().saveEcon();
    }
}
