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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

public class BlockBreak implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Location location = block.getLocation();
        Chunk chunk = location.getChunk();

        // CHECK DEATHCHEST
        if (block.getType().equals(Material.CHEST)) {
            // Is a Chest
            block = Utility.getChestPosition(block).getBlock();
            Chest chest = (Chest) event.getBlock().getState();

            if (chest.getInventory().getHolder() instanceof DoubleChest) {
                // Is a DoubleChest
                DoubleChest doubleChest = (DoubleChest) ((Chest) event.getBlock().getState()).getInventory().getHolder();
                if (doubleChest != null) {
                    location = ((Chest) doubleChest.getLeftSide()).getLocation();
                    // Left side
                    if (OddJob.getInstance().getDeathManager().isDeathChest(location)) {
                        // Is a DeathChest
                        OddJob.getInstance().getDeathManager().replace(location, event.getPlayer().getUniqueId());
                        event.setCancelled(true);
                    }
                }
            }
        }

        // CHECK DOOR LOCK
        if (OddJob.getInstance().getLockManager().getDoors().contains(block.getType())) {
            // Door has a Lock
            block = Utility.getLowerLeftDoor(block).getBlock();
        }

        // CHECK BLOCK LOCK
        if (OddJob.getInstance().getLockManager().isLocked(block.getLocation())) {
            // Block is Locked
            UUID uuid = OddJob.getInstance().getLockManager().getLockOwner(block.getLocation());
            if (uuid.equals(event.getPlayer().getUniqueId())) {
                // I own it
                OddJob.getInstance().getLockManager().unlock(block.getLocation());
                OddJob.getInstance().getMessageManager().warning("Lock broken!", event.getPlayer(), true);
            } else {
                // Someone else owns it
                OddJob.getInstance().getMessageManager().danger("This lock is owned by someone else!", event.getPlayer(), false);
                event.setCancelled(true);
            }
        }


        // Log Diamond & Emerald
        if ((block.getType().equals(Material.DIAMOND_BLOCK)
                || block.getType().equals(Material.EMERALD_BLOCK)
                || block.getType().equals(Material.DIAMOND_ORE)
                || block.getType().equals(Material.EMERALD_ORE)
        ) && !player.hasPermission("noLog"))
            OddJob.getInstance().getMySQLManager().addLog(player.getUniqueId(), block, "break");

        // What Guild is the Player member of
        UUID memberOfGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
        if (memberOfGuild == null) {
            // Player has no associated Guild
            memberOfGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD);
        }

        // What Guild owns the Chunk
        UUID chunkInGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk);
        if (chunkInGuild == null || chunkInGuild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
            // If no Guilds owns the Chunk, feel free
            return;
        }

        // Player is breaking a Block in its own Guild
        if (memberOfGuild.equals(chunkInGuild)) {
            return;
        }

        // Authorization override
        if (player.isOp() || player.hasPermission("oddjob.guild.admin")) {
            return;
        }

        // Everyone else is denied to break Block
        event.setCancelled(true);
    }

}
