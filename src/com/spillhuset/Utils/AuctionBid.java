package com.spillhuset.Utils;

import com.spillhuset.SQL.AuctionSQL;

import java.util.UUID;

public class AuctionBid {
    int id;
    UUID bidder;
    double bid;
    int time;
    boolean refunded;

    public void setRefunded(boolean refunded) {
        this.refunded = refunded;
        AuctionSQL.saveBid(this);
    }

    public int getId() {
        return id;
    }

    public UUID getBidder() {
        return bidder;
    }

    public double getBid() {
        return bid;
    }

    public int getTime() {
        return time;
    }

    public boolean isRefunded() {
        return refunded;
    }

    /**
     *
     * @param id SQLid
     * @param bidder UUID of bidder
     * @param bid Integer bid
     * @param time Integer time of bid
     * @param refunded Boolean if bid is refunded
     */
    public AuctionBid(int id, UUID bidder, int bid, int time, int refunded) {
        this.id = id;
        this.bidder = bidder;
        this.bid = bid;
        this.time = time;
        this.refunded = refunded != 0;
    }
}
