package no.helponline.Managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.UUID;

public class DeathManager {
    private HashMap<Location, HashMap<String, Object>> pos = new HashMap<>();

    public void add(Block leftSide, Block rightSide, UUID uuid) {
        HashMap<String, Object> object = new HashMap<>();
        object.put("owner", uuid);
        object.put("left", leftSide.getType());
        object.put("right", rightSide.getType());
        object.put("leftSide", leftSide.getBlockData());
        object.put("rightSide", rightSide.getBlockData());
        pos.put(leftSide.getLocation(), object);
    }

    public void replace(Location location) {
        HashMap<String, Object> object = pos.get(location);
        Block leftSide = location.getBlock();
        leftSide.setType((Material) object.get("left"));
        leftSide.setBlockData((BlockData) object.get("leftSide"));
        Block rightSide = leftSide.getRelative(0, 0, -1);
        rightSide.setType((Material) object.get("right"));
        rightSide.setBlockData((BlockData) object.get("rightSide"));
        pos.remove(location);
    }
}
