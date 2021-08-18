package com.spillhuset.Events;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Utility;
import com.spillhuset.Utils.Enum.Zone;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

        // CHECK DOOR LOCK
        if (OddJob.getInstance().getLocksManager().getDoors().contains(block.getType())) {
            // Door has a Lock
            block = Utility.getLowerLeftDoor(block).getBlock();
        }

        // CHECK BLOCK LOCK
        if (OddJob.getInstance().getLocksManager().isLocked(block.getLocation())) {
            // Block is Locked
            UUID uuid = OddJob.getInstance().getLocksManager().getLockOwner(block.getLocation());
            if (uuid.equals(event.getPlayer().getUniqueId())) {
                // I own it
                OddJob.getInstance().getLocksManager().unlock(block.getLocation());
                OddJob.getInstance().getMessageManager().broken(uuid);
            } else {
                // Someone else owns it
                OddJob.getInstance().getMessageManager().ownedOther(uuid);
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
        if (player.isOp() || player.hasPermission("guild.admin")) {
            return;
        }

        // Everyone else is denied to break Block
        event.setCancelled(true);
    }

}
