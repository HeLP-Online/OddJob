package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.AuctionSQL;
import com.spillhuset.Utils.AuctionBid;
import com.spillhuset.Utils.AuctionItem;
import com.spillhuset.Utils.Enum.Types;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class AuctionManager {
    public void sell(Player player, double value, double buyout, int expire) {
        // Check if you can afford it
        double inPocket = OddJob.getInstance().getCurrencyManager().get(player.getUniqueId()).get(Types.AccountType.pocket);
        /* 5% of buyout or 20% of value */
        double fee = (buyout != 0) ? buyout / 20 : value / 5;
        if (fee > inPocket) {
            OddJob.getInstance().getMessageManager().auctionsCantAfford(fee, player);
            return;
        }
        int id = AuctionSQL.sell(player, player.getInventory().getItemInMainHand(), value, buyout, expire);
        if (id != 0) {
            OddJob.getInstance().getCurrencyManager().subtract(player.getUniqueId(), fee, false, Types.AccountType.pocket);
            OddJob.getInstance().getMessageManager().auctionsItemSetToSale(id, value, buyout, fee, expire, player);
        }
    }

    public void buyout(int item, double offer, Player player) {
        AuctionItem auctionItem = AuctionSQL.getAuctionItem(item);

        // Check if you can afford it
        double inPocket = OddJob.getInstance().getCurrencyManager().get(player.getUniqueId()).get(Types.AccountType.pocket);
        if (offer > inPocket) {
            OddJob.getInstance().getMessageManager().auctionsCantAfford(offer, player);
            return;
        }

        // Check if sold
        boolean isSold = AuctionSQL.isSold(item);
        if (isSold) {
            OddJob.getInstance().getMessageManager().auctionsItemAlreadySold(player);
            return;
        }

        // Refund current bid
        for (AuctionBid auctionBid : auctionItem.getBids()) {
            if (!auctionBid.isRefunded()) {
                Player bidder = Bukkit.getPlayer(auctionBid.getBidder());
                OddJob.getInstance().getCurrencyManager().add(auctionBid.getBidder(), auctionBid.getBid(), ((bidder != null) ? Types.AccountType.pocket : Types.AccountType.bank));
                auctionBid.setRefunded(true);
            }
        }
        //refundBid(item);

        // Take Money
        OddJob.getInstance().getCurrencyManager().subtract(player.getUniqueId(), offer, false, Types.AccountType.pocket);
        // Give Money
        player.getInventory().addItem(AuctionSQL.buyout(item));
    }

    public void bid(int item, double offer, Player player) {
        checkExpiredBids();

        AuctionItem auctionItem = AuctionSQL.getAuctionItem(item);

        // Check if already sold
        boolean isSold = AuctionSQL.isSold(item);
        if (isSold) {
            OddJob.getInstance().getMessageManager().auctionsItemAlreadySold(player);
            return;
        }

        // Check if can afford to own offer
        double inPocket = OddJob.getInstance().getCurrencyManager().get(player.getUniqueId()).get(Types.AccountType.pocket);
        if (offer > inPocket) {
            OddJob.getInstance().getMessageManager().auctionsCantAfford(offer, player);
            return;
        }

        // Check if more than current bid
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

        // Place bid
        OddJob.getInstance().getCurrencyManager().subtract(player.getUniqueId(), offer, false, Types.AccountType.pocket);
        AuctionSQL.placeBid(item, offer, player);

        // Refund highest bidder
        refundBid(auctionBid);

        OddJob.getInstance().getMessageManager().auctionsBidSet(item, offer, player);
    }

    private void refundBid(AuctionBid auctionBid) {
        Player highest = Bukkit.getPlayer(auctionBid.getBidder());
        OddJob.getInstance().getCurrencyManager().add(auctionBid.getBidder(), auctionBid.getBid(), ((highest != null) ? Types.AccountType.pocket : Types.AccountType.bank));
        auctionBid.setRefunded(true);
    }

    public void checkExpiredBids() {
        List<AuctionItem> expired = getExpiredItems();
        int now = (int) (System.currentTimeMillis() / 1000);
        for (AuctionItem item : expired) {
            int expire = ((item.getExpire() * 60 * 60) + item.getTime());
            expire = now - expire;
            if (expire > 0) {
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
        auctionItem.setSold((int)System.currentTimeMillis()/1000);
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
     *
     * @param item AuctionItem
     * @return AuctionBid
     */
    public AuctionBid getHighestBid(AuctionItem item) {
        AuctionBid auctionBid = null;
        for (AuctionBid bid :item.getBids()) {
            if (auctionBid == null || bid.getBid() > auctionBid.getBid()) {
                auctionBid = bid;
            }
        }
        return auctionBid;
    }
}
