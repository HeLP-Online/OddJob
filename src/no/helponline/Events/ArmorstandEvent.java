package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class ArmorstandEvent implements Listener {
    @EventHandler
    public void armorStandLock(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
            OddJob.getInstance().log("1");
            Entity entity = event.getRightClicked();
            Player player = event.getPlayer();
            UUID locked = OddJob.getInstance().getLockManager().isLocked(entity);
            if (locked != null) {
                OddJob.getInstance().log("2");
                if (OddJob.getInstance().getLockManager().isLockInfo(player.getUniqueId())) {
                    OddJob.getInstance().log("3");
                    OddJob.getInstance().getMessageManager().warning("Entity locked by " + OddJob.getInstance().getPlayerManager().getName(locked), player.getUniqueId());
                    event.setCancelled(true);
                    return;
                }
                if (OddJob.getInstance().getLockManager().isUnlocking(player.getUniqueId()) && locked.equals(player.getUniqueId())) {
                    OddJob.getInstance().log("4");
                    OddJob.getInstance().getLockManager().unlock(entity);
                    OddJob.getInstance().getMessageManager().warning("Entity unlocked.", player.getUniqueId());
                    event.setCancelled(true);
                    return;
                }
                OddJob.getInstance().log("5");
                OddJob.getInstance().getMessageManager().warning("Entity is locked", player.getUniqueId());
                event.setCancelled(true);
            } else {
                OddJob.getInstance().log("6");
                if (OddJob.getInstance().getLockManager().isLocking(player.getUniqueId())) {
                    OddJob.getInstance().log("7");
                    OddJob.getInstance().getLockManager().lock(player.getUniqueId(), entity);
                    OddJob.getInstance().getMessageManager().success("Entity secure!", player.getUniqueId());
                    event.setCancelled(true);
                }
                OddJob.getInstance().log("8");
            }
            OddJob.getInstance().log("9");
        }
        OddJob.getInstance().log("10");
    }

    @EventHandler
    public void armorStandDestroy(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
            Entity entity = event.getEntity();
            if (OddJob.getInstance().getLockManager().isLocked(entity) != null) {
                event.setDamage(0.0D);

                OddJob.getInstance().getMessageManager().warning("Entity is locked", event.getDamager().getUniqueId());
            }
        }
    }
}
