package no.helponline.Events;

import no.helponline.Managers.LockManager;
import no.helponline.Managers.MessageManager;
import no.helponline.Managers.PlayerManager;
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
            UUID locked = LockManager.isLocked(entity);
            if (locked != null) {
                OddJob.getInstance().log("2");
                if (LockManager.isLockInfo(player.getUniqueId())) {
                    OddJob.getInstance().log("3");
                    MessageManager.warning("Entity locked by " + PlayerManager.getName(locked), player.getUniqueId());
                    event.setCancelled(true);
                    return;
                }
                if (LockManager.isUnlocking(player.getUniqueId()) && locked.equals(player.getUniqueId())) {
                    OddJob.getInstance().log("4");
                    LockManager.unlock(entity);
                    MessageManager.warning("Entity unlocked.", player.getUniqueId());
                    event.setCancelled(true);
                    return;
                }
                OddJob.getInstance().log("5");
                MessageManager.warning("Entity is locked", player.getUniqueId());
                event.setCancelled(true);
            } else {
                OddJob.getInstance().log("6");
                if (LockManager.isLocking(player.getUniqueId())) {
                    OddJob.getInstance().log("7");
                    LockManager.lock(player.getUniqueId(), entity);
                    MessageManager.success("Entity secure!", player.getUniqueId());
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
            if (LockManager.isLocked(entity) != null) {
                event.setDamage(0.0D);

                MessageManager.warning("Entity is locked", event.getDamager().getUniqueId());
            }
        }
    }
}
