package com.spillhuset.Events;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Zone;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.UUID;

public class PlayerBucketEmpty implements Listener {

    /**
     * Prevent use of buckets by non/other guild members (Water & Lava)
     *
     */
    @EventHandler
    public void bucketEmpty(PlayerBucketEmptyEvent event) {
        UUID chunkInGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(event.getBlockClicked().getChunk());
        UUID memberOfGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(event.getPlayer().getUniqueId());

        if (chunkInGuild == null || chunkInGuild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
            // Player is in the Wild
            return;
        }
        if (memberOfGuild != null && memberOfGuild.equals(chunkInGuild)) {
            // Player is in his own Guild
            return;
        }

        if (event.getPlayer().isOp()) {
            // Player has authorized access
            return;
        }

        event.setCancelled(true);
    }
}
