package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.ArenaType;
import com.spillhuset.Utils.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.UUID;

public class ArenaSQL extends MySQLManager {
    public static void saveSettings(HashMap<String, Object> settings) {
        for (String string : settings.keySet()) {
            if (string.equals("lobby")) {
                Location lobby = (Location) settings.get(string);
                oddjobConfig.set("arenas." + string + ".world", lobby.getWorld().getUID().toString());
                oddjobConfig.set("arenas." + string + ".x", lobby.getX());
                oddjobConfig.set("arenas." + string + ".y", lobby.getY());
                oddjobConfig.set("arenas." + string + ".z", lobby.getZ());
                oddjobConfig.set("arenas." + string + ".yaw", lobby.getYaw());
                oddjobConfig.set("arenas." + string + ".pitch", lobby.getPitch());
            }
        }
    }

    public static void save(HashMap<UUID, Game> games) {
        for (UUID uuid : games.keySet()) {
            String value = uuid.toString();
            Game game = games.get(uuid);
            oddjobConfig.set("games." + value + ".name", game.getName());
            oddjobConfig.set("games." + value + ".minPlayers", game.getMinPlayers());
            oddjobConfig.set("games." + value + ".maxPlayers", game.getMaxPlayers());
            oddjobConfig.set("games." + value + ".active", game.isActive());
            oddjobConfig.set("games." + value + ".type", game.getType().name());
            oddjobConfig.set("games." + value + ".world", game.getLobbySpawn().getWorld().getUID().toString());
            oddjobConfig.set("games." + value + ".lobbySpawn.x", game.getLobbySpawn().getX());
            oddjobConfig.set("games." + value + ".lobbySpawn.y", game.getLobbySpawn().getY());
            oddjobConfig.set("games." + value + ".lobbySpawn.z", game.getLobbySpawn().getZ());
            oddjobConfig.set("games." + value + ".lobbySpawn.yaw", game.getLobbySpawn().getYaw());
            oddjobConfig.set("games." + value + ".lobbySpawn.pitch", game.getLobbySpawn().getPitch());
            for (Integer num : game.getSpawns().keySet()) {
                Location location = game.getSpawn(num);
                oddjobConfig.set("games." + value + ".spawn.x", location.getX());
                oddjobConfig.set("games." + value + ".spawn.y", location.getY());
                oddjobConfig.set("games." + value + ".spawn.z", location.getZ());
                oddjobConfig.set("games." + value + ".spawn.yaw", location.getYaw());
                oddjobConfig.set("games." + value + ".spawn.pitch", location.getPitch());
            }
        }
    }

    public static HashMap<String, Object> loadSettings() {
        HashMap<String, Object> settings = new HashMap<>();
        ConfigurationSection arenas = oddjobConfig.getConfigurationSection("arenas");
        if (arenas != null) {
            World world = Bukkit.getWorld(UUID.fromString(arenas.getString("world")));
            double x = arenas.getDouble("x");
            double y = arenas.getDouble("y");
            double z = arenas.getDouble("z");
            float yaw = arenas.getInt("yaw");
            float pitch = arenas.getInt("pitch");

            settings.put("lobby", new Location(world, x, y, z, yaw, pitch));
        }
        return settings;
    }

    public static HashMap<UUID, Game> load() {
        HashMap<UUID, Game> games = new HashMap<>();
        int i = 0;

        if (oddjobConfig.getConfigurationSection("games") != null) {
            for (String value : oddjobConfig.getConfigurationSection("games").getKeys(false)) {
                UUID uuid = UUID.fromString(value);
                String name = oddjobConfig.getString("games." + value + ".name");
                int minPlayers = oddjobConfig.getInt("games." + value + ".minPlayers");
                int maxPlayers = oddjobConfig.getInt("games." + value + ".maxPlayers");
                boolean active = oddjobConfig.getBoolean("games." + value + ".active");
                ArenaType type = ArenaType.valueOf(oddjobConfig.getString("games." + value + ".type"));
                UUID world = UUID.fromString(oddjobConfig.getString("games." + value + ".world"));
                double lobbySpawnX = oddjobConfig.getDouble("games." + value + ".lobbySpawn.x");
                double lobbySpawnY = oddjobConfig.getDouble("games." + value + ".lobbySpawn.y");
                double lobbySpawnZ = oddjobConfig.getDouble("games." + value + ".lobbySpawn.x");
                float lobbySpawnYaw = oddjobConfig.getInt("games." + value + ".lobbySpawn.yaw");
                float lobbySpawnPitch = oddjobConfig.getInt("games." + value + ".lobbySpawn.pitch");
                Location lobbySpawn = new Location(Bukkit.getWorld(world), lobbySpawnX, lobbySpawnY, lobbySpawnZ, lobbySpawnYaw, lobbySpawnPitch);
                HashMap<Integer, Location> spawns = new HashMap<>();
                if (oddjobConfig.getConfigurationSection("games." + value + ".spawns") != null) {
                    for (String num : oddjobConfig.getConfigurationSection("games." + value + ".spawns").getKeys(false)) {
                        double spawnX = oddjobConfig.getDouble("games." + value + ".spawn." + num + ".x");
                        double spawnY = oddjobConfig.getDouble("games." + value + ".spawn." + num + ".y");
                        double spawnZ = oddjobConfig.getDouble("games." + value + ".spawn." + num + ".z");
                        float spawnYaw = oddjobConfig.getInt("games." + value + ".spawn." + num + ".yaw");
                        float spawnPitch = oddjobConfig.getInt("games." + value + ".spawn." + num + ".pitch");
                        Location spawn = new Location(Bukkit.getWorld(world), spawnX, spawnY, spawnZ, spawnYaw, spawnPitch);
                        spawns.put(Integer.parseInt(num), spawn);
                    }
                }

                games.put(uuid, new Game(uuid, name, minPlayers, maxPlayers, type, lobbySpawn, spawns, active));
                i++;
            }
        }
        OddJob.getInstance().log("ArenaGames loaded " + i);
        return games;
    }
}
