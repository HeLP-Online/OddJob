package no.helponline.Utils;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;


public class Utility {

    public static void doorToggle(Block block) {
        Door door = (Door) block.getBlockData();
        boolean open = door.isOpen();
        BlockFace doorFace = door.getFacing();
        Bisected.Half doorHalf = door.getHalf();
        Door.Hinge doorHinge = door.getHinge();

        //OddJob.getInstance().log("Open:" + (open ? "true" : "false") + "; Face:" + doorFace + "; Hinge:" + doorHinge + "; Half:" + doorHalf);

        boolean left = (doorHinge == Door.Hinge.LEFT);
        boolean lower = (doorHalf == Bisected.Half.BOTTOM);
        Block doorUpperRight = null;
        Block doorLowerRight = null;
        Block doorLowerLeft = null;
        Block doorUpperLeft = null;

        List<Block> doors = new ArrayList<>();

        if (!lower) {
            if (!left) {
                //UPPER RIGHT
                doorUpperRight = block;
                doorLowerRight = block.getRelative(BlockFace.DOWN);
                Block test = getLowerLeftDoor(block).getBlock();
                if (((Door) test.getBlockData()).getHinge().equals(Door.Hinge.LEFT)) {
                    doorLowerLeft = test;
                    doorUpperLeft = doorLowerLeft.getRelative(BlockFace.UP);
                }

            } else {
                //UPPER LEFT
                doorUpperLeft = block;
                doorLowerLeft = block.getRelative(BlockFace.DOWN);
                Block test = getLowerRightDoor(block).getBlock();
                if (((Door) test.getBlockData()).getHinge().equals(Door.Hinge.RIGHT)) {
                    doorLowerRight = test;
                    doorUpperRight = doorLowerRight.getRelative(BlockFace.UP);
                }
            }
        } else {
            if (!left) {
                //LOWER RIGHT
                doorUpperRight = block.getRelative(BlockFace.UP);
                doorLowerRight = block;
                Block test = getLowerLeftDoor(block).getBlock();
                if (((Door) test.getBlockData()).getHinge().equals(Door.Hinge.LEFT)) {
                    doorLowerLeft = test;
                    doorUpperLeft = doorLowerLeft.getRelative(BlockFace.UP);
                }
            } else {
                //LOWER LEFT
                doorUpperLeft = block.getRelative(BlockFace.UP);
                doorLowerLeft = block;
                Block test = getLowerRightDoor(block).getBlock();
                if (((Door) test.getBlockData()).getHinge().equals(Door.Hinge.RIGHT)) {
                    doorLowerRight = test;
                    doorUpperRight = doorLowerRight.getRelative(BlockFace.UP);
                }
            }
        }
        if (doorLowerLeft != null && OddJob.getInstance().getLockManager().getDoors().contains(doorLowerLeft.getType()))
            doors.add(doorLowerLeft);
        if (doorLowerRight != null && OddJob.getInstance().getLockManager().getDoors().contains(doorLowerRight.getType()))
            doors.add(doorLowerRight);
        if (doorUpperLeft != null && OddJob.getInstance().getLockManager().getDoors().contains(doorUpperLeft.getType()))
            doors.add(doorUpperLeft);
        if (doorUpperRight != null && OddJob.getInstance().getLockManager().getDoors().contains(doorUpperRight.getType()))
            doors.add(doorUpperRight);

        for (Block bl : doors) {
            Openable o = (Openable) bl.getState().getBlockData();
            o.setOpen(!open);
            bl.setBlockData(o);
            bl.getState().update(true, true);
        }
    }

    public static Location getLowerRightDoor(Block block) {
        Door door = (Door) block.getBlockData();
        boolean open = door.isOpen();
        BlockFace doorFace = door.getFacing();
        Bisected.Half doorHalf = door.getHalf();
        Door.Hinge doorHinge = door.getHinge();

        //OddJob.getInstance().log("Open:" + (open ? "true" : "false") + "; Face:" + doorFace + "; Hinge:" + doorHinge + "; Half:" + doorHalf);

        boolean right = (doorHinge == Door.Hinge.RIGHT);
        boolean lower = (doorHalf == Bisected.Half.BOTTOM);

        if (!lower) {
            block = block.getRelative(BlockFace.DOWN);
            //OddJob.getInstance().log("is upper, moving down");
        }
        if (!right) {
            //OddJob.getInstance().log("is left");
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
            if (test != null && OddJob.getInstance().getLockManager().getDoors().contains(test.getType()) && !((Door) test.getBlockData()).getHinge().equals(Door.Hinge.LEFT)) {
                //OddJob.getInstance().log("has right");
                block = test;
            }
        } else {
            OddJob.getInstance().log("is right");
        }
        //OddJob.getInstance().log("Lower right : " + block.getLocation().serialize().toString());
        return block.getLocation();
    }

    public static Location getLowerLeftDoor(Block block) {
        Door door = (Door) block.getBlockData();
        boolean open = door.isOpen();
        BlockFace doorFace = door.getFacing();
        Bisected.Half doorHalf = door.getHalf();
        Door.Hinge doorHinge = door.getHinge();

        //OddJob.getInstance().log("Open:" + (open ? "true" : "false") + "; Face:" + doorFace + "; Hinge:" + doorHinge + "; Half:" + doorHalf);

        boolean left = (doorHinge == Door.Hinge.LEFT);
        boolean lower = (doorHalf == Bisected.Half.BOTTOM);

        if (!lower) {
            block = block.getRelative(BlockFace.DOWN);
            //OddJob.getInstance().log("is upper, moving down");
        }
        if (!left) {
            //OddJob.getInstance().log("is right");
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
            if (test != null && OddJob.getInstance().getLockManager().getDoors().contains(test.getType()) && !((Door) test.getBlockData()).getHinge().equals(Door.Hinge.RIGHT)) {
                //OddJob.getInstance().log("has left");
                block = test;
            }
        } else {
            //OddJob.getInstance().log("is left");
        }

        //OddJob.getInstance().log("Lower left : " + block.getLocation().serialize().toString());
        return block.getLocation();
    }

    public static Location getChestPosition(Block block) {
        Chest chest = (Chest) block.getState();

        if (chest.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest left = (DoubleChest) chest.getInventory().getHolder();
            InventoryHolder inv = left.getLeftSide();
            if (inv != null) {
                return inv.getInventory().getLocation();
            }
        }
        return block.getLocation();
    }

    public static String serializeLoc(Location l) {
        String ret = "";
        if (l.getWorld() != null)
            ret = l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getYaw() + "," + l.getPitch();
        return ret;
    }

    public static Location deserializeLoc(String string) {
        if (string.equals("")) return null;
        String[] st = string.split(",");
        return new Location(Bukkit.getWorld(st[0]), Integer.parseInt(st[1]), Integer.parseInt(st[2]), Integer.parseInt(st[3]), Float.parseFloat(st[4]), Float.parseFloat(st[5]));
    }
}
