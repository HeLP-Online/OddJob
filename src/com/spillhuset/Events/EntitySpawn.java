package com.spillhuset.Events;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Zone;
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
        if (guild != null && !guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
            Zone zone = OddJob.getInstance().getGuildManager().getZoneByGuild(guild);
            if (zone == Zone.GUILD || zone == Zone.SAFE || zone == Zone.JAIL || zone == Zone.ARENA) {
                if (event.getEntity() instanceof Monster) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
