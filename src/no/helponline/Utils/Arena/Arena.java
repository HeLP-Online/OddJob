package no.helponline.Utils.Arena;

import io.netty.util.collection.ByteCollections;
import no.helponline.OddJob;
import no.helponline.Utils.Arena.ArenaManager.GameType;
import no.helponline.Utils.Arena.Games.HungerGames;
import no.helponline.Utils.Arena.Games.Skywars;
import no.helponline.Utils.Arena.Games.TNTTag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.annotation.Nonnull;
import java.util.*;

public class Arena {
    private int id;
    private UUID world = null;
    private GameType gameType;
    private Location lobbySpawn = null;
    private HashMap<String, Location> gameSpawns = new HashMap<>();
    private int spawnNum = 0;
    private List<Player> queue = new ArrayList<>();
    private List<Player> alive = new ArrayList<>();
    private List<Player> dead = new ArrayList<>();
    private List<Player> spectator = new ArrayList<>();
    private boolean disabled = true;
    private int requiredPlayers;
    private int limitPlayers;
    private GameState gameState = GameState.WAITING;

    public Arena(int id, GameType gameType) {
        this.id = id;
        this.gameType = gameType;
    }

    public Arena(int id, boolean disabled, int playersLimited, int playersRequired, GameType gameType, UUID world, Location lobby, int spawnNum, HashMap<String, Location> spawns) {
        this.id = id;
        this.disabled = disabled;
        this.requiredPlayers = playersRequired;
        this.limitPlayers = playersLimited;
        this.gameType = gameType;
        this.world = world;
        this.lobbySpawn = lobby;
        this.spawnNum = spawnNum;
        this.gameSpawns = spawns;
    }

    public void setWorld(UUID world) {
        this.world = world;
    }

    public void start() {
    }

    public void setGameType(GameType valueOf) {
        gameType = valueOf;
    }

    public void setLobbySpawn(Location location) {
        lobbySpawn = location;
    }

    public void removeLobbySpawn() {
        setLobbySpawn(null);
    }

    public void setGameSpawn(Location location, String name) {
        if (name.isEmpty()) name = String.valueOf(++spawnNum);
        gameSpawns.put(name, location);
    }

    public int getGameSpawns() {
        return gameSpawns.size();
    }

    public void removeGameSpawn(String name) {
        gameSpawns.remove(name);
    }

    public Location tpGameSpawn(String name) {
        return gameSpawns.get(name);
    }

    public int getId() {
        return id;
    }

    public void queue(Player player) {
        queue.add(player);
        player.sendMessage("added to queue");
        //todo Start Timer
    }

    public void remove(Player player) {
        queue.remove(player);
        dead.remove(player);
        alive.remove(player);
        spectator.remove(player);
    }

    public GameType getGameType() {
        return gameType;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public int getPlayers() {
        return queue.size();
    }

    public int getRequiredPlayers() {
        return requiredPlayers;
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public Location getSignLocation() {
        return null;
    }

    public int getPlayersLimit() {
        return limitPlayers;
    }

    public UUID getWorld() {
        return world;
    }

    public int getSpawnNum() {
        return spawnNum;
    }

    public HashMap<String, Location> getGameSpawn() {
        return gameSpawns;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public int getQueue() {
        return queue.size();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void broadcast(String string) {
        for (Player player : queue) {
            player.sendMessage(string);
        }
        for (Player player : spectator) {
            player.sendMessage(string);
        }

    }

    public void setRequiredPlayers(int num) {
        requiredPlayers = num;
    }

    public void movePlayers() {
        int i = 0;
        for (Player p : queue) {
            queue.remove(p);
            alive.add(p);
            p.teleport(gameSpawns.get(i), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    enum GameState {
        STARTED(false), COUNTDOWN(true), PREPARING(false), WAITING(true);

        GameState(boolean canJoin) {
        }

        public boolean canJoin() {
            return canJoin();
        }
    }
}
