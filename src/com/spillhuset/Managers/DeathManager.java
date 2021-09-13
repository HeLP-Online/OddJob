package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class DeathManager {
    /**
     * Holds the tasks BukkitTask & Player UUID
     */
    private final HashMap<UUID, BukkitTask> task = new HashMap<>();

    /**
     * Holds the Inventories for Entity (ArmorStands)
     */
    private final HashMap<UUID, Inventory> inventories = new HashMap<>();

    /**
     * Holds the owner information Entity (ArmorStands) & Player UUID
     */
    private final HashMap<UUID, UUID> owner = new HashMap<>();

    /**
     * Adds the Entity (ArmorStands) and Player
     *
     * @param entity Entity (ArmorStand)
     * @param player Player
     */

    public void add(Entity entity, Player player) {
        // Make the PlayerSkull
        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skull = (SkullMeta) playerSkull.getItemMeta();
        if (skull != null) {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
            skull.setDisplayName(ChatColor.DARK_PURPLE + "Head of " + OddJob.getInstance().getPlayerManager().getName(player.getUniqueId()));
            playerSkull.setItemMeta(skull);
        }

        // Make the ArmorStand
        ArmorStand armorStand = (ArmorStand) entity;
        armorStand.setCustomName("The spirit of " + OddJob.getInstance().getPlayerManager().getName(player.getUniqueId()));
        armorStand.setCustomNameVisible(true);

        // Sets the inventory of the ArmorStand
        Inventory pInv = player.getInventory();
        if (armorStand.getEquipment() != null) {
            armorStand.getEquipment().setArmorContents(player.getInventory().getArmorContents());
            armorStand.getEquipment().setItemInMainHand(player.getInventory().getItemInMainHand());
            armorStand.getEquipment().setItemInOffHand(player.getInventory().getItemInOffHand());
        }

        Inventory inventory = Bukkit.createInventory(null, 54, "DEATH CHEST");

        // Put Items inside the armorStand
        if (pInv.getContents().length > 0)
            inventory.setContents(player.getInventory().getContents().clone());
        inventory.addItem(playerSkull);
        player.getInventory().clear();

        // Saving left side of the DoubleChest with the timer of disappearance
        inventories.put(entity.getUniqueId(), inventory);
        owner.put(entity.getUniqueId(), player.getUniqueId());
        task.put(entity.getUniqueId(), new BukkitRunnable() {
                    int i = 1200; // 20 min.

                    @Override
                    public void run() {
                        if (i == 1200) {
                            if (player.isOnline())
                                OddJob.getInstance().getMessageManager().death1200(player);
                        } else if (i == 600) {
                            if (player.isOnline())
                                OddJob.getInstance().getMessageManager().death600(player);
                        } else if (i == 60) {
                            if (player.isOnline())
                                OddJob.getInstance().getMessageManager().death60(player);
                        } else if (i < 10 && i > 0) {
                            if (player.isOnline())
                                OddJob.getInstance().getMessageManager().death10(i, player);
                        } else if (i < 1) {
                            OddJob.getInstance().getDeathManager().replace(entity, null);
                            if (player.isOnline())
                                OddJob.getInstance().getMessageManager().death0(player);
                            cancel();
                        }
                        i--;
                    }

                }.runTaskTimer(OddJob.getInstance(), 20L, 20L)
        );
    }

    /**
     * Removing the ArmorStand
     *
     * @param entity        Entity (ArmorStands)
     * @param findingPlayer UUID of Player finding the Spirit
     */
    public void replace(Entity entity, UUID findingPlayer) {
        // Notify the owner, if the Player is online
        UUID ownerOfArmor = owner.get(entity.getUniqueId());
        if (ownerOfArmor != findingPlayer) {
            // Finders keepers
            Player player = Bukkit.getPlayer(ownerOfArmor);
            if (findingPlayer != null && player != null && player.isOnline())
                OddJob.getInstance().getMessageManager().spiritFoundSomeone(ownerOfArmor);
            if (findingPlayer != null)
                OddJob.getInstance().getMessageManager().spiritFound(OddJob.getInstance().getPlayerManager().getName(findingPlayer), OddJob.getInstance().getPlayerManager().getName(ownerOfArmor));
        } else {
            OddJob.getInstance().getMessageManager().spiritFoundSelf(findingPlayer);
        }

        // Continue to remove()
        entity.remove();
        remove(entity.getUniqueId());

    }

    /**
     * Removing Entity from HashMap and Database
     *
     * @param uuid UUID of Entity
     */
    public void remove(UUID uuid) {
        // If task is not cancelled
        if (task.get(uuid) != null) {
            task.get(uuid).cancel();
        }

        // Remove task
        task.remove(uuid);

        // Remove from database
        Entity entity = Bukkit.getEntity(uuid);
        if (entity != null) entity.remove();
        inventories.remove(uuid);
        owner.remove(uuid);
    }

    /**
     * Checking existence of World and Entity to remove
     *
     * @param worldUUID  UUID of World
     * @param entityUUID UUID of Entity
     * @param playerUUID UUID of Player
     */
    public void replace(UUID worldUUID, UUID entityUUID, UUID playerUUID) {
        // Does the World exists
        World world = Bukkit.getWorld(worldUUID);
        if (world == null) return;

        // Does ArmorStand exists
        ArmorStand armorStand = null;
        for (ArmorStand armor : world.getEntitiesByClass(ArmorStand.class)) {
            if (armor.getUniqueId().equals(entityUUID)) armorStand = armor;
        }
        if (armorStand == null) return;

        int i = 0;
        for (ItemStack is : inventories.get(armorStand.getUniqueId()).getContents()) {
            if (is != null && !is.getType().equals(Material.AIR)) i++;
        }
        if (i > 0) return;

        // Continue to finder and remove
        replace(armorStand, playerUUID);
    }

    /**
     * Cleaning up the World of Spirits
     *
     * @param world World
     * @return number of cleaned Spirits
     */
    public int cleanUp(World world) {
        int i = 0;
        for (ArmorStand armor : world.getEntitiesByClass(ArmorStand.class)) {
            if (owner.containsKey(armor.getUniqueId())) {
                armor.remove();
                remove(armor.getUniqueId());
            }
        }
        return i;
    }

    public HashMap<UUID, UUID> getOwners() {
        return owner;
    }

    public Inventory getInventory(UUID uniqueId) {
        return inventories.get(uniqueId);
    }
}
