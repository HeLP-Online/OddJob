package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ArenaManager {
    public HashMap<UUID, Arena> editArena = new HashMap<>();
    public ItemStack spawnTool;
    private HashMap<String, Arena> arenas = new HashMap<>();
    private HashMap<UUID, Double> health = new HashMap<UUID, Double>();
    private HashMap<UUID, Integer> food = new HashMap<>();
    private HashMap<UUID, ItemStack[]> inventory = new HashMap<>();
    private HashMap<UUID, ItemStack[]> armor = new HashMap<>();
    private Location lobbySpawn;
    private FileConfiguration config = OddJob.getInstance().getConfig();

    public ArenaManager() {
        // MAKE SPAWN TOOL
        spawnTool = new ItemStack(Material.STICK);
        ItemMeta im = spawnTool.getItemMeta();
        im.setDisplayName("Arena Spawn Tool");
        List<String> lore = new ArrayList<>();
        lore.add("Right click a block to select as a spawnpoint to the arena");
        im.setLore(lore);
        spawnTool.setItemMeta(im);
    }

    public Arena getArena(String name) {
        for (String string : arenas.keySet()) {
            if (arenas.get(string).getName().equalsIgnoreCase(name)) {
                return arenas.get(string);
            }
        }
        return null;
    }

    public void setLobbySpawn(Player player) {
        config.set("lobby.spawn", serializeLoc(player.getLocation()));
        OddJob.getInstance().saveConfig();
        OddJob.getInstance().getMessageManager().success("Arena Lobby Spawn set!", player);
    }

    public void addPlayer(Player player, String arenaName) {
        Arena arena = getArena(arenaName);
        if (arena == null) {
            OddJob.getInstance().getMessageManager().warning("Invalid arena", player);
            return;
        }

        arena.getPlayers().add(player.getUniqueId());
        inventory.put(player.getUniqueId(), player.getInventory().getContents());
        armor.put(player.getUniqueId(), player.getInventory().getArmorContents());

        player.getInventory().setArmorContents(null);
        player.getInventory().clear();

        health.put(player.getUniqueId(), player.getHealth());
        food.put(player.getUniqueId(), player.getFoodLevel());
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.teleport(lobbySpawn);
    }

    public void removePlayer(Player player) {
        Arena activeArena = null;
        for (String name : arenas.keySet()) {
            if (arenas.get(name).getPlayers().contains(player.getUniqueId())) {
                activeArena = arenas.get(name);
            }
        }
        if (activeArena == null || !activeArena.getPlayers().contains(player.getUniqueId())) {
            player.sendMessage("Invalid operation!");
            return;
        }

        activeArena.getPlayers().remove(player.getUniqueId());

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(inventory.get(player.getUniqueId()));
        player.getInventory().setArmorContents(armor.get(player.getUniqueId()));
        inventory.remove(player.getUniqueId());
        armor.remove(player.getUniqueId());
        player.teleport(lobbySpawn);
        player.setFireTicks(0);
        player.setHealth(health.get(player.getUniqueId()));
        player.setFoodLevel(food.get(player.getUniqueId()));
    }

    public Arena createArena(String name, Arena.Type type, int minPlayers, int maxPlayers, HashMap<Integer, Location> spawn) {
        Arena arena = new Arena(name, type, minPlayers, maxPlayers, spawn);
        arenas.put(name, arena);

        for (Integer i : spawn.keySet()) {
            config.set("arenas." + name + ".spawn." + i, serializeLoc(spawn.get(i)));
        }
        config.set("arenas." + name + ".type", type.name());
        config.set("arenas." + name + ".name", name);
        config.set("arenas." + name + ".maxPlayers", maxPlayers);
        config.set("arenas." + name + ".minPlayers", minPlayers);
        config.set("arenas." + name + ".complete", false);

        OddJob.getInstance().saveConfig();
        return arena;
    }

    public Arena createArena(Arena arena) {
        arenas.put(arena.getName(), arena);
        for (Integer i : arena.getSpawn().keySet()) {
            config.set("arenas." + arena.getName() + ".spawn." + i, serializeLoc(arena.getSpawn().get(i)));
        }
        config.set("arenas." + arena.getName() + ".type", arena.getType().name());
        config.set("arenas." + arena.getName() + ".name", arena.getName());
        config.set("arenas." + arena.getName() + ".maxPlayers", arena.getMaxPlayers());
        config.set("arenas." + arena.getName() + ".minPlayers", arena.getMinPlayers());
        config.set("arenas." + arena.getName() + ".complete", arena.isComplete());

        OddJob.getInstance().saveConfig();
        return arena;
    }

    public void removeArena(String name) {
        Arena arena = getArena(name);
        if (arena == null) {
            return;
        }
        arenas.remove(name);

        config.set("arenas." + name, null);

        OddJob.getInstance().saveConfig();
    }

    public boolean isInArena(UUID player) {
        for (String name : arenas.keySet()) {
            if (arenas.get(name).getPlayers().contains(player)) {
                return true;
            }
        }
        return false;
    }

    public Arena reloadArena(String name, Arena.Type type, int maxPlayers, int minPlayers, HashMap<Integer, Location> spawn) {
        Arena arena = new Arena(name, type, maxPlayers, minPlayers, spawn);
        arenas.put(name, arena);

        return arena;
    }

    public int loadArenas() {
        int c = 0;
        if (config.contains("arenas")) {
            OddJob.getInstance().log("contains");
            if (config.get("arenas") != null) {
                OddJob.getInstance().log("not null");
                for (String name : config.getConfigurationSection("arenas").getKeys(false)) {
                    OddJob.getInstance().log("looping");
                    c++;
                    HashMap<Integer, Location> spawn = new HashMap<>();
                    Arena.Type type = Arena.Type.valueOf(config.getString("arenas." + name + ".type"));
                    int maxPlayers = config.getInt("arenas." + name + ".maxPlayers");
                    int minPlayers = config.getInt("arenas." + name + ".minPlayers");

                    for (int i = 1; i <= maxPlayers; i++) {
                        if (config.getString("arenas." + name + ".spawn." + i) != null) {
                            spawn.put(i, deserializeLoc(config.getString("arenas." + name + ".spawn." + i)));
                        }
                    }

                    Arena arena = reloadArena(name, type, maxPlayers, minPlayers, spawn);
                }
            }
        }
        return c;
    }

    public String serializeLoc(Location l) {
        return l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getYaw() + "," + l.getPitch();
    }

    public Location deserializeLoc(String string) {
        String[] st = string.split(",");
        return new Location(Bukkit.getWorld(st[0]), Integer.parseInt(st[1]), Integer.parseInt(st[2]), Integer.parseInt(st[3]), Float.parseFloat(st[4]), Float.parseFloat(st[5]));
    }

    public List<Arena> listArenas() {
        return new ArrayList<>(arenas.values());
    }

    public void queue(String name, UUID player) {
        Arena arena = getArena(name);
        if (arena != null) {
            if (arena.getQueue().contains(player)) {
                OddJob.getInstance().getMessageManager().warning("You are already in queue for " + arena.getName(), player);
                return;
            }
            if (arena.getPlayers().contains(player)) {
                OddJob.getInstance().getMessageManager().danger("! You are already in the arena " + arena.getName(), player);
                return;
            }
            arena.getQueue().add(player);
            OddJob.getInstance().getMessageManager().success("You are now queued up for " + arena.getName(), player);
        } else {
            OddJob.getInstance().getMessageManager().warning("Sorry, we cant find arena named " + name, player);
        }

    }

    public void edit(String name, Player player) {
        for (String s : arenas.keySet()) {
            Arena arena = getArena(name);
            if (arena != null) {
                editArena.put(player.getUniqueId(), arena);
                player.getInventory().addItem(spawnTool);
            } else {
                OddJob.getInstance().getMessageManager().warning("Sorry, we cant find arena named " + name, player);
            }
        }
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }
}