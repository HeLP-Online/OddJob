package com.spillhuset.Events;

import com.spillhuset.OddJob;
import net.luckperms.api.model.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuit implements Listener {
    @EventHandler
    public void leave(PlayerQuitEvent event) {
        //ArenaMechanics.cancel(event.getPlayer());
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        User user = OddJob.getInstance().getLuckPermsApi().getUserManager().getUser(uuid);
        if (user != null) {
            player.setCustomName("[" + user.getPrimaryGroup() + "] " + player.getName());
        } else {
            player.setCustomName(player.getName());
        }
        event.setQuitMessage("["+ ChatColor.RED +"+"+ChatColor.RESET+"] " + event.getPlayer().getCustomName());
        OddJob.getInstance().getScoreManager().clear(event.getPlayer());
        OddJob.getInstance().getTeleportManager().leave(uuid);
    }
}
