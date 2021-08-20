package com.spillhuset.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuctionItem {
    int id;
    int expire;
    int time;
    double value;
    int num;
    double buyout;
    int picked_up;
    int sold;
    UUID buyer;
    UUID seller;
    String item;
    double fee;
    List<AuctionBid> bids = new ArrayList<>();

    public AuctionItem(int id, String item, int expire, int time, double value, int num, double buyout, int picked_up, int sold, String buyer, String seller, double fee, List<AuctionBid> bids) {
        this.item = item;
        this.id = id;
        this.expire = expire;
        this.time = time;
        this.value = value;
        this.num = num;
        this.buyout = buyout;
        this.picked_up = picked_up;
        this.sold = sold;
        this.buyer = UUID.fromString(buyer);
        this.seller = UUID.fromString(seller);
        this.fee = fee;
        this.bids = bids;
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
        return num;
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


    public AuctionItem(int id, int expire, int time, double value, int num, double buyout, int picked_up, int sold, String buyer, String seller, double fee, List<AuctionBid> bids) {
        this.id = id;
        this.expire = expire;
        this.time = time;
        this.value = value;
        this.num = num;
        this.buyout = buyout;
        this.picked_up = picked_up;
        this.sold = sold;
        this.buyer = (buyer != null && !buyer.equals("")) ? UUID.fromString(buyer) : null;
        this.seller = (seller != null && !seller.equals("")) ? UUID.fromString(seller) : null;
        this.bids = bids;
        this.fee = fee;
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

}
