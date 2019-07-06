package no.helponline.Managers;

import java.util.HashMap;
import java.util.UUID;

public class EconManager {
    private HashMap<UUID, Double> bal = new HashMap<>();

    public void setBalance(UUID player, double amount) {
        bal.put(player, amount);
    }

    public Double getBalance(UUID player) {
        return (Double) bal.get(player);
    }

    public boolean hasAccount(UUID player) {
        return !bal.containsKey(player);
    }

    public HashMap<UUID, Double> getBalanceMap() {
        return bal;
    }
}
