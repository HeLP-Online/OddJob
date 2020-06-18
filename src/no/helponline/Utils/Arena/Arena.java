package no.helponline.Utils.Arena;

import no.helponline.OddJob;
import no.helponline.Utils.Arena.ArenaManager.GameType;
import no.helponline.Utils.Arena.Games.HungerGames;
import no.helponline.Utils.Arena.Games.TNTTag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Arena {
    private final int id;
    private final Location lobbySpawn;
    private final List<Location> gameSpawns = new ArrayList<>();
    private List<UUID> players = new ArrayList<>();
    private final HashMap<UUID, Kit> kits = new HashMap<>();
    private GameState gameState;
    private Countdown countdown;
    private Game game = null;
    private GameType gameType;
    private final int requiredPlayers;
    private final String prefix;

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    private boolean disabled = true;

    public Arena(Location location, int id) {
        OddJob.getInstance().getMessageManager().console("Arena constructor -> new");
        this.lobbySpawn = location;
        this.id = id;
        this.requiredPlayers = 2;
        this.prefix = "[Arena " + this.id + "] ";
    }

    public Arena(Location location, int id, GameType gameType,boolean disabled) {
        OddJob.getInstance().getMessageManager().console("Arena constructor -> load");
        this.lobbySpawn = location;
        this.id = id;
        this.requiredPlayers = 2;
        this.prefix = "[Arena " + this.id + "] ";
        this.gameType = gameType;

        game = switch(gameType) {
            case SURVIVAL -> new HungerGames();
            case TNT ->new TNTTag();
        };
        this.countdown = new Countdown(this);
        this.disabled = disabled;
    }

    public void reset() {
        OddJob.getInstance().getMessageManager().console("Arena reset");
        this.players.clear();
        this.kits.clear();
        this.gameState = GameState.WAITING;
    }

    public void broadcast(String message) {
        OddJob.getInstance().getMessageManager().console("Arena broadcast");
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                player.sendMessage(prefix + message);
        }
    }

    public void addPlayer(UUID uuid) {
        OddJob.getInstance().getMessageManager().console("Arena addPlayer");
        players.add(uuid);

        if (!countdown.isRunning() && players.size() >= requiredPlayers) {
            countdown.start(60);
        }
    }

    public void removePlayer(UUID uuid) {
        OddJob.getInstance().getMessageManager().console("Arena removePlayer");
        players.remove(uuid);
        kits.remove(uuid);

        if (gameState == GameState.STARTED && players.size() == 1) {

            UUID winner = players.get(0);

            OddJob.getInstance().getMessageManager().broadcast(OddJob.getInstance().getPlayerManager().getName(winner) + " wom arena " + id + "!");

            removePlayer(winner);

            reset();
        }
    }

    public int getId() {
        return this.id;
    }

    public List<UUID> getPlayers() {
        return this.players;
    }

    public Location getLobbySpawn() {
        return this.lobbySpawn;
    }

    public void getGameSpawns(Player player) {

    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public int getRequiredPlayers() {
        return this.requiredPlayers;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public Game getGame() {
        return game;
    }

    public void prepare() {
        OddJob.getInstance().getMessageManager().console("Arena prepare");
        this.gameState = GameState.PREPARING;
    }

    public void start() {
        OddJob.getInstance().getMessageManager().console("Arena start");
        this.gameState = GameState.STARTED;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    enum GameState {
        STARTED, COUNTDOWN, PREPARING, WAITING
    }
}
