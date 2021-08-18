package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.PriceSQL;
import com.spillhuset.Utils.Enum.Types;

import java.util.UUID;

public class CostManager {
    /**
     * Transaction from pocket
     *
     * @param player UUID payer
     * @param friend UUID checked if guild-friend
     * @param plu    String name of price-list-unit
     */
    public static boolean cost(UUID player, UUID friend, String plu) {
        boolean friendly = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player) == OddJob.getInstance().getGuildManager().getGuildUUIDByMember(friend);
        double price = PriceSQL.get(plu, friendly);
        return transaction(player, price);
    }

    public static boolean cost(UUID player, String plu) {
        double price = PriceSQL.get(plu, false);
        return transaction(player, price);
    }

    private static boolean transaction(UUID player, double price) {
        if (OddJob.getInstance().getCurrencyManager().get(player).get(Types.AccountType.pocket) < price) {
            OddJob.getInstance().getMessageManager().insufficientFunds(player, price);
            return false;
        } else {
            OddJob.getInstance().getCurrencyManager().subtract(player, price, false, Types.AccountType.pocket);
            return true;
        }
    }
}
