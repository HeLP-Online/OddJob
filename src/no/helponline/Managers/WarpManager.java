package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpManager {
    public boolean warp(Player player, String name) {
        return warp(player, name, "");
    }

    public boolean warp(Player player, String name, String password) {
        Location location = OddJob.getInstance().getMySQLManager().getWarp(name, password);
        if (location != null) {
            OddJob.getInstance().getTeleportManager().teleport(player, location);
            return true;
        } else {
            OddJob.getInstance().getMessageManager().danger("Sorry, can't find " + name + ", or the password is wrong.", player);
        }
        return false;
    }

    public void addWarp(Player player, String name, String password) {
        OddJob.getInstance().log(player.getLocation().getWorld().getUID().toString());
        OddJob.getInstance().getMySQLManager().addWarp(name, player, password);
    }

    public void delWarp(String name, String password) {
        OddJob.getInstance().getMySQLManager().deleteWarp(name, password);
    }

    public boolean exists(String name) {
        return OddJob.getInstance().getMySQLManager().getWarp(name);
    }

    public List<String> listWarps() {
        return OddJob.getInstance().getMySQLManager().listWarps();
    }
}
