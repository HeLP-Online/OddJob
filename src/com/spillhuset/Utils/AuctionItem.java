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

    public int getId() {
        return id;
    }

    public List<AuctionBid> getBids() {
        return bids;
    }

    public int getExpire() {
        return expire;
    }

    public int getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

    public int getNum() {
        return num;
    }

    public double getBuyout() {
        return buyout;
    }

    public int getPicked_up() {
        return picked_up;
    }

    public int getSold() {
        return sold;
    }

    public UUID getBuyer() {
        return buyer;
    }

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
