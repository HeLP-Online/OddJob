package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BlockExplode implements Listener {
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
    /**
     * Cancel Explode
     *
     * @param event
     */
    @EventHandler
    public void blockExplode(BlockExplodeEvent event) {
        List<Block> blocks = event.blockList();
        HashMap<Location, BlockData> keep = new HashMap<>();
        for (Block block : blocks) {
            Chunk chunk = block.getChunk();
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk);
            if (guild != null) {
                event.setCancelled(true);
                keep.put(block.getLocation(), block.getBlockData());
            }
        }
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (Location location : keep.keySet()) {
                    location.getBlock().setBlockData(keep.get(location));
                }
            }
        };
        runnable.runTaskLater(OddJob.getInstance(), 20L);
    }
}
