package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class JailManager {

    // Player , World
    private final HashMap<UUID, List<ItemStack>> savedInventories = new HashMap<>();
    private final HashMap<UUID, List<ItemStack>> savedArmors = new HashMap<>();
    private final HashMap<UUID, List<ItemStack>> savedExtras = new HashMap<>();
    private final HashMap<Location, Material> point = new HashMap<>();
    private final HashMap<Location, Slot> slots = new HashMap<>();
    //Location jailLobby;
    //Location jailFree;
    //Location jailWarden;

    public UUID in(UUID uniqueId) {
        World world = OddJob.getInstance().getMySQLManager().playerInJail(uniqueId);
        if (world != null) {
            return world.getUID();
        }
        return null;
    }

    public void setInJail(UUID uuidPlayer, UUID world, CommandSender sender) {
        // Check if Player is already in Jail
        if (in(uuidPlayer) != null) {
            OddJob.getInstance().getMessageManager().danger("Is already in jail!", sender, false);
            return;
        }

        // Check if the World has a Jail
        if (!has(world)) {
            OddJob.getInstance().getMessageManager().danger("This world has no finished jail!", sender, false);
            return;
        }

        // If Player is in an Arena, get the Player out!
        if (OddJob.getInstance().getArenaManager().isInArena(uuidPlayer)) {
            OddJob.getInstance().getArenaManager().abort(uuidPlayer);
        }

        Player player = OddJob.getInstance().getPlayerManager().getPlayer(uuidPlayer);
        // Storing Inventory
        /*List<ItemStack> inv = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
        OddJob.getInstance().getMessageManager().console("items:" + inv.toString());
        savedInventories.put(uuidPlayer, inv);
        List<ItemStack> arm = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
        OddJob.getInstance().getMessageManager().console("arms:" + arm.toString());
        savedArmors.put(uuidPlayer, arm);
        List<ItemStack> ext = new ArrayList<>(Arrays.asList(player.getInventory().getExtraContents()));
        OddJob.getInstance().getMessageManager().console("extras:" + ext.toString());
        savedExtras.put(uuidPlayer, ext);
        player.getInventory().clear();
*/

        // Teleport to Jail lobby
        OddJob.getInstance().getMessageManager().console("Jailing "+player.getName()+" has "+player.getInventory().getContents().length);
        OddJob.getInstance().getMySQLManager().setPlayerInJail(uuidPlayer, world);
        player.getInventory().setContents(new ItemStack[]{});

        // Finishing up
        OddJob.getInstance().getPlayerManager().setGameMode(player, GameMode.ADVENTURE);
        OddJob.getInstance().getTeleportManager().jail(player, jailLobby(world));
        OddJob.getInstance().getMessageManager().info("You are now in jail, serving your time!", uuidPlayer, false);
        OddJob.getInstance().getMessageManager().success("You have successfully set " + OddJob.getInstance().getPlayerManager().getName(uuidPlayer) + " in jail!", sender, true);
    }

    public void freeFromJail(UUID uuidPlayer, CommandSender sender, boolean escape) {
        UUID world = in(uuidPlayer);
        Player player = OddJob.getInstance().getPlayerManager().getPlayer(uuidPlayer);

        // Check if Player is already in Jail
        if (world == null) {
            OddJob.getInstance().getMessageManager().danger("Is not in jail!", sender, false);
            return;
        }

        // Restoring Inventory
        /*player.getInventory().clear();
        List<ItemStack> savedArmor = savedArmors.get(uuidPlayer);
        ItemStack[] equippedArmor = player.getInventory().getArmorContents();
        for (int i = 0; i < savedArmor.size(); i++) {
            OddJob.getInstance().getMessageManager().console("arm:" + i);
            equippedArmor[i] = savedArmor.get(i);
        }
        player.getInventory().setArmorContents(equippedArmor);
        savedArmors.remove(uuidPlayer);

        List<ItemStack> savedInventory = savedInventories.get(uuidPlayer);
        ItemStack[] equippedInventory = player.getInventory().getContents();
        for (int i = 0; i < savedInventory.size(); i++) {
            OddJob.getInstance().getMessageManager().console("item:" + i);
            equippedInventory[i] = savedInventory.get(i);
        }
        player.getInventory().setContents(equippedInventory);
        savedInventories.remove(uuidPlayer);

        List<ItemStack> savedExtra = savedExtras.get(uuidPlayer);
        ItemStack[] equippedExtra = player.getInventory().getExtraContents();
        for (int i = 0; i < savedExtra.size(); i++) {
            OddJob.getInstance().getMessageManager().console("extra:" + i);
            equippedExtra[i] = savedExtra.get(i);
        }
        player.getInventory().setExtraContents(equippedExtra);
        savedExtras.remove(uuidPlayer);
        */




        // Remove from Jail
        /*HashMap<String, ItemStack[]> items = OddJob.getInstance().getMySQLManager().getJailItems(uuidPlayer);
        if (items.containsKey("contents")) {
            player.getInventory().setContents(items.get("contents"));
        }*/
        OddJob.getInstance().getMySQLManager().deletePlayerJail(uuidPlayer);

        // Finishing up
        OddJob.getInstance().getPlayerManager().setGameMode(player, GameMode.SURVIVAL);
        if (escape) {
            OddJob.getInstance().getMessageManager().broadcastAchievement("Warden: " + player.getDisplayName() + " has successfully escaped prison!");
            OddJob.getInstance().getMessageManager().console(player.getDisplayName() + " is an escape artist!");
        } else {
            OddJob.getInstance().getTeleportManager().jail(player, jailFree(world));
            OddJob.getInstance().getMessageManager().info("You have served your time, be more careful!", uuidPlayer, false);
            OddJob.getInstance().getMessageManager().success("You have successfully set " + OddJob.getInstance().getPlayerManager().getName(uuidPlayer) + " free from jail!", sender, true);
        }

    }

    /**
     * @param world UUID
     * @return boolean
     */
    public boolean has(UUID world) {
        // Check free location
        if (jailFree(world) == null) {
            OddJob.getInstance().getMessageManager().console("Jail: Missing FREE");
        }

        // Check jail lobby location
        if (jailLobby(world) == null) {
            OddJob.getInstance().getMessageManager().console("Jail: Missing HOLDINGCELL");
        }

        // Check warden location
        if (jailWarden(world) == null) {
            OddJob.getInstance().getMessageManager().console("Jail: Missing WARDEN");
        }

        return (jailFree(world) != null && jailLobby(world) != null && jailWarden(world) != null);
    }

    /**
     * @param world UUID
     * @return Jail lobby Location
     */
    private Location jailLobby(UUID world) {
        return OddJob.getInstance().getMySQLManager().getJail(world, "lobby");
    }

    /**
     * @param world UUID
     * @return Jail free Location
     */
    private Location jailFree(UUID world) {
        return OddJob.getInstance().getMySQLManager().getJail(world, "free");
    }

    /**
     * @param world UUID
     * @return Jail warden Location
     */
    public Location jailWarden(UUID world) {
        return OddJob.getInstance().getMySQLManager().getJail(world, "warden");
    }

    /**
     * @param string Slot
     * @param player Player
     */
    public void set(Slot string, Player player) {
        // Get the Block you are standing on
        Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

        // If the Slot has a Location in the list
        if (slots.containsValue(string)) {
            for (Location location : slots.keySet()) {
                if (slots.get(location).equals(string)) {
                    // Replace the Block back to origin Material
                    location.getBlock().getRelative(BlockFace.DOWN).setType(point.get(location));
                }
            }
        } else {
            // Put the Slot and Location in the list
            slots.put(player.getLocation(), string);
        }

        // Mark the Block you are standing on.
        switch (string) {
            case lobby:
                block.setType(Material.PINK_WOOL);
                break;
            case free:
                block.setType(Material.GRAY_WOOL);
                break;
            case warden:
                block.setType(Material.BLUE_WOOL);
                break;
        }

        // Backup the Material of the Location
        point.put(player.getLocation(), block.getType());

        // Set Location in the Database
        OddJob.getInstance().getMySQLManager().setJail(player.getWorld().getUID(), string.name(), player.getLocation());
        OddJob.getInstance().getMessageManager().console("set-locations:" + point.size());
        OddJob.getInstance().getMessageManager().console("set-slots:" + slots.size());
    }

    public void edit(UUID world) {
        // Puts the Location in the list, backing up Material, and marking Blocks
        if (jailWarden(world) != null) {
            point.put(jailWarden(world), jailWarden(world).getBlock().getRelative(BlockFace.DOWN).getType());
            slots.put(jailWarden(world), Slot.warden);
            jailWarden(world).getBlock().getRelative(BlockFace.DOWN).setType(Material.BLUE_WOOL);
        }
        if (jailFree(world) != null) {
            point.put(jailFree(world), jailFree(world).getBlock().getRelative(BlockFace.DOWN).getType());
            slots.put(jailFree(world), Slot.free);
            jailFree(world).getBlock().getRelative(BlockFace.DOWN).setType(Material.GRAY_WOOL);
        }
        if (jailLobby(world) != null) {
            point.put(jailLobby(world), jailLobby(world).getBlock().getRelative(BlockFace.DOWN).getType());
            slots.put(jailLobby(world), Slot.lobby);
            jailLobby(world).getBlock().getRelative(BlockFace.DOWN).setType(Material.PINK_WOOL);
        }
        OddJob.getInstance().getMessageManager().console("edit-locations:" + point.size());
        OddJob.getInstance().getMessageManager().console("edit-slots:" + slots.size());
    }

    public void revertPoints() {
        OddJob.getInstance().getMessageManager().console("rev-locations:" + point.size());
        OddJob.getInstance().getMessageManager().console("rev-slots:" + slots.size());
        OddJob.getInstance().getMessageManager().console("keyset: " + point.keySet().toString());

        // Replace the Blocks back to origin Material, removing from list
        for (Location location : point.keySet()) {
            OddJob.getInstance().getMessageManager().console(location.serialize().toString());
            OddJob.getInstance().getMessageManager().console(point.get(location).name());
            location.getBlock().getRelative(BlockFace.DOWN).setType(point.get(location));
        }

        point.clear();
        slots.clear();
    }

    /* One world can only contain one jail */
    public enum Slot {
        lobby, free, warden,cell
    }
}
