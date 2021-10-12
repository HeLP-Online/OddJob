package com.spillhuset.Utils;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuctionItem {
    int id;
    int expire;
    int time;
    double value;
    double buyout;
    int picked_up;
    int sold;
    UUID buyer;
    UUID seller;

    public ItemStack getItem() {
        return item;
    }

    public double getFee() {
        return fee;
    }

    public boolean isNotified() {
        return notified;
    }

    ItemStack item;
    double fee;
    List<AuctionBid> bids;
    boolean notified;

    // Get AuctionItem
    public AuctionItem(int id, ItemStack item, int expire, int time, double value, double buyout, int picked_up, int sold, String buyer, String seller, double fee, List<AuctionBid> bids, boolean notified) {
        this.item = item;
        this.id = id;
        this.expire = expire;
        this.time = time;
        this.value = value;
        this.buyout = buyout;
        this.picked_up = picked_up;
        this.sold = sold;
        this.buyer = UUID.fromString(buyer);
        this.seller = UUID.fromString(seller);
        this.fee = fee;
        this.bids = bids;
        this.notified = notified;
    }

    /**
     *
     * @return int auction id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return List of AuctionBid
     */
    public List<AuctionBid> getBids() {
        return bids;
    }

    /**
     *
     * @return int hours till expiring after time was set
     */
    public int getExpire() {
        return expire;
    }

    /**
     *
     * @return int time the auction was set
     */
    public int getTime() {
        return time;
    }

    /**
     *
     * @return double start-bid
     */
    public double getValue() {
        return value;
    }

    /**
     *
     * @return int number of items
     */
    public int getNum() {
        return item.getAmount();
    }

    /**
     *
     * @return double buyout value
     */
    public double getBuyout() {
        return buyout;
    }

    /**
     *
     * @return int time when item was given
     */
    public int getPicked_up() {
        return picked_up;
    }

    /**
     *
     * @return time when item was sold either buyout or bid
     */
    public boolean isSold() {
        return (sold != 0);
    }
    public int getSold() {
        return sold;
    }

    /**
     *
     * @return UUID of player who won
     */
    public UUID getBuyer() {
        return buyer;
    }

    /**
     *
     * @return UUID of player who sells
     */
    public UUID getSeller() {
        return seller;
    }


    // Find expired
    public AuctionItem(int id, int expire, int time, double value, double buyout, int picked_up, int sold, String buyer, String seller, double fee, List<AuctionBid> bids, boolean notified) {
        this.id = id;
        this.expire = expire;
        this.time = time;
        this.value = value;
        this.buyout = buyout;
        this.picked_up = picked_up;
        this.sold = sold;
        this.buyer = (buyer != null && !buyer.equals("")) ? UUID.fromString(buyer) : null;
        this.seller = (seller != null && !seller.equals("")) ? UUID.fromString(seller) : null;
        this.bids = bids;
        this.fee = fee;
        this.notified = notified;
    }

    public void setBuyer(UUID buyer) {
        this.buyer = buyer;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public double getHighestValue() {
        double value = 0.0;
        for (AuctionBid auctionBid : getBids()) {
            if (auctionBid.getBid() > value) value = auctionBid.getBid();
        }
        return value;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public boolean getNotified() {
        return notified;
    }
    public boolean isExpired() {
        return ((int) System.currentTimeMillis()/1000) > expire;
    }
}
