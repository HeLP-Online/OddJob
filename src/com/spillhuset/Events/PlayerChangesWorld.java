package com.spillhuset.Events;

import com.spillhuset.OddJob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangesWorld implements Listener {
    @EventHandler
    public void init(PlayerChangedWorldEvent event) {
        OddJob.getInstance().getMySQLManager().updateWorld(event.getPlayer().getWorld());
    }
}
