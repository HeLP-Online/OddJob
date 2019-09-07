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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class onDeath implements Listener {
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
        //OddJob.getInstance().log(i+"");

        if (i < 1) {
            OddJob.getInstance().log("less");
            if (inventory.getHolder() instanceof DoubleChest) {
                OddJob.getInstance().log("double");
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

        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skull = (SkullMeta) playerSkull.getItemMeta();
        if (skull != null) {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
            skull.setDisplayName(ChatColor.DARK_PURPLE + "Head of " + OddJob.getInstance().getPlayerManager().getName(player.getUniqueId()));
            playerSkull.setItemMeta(skull);
        }

        Block leftSide = player.getLocation().getBlock();
        Block rightSide = leftSide.getRelative(0, 0, -1);

        OddJob.getInstance().getDeathManager().add(leftSide, player.getUniqueId());

        leftSide.setType(Material.CHEST);
        rightSide.setType(Material.CHEST);

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

        leftChest.getInventory().setContents(player.getInventory().getContents());
        leftChest.getInventory().addItem(playerSkull);
        event.getDrops().clear();
        player.getInventory().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void breakBlock(BlockBreakEvent event) {
        Location location = null;
        // is a chest
        OddJob.getInstance().log("is a chest");
        if (event.getBlock().getType().equals(Material.CHEST)) {
            Chest chest = (Chest) event.getBlock().getState();
            // is double chest
            OddJob.getInstance().log("is double chest");
            if (chest.getInventory().getHolder() instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest) ((Chest) event.getBlock().getState()).getInventory().getHolder();
                // exists
                OddJob.getInstance().log("exists");
                if (doubleChest != null) {
                    location = ((Chest) doubleChest.getLeftSide()).getLocation();
                    // left side
                    OddJob.getInstance().log("left side");
                    OddJob.getInstance().log(location.toString());
                    if (OddJob.getInstance().getDeathManager().isDeathChest(location)) {
                        // exists
                        OddJob.getInstance().log("exists");
                        if (location != null) {
                            OddJob.getInstance().getDeathManager().replace(location, event.getPlayer().getUniqueId());
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
