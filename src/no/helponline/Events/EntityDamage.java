package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamage implements Listener {
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity target = event.getEntity();
        if (target instanceof Player && !target.isOp()) {
            OddJob.getInstance().getPlayerManager().setInCombat(target.getUniqueId());
        }
    }
}
