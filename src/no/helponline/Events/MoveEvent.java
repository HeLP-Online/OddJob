package no.helponline.Events;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle;
import no.helponline.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class MoveEvent implements Listener {
    @EventHandler
    public void onGuildMove(PlayerMoveEvent event) {
        Chunk chunk = event.getTo().getChunk();
        Player player = event.getPlayer();
        UUID guildId = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk);


        /* WHEN CHANGING CHUNK */
        if (event.getFrom().getChunk().equals(chunk)) {
            // within same chunk
            return;
        } else if (OddJob.inChunk.containsKey(player.getUniqueId())) {
            if (OddJob.inChunk.get(player.getUniqueId()) == guildId) {
                if (OddJob.getInstance().getGuildManager().hasAutoClaim(player.getUniqueId())) {
                    OddJob.getInstance().getGuildManager().autoClaim(player.getUniqueId(), chunk);
                }
                return;
            }
        }
        OddJob.inChunk.put(player.getUniqueId(), guildId);

        /* PRINT GUILD NAME */
        String s = "";
        if (guildId != null) {
            switch (OddJob.getInstance().getMySQLManager().getZoneByGuild(guildId)) {
                case GUILD:
                    s = s + ChatColor.DARK_BLUE + OddJob.getInstance().getMySQLManager().getGuildNameByUUID(guildId) + " hails you!";
                    break;
                case ARENA:
                case WAR:
                    s = s + ChatColor.RED + "Draw your weapon!";
                    break;
                case JAIL:
                    s = s + ChatColor.GOLD + "Nap time!";
                    break;
                case SAFE:
                    s = s + ChatColor.GREEN + "Take a break and prepare!";
                    break;
            }
        } else {
            s = s + ChatColor.YELLOW + "Welcome to the wild!";
        }
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + s + "\"}"), 40, 20, 20);
        (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(title);
    }
}
