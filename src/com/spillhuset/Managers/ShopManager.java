package com.spillhuset.Managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopManager {
    private ItemStack farmer,forester,jeweler,smith,stone,wood;
    private Inventory farmerInventory,foresterInventory,jewelerInventory,smithInventory,stoneInventory,woodInventory;

    public void loadSmith() {
        smith = new ItemStack(Material.TRAPPED_CHEST);
        smithInventory = Bukkit.createInventory(null, 9, "BLACKSMITH");
        /* iron,copper */
    }

    public void loadWood() {
        wood = new ItemStack(Material.TRAPPED_CHEST);
        woodInventory = Bukkit.createInventory(null, 9, "WOODCUTTER");
        /* log,axe,*/
    }

    public void loadStone() {
        stone = new ItemStack(Material.TRAPPED_CHEST);
        stoneInventory = Bukkit.createInventory(null, 9, "STONECUTTER");
        /* ores,pickAxe */
    }

    public void loadJeweler() {
        jeweler = new ItemStack(Material.TRAPPED_CHEST);
        jewelerInventory = Bukkit.createInventory(null, 9, "JEWELER");
        /* minerals */
    }

    public void loadForester() {
        forester = new ItemStack(Material.TRAPPED_CHEST);
        foresterInventory = Bukkit.createInventory(null, 9, "FORESTER");
        /* saplings,flowers */
    }

    public void loadFarmer() {
        farmer = new ItemStack(Material.TRAPPED_CHEST);
        farmerInventory = Bukkit.createInventory(null, 9, "FARMER");
        /* farming,hoe */
    }

    public void load() {
        loadSmith();
        loadFarmer();
        loadForester();
        loadJeweler();
        loadStone();
        loadWood();
    }
}
