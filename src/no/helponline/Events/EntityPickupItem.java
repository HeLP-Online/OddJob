package no.helponline.Events;

import no.helponline.OddJob;
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
            ItemStack itemStack = event.getItem().getItemStack();
            Player player = (Player) event.getEntity();
            if ((itemStack.getType().equals(Material.DIAMOND_BLOCK)
                    || itemStack.getType().equals(Material.EMERALD_BLOCK)
                    || itemStack.getType().equals(Material.EMERALD)
                    || itemStack.getType().equals(Material.DIAMOND)
                    || itemStack.getType().equals(Material.DIAMOND_ORE)
                    || itemStack.getType().equals(Material.EMERALD_ORE)
            ) && !player.hasPermission("noLog"))
                OddJob.getInstance().getMySQLManager().addLog(player.getUniqueId(), itemStack, "pickup");
        }
    }
}
