package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.Utils.Enum.ArenaType;
import com.spillhuset.Utils.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class ArenaSQL extends MySQLManager {
    public static void save(HashMap<UUID, Game> games) {
        for (UUID uuid : games.keySet()) {
            String value = uuid.toString();
            Game game = games.get(uuid);
            oddjobConfig.set("games." + value + ".name", game.getName());
            oddjobConfig.set("games." + value + ".minPlayers", game.getMinPlayers());
            oddjobConfig.set("games." + value + ".maxPlayers", game.getMaxPlayers());
            oddjobConfig.set("games." + value + ".active", game.isActive());
            oddjobConfig.set("games." +value+ ".type", game.getType().name());
            oddjobConfig.set("games." + value + ".world", game.getLobbySpawn().getWorld().getUID());
            oddjobConfig.set("games." + value + ".lobbySpawn.getX",game.getLobbySpawn().getX());
            oddjobConfig.set("games." + value + ".lobbySpawn.getY",game.getLobbySpawn().getY());
            oddjobConfig.set("games." + value + ".lobbySpawn.getZ",game.getLobbySpawn().getZ());
            oddjobConfig.set("games." +value + ".lobbySpawn.getYaw",game.getLobbySpawn().getYaw());
            oddjobConfig.set("games." + value + ".lobbySpawn.getPitch",game.getLobbySpawn().getPitch());
            for (Integer num : game.getSpawns().keySet()) {
                Location location = game.getSpawn(num);
                oddjobConfig.set("games." + value + ".spawn.getX",location.getX());
                oddjobConfig.set("games." + value + ".spawn.getY",location.getY());
                oddjobConfig.set("games." + value + ".spawn.getZ",location.getZ());
                oddjobConfig.set("games." + value + ".spawn.getYaw",location.getYaw());
                oddjobConfig.set("games." + value+ ".spawn.getPitch",location.getPitch());
            }
        }
    }

    public static HashMap<UUID, Game> load() {
        HashMap<UUID, Game> games = new HashMap<>();
        if (oddjobConfig.getConfigurationSection("games") != null) {
            for (String value : oddjobConfig.getConfigurationSection("games").getKeys(false)) {
                UUID uuid = UUID.fromString(value);
                String name = oddjobConfig.getString("games." + value+ ".name");
                int minPlayers = oddjobConfig.getInt("games." + value + ".minPlayers");
                int maxPlayers = oddjobConfig.getInt("games." +value + ".maxPlayers");
                boolean active = oddjobConfig.getBoolean("games." + value + ".active");
                ArenaType type = ArenaType.valueOf(oddjobConfig.getString("games." + value + ".type"));
                UUID world = UUID.fromString(oddjobConfig.getString("games." +value + ".world"));
                double lobbySpawnX = oddjobConfig.getDouble("games." + value + ".lobbySpawn.getX");
                double lobbySpawnY = oddjobConfig.getDouble("games." + value + ".lobbySpawn.getY");
                double lobbySpawnZ = oddjobConfig.getDouble("games." + value + ".lobbySpawn.getZ");
                float lobbySpawnYaw = oddjobConfig.getInt("games." + value + ".lobbySpawn.getYaw");
                float lobbySpawnPitch = oddjobConfig.getInt("games." +value+ ".lobbySpawn.getPitch");
                Location lobbySpawn = new Location(Bukkit.getWorld(world), lobbySpawnX, lobbySpawnY, lobbySpawnZ, lobbySpawnYaw, lobbySpawnPitch);
                HashMap<Integer, Location> spawns = new HashMap<>();
                if (oddjobConfig.getConfigurationSection("games." + value + ".spawns") != null) {
                    for (String num : oddjobConfig.getConfigurationSection("games." +value + ".spawns").getKeys(false)) {
                        double spawnX = oddjobConfig.getDouble("games." +value+ ".spawn." + num + ".getX");
                        double spawnY = oddjobConfig.getDouble("games." + value+ ".spawn." + num + ".getY");
                        double spawnZ = oddjobConfig.getDouble("games." +value + ".spawn." + num + ".getZ");
                        float spawnYaw = oddjobConfig.getInt("games." + value + ".spawn." + num + ".getYaw");
                        float spawnPitch = oddjobConfig.getInt("games." + value + ".spawn." + num + ".getPitch");
                        Location spawn = new Location(Bukkit.getWorld(world), spawnX, spawnY, spawnZ, spawnYaw, spawnPitch);
                        spawns.put(Integer.parseInt(num), spawn);
                    }
                }

                games.put(uuid, new Game(uuid, name, minPlayers, maxPlayers, type, lobbySpawn, spawns, active));
            }
        }
        return games;
    }
}
