package no.helponline.Managers;

import java.util.HashMap;
import java.util.UUID;

public class EconManager {
    private static HashMap<UUID, Double> bal = new HashMap<>();

    public static void setBalance(UUID player, double amount) {
        bal.put(player, amount);
    }

    public static Double getBalance(UUID player) {
        return (Double) bal.get(player);
    }

    public static boolean hasAccount(UUID player) {
        return !bal.containsKey(player);
    }

    public static HashMap<UUID, Double> getBalanceMap() {
        return bal;
    }
}
