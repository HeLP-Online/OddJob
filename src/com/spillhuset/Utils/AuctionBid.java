package com.spillhuset.Utils;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.AuctionSQL;
import com.spillhuset.Utils.Enum.Types;
import com.spillhuset.Utils.Odd.OddPlayer;
import org.bukkit.entity.Player;

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

    /**
     * Refunds the current bid
     */
    public void refund(double offer, Player target, boolean buyout,int item,UUID sender) {
        // Refund to the bank
        OddJob.getInstance().getCurrencyManager().add(bidder,bid, Types.AccountType.bank);

        OddJob.getInstance().getMessageManager().auctionsOverBid(offer,target,buyout,item,sender);
    }
}
