package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.AuctionSQL;
import com.spillhuset.Utils.AuctionBid;
import com.spillhuset.Utils.AuctionItem;
import com.spillhuset.Utils.Enum.Types;
import com.spillhuset.Utils.Odd.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class AuctionManager {
    public void sell(Player player, double value, double buyout, int expire) {
        // Find current pocket value
        double inPocket = OddJob.getInstance().getCurrencyManager().get(player.getUniqueId()).get(Types.AccountType.pocket);
        // Check if player can afford the fee
        /* 5% of buyout or 20% of value */
        double fee = (buyout != 0) ? buyout / 20 : value / 5;
        if (fee > inPocket) {
            OddJob.getInstance().getMessageManager().auctionsCantAfford(fee, player);
            return;
        }

        // Place the item into the database
        int id = AuctionSQL.sell(player, player.getInventory().getItemInMainHand(), value, buyout, expire);
        if (id != 0) {
            // Take the fee
            OddJob.getInstance().getCurrencyManager().subtract(player.getUniqueId(), fee, false, Types.AccountType.pocket);

            OddJob.getInstance().getMessageManager().auctionsItemSetToSale(id, value, buyout, fee, expire, player);
        }
    }

    public void buyout(int item, Player player) {
        // Find the item
        AuctionItem auctionItem = AuctionSQL.getAuctionItem(item);
        double offer = auctionItem.getBuyout();

        // Buyout!
        buyout(item, offer, player);
    }

    public void buyout(int item, double offer, Player player) {
        // Find the item
        AuctionItem auctionItem = AuctionSQL.getAuctionItem(item);

        // Check if you can afford the item
        double inPocket = OddJob.getInstance().getCurrencyManager().get(player.getUniqueId()).get(Types.AccountType.pocket);
        if (offer > inPocket) {
            OddJob.getInstance().getMessageManager().auctionsCantAfford(offer, player);
            return;
        }

        // Check if the item is already sold
        boolean isSold = AuctionSQL.isSold(item);
        if (isSold) {
            OddJob.getInstance().getMessageManager().auctionsItemAlreadySold(player);
            return;
        }

        // If there is any bids given, lesser than your, refund the previous bid
        for (AuctionBid auctionBid : auctionItem.getBids()) {
            if (!auctionBid.isRefunded()) {
                auctionBid.refund(offer,player,true,item,auctionBid.getBidder());
            }
        }

        // Pay the bid for the item
        OddJob.getInstance().getCurrencyManager().subtract(player.getUniqueId(), offer, false, Types.AccountType.pocket);
        // Mark the item as sold
        auctionItem.setSold((int) System.currentTimeMillis() / 1000);
    }

    public void bid(int item, double offer, Player player) {
        // Checks if any auctions has expired
        checkExpiredBids();

        // Fetch item
        AuctionItem auctionItem = AuctionSQL.getAuctionItem(item);

        // Check if the item is already sold
        boolean isSold = AuctionSQL.isSold(item);
        if (isSold) {
            OddJob.getInstance().getMessageManager().auctionsItemAlreadySold(player);
            return;
        }

        // Check if you can afford your own offer
        double inPocket = OddJob.getInstance().getCurrencyManager().get(player.getUniqueId()).get(Types.AccountType.pocket);
        if (offer > inPocket) {
            OddJob.getInstance().getMessageManager().auctionsCantAfford(offer, player);
            return;
        }

        // Check if your offer are more than current bid
        AuctionBid auctionBid = AuctionSQL.getBid(item, true);
        if (auctionBid.getBid() >= offer) {
            OddJob.getInstance().getMessageManager().auctionsBidNotHighEnough(offer, auctionBid.getBid(), player);
            return;
        }

        // Check if more than buyout
        if (offer >= auctionItem.getBuyout()) {
            buyout(item, offer, player);
            return;
        }

        // Refund highest bidder
        refundBid(auctionBid);

        // Take the money for the bid
        OddJob.getInstance().getCurrencyManager().subtract(player.getUniqueId(), offer, false, Types.AccountType.pocket);

        // Place bid on the item
        AuctionSQL.addBid(item, offer, player);

        OddJob.getInstance().getMessageManager().auctionsBidSet(item, offer, player);
    }

    private void refundBid(AuctionBid auctionBid) {
        // Find player (might be offline)
        OddPlayer highest = OddJob.getInstance().getPlayerManager().getOddPlayer(auctionBid.getBidder());

        // Refund the player
        OddJob.getInstance().getCurrencyManager().add(auctionBid.getBidder(), auctionBid.getBid(), Types.AccountType.bank);

        // Set as refunded
        auctionBid.setRefunded(true);
    }

    public void checkExpiredBids() {
        // List of expired items
        List<AuctionItem> expired = getExpiredItems();

        // Timestamp now
        int now = (int) (System.currentTimeMillis() / 1000);

        // The check loop
        for (AuctionItem item : expired) {
            // Expiring time (count hours down to second, and add the start time)
            int expire = ((item.getExpire() * 60 * 60) + item.getTime());
            // Subtract timestamp with expiration
            expire = now - expire;
            if (expire > 0) {
                // The item has expired
                expired(item);
            }
        }
    }

    private void expired(AuctionItem auctionItem) {
        if (auctionItem.getBids().size() > 0) {
            receiveItemBid(auctionItem, getHighestBid(auctionItem));
            auctionItem.setBuyer(getHighestBid(auctionItem).getBidder());
        } else {
            refundItem(auctionItem);
        }
        auctionItem.setSold((int) System.currentTimeMillis() / 1000);
        AuctionSQL.saveItem(auctionItem);
    }

    private void receiveItemBid(AuctionItem auctionItem, AuctionBid highestBid) {
        auctionItem.setBuyer(highestBid.getBidder());
        auctionItem.setSold((int) System.currentTimeMillis() / 1000);
        Player receiver = Bukkit.getPlayer(highestBid.getBidder());
        if (receiver != null) {
            OddJob.getInstance().getMessageManager().auctionsReceiverWon(auctionItem, receiver);
        }
        Player seller = Bukkit.getPlayer(auctionItem.getSeller());
        if (seller != null) {
            OddJob.getInstance().getCurrencyManager().add(auctionItem.getSeller(), highestBid.getBid(), Types.AccountType.bank);
            OddJob.getInstance().getMessageManager().auctionsSoldWinner(auctionItem, highestBid, seller);
        }
    }

    private void receiveItemBuyout(AuctionItem auctionItem) {
        Player receiver = Bukkit.getPlayer(auctionItem.getBuyer());
        if (receiver != null) {
            OddJob.getInstance().getMessageManager().auctionsReceiverBuyout(auctionItem, receiver);
        }
        Player seller = Bukkit.getPlayer(auctionItem.getSeller());
        if (seller != null) {
            OddJob.getInstance().getMessageManager().auctionsSoldBuyout(auctionItem, seller);
        }
    }

    private void refundItem(AuctionItem auctionItem) {
        Player player = Bukkit.getPlayer(auctionItem.getSeller());
        if (player != null) {
            OddJob.getInstance().getMessageManager().auctionsNoBidsOrBuyout(auctionItem, player);
        }
    }

    public AuctionItem getItem(int item) {
        return AuctionSQL.getAuctionItem(item);
    }

    public List<AuctionItem> getListedItems() {
        return AuctionSQL.getListing();
    }

    public List<AuctionItem> getAllItems() {
        return AuctionSQL.getAllItems();
    }

    public List<AuctionItem> getExpiredItems() {
        return AuctionSQL.findExpired();
    }

    public List<AuctionBid> getBids(int item) {
        return AuctionSQL.getBids(item);
    }

    /**
     * @param item AuctionItem
     * @return AuctionBid
     */
    public AuctionBid getHighestBid(AuctionItem item) {
        AuctionBid auctionBid = null;
        for (AuctionBid bid : item.getBids()) {
            if (auctionBid == null || bid.getBid() > auctionBid.getBid()) {
                auctionBid = bid;
            }
        }
        return auctionBid;
    }

    public void checkUnRetrievedItems(Player player) {
        List<Integer> list = AuctionSQL.getUnRetrievedItems(player.getUniqueId());

    }
}
