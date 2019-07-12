package no.helponline.Events;

import no.helponline.Guilds.Guild;
import no.helponline.Guilds.Zone;
import no.helponline.OddJob;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamagePlayer implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            /* WILD FREE FOR ALLE?
               SAFE IS SAFE
               GUILD IS OPTIONAL
               WAR FREE FOR ALL
               ARENA FREE FOR ALL
               JAIL FREE FOR ALL
             */
            // ATTACKED IS A PLAYER
            Player target = (Player) event.getEntity();
            Chunk chunk = target.getLocation().getChunk();
            Zone zone = Zone.WILD;
            Guild guild = OddJob.getInstance().getGuildManager().getGuildByChunk(chunk);
            if (guild != null) {
                // IS THE CHUNK ATTACKED TO ASSIGNED TO A GUILD
                zone = guild.getZone();
                if (event.getDamager() instanceof Player) {
                    if (zone.equals(Zone.SAFE) ||
                            (zone.equals(Zone.WILD) && guild.getConfig("config", "friendlyfire", guild.getId(), true)) ||
                            (zone.equals(Zone.GUILD) && guild.getConfig("guild", "friendlyfire", guild.getId(), true))) {
                        event.setCancelled(true);
                    }
                } else {
                    if (zone.equals(Zone.SAFE) ||
                            (zone.equals(Zone.WILD) && guild.getConfig("config", "damagefromentity", guild.getId(), true)) ||
                            (zone.equals(Zone.GUILD) && guild.getConfig("guild", "damagefromentity", guild.getId(), true))) {
                        event.setCancelled(true);
                    }
                }
            }
        }
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
        }
    }
}
