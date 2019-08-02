package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class onDeath implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skull = (SkullMeta) playerSkull.getItemMeta();
        if (skull != null) {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
            skull.setDisplayName(ChatColor.DARK_PURPLE + "Head of " + OddJob.getInstance().getPlayerManager().getName(player.getUniqueId()));
            playerSkull.setItemMeta(skull);
        }
        player.getWorld().dropItem(player.getLocation(), playerSkull);

    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

    }
}
