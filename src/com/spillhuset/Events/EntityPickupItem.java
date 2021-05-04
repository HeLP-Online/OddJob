package com.spillhuset.Events;

import com.spillhuset.OddJob;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class EntityPickupItem implements Listener {
    @EventHandler
    public void pickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            // A Player picking up items
            ItemStack itemStack = event.getItem().getItemStack();
            Player player = (Player) event.getEntity();
            if ((itemStack.getType().equals(Material.DIAMOND_BLOCK)
                    || itemStack.getType().equals(Material.EMERALD_BLOCK)
                    || itemStack.getType().equals(Material.EMERALD)
                    || itemStack.getType().equals(Material.DIAMOND)
                    || itemStack.getType().equals(Material.DIAMOND_ORE)
                    || itemStack.getType().equals(Material.EMERALD_ORE)
            ) && !player.hasPermission("noLog"))
                // Log the event
                OddJob.getInstance().getMySQLManager().addLog(player.getUniqueId(), itemStack, "pickup");
        }
    }
}
