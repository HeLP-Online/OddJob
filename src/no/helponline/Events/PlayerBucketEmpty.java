package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.Zone;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.UUID;

public class PlayerBucketEmpty implements Listener {
    /**
     * Prevent use of buckets by non/other guild members (Water & Lava)
     *
     * @param event
     */
    @EventHandler
    public void bucketEmpty(PlayerBucketEmptyEvent event) {
        UUID chunkInGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(event.getBlockClicked().getChunk(), event.getBlockClicked().getWorld());
        UUID memberOfGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(event.getPlayer().getUniqueId());
        if (memberOfGuild == null) {
            memberOfGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD);
        }

        if (chunkInGuild == null || chunkInGuild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
            return;
        }
        if (memberOfGuild.equals(chunkInGuild)) {
            return;
        }

        if (event.getPlayer().isOp()) {
            return;
        }

        event.setCancelled(true);
    }
}
