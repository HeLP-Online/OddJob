package com.spillhuset.Events;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Zone;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class EntityDamageByEntity implements Listener {
    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        EntityType type = event.getDamager().getType();

        UUID entityGuild = null;
        UUID damagerGuild = null;
        Player player = null;
        Player damager = null;

        // The one who does damage is a Player
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
            damagerGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(event.getDamager().getUniqueId());
            // Set in combat
            OddJob.getInstance().getPlayerManager().setInCombat(event.getDamager().getUniqueId());
        }

        // The on whom receive damage is a Player
        if (event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
            entityGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(event.getEntity().getUniqueId());
            // Set in combat
            OddJob.getInstance().getPlayerManager().setInCombat(event.getEntity().getUniqueId());
        }

        // Creeper explosion
        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            Location location = event.getEntity().getLocation();
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(location.getChunk());
            Zone zone = OddJob.getInstance().getGuildManager().getZoneByGuild(guild);
            if (zone != Zone.WILD) {
                event.setDamage(0);
                event.setCancelled(true);
            }
            return;
        }

        // A Player receive damage, the damaging player is in your guild
        if (player != null && damagerGuild != null && damagerGuild.equals(entityGuild)) {
            if (OddJob.getInstance().getGuildManager().getGuild(damagerGuild).getFriendlyFire()) {
                return;
            }
            event.setDamage(0);
            event.setCancelled(true);
            OddJob.getInstance().getMessageManager().guildFriendlyFireDisabled(damager);
            return;
        }

        // Armor_stand received damage and is a spirit
        if (event.getEntity().getType().equals(EntityType.ARMOR_STAND) && OddJob.getInstance().getDeathManager().getOwners().containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            if (event.getDamager() instanceof Player) {
                OddJob.getInstance().getDeathManager().replace(event.getEntity(), null);
            }
            return;
        }

        // Armor_stand is attacked by a Player
        if (event.getEntity().getType().equals(EntityType.ARMOR_STAND) && event.getDamager() instanceof Player) {
            Entity entity = event.getEntity();

            // The armor_stand is Locked
            if (OddJob.getInstance().getLocksManager().isLocked(entity)) {
                UUID locked = OddJob.getInstance().getLocksManager().getLockOwner(entity);

                // Weapon is InfoWand
                if (OddJob.getInstance().getPlayerManager().getPlayer(damager.getUniqueId()).getInventory().getItemInMainHand().equals(OddJob.getInstance().getLocksManager().infoWand)) {
                    OddJob.getInstance().getMessageManager().lockEntityDamage(OddJob.getInstance().getPlayerManager().getName(locked), damager);
                    event.setCancelled(true);
                } else
                    // Weapon is UnlockWand
                    if (OddJob.getInstance().getPlayerManager().getPlayer(damager.getUniqueId()).getInventory().getItemInMainHand().equals(OddJob.getInstance().getLocksManager().unlockWand)) {
                        OddJob.getInstance().getLocksManager().unlock(entity);
                        OddJob.getInstance().getMessageManager().lockEntityUnlock(damager);
                        OddJob.getInstance().getLocksManager().remove(damager.getUniqueId());
                        event.setCancelled(true);
                    }
                    // Not your target_dummy
                    else {
                        event.setDamage(0.0D);
                        event.setCancelled(true);
                    }
            }
            // Armor_stand is not locked
            else {
                // Weapon is LockTool
                if (OddJob.getInstance().getPlayerManager().getPlayer(damager.getUniqueId()).getInventory().getItemInMainHand().equals(OddJob.getInstance().getLocksManager().lockWand)) {
                    OddJob.getInstance().getLocksManager().lock(damager.getUniqueId(), entity);
                    OddJob.getInstance().getMessageManager().lockEntityLock(damager);
                    OddJob.getInstance().getLocksManager().remove(damager.getUniqueId());
                    event.setCancelled(true);
                }
            }
        }
    }
}
