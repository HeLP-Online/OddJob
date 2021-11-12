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
    /**
     * Making an AuctionItem
     *
     * @param player Player
     * @param value  Double starting bid
     * @param buyout Double buyout value
     * @param expire Integer time defined as hours
     * @return Integer the ID of the AuctionItem
     */
    public int sell(Player player, double value, double buyout, int expire) {
        // Find current pocket value
        // Check if player can afford the fee
        /* 5% of buyout or 20% of value */
        double fee = (buyout != 0) ? buyout / 20 : value / 5;

        // Place the item into the database
        // Take the fee
        if (OddJob.getInstance().getCurrencyManager().subtract(player.getUniqueId(), fee, false, Types.AccountType.pocket)) {
            int id = AuctionSQL.sell(player, player.getInventory().getItemInMainHand(), value, buyout, expire, fee);
            OddJob.getInstance().getMessageManager().auctionsItemSetToSale(id, value, buyout, fee, expire, player);
            // Return the ID of the AuctionItem
            return id;
        }

        // Player can't afford to start an auction
        OddJob.getInstance().getMessageManager().auctionsCantAfford(fee, player);
        return 0;
    }

    /**
     * Buyout without defining a buyout value
     *
     * @param item   Integer the ID of the AuctionItem
     * @param player Player buyer
     */
    public void buyout(int item, Player player) {
        // Find the item in the AuctionItem
        AuctionItem auctionItem = AuctionSQL.getAuctionItem(item);
        double offer = auctionItem.getBuyout();

        // Buyout the AuctionItem with the buyout value
        buyout(item, offer, player);
    }

    /**
     * Buyout the AuctionItem with a defined buyout value
     *
     * @param item   Integer the ID of the AuctionItem
     * @param offer  Double offer value
     * @param player Player the buyer
     */
    public void buyout(int item, double offer, Player player) {
        // Find the item
        AuctionItem auctionItem = AuctionSQL.getAuctionItem(item);

        // Check if the item is already sold
        if (auctionItem.isSold()) {
            OddJob.getInstance().getMessageManager().auctionsItemAlreadySold(player);
            return;
        }

        // Check if you can afford the item
        // Pay the bid for the item
        if (!OddJob.getInstance().getCurrencyManager().subtract(player.getUniqueId(), offer, false, Types.AccountType.pocket)) {
            OddJob.getInstance().getMessageManager().auctionsCantAfford(offer, player);
            return;
        }

        // If there is any bids given, lesser than your, refund the previous bid
        for (AuctionBid auctionBid : auctionItem.getBids()) {
            if (!auctionBid.isRefunded()) {
                auctionBid.refund(offer, player, true, item, auctionBid.getBidder());
            }
        }

        // Mark the item as sold
        auctionItem.setSold((int) System.currentTimeMillis() / 1000);
    }

    /**
     * Placing an AuctionBid on a AuctionItem
     *
     * @param item   Integer the ID of the AuctionItem
     * @param offer  Double offer value for the AuctionItem
     * @param player Player the buyer
     */
    public void bid(int item, double offer, Player player) {
        // Fetch item
        AuctionItem auctionItem = AuctionSQL.getAuctionItem(item);

        // Check if the item is already sold
        if (auctionItem.isSold()) {
            OddJob.getInstance().getMessageManager().auctionsItemAlreadySold(player);
            return;
        }

        // Check if more than buyout
        if (offer >= auctionItem.getBuyout()) {
            buyout(item, offer, player);
            return;
        }

        // Check if you can afford your own offer
        // Take the money for the bid
        if (OddJob.getInstance().getCurrencyManager().subtract(player.getUniqueId(), offer, false, Types.AccountType.pocket)) {
            OddJob.getInstance().getMessageManager().auctionsCantAfford(offer, player);
            return;
        }

        // Check if your offer are more than current bid
        AuctionBid auctionBid = AuctionSQL.getBid(item);
        if (auctionBid != null) {
            // Found earlier bids
            if (auctionBid.getBid() >= offer) {
                // Bid not high enough
                OddJob.getInstance().getMessageManager().auctionsBidNotHighEnough(offer, auctionBid.getBid(), player);
                return;
            }

            // Refund highest bidder
            refundBid(auctionBid);
            OddJob.getInstance().getMessageManager().auctionsOverBid(offer,player,false,item,auctionBid.getBidder());
        }

        // Place bid on the item
        AuctionSQL.addBid(item, offer, player);
        OddJob.getInstance().getMessageManager().auctionsBidSet(item, offer, player);
    }

    /**
     * Refunding the bid
     *
     * @param auctionBid AuctionBid to be refunded
     */
    private void refundBid(AuctionBid auctionBid) {
        // Refund the player
        OddJob.getInstance().getCurrencyManager().add(auctionBid.getBidder(), auctionBid.getBid(), Types.AccountType.bank);

        // Set as refunded
        auctionBid.setRefunded(true);
    }

    /**
     * Maintaining the Auction list.
     */
    public void checkExpiredAuctions() {
        // List of expired items
        List<Integer> notify = AuctionSQL.findExpired();

        // The check loop
        for (int item : notify) {
            AuctionItem auctionItem = getItem(item);
        }
    }

    /**
     * Timer has expired
     *
     * @param auctionItem AuctionItem
     */
    private void expired(AuctionItem auctionItem) {
        if (auctionItem.getBids().size() > 0) {
            // Item have one or more bids
            receiveItemBid(auctionItem, getHighestBid(auctionItem));
            auctionItem.setBuyer(getHighestBid(auctionItem).getBidder());
        } else {
            // There were no bids
            if (!auctionItem.getNotified() && refundItem(auctionItem)) auctionItem.setNotified(true);
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

    private boolean refundItem(AuctionItem auctionItem) {
        Player player = Bukkit.getPlayer(auctionItem.getSeller());
        return player != null;
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
