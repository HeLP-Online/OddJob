package no.helponline.Utils;

import no.helponline.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class Lore {
    public static boolean lore(PlayerInteractEvent event, Player player, boolean door, UUID uuid, Block block, ItemMeta meta, boolean guild) {
        try {
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                if (lore != null && lore.size() > 1) {
                    // Key has Lore
                    String one = ChatColor.stripColor(lore.get(1));
                    UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk());
                    if (!guild && !one.equalsIgnoreCase(uuid.toString())) {
                        // Wrong Player Key
                        OddJob.getInstance().getMessageManager().warning("You don't have the correct Player-Key", player, false);
                        event.setCancelled(true);
                        return true;
                    } else if (guild && !one.equalsIgnoreCase(guildUUID.toString())) {
                        // Wrong Guild Key
                        OddJob.getInstance().getMessageManager().warning("You don't have the correct Guild-Key", player, false);
                        event.setCancelled(true);
                        return true;
                    }

                    // Opened with the correct Key
                    OddJob.getInstance().getMessageManager().success("Lock opened by " + meta.getDisplayName(), player, true);
                    if (door) {
                        // Door
                        Utility.doorToggle(block);
                        player.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
                        event.setCancelled(true);
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
