package no.helponline.Managers;

import no.helponline.OddJob;

import java.util.HashMap;
import java.util.UUID;

public class EconManager {

    public void setBalance(UUID uuid, double amount, boolean guild) {
        OddJob.getInstance().getMySQLManager().setBalance(uuid, amount, guild);
    }

    public Double getBalance(UUID player) {
        return OddJob.getInstance().getMySQLManager().getBalance(player);
    }

    public boolean hasAccount(UUID player) {
        return OddJob.getInstance().getMySQLManager().hasBalance(player);
    }

    public HashMap<UUID, Double> getBalanceMapPlayer() {
        return OddJob.getInstance().getMySQLManager().getBalanceMapPlayer();
    }

    public HashMap<UUID, Double> getBalanceMapGuild() {
        return OddJob.getInstance().getMySQLManager().getBalanceMapGuild();
    }

    public void subtract(UUID player, double cost, boolean guild) {
        setBalance(player, getBalance(player) - cost, guild);
    }
}
