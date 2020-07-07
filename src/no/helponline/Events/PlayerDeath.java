package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location location = player.getLocation();

        // ArmorStand
        Entity entity = null;
        try {
            entity = location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        OddJob.getInstance().getDeathManager().add(entity, player);

        // Clean up
        event.getDrops().clear();
    }
}
