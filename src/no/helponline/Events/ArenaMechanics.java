package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.Arena;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class ArenaMechanics implements Listener {
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity target = event.getEntity();
        if (target instanceof Player && !target.isOp()) {
            OddJob.getInstance().getPlayerManager().setInCombat(target.getUniqueId());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        cancel(event.getPlayer());
        UUID uuid = event.getPlayer().getUniqueId();

    }

    @EventHandler
    public void playerTagPlayer(EntityDamageByEntityEvent event) {

    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if ((event.getHand() == EquipmentSlot.OFF_HAND) || (event.getClickedBlock() == null)) {
            return;
        }
        if (block == null) return;
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getArenaManager().spawnTool)) {
            event.setCancelled(true);
            Arena arena = OddJob.getInstance().getArenaManager().editArena.get(player.getUniqueId());
            if (arena.next > arena.getMaxPlayers()) {
                cancel(player);
            } else {
                arena.getSpawn().put(arena.next, block.getLocation().add(0, 1, 0));
                block.setType(Material.PINK_WOOL);
                OddJob.getInstance().getMessageManager().success("Spawn point " + arena.next + "/" + arena.getMaxPlayers() + " set.", player,false);
                arena.next++;
                if (arena.next > arena.getMaxPlayers()) {
                    arena.next = 1;
                    OddJob.getInstance().getMessageManager().success("All spawn points are now set. You can now start the game.", player,false);
                    OddJob.getInstance().getArenaManager().createArena(arena);
                    cancel(player);
                }
            }
        }
    }

    public void cancel(Player player) {
        OddJob.getInstance().getArenaManager().editArena.remove(player.getUniqueId());
        player.getInventory().remove(OddJob.getInstance().getArenaManager().spawnTool);
    }
}
