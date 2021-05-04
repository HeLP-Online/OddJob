package com.spillhuset.Events;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Zone;
import com.spillhuset.Utils.Lore;
import com.spillhuset.Utils.Utility;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class PlayerInteract implements Listener {
    /**
     * @param event Interact made by player
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEvent(PlayerInteractEvent event) {
        OddJob.getInstance().log("Triggered");
        Player player = event.getPlayer();
        boolean door = false;
        UUID uuid = null;

        // Prevent generating map from InfoLockTool
        if (event.getItem() != null && event.getItem().equals(OddJob.getInstance().getLockManager().infoWand))
            event.setUseItemInHand(Event.Result.DENY);

        // Log Diamond & Emerald
        ItemStack item = event.getItem();
        if ((item != null) && (item.getType().equals(Material.DIAMOND_BLOCK) || item.getType().equals(Material.EMERALD) || item.getType().equals(Material.EMERALD) || item.getType().equals(Material.EMERALD_BLOCK)) && !player.hasPermission("noLog"))
            OddJob.getInstance().getMySQLManager().addLog(player.getUniqueId(), item, "interact");

        // Check Authorization
        if ((event.getHand() == EquipmentSlot.OFF_HAND) || (event.getClickedBlock() == null) || player.isOp()) {
            return;
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || (event.getAction().equals(Action.PHYSICAL))) {
            // Opening or Stepping
            Block block = event.getClickedBlock();
            Material t = block.getType();

            if (OddJob.getInstance().getLockManager().getLockable().contains(t)) {
                OddJob.getInstance().log("Lockable");
                // Lockable Block
                try {
                    if (OddJob.getInstance().getLockManager().getDoors().contains(t)) {
                        OddJob.getInstance().log("Door");
                        // Door
                        door = true;
                        block = Utility.getLowerLeftDoor(block).getBlock();
                        uuid = OddJob.getInstance().getLockManager().getLockOwner(block.getLocation());
                    } else if (t.equals(Material.CHEST)) {
                        OddJob.getInstance().log("Chest");
                        // Chest
                        block = Utility.getChestPosition(block).getBlock();
                        uuid = OddJob.getInstance().getLockManager().getLockOwner(block.getLocation());
                    } else {
                        // Whatever right-clicked or stepped on
                        OddJob.getInstance().log("Else");
                        uuid = OddJob.getInstance().getLockManager().getLockOwner(block.getLocation());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (uuid != null) {
                    // This Block has a Lock by a Player
                    if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().infoWand)) {
                        // InfoWand in hand
                        if (OddJob.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().infoWand)) {
                            if (OddJob.getInstance().getPlayerManager().getName(uuid) != null)
                                OddJob.getInstance().getMessageManager().info("The " + block.getType().name() + " is owned by " + OddJob.getInstance().getPlayerManager().getName(uuid), player, false);
                            event.setCancelled(true);
                            return;
                        }
                    }
                    if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().unlockWand)) {
                        // UnlockWand in hand
                        if (OddJob.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().unlockWand)) {
                            if (!uuid.equals(player.getUniqueId())) {
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

                    if (uuid.equals(player.getUniqueId())) {
                        // Lock is owned by you
                        if (door) {
                            // Door
                            Utility.doorToggle(block);
                        }
                        return;
                    }

                    if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().skeletonKey)) {
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
                        if (Lore.lore(event, player, door, uuid, block, meta, false)) {
                            // Has the correct key
                            return;
                        }
                    } else if (player.getInventory().getItemInOffHand().getType().equals(Material.TRIPWIRE_HOOK)) {
                        // Has a key in offhand
                        ItemMeta meta = player.getInventory().getItemInOffHand().getItemMeta();
                        if (Lore.lore(event, player, door, uuid, block, meta, false)) {
                            // Has the correct key
                            return;
                        }
                    }

                    OddJob.getInstance().getMessageManager().danger("This block is locked by someone else.", player, false);
                    event.setCancelled(true);

                }

                if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().lockWand)) {
                    // LockWand in hand
                    OddJob.getInstance().getMessageManager().console("In hand");
                    if (OddJob.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().lockWand)) {
                        OddJob.getInstance().getMessageManager().console("Locking");
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

                // GUILD owning block
                UUID blockGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(block.getChunk());
                // GUILD associated with you
                UUID yourGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());


                if (blockGuild != null && !blockGuild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD)) && !yourGuild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
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
                        if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().skeletonKey)) {
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
                            if (Lore.lore(event, player, door, yourGuild, block, meta, true)) {
                                // Has the correct key
                                return;
                            }
                        }
                        OddJob.getInstance().getMessageManager().warning("Block is locked by the guild " + ChatColor.GOLD + OddJob.getInstance().getGuildManager().getGuildNameByUUID(blockGuild), player, false);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
