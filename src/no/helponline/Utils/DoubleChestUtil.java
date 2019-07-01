package no.helponline.Utils;

import no.helponline.OddJob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;

import java.util.HashMap;


public class DoubleChestUtil {

    public static void doorToggle(Block block) {
        Door door = (Door) block.getBlockData();
        boolean open = door.isOpen();
        BlockFace doorFace = door.getFacing();
        Bisected.Half doorHalf = door.getHalf();
        Door.Hinge doorHinge = door.getHinge();

        OddJob.getInstance().log("Open:" + (open ? "true" : "false") + "; Face:" + doorFace + "; Hinge:" + doorHinge + "; Half:" + doorHalf);

        boolean left = (doorHinge == Door.Hinge.LEFT);
        boolean lower = (doorHalf == Bisected.Half.BOTTOM);
        Block doorUpperRight;
        Block doorLowerRight;
        Block doorLowerLeft;
        Block doorUpperLeft;

        HashMap<Block, Door> doors = new HashMap<>();

        if (!lower) {
            if (!left) {
                //UPPER RIGHT
                doorUpperRight = block;
                doorLowerRight = block.getRelative(BlockFace.DOWN);
                doorLowerLeft = getLowerLeftDoor(block).getBlock();
                doorUpperLeft = doorLowerLeft.getRelative(BlockFace.UP);
            } else {
                //UPPER LEFT
                doorUpperLeft = block;
                doorLowerLeft = block.getRelative(BlockFace.DOWN);
                doorLowerRight = getLowerRightDoor(block).getBlock();
                doorUpperRight = doorLowerRight.getRelative(BlockFace.UP);
            }
        } else {
            if (!left) {
                //LOWER RIGHT
                doorUpperRight = block.getRelative(BlockFace.UP);
                doorLowerRight = block;
                doorLowerLeft = getLowerLeftDoor(block).getBlock();
                doorUpperLeft = doorLowerLeft.getRelative(BlockFace.UP);
            } else {
                //LOWER LEFT
                doorUpperLeft = block.getRelative(BlockFace.UP);
                doorLowerLeft = block;
                doorLowerRight = getLowerRightDoor(block).getBlock();
                doorUpperRight = doorLowerRight.getRelative(BlockFace.UP);
            }
        }
        if (doorLowerLeft.getType().equals(Material.IRON_DOOR))
            doors.put(doorLowerLeft, (Door) doorLowerLeft.getBlockData());
        if (doorLowerRight.getType().equals(Material.IRON_DOOR))
            doors.put(doorLowerRight, (Door) doorLowerRight.getBlockData());
        if (doorUpperLeft.getType().equals(Material.IRON_DOOR))
            doors.put(doorUpperLeft, (Door) doorUpperLeft.getBlockData());
        if (doorUpperRight.getType().equals(Material.IRON_DOOR))
            doors.put(doorUpperRight, (Door) doorUpperRight.getBlockData());
        OddJob.getInstance().log("Doors: " + doors.size());
        for (Block bl : doors.keySet()) {
            OddJob.getInstance().log("Toggle doors!");
            if (open) doors.get(bl).setOpen(false);
            else doors.get(bl).setOpen(true);
            bl.getState().update();
        }
    }

    public static Location getLowerRightDoor(Block block) {
        Door door = (Door) block.getBlockData();
        boolean open = door.isOpen();
        BlockFace doorFace = door.getFacing();
        Bisected.Half doorHalf = door.getHalf();
        Door.Hinge doorHinge = door.getHinge();

        OddJob.getInstance().log("Open:" + (open ? "true" : "false") + "; Face:" + doorFace + "; Hinge:" + doorHinge + "; Half:" + doorHalf);

        boolean right = (doorHinge == Door.Hinge.RIGHT);
        boolean lower = (doorHalf == Bisected.Half.BOTTOM);

        if (!lower) {
            block = block.getRelative(BlockFace.DOWN);
            OddJob.getInstance().log("is upper, moving down");
        }
        if (!right) {
            OddJob.getInstance().log("is left");
            Block test = null;
            switch (doorFace) {
                case NORTH:
                    test = block.getRelative(BlockFace.EAST);
                    break;
                case WEST:
                    test = block.getRelative(BlockFace.NORTH);
                    break;
                case SOUTH:
                    test = block.getRelative(BlockFace.WEST);
                    break;
                case EAST:
                    test = block.getRelative(BlockFace.SOUTH);
                    break;
            }
            if (test.getType() == Material.IRON_DOOR) {
                //TODO doors
                OddJob.getInstance().log("has right");
                block = test;
            }
        } else {
            OddJob.getInstance().log("is right");
        }
        OddJob.getInstance().log("Lower right : " + block.getLocation().serialize().toString());
        return block.getLocation();
    }

    public static Location getLowerLeftDoor(Block block) {
        Door door = (Door) block.getBlockData();
        boolean open = door.isOpen();
        BlockFace doorFace = door.getFacing();
        Bisected.Half doorHalf = door.getHalf();
        Door.Hinge doorHinge = door.getHinge();

        OddJob.getInstance().log("Open:" + (open ? "true" : "false") + "; Face:" + doorFace + "; Hinge:" + doorHinge + "; Half:" + doorHalf);

        boolean left = (doorHinge == Door.Hinge.LEFT);
        boolean lower = (doorHalf == Bisected.Half.BOTTOM);

        if (!lower) {
            block = block.getRelative(BlockFace.DOWN);
            OddJob.getInstance().log("is upper, moving down");
        }
        if (!left) {
            OddJob.getInstance().log("is right");
            Block test = null;
            switch (doorFace) {
                case NORTH:
                    test = block.getRelative(BlockFace.WEST);
                    break;
                case WEST:
                    test = block.getRelative(BlockFace.SOUTH);
                    break;
                case SOUTH:
                    test = block.getRelative(BlockFace.EAST);
                    break;
                case EAST:
                    test = block.getRelative(BlockFace.NORTH);
                    break;
            }
            if (test.getType() == Material.IRON_DOOR) {
                //TODO doors
                OddJob.getInstance().log("has left");
                block = test;
            }
        } else {
            OddJob.getInstance().log("is left");
        }
        OddJob.getInstance().log("Lower left : " + block.getLocation().serialize().toString());
        return block.getLocation();
    }

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
