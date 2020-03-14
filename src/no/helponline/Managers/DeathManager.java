package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class DeathManager {
    private final HashMap<Location, BukkitTask> task = new HashMap<>();
    private final HashMap<Location, Block> leftSide = new HashMap<>();
    private final HashMap<Location, Block> rightSide = new HashMap<>();
    private final HashMap<Location, UUID> owner = new HashMap<>();

    public void add(Block chest, UUID player) {
        Block right = chest.getRelative(0, 0, -1);
        Player p = Bukkit.getPlayer(player);

        // Saving left side of the DoubleChest with the timer of disappearance
        task.put(chest.getLocation(), new BukkitRunnable() {
                    int i = 1200; // 20 min.

                    @Override
                    public void run() {
                        if (i == 1200) {
                            if (p != null && p.isOnline())
                                OddJob.getInstance().getMessageManager().info("Sorry to hear about your death. Your deathchest despawning in " + ChatColor.WHITE + "20" + ChatColor.RESET + " min, if you don't get it!", player, true);
                        } else if (i == 600) {
                            if (p != null && p.isOnline())
                                OddJob.getInstance().getMessageManager().info("Deathchest despawning in " + ChatColor.WHITE + "10" + ChatColor.RESET + " min.", player, false);
                        } else if (i == 60) {
                            if (p != null && p.isOnline())
                                OddJob.getInstance().getMessageManager().info("Deathchest despawning in " + ChatColor.WHITE + "1" + ChatColor.RESET + " min.", player, false);
                        } else if (i < 10 && i > 0) {
                            if (p != null && p.isOnline())
                                OddJob.getInstance().getMessageManager().danger("Deathchest despawning in " + ChatColor.WHITE + i + ChatColor.RESET + " sec.", player, false);
                        } else if (i < 1) {
                            OddJob.getInstance().getDeathManager().replace(chest.getLocation(), null);
                            if (p != null && p.isOnline())
                                OddJob.getInstance().getMessageManager().danger("All your item from your deathchest is gone, sorry.", player, true);
                            cancel();
                        }
                        i--;
                    }

                }.runTaskTimer(OddJob.getInstance(), 20L, 20L)
        );

        // Storing the DoubleChest replacement blocks to the database
        leftSide.put(chest.getLocation(), chest);
        rightSide.put(chest.getLocation(), right);
        owner.put(chest.getLocation(), player);
        //OddJob.getInstance().getMySQLManager().addDeathChest(chest.getLocation(), chest.getType(), right.getType(), player);
    }

    public void replace(Location location, UUID findingPlayer) {

        if (location.getBlock().getType().equals(Material.CHEST)) {
            Chest chest = (Chest) location.getBlock().getState();

            //HashMap<String, String> ret = OddJob.getInstance().getMySQLManager().getDeathChest(location);
            //if (!ret.isEmpty()) {
            // Empty the DoubleChest
            chest.getInventory().clear();

            // Replace the blocks
            location.getBlock().setType(leftSide.get(location).getType(), true);
            location.getBlock().setBlockData(leftSide.get(location).getBlockData(), true);
            //location.getBlock().setType(Material.valueOf(ret.get("left")));

            Block right = location.getBlock().getRelative(0, 0, -1);
            right.setType(rightSide.get(location).getType(), true);
            right.setBlockData(rightSide.get(location).getBlockData(), true);
            //right.setType(Material.valueOf(ret.get("right")));

            // Notify the owner, if the Player is online
            UUID ownerOfChest = owner.get(location);
            if (!ownerOfChest.equals(findingPlayer)) {
                // Finders keepers
                if (findingPlayer != null) {
                    Player player = Bukkit.getPlayer(ownerOfChest);
                    if (player != null && player.isOnline()) {
                        OddJob.getInstance().getMessageManager().danger("Somebody found your stuff.", ownerOfChest, false);
                    }
                    OddJob.getInstance().getMessageManager().console(ChatColor.AQUA + OddJob.getInstance().getPlayerManager().getName(findingPlayer) + ChatColor.RESET + " found the stuff from " + ChatColor.AQUA + OddJob.getInstance().getPlayerManager().getName(ownerOfChest));
                }
            }

            // Remove from database
            remove(location);
            //}
        }
    }

    public boolean isDeathChest(Location location) {
        return owner.containsKey(location);
    }

    public void remove(Location location) {
        // If task is not cancelled
        if (task.get(location) != null) {
            task.get(location).cancel();
        }

        // Remove task
        task.remove(location);

        // Remove from database
        //OddJob.getInstance().getMySQLManager().deleteDeathChest(location);
        leftSide.remove(location);
        rightSide.remove(location);
        owner.remove(location);
    }

    public void replace(UUID world, double x, double y, double z, Material leftBlock, Material rightBlock) {
        // Does the World exists
        if (Bukkit.getWorld(world) == null) {
            return;
        }

        Location location = new Location(Bukkit.getWorld(world), x, y, z);
        Block left = location.getBlock();

        // Is it a DoubleChest
        if (!left.getType().equals(Material.CHEST)) {
            // Remove from database
            OddJob.getInstance().getMessageManager().console("Chest is not there removing from DB");
            //OddJob.getInstance().getMySQLManager().deleteDeathChest(left.getLocation());
            leftSide.remove(location);
            rightSide.remove(location);
            owner.remove(location);
            return;
        }

        // Replace blocks
        Chest chest = (Chest) left.getState();
        chest.getInventory().clear();
        left.setType(leftBlock);
        Block right = left.getRelative(0, 0, -1);
        right.setType(rightBlock);

        // Remove from database
        //OddJob.getInstance().getMySQLManager().deleteDeathChest(left.getLocation());
        leftSide.remove(location);
        rightSide.remove(location);
        owner.remove(location);
    }

    public int cleanUp(World world) {
        return OddJob.getInstance().getMySQLManager().cleanDeathChests(world);
    }
}
