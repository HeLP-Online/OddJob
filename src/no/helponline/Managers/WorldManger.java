package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldManger {
    public GameMode getGamemode(World world) {
        return OddJob.getInstance().getMySQLManager().getWorldMode(world);
    }

    public boolean getForceMode(World world) {
        return OddJob.getInstance().getMySQLManager().getForceMode(world);
    }

    public void setGameMode(Player player, String string) {
    }

    public void setSpawn(Player player) {
    }
}
