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
        UUID entityGuild = null;
        UUID damagerGuild = null;
        Player player = null;
        Player damager = null;
        if (event.getDamager() instanceof Player) {
            // Damager is a Player
            damager = (Player) event.getDamager();
            // Check Guild
            damagerGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(event.getDamager().getUniqueId());
            OddJob.getInstance().getPlayerManager().setInCombat(event.getDamager().getUniqueId());
        }
        if (event.getEntity() instanceof Player) {
            // Damaged is a Player
            player = (Player) event.getEntity();
            // Check Guild
            entityGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(event.getEntity().getUniqueId());
            OddJob.getInstance().getPlayerManager().setInCombat(event.getEntity().getUniqueId());
        }

        if (player != null && damagerGuild != null && damagerGuild.equals(entityGuild)) {
            event.setDamage(0);
            event.setCancelled(true);
            return;
        }

        if (event.getEntity().getType().equals(EntityType.ARMOR_STAND) && OddJob.getInstance().getDeathManager().getOwners().containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            OddJob.getInstance().getDeathManager().replace(event.getEntity(), null);
            return;
        }

        if (event.getEntity().getType().equals(EntityType.ARMOR_STAND) && event.getDamager() instanceof Player) {
            // A Player damages an ArmorStand
            Entity entity = event.getEntity();

            if (OddJob.getInstance().getLockManager().isLocked(entity)) {
                // ArmorStand has a Lock
                UUID locked = OddJob.getInstance().getLockManager().getLockOwner(entity);
                if (OddJob.getInstance().getPlayerManager().getPlayer(damager.getUniqueId()).getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().infoWand)) {
                    // Using InfoWand
                    OddJob.getInstance().getMessageManager().warning("Entity locked by " + ChatColor.AQUA + OddJob.getInstance().getPlayerManager().getName(locked), damager.getUniqueId(), false);
                    event.setCancelled(true);
                } else if (OddJob.getInstance().getPlayerManager().getPlayer(damager.getUniqueId()).getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().unlockWand)) {
                    // Using UnLockWand
                    OddJob.getInstance().getLockManager().unlock(entity);
                    OddJob.getInstance().getMessageManager().warning("Entity unlocked.", damager.getUniqueId(), true);
                    OddJob.getInstance().getLockManager().remove(damager.getUniqueId());
                    event.setCancelled(true);
                } else {
                    // Entity is Locked
                    event.setDamage(0.0D);
                    event.setCancelled(true);
                }
            } else {
                // Entity is not Locked
                if (OddJob.getInstance().getPlayerManager().getPlayer(damager.getUniqueId()).getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().lockWand)) {
                    // Using LockWand
                    OddJob.getInstance().getLockManager().lock(damager.getUniqueId(), entity);
                    OddJob.getInstance().getMessageManager().success("Entity secure!", damager.getUniqueId(), true);
                    OddJob.getInstance().getLockManager().remove(damager.getUniqueId());
                    event.setCancelled(true);
                }
            }
        }
    }
}
