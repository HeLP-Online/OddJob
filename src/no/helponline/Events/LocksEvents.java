package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.Utility;
import no.helponline.Utils.Zone;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class LocksEvents implements Listener {

    /**
     * @param event Interact made by a non player entity
     *              <p>
     *              No need to check if block is a chest
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityOpen(EntityInteractEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            // Interacted by non Player
            Block block = event.getBlock();
            boolean locked = false;

            Material t = block.getType();
            if (OddJob.getInstance().getLockManager().getLockable().contains(t)) {
                // Lockable Block
                try {
                    if (OddJob.getInstance().getLockManager().getDoors().contains(t)) {
                        // Door
                        block = Utility.getLowerLeftDoor(block).getBlock();
                    }
                    locked = OddJob.getInstance().getLockManager().isLocked(block.getLocation()) != null;
                } catch (Exception e) {
                    return;
                }
            }

            if (locked) event.setCancelled(true);
        }
    }

    /**
     * @param event Interact made by player
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        boolean door = false;
        UUID uuid = null;

        if ((event.getHand() == EquipmentSlot.OFF_HAND) || (event.getClickedBlock() == null) || player.isOp()) {
            return;
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || (event.getAction().equals(Action.PHYSICAL))) {
            // Opening or Stepping
            Block block = event.getClickedBlock();
            Material t = block.getType();

            if (OddJob.getInstance().getLockManager().getLockable().contains(t)) {
                // Lockable Block
                try {
                    if (OddJob.getInstance().getLockManager().getDoors().contains(t)) {
                        // Door
                        door = true;
                        block = Utility.getLowerLeftDoor(block).getBlock();
                        uuid = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
                    } else if (t.equals(Material.CHEST)) {
                        // Chest
                        block = Utility.getChestPosition(block).getBlock();
                        uuid = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
                    } else {
                        // Whatever right-clicked or stepped on
                        uuid = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (uuid != null) {
                    // This Block has a Lock by a Player
                    if (uuid.equals(player.getUniqueId())) {
                        // Lock is owned by you
                        if (door) {
                            // Door
                            Utility.doorToggle(block);
                        }
                        return;
                    }

                    if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().makeSkeletonKey())) {
                        // Has SkeletonKey
                        if (door) {
                            // Door
                            Utility.doorToggle(block);
                            player.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
                            event.setCancelled(true);
                        }
                        OddJob.getInstance().getMessageManager().danger("! " + ChatColor.RESET + "Lock opened by the SkeletonKey", player, true);
                        return;
                    }

                    if (player.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK)) {
                        // Has a Key in hand
                        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                        if (lore(event, player, door, uuid, block, meta, false)) {
                            // Has the correct key
                            return;
                        }
                    } else if (player.getInventory().getItemInOffHand().getType().equals(Material.TRIPWIRE_HOOK)) {
                        // Has a key in offhand
                        ItemMeta meta = player.getInventory().getItemInOffHand().getItemMeta();
                        if (lore(event, player, door, uuid, block, meta, false)) {
                            // Has the correct key
                            return;
                        }
                    }

                    //OddJob.getInstance().getMessageManager().danger("This block is locked by someone else.", player, false);
                    event.setCancelled(true);

                }

                if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().infoWand)) {
                    // InfoWand in hand
                    if (OddJob.getInstance().getLockManager().isLockInfo(player.getUniqueId())) {
                        if (OddJob.getInstance().getPlayerManager().getName(uuid) != null)
                            OddJob.getInstance().getMessageManager().info("The " + block.getType().name() + " is owned by " + OddJob.getInstance().getPlayerManager().getName(uuid), player, false);
                        event.setCancelled(true);
                        return;
                    }
                }

                if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().lockWand)) {
                    // LockWand in hand
                    if (OddJob.getInstance().getLockManager().isLocking(player.getUniqueId())) {
                        if (uuid == player.getUniqueId()) {
                            OddJob.getInstance().getMessageManager().warning("You have already locked this.", player, false);
                            event.setCancelled(true);
                            return;
                        }
                        if (uuid != null) {
                            OddJob.getInstance().getMessageManager().danger("A lock is already set on this.", player, false);
                            event.setCancelled(true);
                            return;
                        }

                        // Locking
                        OddJob.getInstance().getLockManager().lock(player.getUniqueId(), block.getLocation());
                        OddJob.getInstance().getLockManager().remove(player.getUniqueId());
                        OddJob.getInstance().getMessageManager().success("Locked " + ChatColor.GOLD + block.getType().name(), player, true);
                        event.setCancelled(true);
                        return;
                    }
                }

                if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().unlockWand)) {
                    // UnlockWand in hand
                    if (OddJob.getInstance().getLockManager().isUnlocking(player.getUniqueId())) {
                        if (uuid != null && !uuid.equals(player.getUniqueId())) {
                            OddJob.getInstance().getMessageManager().danger("A lock is set by someone else.", player, false);
                            event.setCancelled(true);
                            return;
                        }

                        // Unlocking
                        OddJob.getInstance().getLockManager().unlock(block.getLocation());
                        OddJob.getInstance().getLockManager().remove(player.getUniqueId());
                        OddJob.getInstance().getMessageManager().warning("Unlocked " + ChatColor.GOLD + block.getType().name(), player, true);
                        event.setCancelled(true);
                        return;
                    }
                }

                // GUILD owning block
                UUID blockGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(block.getChunk(), block.getWorld());
                // GUILD associated with you
                UUID yourGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());


                if (blockGuild != null && yourGuild != null) {
                    // You are in a Guild, and Block is owned by a Guild
                    if (blockGuild.equals(yourGuild)) {
                        // Owned by your Guild
                        if (door) {
                            // Door
                            Utility.doorToggle(block);
                        }
                    } else if (!OddJob.getInstance().getGuildManager().getZoneByGuild(blockGuild).equals(Zone.GUILD)) {
                        // Is not inside a Zone or Guild
                        if (door) {
                            // Door
                            Utility.doorToggle(block);
                        }
                    } else {
                        // Is owned by a Guild, but not yours
                        if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().makeSkeletonKey())) {
                            // Has SkeletonKey
                            if (door) {
                                // Door
                                Utility.doorToggle(block);
                                player.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
                                event.setCancelled(true);
                            }
                            OddJob.getInstance().getMessageManager().danger("! " + ChatColor.RESET + "Lock opened by the SkeletonKey", player, true);
                            return;
                        } else if (player.getInventory().getItemInOffHand().getType().equals(Material.TRIPWIRE_HOOK)) {
                            // Has a key in offhand
                            ItemMeta meta = player.getInventory().getItemInOffHand().getItemMeta();
                            if (lore(event, player, door, yourGuild, block, meta, true)) {
                                // Has the correct key
                                return;
                            }
                        }
                        OddJob.getInstance().getMessageManager().warning("Block is locked by "+OddJob.getInstance().getGuildManager().getGuildNameByUUID(blockGuild), player, false);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private boolean lore(PlayerInteractEvent event, Player player, boolean door, UUID uuid, Block block, ItemMeta meta, boolean guild) {
        try {
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                if (lore != null && lore.size() > 1) {
                    // Key has Lore
                    String one = ChatColor.stripColor(lore.get(1));
                    UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk(), player.getLocation().getWorld());
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlaceLock(BlockPlaceEvent event) {
        if (event.getItemInHand().equals(OddJob.getInstance().getLockManager().unlockWand) ||
                event.getItemInHand().equals(OddJob.getInstance().getLockManager().lockWand) ||
                event.getItemInHand().equals(OddJob.getInstance().getLockManager().infoWand)) {
            event.setCancelled(true);
            return;
        }
        if (event.getItemInHand().getType().equals(Material.TRIPWIRE_HOOK)) {
            ItemMeta meta = event.getItemInHand().getItemMeta();
            if (meta != null && ChatColor.stripColor(meta.getDisplayName()).startsWith("Key to")) {
                event.setCancelled(true);
            }
        }
        if (event.getItemInHand().equals(OddJob.getInstance().getLockManager().makeSkeletonKey())) {
            OddJob.getInstance().getMessageManager().danger("Sorry, The SKELETON KEY is tooooooooo powerful!", event.getPlayer().getUniqueId(), true);
            event.setCancelled(true);
        }
    }

    /**
     * @param event Breaking block
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBreakLock(BlockBreakEvent event) {
        Block block = event.getBlock();
        // CHEST ?
        if (block.getType().equals(Material.CHEST)) {
            block = Utility.getChestPosition(block).getBlock();
        }
        // DOOR ?
        else if (OddJob.getInstance().getLockManager().getDoors().contains(block.getType())) {
            block = Utility.getLowerLeftDoor(block).getBlock();
        }

        UUID uuid = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
        // LOCKED BY A PLAYER ?
        if (uuid != null) {
            if (uuid.equals(event.getPlayer().getUniqueId())) {
                OddJob.getInstance().getLockManager().unlock(block.getLocation());
                OddJob.getInstance().getMessageManager().warning("Lock broken!", event.getPlayer(), true);
            } else {
                OddJob.getInstance().getMessageManager().danger("This lock is owned by someone else!", event.getPlayer(), false);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(OddJob.getInstance().getLockManager().makeSkeletonKey())) {
            OddJob.getInstance().getMessageManager().danger("Sorry, The SKELETON KEY can't be cast away like that!", event.getPlayer().getUniqueId(), true);
            event.setCancelled(true);
        }
    }
}
