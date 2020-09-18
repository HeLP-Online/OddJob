package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

        try {
            config.addDefault("Map.Address","http://"+ InetAddress.getLocalHost().getHostAddress() +":8123");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        config.addDefault("Arena.Use-permission", true);
        config.addDefault("Arena.Broadcast-win", true);

        config.addDefault("SQL.Type", "MYSQL");
        config.addDefault("SQL.TablePrefix", "mine_");
        config.addDefault("SQL.Hostname", "localhost");
        config.addDefault("SQL.Port", 3306);
        config.addDefault("SQL.Database", "minecraft");
        config.addDefault("SQL.Username", "root");
        config.addDefault("SQL.Password", "");

        ArrayList<String> allowedBlocks = new ArrayList<>();

        allowedBlocks.add(Material.OAK_LEAVES.name()); // 18
        allowedBlocks.add(Material.SPRUCE_LEAVES.name()); // 18
        allowedBlocks.add(Material.BIRCH_LEAVES.name());  // 18
        allowedBlocks.add(Material.JUNGLE_LEAVES.name()); // 18
        allowedBlocks.add(Material.DEAD_BUSH.name());   // 32
        allowedBlocks.add(Material.GRASS.name());  // 31:1
        allowedBlocks.add(Material.FERN.name());  // 31:2
        allowedBlocks.add(Material.CAKE.name());  // 92
        allowedBlocks.add(Material.MELON.name()); // 103
        allowedBlocks.add(Material.BROWN_MUSHROOM.name()); // 39
        allowedBlocks.add(Material.RED_MUSHROOM.name()); // 40
        allowedBlocks.add(Material.PUMPKIN.name()); // 86
        allowedBlocks.add(Material.TNT.name());  // 46
        allowedBlocks.add(Material.FIRE.name()); // 51
        allowedBlocks.add(Material.COBWEB.name()); // 30

        config.addDefault("Arena.Survival.Allowed-Blocks", allowedBlocks);

        config.options().copyDefaults(true);
        OddJob.getInstance().saveConfig();

    }
}
