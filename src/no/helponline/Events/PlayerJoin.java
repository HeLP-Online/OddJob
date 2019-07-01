package no.helponline.Events;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle;
import no.helponline.Managers.EconManager;
import no.helponline.Managers.LockManager;
import no.helponline.Managers.PlayerManager;
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

        PlayerManager.updatePlayer(uuid, player.getName());

        if (EconManager.hasAccount(uuid)) {
            EconManager.setBalance(player.getUniqueId(), 200.0D);
            player.sendMessage("Your first balance is initialized!");
            OddJob.getInstance().log("Initializing account for " + player.getName());
        }

        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"�aWelcome to HeLP\"}"), 40, 20, 20);
        (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(title);

        LockManager.remove(uuid);
    }
}
