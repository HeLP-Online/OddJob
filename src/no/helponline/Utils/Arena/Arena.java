package no.helponline.Utils.Arena;

import com.sk89q.worldedit.bukkit.fastutil.Hash;
import no.helponline.OddJob;
import no.helponline.Utils.Arena.ArenaManager.GameType;
import no.helponline.Utils.Arena.Games.HungerGames;
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

import javax.annotation.Nonnull;
import java.util.*;

public class Arena {
    private final int id;
    private final List<Location> gameSpawns = new ArrayList<>();
    public Location lobbySpawn;
    public Location signLocation;
    private List<UUID> players = new ArrayList<>();
    private final HashMap<UUID, Kit> kits = new HashMap<>();
    private GameState gameState;
    private Countdown countdown;
    private Game game = null;
    private GameType gameType;
    private final int requiredPlayers;
    private final String prefix;
    private int blocks = 0;
    public HashMap<Integer,Location> blPos = new HashMap<>();
    public HashMap<Integer,Material> blMat = new HashMap<>();

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    private boolean disabled = true;

    public Arena(int id) {
        OddJob.getInstance().getMessageManager().console("Arena constructor -> new");
        this.id = id;
        this.requiredPlayers = 2;
        this.prefix = "[Arena " + this.id + "] ";
    }

    public Arena(int id, GameType gameType, boolean disabled) {
        OddJob.getInstance().getMessageManager().console("Arena constructor -> load");
        this.id = id;
        this.requiredPlayers = 2;
        this.prefix = "[Arena " + this.id + "] ";
        this.gameType = gameType;

        switch (gameType) {
            case SURVIVAL:
                game = new HungerGames();
                break;
            case TNT:
                game = new TNTTag();
                break;
        }
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

    public void addPlayer(@Nonnull UUID uuid) {
        OddJob.getInstance().getMessageManager().console("Arena addPlayer");
        players.add(uuid);

        BossBar bossBar = Bukkit.createBossBar("Waiting for more players", BarColor.GREEN, BarStyle.SOLID);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            bossBar.addPlayer(player);
        bossBar.setProgress(0.5);

        if (!countdown.isRunning() && players.size() >= requiredPlayers) {
            countdown.start(60);
        }
    }

    public void removePlayer(UUID uuid) {
        OddJob.getInstance().getMessageManager().console("Arena removePlayer");
        players.remove(uuid);
        kits.remove(uuid);

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            for (Iterator<KeyedBossBar> it = Bukkit.getBossBars(); it.hasNext(); ) {
                BossBar b = it.next();
                b.removePlayer(player);
                OddJob.getInstance().log(String.valueOf(b.getProgress()));
            }
        }

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

    public List<Location> getGameSpawns() {
        return gameSpawns;
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

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public Location getSignLocation() {
        return signLocation;
    }

    public void setLobbySpawn(Location location) {
        lobbySpawn = location;
    }

    public void removeLobbySpawn() {
        lobbySpawn = null;
    }

    public void setGameSpawn(Location location) {
        Block position = location.getBlock();
        Block block = position.getRelative(BlockFace.DOWN);
        blPos.put(blocks,block.getLocation());
        blMat.put(blocks,block.getType());
        block.setType(Material.GOLD_BLOCK);
        blocks++;
    }

    public void removeGameSpawn(int i) {
        Location location = blPos.get(i);
        Block block = location.getBlock();
        block.setType(blMat.get(i));
        blMat.remove(i);
        blPos.remove(i);
    }

    enum GameState {
        STARTED, COUNTDOWN, PREPARING, WAITING
    }
}
