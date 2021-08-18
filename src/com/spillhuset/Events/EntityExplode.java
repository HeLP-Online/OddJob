package com.spillhuset.Events;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Zone;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
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
     */
    @EventHandler
    public void entityExplode(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();
        HashMap<Location, BlockData> keep = new HashMap<>();
        for (Block block : blocks) {
            // Every Block in the explosion
            Chunk chunk = block.getChunk();

            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk);
            if (guild != null && !guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
                // The Chunk is inside a Guild
                event.setCancelled(true);
                keep.put(block.getLocation(), block.getBlockData());
            }

            // CHECK LOCKS
            if (OddJob.getInstance().getLocksManager().getLockable().contains(block.getType()) || OddJob.getInstance().getLocksManager().getDoors().contains(block.getType())) {
                Location location = block.getLocation();
                if (OddJob.getInstance().getLocksManager().isLocked(location)) {
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
