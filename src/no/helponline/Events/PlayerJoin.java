package no.helponline.Events;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle;
import no.helponline.OddJob;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoin implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Making an OddPlayer
        OddJob.getInstance().getPlayerManager().updatePlayer(uuid, player.getName());
        if (OddJob.getInstance().getBanManager().getBan(uuid) != null) {
            OddJob.getInstance().getBanManager().kick(player);
        } else {

            if (OddJob.getInstance().getEconManager().hasAccount(uuid)) {
                OddJob.getInstance().getEconManager().setBalance(player.getUniqueId(), 200.0D);
                //player.sendMessage("Your first balance is initialized!");
                OddJob.getInstance().log("Initializing account for " + player.getName());
            }

            PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"§aWelcome to HeLP\"}"), 40, 20, 20);
            (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(title);

            OddJob.getInstance().getLockManager().remove(uuid);
        }
    }
}
