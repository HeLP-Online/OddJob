package no.helponline.Managers;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class DeathManager {
    public void add(Block leftSide, Block rightSide) {
        BlockData leftData = leftSide.getBlockData();
        BlockData rightData = rightSide.getBlockData();

        Location left = leftSide.getLocation();
        Location right = rightSide.getLocation();

        //OddJob.getInstance().getMySQLManager().addDeathChest(left,leftSide.getType(),leftData);
    }
}
