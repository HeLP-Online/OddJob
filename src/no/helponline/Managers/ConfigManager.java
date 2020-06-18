package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    public FileConfiguration scoreboard,messages,signs;

    public void load() {
        reloadConfig();
    }

    private void reloadConfig() {
        OddJob.getInstance().reloadConfig();
        FileConfiguration config = OddJob.getInstance().getConfig();

        config.addDefault("use-permission", true);
        config.addDefault("broadcast-win", true);

        config.addDefault("SQL.Type", "MYSQL");
        config.addDefault("SQL.TablePrefix", "mine_");
        config.addDefault("SQL.Hostname", "localhost");
        config.addDefault("SQL.Port", 3306);
        config.addDefault("SQL.Database", "minecraft");
        config.addDefault("SQL.Username", "root");
        config.addDefault("SQL.Password", "");

        ArrayList<Material> allowedBlocks = new ArrayList<>();

        allowedBlocks.add(Material.OAK_LEAVES); // 18
        allowedBlocks.add(Material.SPRUCE_LEAVES); // 18
        allowedBlocks.add(Material.BIRCH_LEAVES);  // 18
        allowedBlocks.add(Material.JUNGLE_LEAVES); // 18
        allowedBlocks.add(Material.DEAD_BUSH);   // 32
        allowedBlocks.add(Material.GRASS);  // 31:1
        allowedBlocks.add(Material.FERN);  // 31:2
        allowedBlocks.add(Material.CAKE);  // 92
        allowedBlocks.add(Material.MELON); // 103
        allowedBlocks.add(Material.BROWN_MUSHROOM); // 39
        allowedBlocks.add(Material.RED_MUSHROOM); // 40
        allowedBlocks.add(Material.PUMPKIN); // 86
        allowedBlocks.add(Material.TNT);  // 46
        allowedBlocks.add(Material.FIRE); // 51
        allowedBlocks.add(Material.COBWEB); // 30

        config.addDefault("Survival.Arena.Allowed-Blocks", allowedBlocks);

        config.options().copyDefaults(true);
        OddJob.getInstance().saveConfig();

    }
}
