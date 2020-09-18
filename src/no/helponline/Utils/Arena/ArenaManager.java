package no.helponline.Utils.Arena;

import no.helponline.OddJob;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class ArenaManager {
    /**
     * Makes an unique id for each arena
     */
    private int num;
    private int size;

    /**
     * A list of created Arenas
     */
    private final HashMap<Integer, Arena> arenas = new HashMap<>();
    private final HashMap<UUID, Integer> editor = new HashMap<>();

    private final HashMap<UUID, ItemStack[]> inventories = new HashMap<>();
    private final HashMap<UUID, ItemStack[]> armor = new HashMap<>();
    private final HashMap<UUID, Double> health = new HashMap<>();
    private final HashMap<UUID, Integer> food = new HashMap<>();
    private final HashMap<UUID, Location> from = new HashMap<>();

    /**
     * Constructor
     */
    public ArenaManager() {
        OddJob.getInstance().getMessageManager().console("ArenaManager constructor");
        num = OddJob.getInstance().getConfig().getInt("Arenas.Num", 1);
        size = OddJob.getInstance().getConfig().getInt("Arenas.Default.Size", 10);
    }

    /**
     * Creating a new Arena
     */
    public void createArena(UUID uuid) {
        OddJob.getInstance().getMessageManager().console("ArenaManager createArena");
        // Creating
        Arena arena = new Arena(num);
        // Storing
        arenas.put(num, arena);
        // Selecting editor
        editor.put(uuid, num);

        // Increment number of arenas
        num++;
        OddJob.getInstance().getConfig().set("Arenas.Num", num);
        OddJob.getInstance().getMessageManager().success("Arena created", uuid, true);
    }

    public Arena getArena(int id) {
        return arenas.getOrDefault(id, null);
    }

    public void addPlayer(Player player, int id) {
        OddJob.getInstance().getMessageManager().console("ArenaManager addPlayer");
        Arena arena = getArena(id);
        if (arena == null) {
            OddJob.getInstance().getMessageManager().danger("Arena not found", player, false);
            return;
        }
        UUID uuid = player.getUniqueId();
        from.put(uuid, player.getLocation());
        arena.addPlayer(uuid);

        inventories.put(uuid, player.getInventory().getContents());
        armor.put(uuid, player.getInventory().getArmorContents());
        health.put(uuid, player.getHealth());
        food.put(uuid, player.getFoodLevel());

        player.getInventory().setArmorContents(null);
        player.getInventory().clear();

        player.setFoodLevel(20);
        player.setHealth(20D);
        player.setFireTicks(0);
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        for (Arena a : arenas.values()) {
            if (a.getPlayers().contains(uuid)) {
                a.removePlayer(uuid);
                break;
            }
        }

        player.getInventory().setArmorContents(null);
        player.getInventory().clear();

        player.getInventory().setArmorContents(armor.get(uuid));
        player.getInventory().setContents(inventories.get(uuid));
        player.setFireTicks(0);
        player.setHealth(health.get(uuid));
        player.setFoodLevel(food.get(uuid));
        player.teleport(from.get(uuid));
    }

    public void load() {
        OddJob.getInstance().getMessageManager().console("ArenaManager load");
        int id = 1;
        World world = (Bukkit.getWorld("flat") != null) ? Bukkit.getWorld("flat") : Bukkit.createWorld(new WorldCreator("flat").type(WorldType.FLAT));
        Location location = new Location(world, 0, 10, 0);
        GameType gameType = GameType.SURVIVAL;
        arenas.put(id, new Arena(id, gameType, false));
    }

    public HashMap<Integer, Arena> getList() {
        return arenas;
    }

    public int getEditor(UUID uuid) {
        return editor.get(uuid);
    }


    enum GameType {
        SURVIVAL, TNT
    }
}
