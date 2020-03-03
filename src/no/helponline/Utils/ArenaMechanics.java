package no.helponline.Utils;

import no.helponline.OddJob;
import org.bukkit.entity.Player;

public class ArenaMechanics {
    public static void cancel(Player player) {
        OddJob.getInstance().getArenaManager().editArena.remove(player.getUniqueId());
        player.getInventory().remove(OddJob.getInstance().getArenaManager().spawnTool);
    }
}
