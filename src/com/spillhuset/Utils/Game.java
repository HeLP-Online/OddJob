package com.spillhuset.Utils;

import com.spillhuset.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Game {
    private String displayName;
    private int minPlayers;
    private int maxPlayers;
    private World world;
    private List<Location> spawnPoints;
    private boolean isTeamGame;
    private Location lobbyPoint;
    private List<ItemStack> normalItems;
    private List<ItemStack> rareItems;

    private List<UUID> players;
    private List<UUID> spectators;
    private GameState gameState = GameState.LOBBY;
    private HashMap<UUID, Location> gamePlayerToSpawnPoint = new HashMap<>();
    private List<Chest> opened;
    private boolean movementFrozen = false;

    public Game(String gameName, String displayName, int maxPlayers, int minPlayers, String worldName, Location lobbyPoint, List<Location> spawnPoints, List<ItemStack> normalItems, List<ItemStack> rareItems) {
        RollbackHandler.getRollbackHandler().rollback(worldName);
        world = Bukkit.createWorld(new WorldCreator(worldName + "_active"));
        this.spawnPoints = spawnPoints;
        opened = new ArrayList<>();
        this.normalItems = normalItems;
        this.rareItems = rareItems;
        players = new ArrayList<>();
        spectators = new ArrayList<>();
    }

    public boolean joinGame(UUID gamePlayer) {
        if (isState(GameState.LOBBY) || isState(GameState.STARTING)) {
            if (players.size() >= maxPlayers) {
                // SEND MESSAGE 'FULL'
                return false;
            }

            players.add(gamePlayer);
            // Teleport player   gamePlayer.teleport(isState(GameState.LOBBY) ? lobbyPoint : null);
            // Send message -- size

            // COPY INVENTORY
            // COPY ARMOR
            // SET ADVENTURE
            // SET HEALTH AND FOOD

            if (players.size() == minPlayers && !isState(GameState.STARTING)) {
                gameState = GameState.STARTING;
                // ANNOUNCE COUNTDOWN TO START
                startCountdown();
                OddJob.getInstance().getArenaManager().setGame(gamePlayer, this);
            } else {
                activateSpectatorSettings(gamePlayer);
                OddJob.getInstance().getArenaManager().setGame(gamePlayer, this);
            }

        }
        return true;

    }

    private boolean isState(GameState state) {
        return gameState == state;
    }

    public void activateSpectatorSettings(UUID player) {

    }

    public enum GameState {
        STARTING, LOBBY
    }
}
