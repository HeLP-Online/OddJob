package no.helponline.Utils;

import no.helponline.OddJob;
import no.helponline.Utils.Arena.Arena;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class GameStartCountdown {
    private int timeUntilStart;
    private final OddJob plugin;
    private final Arena arena;
    private int timer;

    public GameStartCountdown(int timeUntilStart,int arena) {
        plugin = OddJob.getInstance();
        this.timeUntilStart = timeUntilStart;
        this.arena = plugin.getArenaManager().getArena(arena);
    }

    public void start() {
        timer = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

           for (; timeUntilStart >= 0; timeUntilStart--) {
               if (timeUntilStart == 0) {
                   arena.start();
                   break;
               }

               if (timeUntilStart % 10 == 0 || timeUntilStart < 10) {
                   //TODO broadcast time until start
               }
           }
       }, 0, 20);
    }
    public void stop(){
        plugin.getServer().getScheduler().cancelTask(timer);
        //TODO broadcast timer canceled
    }
}
