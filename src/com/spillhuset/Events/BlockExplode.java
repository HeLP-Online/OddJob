package com.spillhuset.Events;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Zone;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
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
        HashMap<Location, BlockData> keep = new HashMap<>();
        Location location;


        for (Block block : blocks) {
            // CHECK GUILD
            Chunk chunk = block.getChunk();
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk);
            if (guild != null && !guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
                event.setCancelled(true);
                keep.put(block.getLocation(), block.getBlockData());
            }

            // CHECK LOCK
            if (OddJob.getInstance().getLockManager().getLockable().contains(block.getType())) {

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
