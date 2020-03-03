package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.Utility;
import no.helponline.Utils.Zone;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BlockBreak implements Listener {
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Location location = block.getLocation();
        Chunk chunk = location.getChunk();

        // Log Diamond & Emerald
        if ((block.getType().equals(Material.DIAMOND_BLOCK) || block.getType().equals(Material.EMERALD_BLOCK)) && !player.hasPermission("noLog")) OddJob.getInstance().getMySQLManager().addLog(player.getUniqueId(),block,"break");

        // PLAYER
        UUID memberOfGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
        if (memberOfGuild == null) {
            memberOfGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD);
        }
        // CHUNK
        UUID chunkInGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk, player.getWorld());
        if (chunkInGuild == null || chunkInGuild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
            return;
        }

        // IN GUILD
        //TODO CHECK ACCESS
        if (memberOfGuild.equals(chunkInGuild)) {
            return;
        }

        if (player.isOp()) {
            return;
        }

        event.setCancelled(true);
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

    /**
     * @param event Breaking block
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBreakLock(BlockBreakEvent event) {
        Block block = event.getBlock();
        // CHEST ?
        if (block.getType().equals(Material.CHEST)) {
            block = Utility.getChestPosition(block).getBlock();
        }
        // DOOR ?
        else if (OddJob.getInstance().getLockManager().getDoors().contains(block.getType())) {
            block = Utility.getLowerLeftDoor(block).getBlock();
        }

        UUID uuid = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
        // LOCKED BY A PLAYER ?
        if (uuid != null) {
            if (uuid.equals(event.getPlayer().getUniqueId())) {
                OddJob.getInstance().getLockManager().unlock(block.getLocation());
                OddJob.getInstance().getMessageManager().warning("Lock broken!", event.getPlayer(), true);
            } else {
                OddJob.getInstance().getMessageManager().danger("This lock is owned by someone else!", event.getPlayer(), false);
                event.setCancelled(true);
            }
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
