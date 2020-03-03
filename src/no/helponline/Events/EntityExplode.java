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
        List<Block> blocks = event.blockList();
        HashMap<Location, BlockData> keep = new HashMap<>();
        for (Block block : blocks) {
            Chunk chunk = block.getChunk();
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk, block.getWorld());
            if (guild != null && !guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
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
}
