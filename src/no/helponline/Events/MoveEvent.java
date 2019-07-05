package no.helponline.Events;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle;
import no.helponline.Guilds.Guild;
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
        Guild guild = OddJob.getInstance().getGuildManager().getGuildByChunk(chunk);
        UUID guildId = (guild != null) ? guild.getId() : null;
        if (event.getFrom().getChunk().equals(chunk)) {
            // within same chunk
            return;
        } else if (OddJob.inChunk.containsKey(player.getUniqueId())) {
            if (OddJob.inChunk.get(player.getUniqueId()) == guildId) {
                return;
            }
        }
        OddJob.inChunk.put(player.getUniqueId(), guildId);

        OddJob.getInstance().log("Chunk changed; new Chunk: X: " + chunk.getX() + "; Z: " + chunk.getZ() + "; World: " + chunk.getWorld().getName() + ";");

        String s = "";

        if (guild != null) {

            OddJob.getInstance().log("We know this place");
            switch (guild.getZone()) {
                case GUILD:
                    s = s + ChatColor.DARK_BLUE;
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

            s = s + guild.getName() + " hails you!";
        } else {
            s = s + ChatColor.YELLOW + "Welcome to the wild!";
        }

        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + s + "\"}"), 40, 20, 20);
        (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(title);
    }
}
