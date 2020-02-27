package no.helponline.Managers;

import com.sk89q.worldedit.bukkit.fastutil.Hash;
import no.helponline.OddJob;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class JailManager {

    // Player , World
    private final HashMap<UUID, UUID> inJail = new HashMap<>();
    private final HashMap<UUID, List<ItemStack>> inventory = new HashMap<>();
    private final HashMap<UUID, List<ItemStack>> armor = new HashMap<>();

    public boolean in(UUID uniqueId) {
        return inJail.get(uniqueId) != null;
    }

    public void addInJail(UUID uuidPlayer, UUID world) {
        inJail.put(uuidPlayer,world);
        Player player = OddJob.getInstance().getPlayerManager().getPlayer(uuidPlayer);
        List<ItemStack> inv = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
        inventory.put(uuidPlayer,inv);
        List<ItemStack> arm = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
        armor.put(uuidPlayer,arm);
        player.getInventory().clear();
        OddJob.getInstance().getPlayerManager().setGameMode(player, GameMode.ADVENTURE);
    }

    /* One world can only contain one jail */
}
