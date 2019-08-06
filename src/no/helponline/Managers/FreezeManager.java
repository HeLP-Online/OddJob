package no.helponline.Managers;

import org.bukkit.Location;

import java.util.UUID;

public class FreezeManager {
    public void add(UUID player, Location location) {
        //OddJob.getInstance().getMySQLManager().addFrozen(player,location);
    }

    public Location get(UUID player) {
        // return OddJob.getInstance().getMySQLManager().getFrozen(player);
        return null;
    }

    public void del(UUID player) {
        //OddJob.getInstance().getMySQLManager().deleteFrozen(player);
    }
}
