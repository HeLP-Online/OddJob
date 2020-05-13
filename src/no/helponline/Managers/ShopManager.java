package no.helponline.Managers;

import com.google.common.collect.Sets;
import io.netty.util.collection.ByteCollections;
import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ShopManager {
    private final HashMap<Material, Double> priceBuy = new HashMap<>();
    private final HashMap<Material, Double> priceSell = new HashMap<>();
    private final HashMap<Material,Integer> itemsSold = new HashMap<>();
    private final HashMap<Material,Integer> itemsBought = new HashMap<>();

    private final ItemStack buy;
    private final ItemStack sell;
    private final ItemStack divider;

    public ShopManager() {
        ItemStack itemStack = new ItemStack(Material.MAP);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("BUY");
        List<String> lore = new ArrayList<>();
        lore.add("Buy menu");
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        buy = itemStack;

        itemStack = new ItemStack(Material.FILLED_MAP);
        meta = itemStack.getItemMeta();
        meta.setDisplayName("SELL");
        lore.clear();
        lore.add("Sell menu");
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        sell = itemStack;

        itemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        meta = itemStack.getItemMeta();
        meta.setDisplayName(" ");
        itemStack.setItemMeta(meta);
        divider = itemStack;
    }

    public void buy(Material material, int amount, Player player) {
        double cost = priceBuy.getOrDefault(material,2.5) * amount;
        double wallet = OddJob.getInstance().getEconManager().getPocketBalance(player.getUniqueId());

        if (wallet < cost) {
            OddJob.getInstance().getMessageManager().insufficientFunds(player);
            return;
        }

        itemsBought.put(material,itemsBought.getOrDefault(material,0)+amount);

        ItemStack itemStack = new ItemStack(material, amount);
        OddJob.getInstance().getEconManager().subtractPocketBalance(player.getUniqueId(), cost);
        OddJob.getInstance().getMessageManager().success("You bought " + amount + " of " + material.name() + " for " + cost, player, false);
        player.getInventory().addItem(itemStack);
    }

    public void sell(Material material, int amount, Player player) {
        double cost = priceSell.getOrDefault(material,1.0) * amount;
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
        OddJob.getInstance().getEconManager().addPocketBalance(player.getUniqueId(), cost);
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

    public void menu(Player player) {
        Inventory inventory = Bukkit.createInventory(null,54,"Store");

        inventory.setItem(2,buy);
        inventory.setItem(6,sell);
        for (int i = 9; i <= 17; i++) {
            inventory.setItem(i,divider);
        }

        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack != null && itemStack.getType().equals(Material.AIR)) {
                if (itemStack.getAmount() != 1) {
                    itemStack = new ItemStack(itemStack.getType(),1);
                }
                ItemMeta meta = itemStack.getItemMeta();
                List<String> lore = new ArrayList<>();
                if (priceBuy.get(itemStack.getType()) != null) lore.add("Buying price: "+priceBuy.get(itemStack.getType()));
                if (priceSell.get(itemStack.getType()) != null) lore.add("Selling price: "+priceSell.get(itemStack.getType()));
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
                inventory.setItem(i+18,itemStack);
            }
        }

        player.openInventory(inventory);
    }
}
