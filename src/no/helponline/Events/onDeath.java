package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class onDeath implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skull = (SkullMeta) playerSkull.getItemMeta();
        if (skull != null) {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
            skull.setDisplayName(ChatColor.DARK_PURPLE + "Head of " + OddJob.getInstance().getPlayerManager().getName(player.getUniqueId()));
            playerSkull.setItemMeta(skull);
        }
        //player.getWorld().dropItem(player.getLocation(), playerSkull);

        Block leftSide = player.getLocation().getBlock();
        Block rightSide = leftSide.getRelative(0, 0, -1);

        leftSide.setType(Material.CHEST);
        rightSide.setType(Material.CHEST);

        BlockData leftData = leftSide.getBlockData();
        ((Directional) leftData).setFacing(BlockFace.EAST);
        leftSide.setBlockData(leftData);

        org.bukkit.block.data.type.Chest chestDataLeft = (org.bukkit.block.data.type.Chest) leftData;
        chestDataLeft.setType(org.bukkit.block.data.type.Chest.Type.RIGHT);
        leftSide.setBlockData(chestDataLeft);

        Chest leftChest = (Chest) leftSide.getState();

        BlockData rightData = rightSide.getBlockData();
        ((Directional) rightData).setFacing(BlockFace.EAST);
        rightSide.setBlockData(rightData);

        org.bukkit.block.data.type.Chest chestDataRight = (org.bukkit.block.data.type.Chest) rightData;
        chestDataRight.setType(org.bukkit.block.data.type.Chest.Type.LEFT);
        rightSide.setBlockData(chestDataRight);

        leftChest.getInventory().setContents(player.getInventory().getContents());
        leftChest.getInventory().addItem(playerSkull);
        event.getDrops().clear();
        player.getInventory().clear();

    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

    }
}
