package no.helponline.Utils.Arena;

import no.helponline.OddJob;
import no.helponline.Utils.Odd.OddPlayer;
import no.helponline.Utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private static ItemStack leaveItem;
    private final String name;
    private final Location lobby;
    private final List<Arena> arenas;
    private int reqplayers, maxplayers;
    private boolean reset;
    private GameState gameState;
    private int lobbytime;
    private final int cooldown = 30;
    private final int death = 0;
    private static ItemStack playerNavigator;
    private static String inventoryTitle;
    private ScoreboardPhase scoreboardPhase;
    private final List<OddPlayer> spectators = new ArrayList<>();
    private final List<OddPlayer> players = new ArrayList<>();
    private Arena arena;
    private CooldownPhase cooldownPhase;
    private IngamePhase ingamePhase;
    private DeathmatchPhase deathmatchPhase;
    private FinishPhase finishPhase;

    public static void reinitializeDatabase() {
        leaveItem = Utility.parseItemStack(OddJob.getInstance().getConfig().getString("Leave-Item"));
        playerNavigator = Utility.parseItemStack(OddJob.getInstance().getConfig().getString("Spectating.Player-Navigator.Item"));
        String string = OddJob.getInstance().getConfig().getString("Spectating.Player-Navigator.Inventory-Title");
        if (string != null && string.length() > 32) {
            string = string.substring(0, 32);
        }

        inventoryTitle = string;
    }

    public static ItemStack getLeaveItem() {
        return leaveItem;
    }

    public Game(String name, Location lobby, int lobbytime, int reqplayers, List<Arena> arenas, boolean reset) {
        this.name = name;
        this.lobby = lobby;
        this.lobbytime = lobbytime;
        this.arenas = arenas;
        this.reset = reset;

        if (reqplayers < 2) reqplayers = 2;
        this.reqplayers = reqplayers;
        this.maxplayers = getFewestArena().getSpawns().size();
        setScoreboardPhase(OddJob.getInstance().getScoreManager().getNewScoreboardPhase(GameState.WAITING));
        setState(GameState.WAITING);
    }
    public void setState(GameState gameState) {
        this.gameState = gameState;
        if (OddJob.getInstance().getSignManager() != null) {
            OddJob.getInstance().getSignManager().updateSigns();
        }
        // TODO Bossbar
    }

    private Arena getFewestArena() {
        int slot = Integer.MAX_VALUE;
        Arena arena = null;
        for (Arena a : arenas) {
            if (a.getSpawns().size() < slot) {
                slot = a.getSpawns().size();
                arena = a;
            }
        }
        return arena;
    }

    private void setScoreboardPhase(ScoreboardPhase scoreboardPhase) {
        if (this.scoreboardPhase != null && scoreboardPhase == null) {
            for (OddPlayer oddPlayer : players) {
                oddPlayer.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
            for (OddPlayer oddPlayer : spectators) {
                oddPlayer.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }

            this.scoreboardPhase = scoreboardPhase;

            if (scoreboardPhase != null) {
                scoreboardPhase.initScoreboard(this);
                updateScoreboard();
            }
        }
    }

    private void updateScoreboard() {
        if (scoreboardPhase != null) {
            for (CustomScore customScore : scoreboardPhase.getScores()) {
                customScore.update(this);
            }
            for (OddPlayer oddPlayer : players) {
                updateScoreboard(oddPlayer);
            }
            for (OddPlayer oddPlayer : spectators) {
                updateScoreboard(oddPlayer);
            }
        }
    }
    private void updateScoreboard(OddPlayer oddPlayer) {
        oddPlayer.getPlayer().setScoreboard(scoreboardPhase.getScoreboard());
    }

    public Arena getCurrentArena() {
        return arena;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getRequiredPlayers() {
        return reqplayers;
    }

    public int getPlayingUsers() {
        return players.size();
    }

    public int getDeathAmount() {
        return death;
    }

    public List<OddPlayer> getSpectators() {
        return spectators;
    }

    public CooldownPhase getCooldownPhase() {
        return cooldownPhase;
    }

    public IngamePhase getIngamePhase() {
        return ingamePhase;
    }

    public DeathmatchPhase getDeathmatchPhase() {
        return deathmatchPhase;
    }

    public String getName() {
        return name;
    }

    public int getMaximumPlayers() {
        return maxplayers;
    }
}
