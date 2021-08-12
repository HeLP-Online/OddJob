package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.AuctionSQL;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AuctionManager {
    public void sell(Player player, double value, double buyout, int expire) {
        OddJob.getInstance().log("Selling to SQL");
        AuctionSQL.sell(player,player.getInventory().getItemInMainHand(),value,buyout,expire);
    }

    public HashMap<String, Object> buyout(int num) {
        OddJob.getInstance().log("Checking SQL");
        HashMap<String,Object> ret = AuctionSQL.buyout(num);
        return ret;
    }
}
