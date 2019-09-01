package no.helponline.Managers;

import no.helponline.OddJob;

import java.util.HashMap;
import java.util.UUID;

public class EconManager {

    public void createBalance(UUID player, boolean guild) {
        OddJob.getInstance().getMySQLManager().createBalance(player, guild);
    }

    public void setBalance(UUID player, double amount) {
        OddJob.getInstance().getMySQLManager().setBalance(player, amount);
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

    public void subtract(UUID player, double cost) {
        setBalance(player, getBalance(player) - cost);
    }
}
