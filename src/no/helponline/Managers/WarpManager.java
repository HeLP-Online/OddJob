package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WarpManager {
    public void warp(Player player, String name) {
        warp(player, name, "");
    }

    public void warp(Player player, String name, String password) {
        Location location = OddJob.getInstance().getMySQLManager().getWarp(name, password);
        if (location != null) {
            OddJob.getInstance().getTeleportManager().teleport(player, location);
        }
    }

    public void addWarp(Player player, String name, String password) {
        OddJob.getInstance().getMySQLManager().addWarp(name, player, password);
    }

    public void delWarp(String name, String password) {
        OddJob.getInstance().getMySQLManager().deleteWarp(name, password);
    }

    public boolean exists(String name) {
        return OddJob.getInstance().getMySQLManager().getWarp(name);
    }
}
