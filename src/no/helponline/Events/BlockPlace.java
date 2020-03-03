package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.Zone;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class BlockPlace implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Location location = block.getLocation();
        Chunk chunk = location.getChunk();

        // Log Diamond & Emerald
        if ((block.getType().equals(Material.DIAMOND_BLOCK) || block.getType().equals(Material.EMERALD_BLOCK)) && !player.hasPermission("noLog")) OddJob.getInstance().getMySQLManager().addLog(player.getUniqueId(),block,"place");

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
        // TODO CHECK ACCESS
        if (memberOfGuild.equals(chunkInGuild)) {
            return;
        }

        if (player.isOp()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlaceLock(BlockPlaceEvent event) {
        if (event.getItemInHand().equals(OddJob.getInstance().getLockManager().unlockWand) ||
                event.getItemInHand().equals(OddJob.getInstance().getLockManager().lockWand) ||
                event.getItemInHand().equals(OddJob.getInstance().getLockManager().infoWand)) {
            event.setCancelled(true);
            return;
        }
        if (event.getItemInHand().getType().equals(Material.TRIPWIRE_HOOK)) {
            ItemMeta meta = event.getItemInHand().getItemMeta();
            if (meta != null && ChatColor.stripColor(meta.getDisplayName()).startsWith("Key to")) {
                event.setCancelled(true);
            }
        }
        if (event.getItemInHand().equals(OddJob.getInstance().getLockManager().makeSkeletonKey())) {
            OddJob.getInstance().getMessageManager().danger("Sorry, The SKELETON KEY is tooooooooo powerful!", event.getPlayer().getUniqueId(), true);
            event.setCancelled(true);
        }
    }
}
