package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.Zone;
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
        if ((block.getType().equals(Material.DIAMOND_BLOCK)
                || block.getType().equals(Material.EMERALD_BLOCK)
                || block.getType().equals(Material.DIAMOND_ORE)
                || block.getType().equals(Material.EMERALD_ORE)
        ) && !player.hasPermission("noLog"))
            OddJob.getInstance().getMySQLManager().addLog(player.getUniqueId(), block, "place");

        // What Guild is the Player member of
        UUID memberOfGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
        if (memberOfGuild == null) {
            memberOfGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD);
        }

        // Prevent placement of the Locking tools and keys
        if (event.getItemInHand().equals(OddJob.getInstance().getLockManager().unlockWand) ||
                event.getItemInHand().equals(OddJob.getInstance().getLockManager().lockWand) ||
                event.getItemInHand().equals(OddJob.getInstance().getLockManager().infoWand)) {
            event.setCancelled(true);
            return;
        } else if (event.getItemInHand().getType().equals(Material.TRIPWIRE_HOOK)) {
            ItemMeta meta = event.getItemInHand().getItemMeta();
            if (meta != null && ChatColor.stripColor(meta.getDisplayName()).startsWith("Key to")) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getItemInHand().equals(OddJob.getInstance().getLockManager().skeletonKey)) {
            OddJob.getInstance().getMessageManager().danger("Sorry, The SKELETON KEY is tooooooooo powerful!", event.getPlayer().getUniqueId(), true);
            event.setCancelled(true);
            return;
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
