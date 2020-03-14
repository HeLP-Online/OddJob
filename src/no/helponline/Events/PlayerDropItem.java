package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDropItem implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent event) {
        // Log Diamond & Emerald
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();

        // Log Diamond & Emerald
        if ((itemStack.getType().equals(Material.DIAMOND_BLOCK)
                || itemStack.getType().equals(Material.EMERALD_BLOCK)
                || itemStack.getType().equals(Material.EMERALD)
                || itemStack.getType().equals(Material.DIAMOND)
                || itemStack.getType().equals(Material.EMERALD_ORE)
                || itemStack.getType().equals(Material.DIAMOND_ORE)
        ) && !player.hasPermission("noLog"))
            OddJob.getInstance().getMySQLManager().addLog(player.getUniqueId(), itemStack, "drop");

        // Locks prevent drop of Skeletonkey
        if (event.getItemDrop().getItemStack().equals(OddJob.getInstance().getLockManager().skeletonKey)) {
            OddJob.getInstance().getMessageManager().danger("Sorry, The SKELETON KEY can't be cast away like that!", event.getPlayer().getUniqueId(), true);
            event.setCancelled(true);
        } else if (itemStack.equals(OddJob.getInstance().getLockManager().unlockWand) ||
                itemStack.equals(OddJob.getInstance().getLockManager().lockWand) ||
                itemStack.equals(OddJob.getInstance().getLockManager().infoWand)) {
            event.setCancelled(true);
        }
    }
}
