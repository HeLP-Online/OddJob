package no.helponline.Utils;

import org.bukkit.Location;
import org.bukkit.block.*;


public class DoubleChestUtil {
    public static Location getTopLocation(Block block) {
        Chest chest = (Chest) block.getState();
        if (chest.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();
            BlockState state = block.getState();
            String s = state.getBlockData().getAsString();
            String chestFace = s.substring(s.indexOf("=") + 1, s.indexOf(","));


            if (chestFace.equalsIgnoreCase("north")) {
                BlockState otherBlock = block.getRelative(BlockFace.WEST).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return otherBlock.getLocation();
                    }
                }
                return block.getLocation();
            }
            if (chestFace.equalsIgnoreCase("east")) {
                BlockState otherBlock = block.getRelative(BlockFace.NORTH).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return otherBlock.getLocation();
                    }
                }
                return block.getLocation();
            }
            if (chestFace.equalsIgnoreCase("south")) {
                BlockState otherBlock = block.getRelative(BlockFace.EAST).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return block.getLocation();
                    }
                }
                return block.getRelative(BlockFace.WEST).getLocation();
            }
            if (chestFace.equalsIgnoreCase("west")) {
                BlockState otherBlock = block.getRelative(BlockFace.SOUTH).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return block.getLocation();
                    }
                }
                return block.getRelative(BlockFace.NORTH).getLocation();
            }
        }
        return block.getLocation();
    }

    public static Location getBottomLocation(Block block) {
        Chest chest = (Chest) block.getState();
        if (chest.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();
            BlockState state = block.getState();
            String s = state.getBlockData().getAsString();
            String chestFace = s.substring(s.indexOf("=") + 1, s.indexOf(","));

            if (chestFace.equalsIgnoreCase("north")) {
                BlockState otherBlock = block.getRelative(BlockFace.WEST).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return block.getLocation();
                    }
                }
                return block.getRelative(BlockFace.EAST).getLocation();
            }
            if (chestFace.equalsIgnoreCase("east")) {
                BlockState otherBlock = block.getRelative(BlockFace.NORTH).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return block.getLocation();
                    }
                }
                return block.getRelative(BlockFace.SOUTH).getLocation();
            }
            if (chestFace.equalsIgnoreCase("south")) {
                BlockState otherBlock = block.getRelative(BlockFace.EAST).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return otherBlock.getLocation();
                    }
                }
                return block.getLocation();
            }
            if (chestFace.equalsIgnoreCase("west")) {
                BlockState otherBlock = block.getRelative(BlockFace.SOUTH).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return otherBlock.getLocation();
                    }
                }
                return block.getLocation();
            }
        }
        return null;
    }
}
