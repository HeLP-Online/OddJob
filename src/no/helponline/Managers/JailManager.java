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

import java.util.*;

public class JailManager {

    // Player , World
    private final HashMap<UUID, List<ItemStack>> inventory = new HashMap<>();
    private final HashMap<UUID, List<ItemStack>> armor = new HashMap<>();
    private final HashMap<Location, Material> point = new HashMap<>();
    //Location jailLobby;
    //Location jailFree;
    //Location jailWarden;

    public UUID in(UUID uniqueId) {
        World world = OddJob.getInstance().getMySQLManager().inPlayerJail(uniqueId);
        if (world != null) {
            return world.getUID();
        }
        return null;
    }

    public boolean setInJail(UUID uuidPlayer, UUID world, CommandSender sender) {
        if (in(uuidPlayer) != null) {
            OddJob.getInstance().getMessageManager().danger("Is already in jail!", sender, false);
            return false;
        }
        if (!has(world)) {
            OddJob.getInstance().getMessageManager().danger("This world has no finished jail!", sender, false);
            return false;
        }

        // IF IN ARENA, GET HIM OUT
        if (OddJob.getInstance().getArenaManager().isInArena(uuidPlayer)) {
            OddJob.getInstance().getArenaManager().abort(uuidPlayer);
        }
        // PLACING IN JAIL LIBRARY
        OddJob.getInstance().getMySQLManager().addPlayerJail(uuidPlayer, world);

        Player player = OddJob.getInstance().getPlayerManager().getPlayer(uuidPlayer);
        // STORING INVENTORY
        List<ItemStack> inv = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
        inventory.put(uuidPlayer, inv);
        List<ItemStack> arm = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
        armor.put(uuidPlayer, arm);
        player.getInventory().clear();
        // CHANGING GAMEMODE
        OddJob.getInstance().getPlayerManager().setGameMode(player, GameMode.ADVENTURE);
        OddJob.getInstance().getTeleportManager().jail(player, jailLobby(world));
        OddJob.getInstance().getMessageManager().info("You are now in jail, serving your time!", uuidPlayer, false);
        OddJob.getInstance().getMessageManager().success("You have successfully set " + OddJob.getInstance().getPlayerManager().getName(uuidPlayer) + " in jail!", sender, true);
        return true;
    }

    public boolean freeFromJail(UUID uuidPlayer, CommandSender sender) {
        UUID world = in(uuidPlayer);
        if (world == null) {
            OddJob.getInstance().getMessageManager().danger("Is not in jail!", sender, false);
            return false;
        }

        OddJob.getInstance().getMySQLManager().deletePlayerJail(uuidPlayer);

        Player player = OddJob.getInstance().getPlayerManager().getPlayer(uuidPlayer);
        // RESTORING INVENTORY
        player.getInventory().clear();
        List<ItemStack> armorContent = armor.get(uuidPlayer);
        ItemStack[] armors = player.getInventory().getArmorContents();
        for (int i = 0; i < armorContent.size(); i++) {
            armors[i] = armorContent.get(i);
        }
        armor.remove(uuidPlayer);
        List<ItemStack> content = inventory.get(uuidPlayer);
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < content.size(); i++) {
            contents[i] = content.get(i);
        }
        inventory.remove(uuidPlayer);
        // CHANGING GAMEMODE
        OddJob.getInstance().getPlayerManager().setGameMode(player, GameMode.SURVIVAL);
        OddJob.getInstance().getTeleportManager().jail(player, jailFree(world));
        OddJob.getInstance().getMessageManager().info("You have served your time, be more careful!", uuidPlayer, false);
        OddJob.getInstance().getMessageManager().success("You have successfully set " + OddJob.getInstance().getPlayerManager().getName(uuidPlayer) + " free from jail!", sender, true);

        return true;
    }

    public boolean has(UUID world) {
        if (jailFree(world) == null) {
            OddJob.getInstance().getMessageManager().console("Jail: Missing FREE");
        }
        if (jailLobby(world) == null) {
            OddJob.getInstance().getMessageManager().console("Jail: Missing HOLDINGCELL");
        }
        if (jailWarden(world) == null) {
            OddJob.getInstance().getMessageManager().console("Jail: Missing WARDEN");
        }
        return (jailFree(world) != null && jailLobby(world) != null && jailWarden(world) != null);
    }

    private Location jailLobby(UUID world) {
        return OddJob.getInstance().getMySQLManager().getJail(world, "lobby");
    }

    private Location jailFree(UUID world) {
        return OddJob.getInstance().getMySQLManager().getJail(world, "free");
    }

    public Location jailWarden(UUID world) {
        return OddJob.getInstance().getMySQLManager().getJail(world, "warden");
    }

    public void set(Slot string, Player player) {
        Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        point.put(player.getLocation(), block.getType());
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
        OddJob.getInstance().getMySQLManager().setJail(player.getWorld().getUID(), string.name(), player.getLocation());
    }

    public void revertPoints() {
        for (Location location : point.keySet()) {
            location.getBlock().getRelative(BlockFace.DOWN).setType(point.get(location));
        }
    }

    /* One world can only contain one jail */
    public enum Slot {
        lobby, free, warden
    }
}
