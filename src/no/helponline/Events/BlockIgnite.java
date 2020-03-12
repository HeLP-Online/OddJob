package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

import java.util.UUID;

public class BlockIgnite implements Listener {
    /**
     * Cancel Ignition
     *
     * @param event
     */
    @EventHandler
    public void blockIgnite(BlockIgniteEvent event) {
        if (event.getBlock().getType() == Material.TNT) {
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(event.getBlock().getLocation().getChunk());
            if (guild != null) {
                event.setCancelled(true);
            }
        }
    }
}
