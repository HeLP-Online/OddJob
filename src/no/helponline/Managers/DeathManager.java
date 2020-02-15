package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
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
                            OddJob.getInstance().getMessageManager().sendMessage(player, "Sorry to hear about your death. Your deathchest despawning in 5 min, if you don't get it!");
                        } else if (i == 60) {
                            OddJob.getInstance().getMessageManager().sendMessage(player, "Deathchest despawning in 1 min.");
                        } else if (i < 20 && i > 0) {
                            OddJob.getInstance().getMessageManager().sendMessage(player, "Deathchest despawning in " + i + " sec.");
                        } else if (i < 1) {
                            OddJob.getInstance().getDeathManager().replace(chest.getLocation(), player);
                            OddJob.getInstance().getMessageManager().sendMessage(player, "All your item from your deathchest is gone, sorry.");
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
                location.getBlock().setType(Material.valueOf(ret.get("right")));
                Block right = location.getBlock().getRelative(0, 0, -1);
                //right.setType(posRight.get(location));
                right.setType(Material.valueOf(ret.get("right")));
                //if (posPlayer.get(location) == player) {
                UUID owner = UUID.fromString(ret.get("uuid"));
                if (owner == player) {
                    OddJob.getInstance().log("got your own stuff");
                } else if (player != null) {
                    if (Bukkit.getPlayer(owner).isOnline()) {
                        OddJob.getInstance().getMessageManager().sendMessage(player, "Somebody found your stuff.");
                    }
                    OddJob.getInstance().log(OddJob.getInstance().getPlayerManager().getName(player) + " found the stuff to " + OddJob.getInstance().getPlayerManager().getName(UUID.fromString(ret.get("uuid"))));
                } else {
                    OddJob.getInstance().log("clean up");
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
        Block left = new Location(Bukkit.getWorld(world), x, y, z).getBlock();
        Chest chest = (Chest) left.getState();
        chest.getInventory().clear();
        left.setType(leftBlock);
        Block right = left.getRelative(0, 0, -1);
        right.setType(rightBlock);
        OddJob.getInstance().getMySQLManager().deleteDeathChest(left.getLocation());
    }
}
