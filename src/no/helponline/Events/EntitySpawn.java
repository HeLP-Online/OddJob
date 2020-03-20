package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.Zone;
import org.bukkit.Chunk;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.UUID;

public class EntitySpawn implements Listener {
    /**
     * Prevent monster spawn in guilds
     *
     * @param event
     */
    @EventHandler
    public void entitySpawn(EntitySpawnEvent event) {
        Chunk chunk = event.getLocation().getChunk();
        //OddJob.getInstance().getMessageManager().console("x="+chunk.getX()+" z="+chunk.getX()+" event=Spawn");
        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk);
        if (guild != null) {
            Zone zone = OddJob.getInstance().getGuildManager().getZoneByGuild(guild);
            if (zone == Zone.GUILD || zone == Zone.SAFE || zone == Zone.JAIL || zone == Zone.ARENA) {
                if (event.getEntity() instanceof Monster) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
