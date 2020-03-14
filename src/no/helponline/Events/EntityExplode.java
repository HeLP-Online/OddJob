package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.Zone;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EntityExplode implements Listener {
    /**
     * Cancel Creeper AND TNT
     *
     * @param event
     */
    @EventHandler
    public void entityExplode(EntityExplodeEvent event) {
        // CHECK GUILD
        List<Block> blocks = event.blockList();
        HashMap<Location, BlockData> keep = new HashMap<>();
        Location location;
        for (Block block : blocks) {
            // Every Block in the explosion
            Chunk chunk = block.getChunk();
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk);
            if (!guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
                // The Chunk is inside a Guild
                event.setCancelled(true);
                keep.put(block.getLocation(), block.getBlockData());
            }

            // CHECK DEATHCHEST
            if (block.getType().equals(Material.CHEST)) {
                // The Block is a Chest
                Chest chest = (Chest) block.getState();
                if (chest.getInventory().getHolder() instanceof DoubleChest) {
                    // The Block a DoubleChest
                    DoubleChest doubleChest = (DoubleChest) ((Chest) block.getState()).getInventory().getHolder();
                    if (doubleChest != null) {
                        location = ((Chest) doubleChest.getLeftSide()).getLocation();
                        if (OddJob.getInstance().getDeathManager().isDeathChest(location)) {
                            // Is a DeathChest
                            event.setCancelled(true);
                        }
                    }
                }
            }

            // CHECK LOCKS
            if (OddJob.getInstance().getLockManager().getLockable().contains(block.getType()) || OddJob.getInstance().getLockManager().getDoors().contains(block.getType())) {
                location = block.getLocation();
                if (OddJob.getInstance().getLockManager().isLocked(location)) {
                    event.setCancelled(true);
                }
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
