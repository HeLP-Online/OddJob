package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class DeathManager {
    private final HashMap<Location, BukkitTask> task = new HashMap<>();

    public void add(Block chest, UUID player) {

        // FIND BLOCK TO THE RIGHT
        Block right = chest.getRelative(0, 0, -1);

        // SAVING AS LEFT SIDE
        task.put(chest.getLocation(), new BukkitRunnable() {
                    int i = 300;

                    @Override
                    public void run() {
                        if (i == 300) {
                            OddJob.getInstance().getMessageManager().info("Sorry to hear about your death. Your deathchest despawning in " + ChatColor.WHITE + "5" + ChatColor.RESET + " min, if you don't get it!", player,true);
                        } else if (i == 60) {
                            OddJob.getInstance().getMessageManager().info("Deathchest despawning in " + ChatColor.WHITE + "1" + ChatColor.RESET + " min.", player,false);
                        } else if (i < 10 && i > 0) {
                            OddJob.getInstance().getMessageManager().danger("Deathchest despawning in " + ChatColor.WHITE + i + ChatColor.RESET + " sec.", player, false);
                        } else if (i < 1) {
                            OddJob.getInstance().getDeathManager().replace(chest.getLocation(), null);
                            OddJob.getInstance().getMessageManager().danger("All your item from your deathchest is gone, sorry.", player,true);
                            cancel();
                        }
                        i--;
                    }

                }.runTaskTimer(OddJob.getInstance(), 20L, 20L)
        );
        OddJob.getInstance().getMySQLManager().addDeathChest(chest.getLocation(), chest.getType(), right.getType(), player);
    }

    public void replace(Location location, UUID player) {
        // LEFT OR RIGHT?
        if (location.getBlock().getType().equals(Material.CHEST)) {
            Chest chest = (Chest) location.getBlock().getState();

            HashMap<String, String> ret = OddJob.getInstance().getMySQLManager().getDeathChest(location);
            if (!ret.isEmpty()) {
                // EMPTY IT
                chest.getInventory().clear();
                // LEFT BLOCK
                location.getBlock().setType(Material.valueOf(ret.get("left")));
                // BLOCK TO THE RIGHT
                Block right = location.getBlock().getRelative(0, 0, -1);
                right.setType(Material.valueOf(ret.get("right")));

                // WHO OWNS IT?
                UUID owner = UUID.fromString(ret.get("uuid"));
                if (!owner.equals(player)) {
                    if (player != null) {
                        if (Bukkit.getPlayer(owner).isOnline()) {
                            OddJob.getInstance().getMessageManager().danger("Somebody found your stuff.", player,false);
                        }
                        OddJob.getInstance().getMessageManager().console(ChatColor.AQUA+OddJob.getInstance().getPlayerManager().getName(player) +ChatColor.RESET+ " found the stuff from "+ChatColor.AQUA + OddJob.getInstance().getPlayerManager().getName(UUID.fromString(ret.get("uuid"))));
                    }
                }
                remove(location);
            }
        }
    }

    public boolean isDeathChest(Location location) {
        return OddJob.getInstance().getMySQLManager().isDeathChest(location);
    }

    public void remove(Location location) {
        if (task.get(location) != null) {
            task.get(location).cancel();
        }
        task.remove(location);

        OddJob.getInstance().getMySQLManager().deleteDeathChest(location);
    }

    public int timeCheck(int t) {
        return OddJob.getInstance().getMySQLManager().timeCheck(t);
    }

    public void replace(UUID world, double x, double y, double z, Material leftBlock, Material rightBlock) {
        if (Bukkit.getWorld(world) == null) {
            return;
        }
        Location location = new Location(Bukkit.getWorld(world), x, y, z);
        OddJob.getInstance().log(location.toString());
        Block left = location.getBlock();
        if (!left.getType().equals(Material.CHEST)) {
            return;
        }
        Chest chest = (Chest) left.getState();
        chest.getInventory().clear();
        left.setType(leftBlock);
        Block right = left.getRelative(0, 0, -1);
        right.setType(rightBlock);
        OddJob.getInstance().getMySQLManager().deleteDeathChest(left.getLocation());
    }
}
