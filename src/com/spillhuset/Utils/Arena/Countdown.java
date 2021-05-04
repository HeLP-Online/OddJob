package com.spillhuset.Utils.Arena;

import com.spillhuset.OddJob;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {
    public int counterStart = 0;
    public Arena arena;

    public Countdown(int counter, Arena arena) {
        this.counterStart = counter;
        this.arena = arena;
    }

    @Override
    public void run() {
        OddJob.getInstance().log("test");
    }
}
