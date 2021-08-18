package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.AuctionSQL;
import com.spillhuset.Utils.Enum.Types;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class AuctionManager {
    public void sell(Player player, double value, double buyout, int expire) {
        OddJob.getInstance().log("Selling to SQL");
        AuctionSQL.sell(player, player.getInventory().getItemInMainHand(), value, buyout, expire);
    }

    public void buyout(int num, Player player) {

        HashMap<String, Object> ret = AuctionSQL.buyout(num);
        ItemStack item = ((ItemStack) ret.get("item"));
        OddJob.getInstance().log("You bought id:" + num + " for " + ret.get("buyout") + " item:" + item.getType().name());
        player.getInventory().addItem(item);
    }

    public void bid(int item, double offer, Player player) {
        double inPocket = OddJob.getInstance().getCurrencyManager().get(player.getUniqueId()).get(Types.AccountType.pocket);
        if (offer > inPocket) {
            OddJob.getInstance().getMessageManager().auctionsCantAfford(offer, player);
            return;
        }

        boolean isSold = AuctionSQL.isSold(item);
        if (isSold) {
            OddJob.getInstance().getMessageManager().auctionsItemAlreadySold(player);
            return;
        }

        double bid = AuctionSQL.getHighestBid(item);
        if (bid >= offer) {
            OddJob.getInstance().getMessageManager().auctionsBidNotHighEnough(offer, bid, player);
            return;
        }

        OddJob.getInstance().getCurrencyManager().subtract(player.getUniqueId(), offer, false, Types.AccountType.pocket);
        AuctionSQL.placeBid(item, offer, player);
        OddJob.getInstance().getMessageManager().auctionsBidSet(item, offer, player);
    }

    public void checkBids(long time) {
        List<Integer> expired = AuctionSQL.findExpired(time);
        OddJob.getInstance().log("found "+expired.size());
        for (Integer i : expired) {
            OddJob.getInstance().log("" + i);
        }
    }
}
