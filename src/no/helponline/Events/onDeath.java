package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.UUID;

public class onDeath implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skull = (SkullMeta) playerSkull.getItemMeta();
        if (skull != null) {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
            skull.setDisplayName(ChatColor.DARK_PURPLE + "Head of " + OddJob.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getName());
            playerSkull.setItemMeta(skull);
        }

        Location location = player.getLocation();
        location.getBlock().setType(Material.CHEST);
        Block upper = location.getBlock().getRelative(0, 1, 0);
        upper.setType(Material.CHEST);
        Chest chest = (Chest) location.getBlock().getState();
        ArrayList<ItemStack> list = new ArrayList<>();
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null && is.getType() != Material.AIR) {
                list.add(is);
            }
        }
        OddJob.getInstance().log("Content: " + list.toArray(new ItemStack[list.size()]).toString());
        chest.getInventory().setContents(list.toArray(new ItemStack[list.size()]));
        //AREMOR CHEST
        Chest upperChest = (Chest) chest.getBlock().getState();
        list.clear();
        for (ItemStack is : player.getInventory().getArmorContents()) {
            if (is != null && is.getType() != Material.AIR) {
                list.add(is);
            }
        }
        list.add(playerSkull);
        OddJob.getInstance().log("ArmorContent: " + list.toArray(new ItemStack[list.size()]).toString());
        upperChest.getInventory().setContents(list.toArray(new ItemStack[list.size()]));
        //SKULL


        if (OddJob.deathChest.containsKey(player.getUniqueId())) {
            Location trap = OddJob.deathChest.get(player.getUniqueId());
            trap.getBlock().setType(Material.TRAPPED_CHEST);
            trap.getBlock().getRelative(0, 1, 0).setType(Material.TRAPPED_CHEST);
            OddJob.deathTrappedChest.add(trap);
            Block block = trap.getBlock().getRelative(0, -2, 0);
            block.setType(Material.TNT);
        }

        chest.update(true, true);
        OddJob.deathChest.put(player.getUniqueId(), location);
        player.setCompassTarget(location);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block chest = event.getClickedBlock();
        if (chest != null) {
            if (chest.getType().equals(Material.TNT)) {
                chest = chest.getRelative(0, 2, 0);
                if (chest.getType() == Material.TRAPPED_CHEST) {
                    event.getClickedBlock().setType(Material.AIR);
                    OddJob.deathTrappedChest.remove(chest.getLocation());
                }
            } else if (chest.getType().equals(Material.CHEST)) {
                if (OddJob.deathChest.containsValue(chest.getLocation())) {
                    for (UUID uuid : OddJob.deathChest.keySet()) {
                        Block armo = chest.getRelative(0, 1, 0);
                        if (OddJob.deathChest.get(uuid).equals(chest.getLocation())) {
                            chest.setType(Material.AIR);
                            chest.getState().update(true, true);
                            if (!chest.getType().equals(Material.CHEST) && !armo.getType().equals(Material.CHEST)) {
                                OddJob.deathChest.remove(uuid);
                                OddJob.getInstance().getPlayerManager().getPlayer(uuid).sendMessage("All your item's have been picked up by " + event.getPlayer().getName());
                            }
                        }
                    }
                } else if (OddJob.deathChest.containsValue(chest.getRelative(0, -1, 0).getLocation())) {
                    for (UUID uuid : OddJob.deathChest.keySet()) {
                        Block main = chest.getRelative(0, -1, 0);
                        if (OddJob.deathChest.get(uuid).equals(main.getLocation())) {
                            chest.setType(Material.AIR);
                            chest.getState().update(true, true);
                            if (!main.getType().equals(Material.CHEST) && !chest.getType().equals(Material.CHEST)) {
                                OddJob.deathChest.remove(uuid);
                                OddJob.getInstance().getPlayerManager().getPlayer(uuid).sendMessage("All your item's have been picked up by " + event.getPlayer().getName());
                            }
                        }
                    }
                }
            } else if (chest.getType().equals(Material.TRAPPED_CHEST)) {

            }
            chest.getState().update();
        }
    }
}
