package no.helponline.Managers;

import com.google.common.collect.Sets;
import io.netty.util.collection.ByteCollections;
import no.helponline.OddJob;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ShopManager {
    private HashMap<Material, Double> priceBuy = new HashMap<>();
    private HashMap<Material, Double> priceSell = new HashMap<>();
    private HashMap<Material,Integer> itemsSold = new HashMap<>();
    private HashMap<Material,Integer> itemsBought = new HashMap<>();

    public void buy(Material material, int amount, Player player) {
        Double cost = priceBuy.getOrDefault(material,2.5) * amount;
        Double wallet = OddJob.getInstance().getEconManager().getBalance(player.getUniqueId());

        if (wallet < cost) {
            OddJob.getInstance().getMessageManager().insufficientFunds(player);
            return;
        }

        itemsBought.put(material,itemsBought.getOrDefault(material,0)+amount);

        ItemStack itemStack = new ItemStack(material, amount);
        OddJob.getInstance().getEconManager().subtract(player.getUniqueId(), cost, false);
        OddJob.getInstance().getMessageManager().success("You bought " + amount + " of " + material.name() + " for " + cost, player, false);
        player.getInventory().addItem(itemStack);
    }

    public void sell(Material material, int amount, Player player) {
        Double cost = priceSell.getOrDefault(material,1.0) * amount;
        OddJob.getInstance().getMessageManager().console("value "+cost);
        if (!player.getInventory().contains(material, amount)) {
            OddJob.getInstance().getMessageManager().console("insufficient");
            OddJob.getInstance().getMessageManager().insufficientItems(player);
            return;
        }

        OddJob.getInstance().getMessageManager().console("removing");
        for (int i = 1; i <= amount; i++) {
            player.getInventory().remove(material);
        }

        OddJob.getInstance().getMessageManager().console("updating");
        itemsSold.put(material,itemsSold.getOrDefault(material,0) + amount);

        OddJob.getInstance().getMessageManager().console("giving money");
        OddJob.getInstance().getEconManager().add(player.getUniqueId(), cost, false);
        OddJob.getInstance().getMessageManager().success("You sold " + amount + " of " + material.name() + " for " + cost, player, false);
    }

    public void save() {
        OddJob.getInstance().getMessageManager().console("saving");
        Set<Material> set = Sets.union(itemsBought.keySet(),itemsSold.keySet());
        for (Material mat : set) {
            int diff = (itemsSold.getOrDefault(mat,0) - itemsBought.getOrDefault(mat,0))/100;
            double buyOld = priceBuy.getOrDefault(mat,10000D);
            double buyNew = buyOld+diff;
            priceBuy.put(mat,buyNew);
            OddJob.getInstance().getMessageManager().console("Buy: "+mat.name()+" old: "+buyOld+"; new: "+buyNew);

            double sellOld = priceSell.getOrDefault(mat,1.0);
            double sellNew = sellOld+diff;
            priceSell.put(mat,sellNew);
            OddJob.getInstance().getMessageManager().console("Sell: "+mat.name()+" old: "+sellOld+"; new: "+sellNew);
        }
        //OddJob.getInstance().getMySQLManager().savePriceBuy(priceBuy);
    }
}
