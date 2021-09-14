package com.spillhuset.Events;

import com.spillhuset.OddJob;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class PlayerSpawnLocation  implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSpawnLocationEvent(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        Location location = event.getSpawnLocation();
        if (location.getWorld() != null) {
            GameMode gameMode = OddJob.getInstance().getPlayerManager().getGameMode(player.getUniqueId(),location.getWorld().getUID());
            if (!gameMode.equals(player.getGameMode())) {
                OddJob.getInstance().getPlayerManager().setGameMode(player,gameMode,false);
            }
        }
    }
}
