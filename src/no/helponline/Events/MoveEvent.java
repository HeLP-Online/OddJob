package no.helponline.Events;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle;
import no.helponline.Guilds.Guild;
import no.helponline.Guilds.Zone;
import no.helponline.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveEvent implements Listener {
    @EventHandler
    public void onGuildMove(PlayerMoveEvent event) {
        Chunk chunk = event.getTo().getChunk();
        if (event.getFrom().getChunk().equals(chunk)) {
            return;
        }

        OddJob.getInstance().log("Chunk changed; new Chunk: X: " + chunk.getX() + "; Z: " + chunk.getZ() + "; World: " + chunk.getWorld().getName() + ";");
        Guild guild = OddJob.getInstance().getGuildManager().getGuildByChunk(chunk);

        Player player = event.getPlayer();

        String s = "";

        if (guild != null) {


            OddJob.getInstance().log("We know this place");
            switch (guild.getZone()) {
                case GUILD:
                    s = s + ChatColor.BLUE;
                    break;
                case ARENA:
                case WAR:
                    s = s + ChatColor.RED;
                    break;
                case SAFE:
                    s = s + ChatColor.GREEN;
                    break;
                default:
                    s = s + ChatColor.YELLOW;
                    break;
            }

            s = s + guild.getName();
        }

        if (guild != null && guild.getZone().equals(Zone.GUILD)) {
            PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + s + guild.getName() + " hails you!\"}"), 40, 20, 20);
            (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(title);
        }
    }
}
