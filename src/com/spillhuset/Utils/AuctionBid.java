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

    /**
     *
     * @return int ID of bid
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return UUID of player placed the bid
     */
    public UUID getBidder() {
        return bidder;
    }

    /**
     *
     * @return double the actual bid
     */
    public double getBid() {
        return bid;
    }

    /**
     *
     * @return int time bid was set
     */
    public int getTime() {
        return time;
    }

    /**
     *
     * @return int time the bid was refunded
     */
    public boolean isRefunded() {
        return refunded;
    }

    /**
     * @param id       SQLid
     * @param bidder   UUID of bidder
     * @param bid      Integer bid
     * @param time     Integer time of bid
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
