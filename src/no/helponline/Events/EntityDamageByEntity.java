package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class EntityDamageByEntity implements Listener {
    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            // Damager is a Player
            OddJob.getInstance().getPlayerManager().setInCombat(event.getDamager().getUniqueId());
        }
        if (event.getEntity() instanceof Player) {
            // Damaged is a Player
            OddJob.getInstance().getPlayerManager().setInCombat(event.getEntity().getUniqueId());
        }
        if (event.getEntity().getType().equals(EntityType.ARMOR_STAND) && event.getDamager() instanceof Player) {
            // A Player damages an ArmorStand
            Entity entity = event.getEntity();
            Player player = (Player) event.getDamager();

            if (OddJob.getInstance().getLockManager().isLocked(entity)) {
                // ArmorStand has a Lock
                UUID locked = OddJob.getInstance().getLockManager().getLockOwner(entity);
                if (OddJob.getInstance().getLockManager().isLockInfo(player.getUniqueId())) {
                    // Using InfoWand
                    OddJob.getInstance().getMessageManager().warning("Entity locked by " + ChatColor.AQUA + OddJob.getInstance().getPlayerManager().getName(locked), player.getUniqueId(), false);
                    event.setCancelled(true);
                } else if (OddJob.getInstance().getLockManager().isUnlocking(player.getUniqueId()) && locked.equals(player.getUniqueId())) {
                    // Using UnLockWand
                    OddJob.getInstance().getLockManager().unlock(entity);
                    OddJob.getInstance().getMessageManager().warning("Entity unlocked.", player.getUniqueId(), true);
                    OddJob.getInstance().getLockManager().remove(player.getUniqueId());
                    event.setCancelled(true);
                } else {
                    // Entity is Locked
                    event.setDamage(0.0D);
                    event.setCancelled(true);
                }
            } else {
                // Entity is not Locked
                if (OddJob.getInstance().getLockManager().isLocking(player.getUniqueId())) {
                    // Using LockWand
                    OddJob.getInstance().getLockManager().lock(player.getUniqueId(), entity);
                    OddJob.getInstance().getMessageManager().success("Entity secure!", player.getUniqueId(), true);
                    OddJob.getInstance().getLockManager().remove(player.getUniqueId());
                    event.setCancelled(true);
                }
            }
        }
    }
}
