package no.helponline.Events;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle;
import no.helponline.OddJob;
import no.helponline.Utils.Zone;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class MoveEvent implements Listener {
    @EventHandler
    public void freeze(PlayerMoveEvent event) {
        //TODO block teleport
        Player player = event.getPlayer();
        if (OddJob.getInstance().getFreezeManager().get(player.getUniqueId()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        World world = event.getPlayer().getWorld();
        Player player = event.getPlayer();

        GameMode playerMode = OddJob.getInstance().getPlayerManager().getGamemode(player, world);
        GameMode worldMode = OddJob.getInstance().getWorldManager().getGamemode(world);
        boolean forceMode = OddJob.getInstance().getWorldManager().getForceMode(world);

        if (forceMode) {
            player.setGameMode(worldMode);
        } else {
            if (player.getGameMode() != playerMode) {
                player.setGameMode(playerMode);
            }
        }
    }

    @EventHandler
    public void onGuildMove(PlayerMoveEvent event) {
        Chunk chunk = event.getTo().getChunk();
        Player player = event.getPlayer();
        UUID guild = null;

        // Have the player changed chunk?
        if (event.getFrom().getChunk().equals(chunk)) {
            // within same chunk
            return;
        }

        // Who owns the chunk?
        guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk, player.getWorld());
        if (guild == null) {
            guild = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD);
        }
        if (OddJob.inChunk.containsKey(player.getUniqueId())) {
            // If nobody, it's the wild
            if (guild == null) {
                guild = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD);
            }

            if (OddJob.inChunk.get(player.getUniqueId()) == guild) {
                if (OddJob.getInstance().getGuildManager().hasAutoClaim(player.getUniqueId())) {
                    OddJob.getInstance().getGuildManager().autoClaim(player, chunk);
                }
                return;
            }

        }

        OddJob.inChunk.put(player.getUniqueId(), guild);
        /* PRINT GUILD NAME */
        String s = "";
        if (guild != null) {
            switch (OddJob.getInstance().getGuildManager().getZoneByGuild(guild)) {
                case GUILD:
                    s = s + ChatColor.DARK_BLUE + OddJob.getInstance().getMySQLManager().getGuildNameByUUID(guild) + " hails you!";
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
