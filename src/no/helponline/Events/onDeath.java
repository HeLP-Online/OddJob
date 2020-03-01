package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class onDeath implements Listener {

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();
        Location location;
        for (Block block : blocks) {
            // Is a Chest
            if (block.getType().equals(Material.CHEST)) {
                Chest chest = (Chest) block.getState();
                // Is a DoubleChest
                if (chest.getInventory().getHolder() instanceof DoubleChest) {
                    DoubleChest doubleChest = (DoubleChest) ((Chest) block.getState()).getInventory().getHolder();
                    if (doubleChest != null) {
                        location = ((Chest) doubleChest.getLeftSide()).getLocation();
                        // Left side
                        if (OddJob.getInstance().getDeathManager().isDeathChest(location)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onExplosion(BlockExplodeEvent event) {
        List<Block> blocks = event.blockList();
        Location location;
        for (Block block : blocks) {
            // Is a Chest
            if (block.getType().equals(Material.CHEST)) {
                Chest chest = (Chest) block.getState();
                // Is a DoubleChest
                if (chest.getInventory().getHolder() instanceof DoubleChest) {
                    DoubleChest doubleChest = (DoubleChest) ((Chest) block.getState()).getInventory().getHolder();
                    if (doubleChest != null) {
                        location = ((Chest) doubleChest.getLeftSide()).getLocation();
                        // Left side
                        if (OddJob.getInstance().getDeathManager().isDeathChest(location)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void inventory(InventoryCloseEvent event) {
        Player player = null;
        HumanEntity human = event.getPlayer();
        if (human instanceof Player) {
            player = (Player) human;
        }

        Inventory inventory = event.getInventory();

        int i = 0;
        for (ItemStack is : inventory.getContents()) {
            if (is != null) i++;
        }

        if (i < 1) {
            if (inventory.getHolder() instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest) inventory.getHolder();
                Chest left = (Chest) doubleChest.getLeftSide();
                if (left != null) {
                    OddJob.getInstance().getDeathManager().replace(left.getLocation(), player.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Make the PlayerSkull
        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skull = (SkullMeta) playerSkull.getItemMeta();
        if (skull != null) {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
            skull.setDisplayName(ChatColor.DARK_PURPLE + "Head of " + OddJob.getInstance().getPlayerManager().getName(player.getUniqueId()));
            playerSkull.setItemMeta(skull);
        }

        // Find the ChestBlocks
        Block leftSide = player.getLocation().getBlock();
        Block rightSide = leftSide.getRelative(0, 0, -1);
        leftSide.setType(Material.CHEST);
        rightSide.setType(Material.CHEST);

        // Add it to the list
        OddJob.getInstance().getDeathManager().add(leftSide, player.getUniqueId());

        // Make the chest the correct side
        BlockData leftData = leftSide.getBlockData();
        ((Directional) leftData).setFacing(BlockFace.EAST);
        leftSide.setBlockData(leftData);
        org.bukkit.block.data.type.Chest chestDataLeft = (org.bukkit.block.data.type.Chest) leftData;
        chestDataLeft.setType(org.bukkit.block.data.type.Chest.Type.RIGHT);
        leftSide.setBlockData(chestDataLeft);
        Chest leftChest = (Chest) leftSide.getState();
        BlockData rightData = rightSide.getBlockData();
        ((Directional) rightData).setFacing(BlockFace.EAST);
        rightSide.setBlockData(rightData);
        org.bukkit.block.data.type.Chest chestDataRight = (org.bukkit.block.data.type.Chest) rightData;
        chestDataRight.setType(org.bukkit.block.data.type.Chest.Type.LEFT);
        rightSide.setBlockData(chestDataRight);

        // Put Items inside the Chest
        leftChest.getInventory().setContents(player.getInventory().getContents());
        leftChest.getInventory().addItem(player.getInventory().getArmorContents());
        leftChest.getInventory().addItem(player.getInventory().getExtraContents());
        leftChest.getInventory().addItem(playerSkull);

        // Clean up
        event.getDrops().clear();
        player.getInventory().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void breakBlock(BlockBreakEvent event) {
        Location location;
        // Is a Chest
        if (event.getBlock().getType().equals(Material.CHEST)) {
            Chest chest = (Chest) event.getBlock().getState();
            // Is a DoubleChest
            if (chest.getInventory().getHolder() instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest) ((Chest) event.getBlock().getState()).getInventory().getHolder();
                if (doubleChest != null) {
                    location = ((Chest) doubleChest.getLeftSide()).getLocation();
                    // Left side
                    if (OddJob.getInstance().getDeathManager().isDeathChest(location)) {
                        OddJob.getInstance().getDeathManager().replace(location, event.getPlayer().getUniqueId());
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
