package no.helponline.Utils.Arena;

import no.helponline.OddJob;
import no.helponline.Utils.Arena.Arena.GameState;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {
    private final Arena arena;
    private int time;

    public Countdown(Arena arena) {
        this.arena = arena;
        this.time = 0;
    }

    public void start(int time) {
        arena.setGameState(GameState.COUNTDOWN);
        this.time = time;
        this.runTaskTimer(OddJob.getInstance(),0L,20L);
    }

    @Override
    public void run() {
        if (time == 0) {
            cancel();
            arena.start();
            return;
        }

        if (time % 15 == 0 || time <= 10) {
            if (time != 1) {
                arena.broadcast("Game will start in "+time+" seconds.");
            } else {
                arena.broadcast("Game will start in "+time+" second.");
            }
        }

        if (arena.getPlayers().size() < arena.getRequiredPlayers()) {
            cancel();
            arena.setGameState(GameState.WAITING);
            arena.broadcast("There are too few players. Countdown stopped.");
            return;
        }
        time--;
    }

    public boolean isRunning() {
        return arena.getGameState() == GameState.COUNTDOWN;
    }
}
