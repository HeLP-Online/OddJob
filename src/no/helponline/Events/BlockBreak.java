package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.Zone;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BlockBreak implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Location location = block.getLocation();
        Chunk chunk = location.getChunk();
        UUID memberOfGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
        UUID chunkInGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk, player.getWorld());
        if (chunkInGuild == null) {
            return;
        }

        if (memberOfGuild == chunkInGuild) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Location location = block.getLocation();
        Chunk chunk = location.getChunk();
        UUID memberOfGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
        UUID chunkInGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk, player.getWorld());
        if (chunkInGuild == null) {
            return;
        }

        if (memberOfGuild == chunkInGuild) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void blockExplode(BlockExplodeEvent event) {
        List<Block> blocks = event.blockList();
        HashMap<Location, BlockData> keep = new HashMap<>();
        for (Block block : blocks) {
            Chunk chunk = block.getChunk();
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk, block.getWorld());
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

    @EventHandler
    public void entityExplode(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();
        HashMap<Location, BlockData> keep = new HashMap<>();
        for (Block block : blocks) {
            Chunk chunk = block.getChunk();
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk, block.getWorld());
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

    @EventHandler
    public void entitySpawn(EntitySpawnEvent event) {
        Chunk chunk = event.getLocation().getChunk();
        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk, event.getLocation().getWorld());
        if (guild != null) {
            Zone zone = OddJob.getInstance().getGuildManager().getZoneByGuild(guild);
            if (zone == Zone.GUILD || zone == Zone.SAFE || zone == Zone.JAIL || zone == Zone.ARENA) {
                if (event.getEntity() instanceof Monster) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void entityInteract(EntityInteractEvent event) {
        UUID chunkInGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(event.getBlock().getChunk(), event.getBlock().getWorld());
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            UUID playerInGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
//TODO
        }
    }

    /*
     * Block break - GUILD,SAFE,WAR,JAIL,ARENA
     * Block place - GUILD,SAFE,WAR,JAIL,ARENA
     * Block explode - GUILD,SAFE,WAR,JAIL,ARENA
     * Entity explode - GUILD,SAFE,WAR,JAIL,ARENA
     * Entity spawn - GUILD,SAFE,JAIL,ARENA
     * Entity interact - GUILD,SAFE,WAR,JAIL,ARENA
     */
}
