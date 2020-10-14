package no.helponline.Utils;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class ScoreBoard {
    private final HashMap<UUID, Integer> tasks = new HashMap<>();
    private final UUID uuid;

    public ScoreBoard(UUID uuid) {
        this.uuid = uuid;
    }

    public void setID(int id) {
        tasks.put(uuid,id);
    }

    public int getID() {
        return tasks.get(uuid);
    }

    public boolean hasID() {
        return tasks.containsKey(uuid);
    }

    public void stop() {
        if (hasID()) {
            Bukkit.getScheduler().cancelTask(getID());
            tasks.remove(uuid);
        }
    }
}