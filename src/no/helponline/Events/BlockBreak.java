package no.helponline.Events;

import no.helponline.Guilds.Guild;
import no.helponline.Guilds.Zone;
import no.helponline.OddJob;
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

public class BlockBreak implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Location location = block.getLocation();
        Chunk chunk = location.getChunk();
        Guild memberOfGuild = OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId());
        Guild chunkInGuild = OddJob.getInstance().getGuildManager().getGuildByChunk(chunk);
        if (chunkInGuild == null || chunkInGuild.getZone().equals(Zone.WILD)) {
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
        Guild memberOfGuild = OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId());
        Guild chunkInGuild = OddJob.getInstance().getGuildManager().getGuildByChunk(chunk);
        if (chunkInGuild == null || chunkInGuild.getZone().equals(Zone.WILD)) {
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
            Guild guild = OddJob.getInstance().getGuildManager().getGuildByChunk(chunk);
            if (guild != null && guild.getZone() != Zone.WILD) {
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
            Guild guild = OddJob.getInstance().getGuildManager().getGuildByChunk(chunk);
            if (guild != null && guild.getZone() != Zone.WILD) {
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
        Guild guild = OddJob.getInstance().getGuildManager().getGuildByChunk(chunk);
        if (guild != null) {
            Zone zone = guild.getZone();
            if (zone == Zone.GUILD || zone == Zone.SAFE || zone == Zone.JAIL || zone == Zone.ARENA) {
                if (event.getEntity() instanceof Monster) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void entityInteract(EntityInteractEvent event) {
        Guild chunkInGuild = OddJob.getInstance().getGuildManager().getGuildByChunk(event.getBlock().getChunk());
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Guild playerInGuild = OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId());

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
