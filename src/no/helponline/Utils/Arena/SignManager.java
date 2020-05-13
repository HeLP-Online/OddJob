package no.helponline.Utils.Arena;

import no.helponline.OddJob;
import no.helponline.Utils.Utility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignManager {
    private String[] design = new String[4];
    private String[] leaveDesign = new String[4];
    private boolean arena, playersleft;
    private HashMap<GameState, String> translations = new HashMap<>();
    private HashMap<Location, String> signs = new HashMap<>();


    public SignManager() {
        reload();
    }

    public void reload() {
        FileConfiguration config = OddJob.getInstance().getConfigManager().signs;
        for (int i = 1; i <= 4; i++) {
            design[i - 1] = config.getString("Sign.Line." + i);
        }

        arena = config.getBoolean("Sign.LeftClick.Show current arena");
        playersleft = config.getBoolean("Sign.LeftClick.Show players remain");

        leaveDesign[0] = config.getString("Sign.LeavePrefix");
        for (int i = 2; i <= 4; i++) {
            leaveDesign[i - 1] = config.getString("Sign.Leave.Line." + i);
        }

        for (String key : config.getConfigurationSection("Translations.").getKeys(false)) {
            translations.put(GameState.valueOf(key), config.getString("Translations." + key));
        }

        List<String> string = config.getStringList("Sign.List");
        int a = 0;
        for (String key : string) {
            String[] split = key.split(":");
            Location location = Utility.deserializeLoc(split[0]);
            if (location != null) {
                signs.put(location, split[1]);
            }
            a++;
        }
    }

    public void updateSigns() {
        for (Map.Entry<Location, String> sign : signs.entrySet()) {
            Location location = sign.getKey();
            if (location != null && location.getWorld() != null) updateSigns(location, sign.getValue());
        }
    }

    private void updateSigns(Location location, String value) {
        Block block = location.getBlock();
        if (block.getType() == Material.BIRCH_SIGN ||
                block.getType() == Material.OAK_SIGN ||
                block.getType() == Material.ACACIA_SIGN ||
                block.getType() == Material.SPRUCE_SIGN ||
                block.getType() == Material.JUNGLE_SIGN ||
                block.getType() == Material.DARK_OAK_SIGN ||
                block.getType() == Material.BIRCH_WALL_SIGN ||
                block.getType() == Material.OAK_WALL_SIGN ||
                block.getType() == Material.ACACIA_WALL_SIGN ||
                block.getType() == Material.SPRUCE_WALL_SIGN ||
                block.getType() == Material.JUNGLE_WALL_SIGN ||
                block.getType() == Material.DARK_OAK_WALL_SIGN
        ) {
            Sign sign = (Sign) block.getState();
            Game game = OddJob.getInstance().getGameManager().getGame(value);
            if (game != null) {
                String state = translations.get(game.getGameState());
                for (int i = 0; i < 4; i++) {
                    sign.setLine(i, design[i].replace("%name%", game.getName()).replace("%state%", state).replace("%currentplayers%", Integer.valueOf(game.getPlayingUsers()).toString()).replace("%requiredplayers%", Integer.valueOf(game.getRequiredPlayers()).toString()).replace("%maxplayers%", Integer.valueOf(game.getMaximumPlayers()).toString()));
                }
                sign.update();
            }
        }
    }

}
