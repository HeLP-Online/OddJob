package no.helponline.Utils.Arena;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.ScoreBoard;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ArenaManager {
    /**
     * Makes an unique id for each arena
     */
    private int num = 0;
    /**
     * A list of created Arenas
     */
    private HashMap<Integer, Arena> arenas = new HashMap<>();
    private HashMap<UUID, Integer> editors = new HashMap<>();
    private final HashMap<UUID, ItemStack[]> inventories = new HashMap<>();
    private final HashMap<UUID, ItemStack[]> armor = new HashMap<>();
    private final HashMap<UUID, Double> health = new HashMap<>();
    private final HashMap<UUID, Integer> food = new HashMap<>();
    private final HashMap<UUID, Location> from = new HashMap<>();
    public File fileConfig;
    public FileConfiguration config;
    public Countdown countdown;

    /**
     * Constructor
     */
    public ArenaManager() {
    }

    public Arena getArena(int arena) {
        return arenas.get(arena);
    }

    public void editArena(Player player, int id) {
        editors.put(player.getUniqueId(), id);
        OddJob.getInstance().getScoreManager().create(player, ScoreBoard.ArenaMaker);
    }

    public int getEditor(UUID uniqueId) {
        return editors.get(uniqueId);
    }

    public int createArena(Player player, GameType gameType) {
        Arena arena = new Arena(++num, gameType);
        arena.setRequiredPlayers(2);
        arena.setGameType(gameType);
        if (gameType.equals(GameType.SKYWARS)) {
            arena.setLobbySpawn(OddJob.getInstance().getTeleportManager().getSpawn());
            arena.setWorld(player.getWorld().getUID());
        }
        arenas.put(arena.getId(), arena);
        editors.put(player.getUniqueId(), arena.getId());
        OddJob.getInstance().getScoreManager().create(player, ScoreBoard.ArenaMaker);
        return arena.getId();
    }

    public HashMap<Integer, Arena> getList() {
        return arenas;
    }

    public void addPlayer(Player player, int id) {
        Arena arena = OddJob.getInstance().getArenaManager().getArena(id);
        arena.queue(player);
        if (arena.getQueue() >= arena.getPlayersLimit()) {
            arena.start();
        } else if (arena.getQueue() >= arena.getRequiredPlayers()) {
            arena.movePlayers();
        }
    }

    public void removePlayer(Player player) {
        for (int id : arenas.keySet()) {
            arenas.get(id).remove(player);
        }

        player.sendMessage("removed!");
    }

    public void save() {
        if (fileConfig == null) {
            fileConfig = new File(OddJob.getInstance().getDataFolder(), "arenas.yml");
        }
        config = YamlConfiguration.loadConfiguration(fileConfig);

        for (int id : arenas.keySet()) {
            OddJob.getInstance().log("save " + id);
            Arena arena = arenas.get(id);
            config.set("arena." + id + ".disabled", arena.isDisabled());
            config.set("arena." + id + ".limit", arena.getPlayersLimit());
            config.set("arena." + id + ".required", arena.getRequiredPlayers());
            config.set("arena." + id + ".type", arena.getGameType().toString());
            config.set("arena." + id + ".world", arena.getWorld().toString());
            config.set("arena." + id + ".lobby", arena.getLobbySpawn());
            config.set("arena." + id + ".spawnnum", arena.getSpawnNum());
            config.createSection("arena." + id + ".spawns", arena.getGameSpawn());
            try {
                config.save(fileConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void load() {
        /*
        int i = 0;
        if (fileConfig == null) {
            fileConfig = new File(OddJob.getInstance().getDataFolder(), "arenas.yml");
        }
        config = YamlConfiguration.loadConfiguration(fileConfig);

        Set<String> arenas = config.getConfigurationSection("arena").getKeys(false);
        int id = 0, playersRequired = 0, playersLimited = 0, spawnNum = 0;
        boolean disabled = true;
        GameType gameType = null;
        UUID world = null;
        Location lobby = null;
        Map<String, Object> sp;
        HashMap<String, Location> spawns = new HashMap<>();
        for (String a : arenas) {
            id = Integer.parseInt(a);
            disabled = config.getBoolean("arena." + id + ".disabled");
            playersLimited = config.getInt("arena." + id + ".limit");
            playersRequired = config.getInt("arena." + id + ".required");
            gameType = GameType.valueOf(config.getString("arena." + id + ".type"));
            world = UUID.fromString(config.getString("arena." + id + ".world"));
            lobby = config.getLocation("arena." + id + ".lobby");
            spawnNum = config.getInt("arena." + id + ".spawnnum");
            sp = config.getConfigurationSection("arena." + id + ".spawns").getValues(true);

            for (String s : sp.keySet()) {
                spawns.put(s, (Location) sp.get(s));
            }
            Arena arena = new Arena(id, disabled, playersLimited, playersRequired, gameType, world, lobby, spawnNum, spawns);
            this.arenas.put(id, arena);
            i++;
        }
        OddJob.getInstance().log("loaded " + i);
        
         */

    }

    public enum GameType {
        SURVIVAL, TNT, SKYWARS
    }
}
