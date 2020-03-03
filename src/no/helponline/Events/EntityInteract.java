package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.Utility;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;

import java.util.UUID;

public class EntityInteract implements Listener {

    /**
     * @param event Interact made by a non player entity
     *              <p>
     *              No need to check if block is a chest
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityOpen(EntityInteractEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            // Interacted by non Player
            Block block = event.getBlock();
            boolean locked = false;

            Material t = block.getType();
            if (OddJob.getInstance().getLockManager().getLockable().contains(t)) {
                // Lockable Block
                try {
                    if (OddJob.getInstance().getLockManager().getDoors().contains(t)) {
                        // Door
                        block = Utility.getLowerLeftDoor(block).getBlock();
                    }
                    locked = OddJob.getInstance().getLockManager().isLocked(block.getLocation()) != null;
                } catch (Exception e) {
                    return;
                }
            }

            if (locked) event.setCancelled(true);
        }
    }
}
