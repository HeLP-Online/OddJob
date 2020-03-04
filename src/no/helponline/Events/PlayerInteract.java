package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.*;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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
        Player player = event.getPlayer();
        boolean door = false;
        UUID uuid = null;

        // Log Diamond & Emerald
        ItemStack item = event.getItem();
        if ((item != null) && (item.getType().equals(Material.DIAMOND_BLOCK) || item.getType().equals(Material.EMERALD) || item.getType().equals(Material.EMERALD) || item.getType().equals(Material.EMERALD_BLOCK)) && !player.hasPermission("noLog")) OddJob.getInstance().getMySQLManager().addLog(player.getUniqueId(),item,"interact");

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
                            if (Lore.lore(event, player, door, yourGuild, block, meta, true)) {
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

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if ((event.getHand() == EquipmentSlot.OFF_HAND) || (event.getClickedBlock() == null)) {
            return;
        }
        if (block == null) return;
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getArenaManager().spawnTool)) {
            event.setCancelled(true);
            Arena arena = OddJob.getInstance().getArenaManager().editArena.get(player.getUniqueId());
            if (arena.next > arena.getMaxPlayers()) {
                ArenaMechanics.cancel(player);
            } else {
                arena.getSpawn().put(arena.next, block.getLocation().add(0, 1, 0));
                block.setType(Material.PINK_WOOL);
                OddJob.getInstance().getMessageManager().success("Spawn point " + arena.next + "/" + arena.getMaxPlayers() + " set.", player,false);
                arena.next++;
                if (arena.next > arena.getMaxPlayers()) {
                    arena.next = 1;
                    OddJob.getInstance().getMessageManager().success("All spawn points are now set. You can now start the game.", player,false);
                    OddJob.getInstance().getArenaManager().createArena(arena);
                    ArenaMechanics.cancel(player);
                }
            }
        }
    }
}