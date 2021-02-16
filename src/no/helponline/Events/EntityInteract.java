package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.Utility;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

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

            Material material = block.getType();
            if (OddJob.getInstance().getLockManager().getLockable().contains(material)) {
                // Lockable Block
                OddJob.getInstance().log("Lockable");
                try {
                    if (OddJob.getInstance().getLockManager().getDoors().contains(material)) {
                        OddJob.getInstance().log("Door");
                        // Door
                        block = Utility.getLowerLeftDoor(block).getBlock();
                    }
                    locked = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
                } catch (Exception e) {
                    return;
                }
            }

            if (locked) event.setCancelled(true);
        } else {
            OddJob.getInstance().getPlayerManager().abort(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void test(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity.getType().equals(EntityType.ARMOR_STAND) && OddJob.getInstance().getDeathManager().getOwners().containsKey(entity.getUniqueId())) {
            ArmorStand armorStand = (ArmorStand) entity;
            Player player = event.getPlayer();
            armorStand.getEquipment().clear();
            player.openInventory(OddJob.getInstance().getDeathManager().getInventory(armorStand.getUniqueId()));
        }

    }
}
