package no.helponline.Events;

import no.helponline.Guilds.Guild;
import no.helponline.Guilds.Zone;
import no.helponline.OddJob;
import org.bukkit.Chunk;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamagePlayer implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            OddJob.getInstance().log("Player get damage");
            /* WILD FREE FOR ALL?
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
                OddJob.getInstance().log("Is inside any chunk assigned to " + guild.getName());
                // IS THE CHUNK ATTACKED TO ASSIGNED TO A GUILD
                zone = guild.getZone();
                if (event.getDamager() instanceof Player) {
                    OddJob.getInstance().log("player attacking player");
                    if (zone.equals(Zone.SAFE) ||
                            (zone.equals(Zone.WILD) && !guild.getConfig("config", "friendlyfire", guild.getId(), true)) ||
                            (zone.equals(Zone.GUILD) && !guild.getConfig("guild", "friendlyfire", guild.getId(), true))) {
                        OddJob.getInstance().log("Inside safety");
                        event.setCancelled(true);
                    }
                } else {
                    if (zone.equals(Zone.SAFE) ||
                            (zone.equals(Zone.WILD) && !guild.getConfig("config", "damagefromentity", guild.getId(), true)) ||
                            (zone.equals(Zone.GUILD) && !guild.getConfig("guild", "damagefromentity", guild.getId(), true))) {
                        OddJob.getInstance().log("Player damaged by entity");
                        event.setCancelled(true);
                    }
                }
            }
            if (zone.equals(Zone.WILD)) {
                OddJob.getInstance().log("Outside of a guild");
                if (event.getDamager() instanceof Player) {
                    Player damager = (Player) event.getDamager();
                    guild = OddJob.getInstance().getGuildManager().getGuildByMember(target.getUniqueId());
                    if (guild.equals(OddJob.getInstance().getGuildManager().getGuildByMember(damager.getUniqueId()))) {
                        if (!guild.getConfig("guild", "friendlyfire", guild.getId(), true)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
        if (event.getDamager() instanceof Player) {
            OddJob.getInstance().log("Damage by player");
            Player damager = (Player) event.getDamager();
            if (event.getEntity() instanceof Monster) {
                OddJob.getInstance().log("Damage monster by player");
            }
        }
    }
}
