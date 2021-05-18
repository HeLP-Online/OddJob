package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Role;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConfigManager {
    public FileConfiguration scoreboard;
    private static final List<String> permissions = new ArrayList<>();
    public static FileConfiguration config;

    public static void load() {
        permissions.add("default");
        permissions.add("moderators");
        permissions.add("vip");
        permissions.add("operators");
        permissions.add("wood");
        permissions.add("stone");
        permissions.add("iron");
        permissions.add("gold");
        permissions.add("diamond");
        permissions.add("emerald");

        OddJob.getInstance().reloadConfig();
        config = OddJob.getInstance().getConfig();

        config.addDefault("map.address", "https://spillhuset.com:8123");

        config.addDefault("arena.use-permission", true);
        config.addDefault("arena.broadcast-win", true);

        config.addDefault("sql.Type", "mysql");
        config.addDefault("sql.Prefix", "mine_");
        config.addDefault("sql.Hostname", "localhost");
        config.addDefault("sql.Port", 3306);
        config.addDefault("sql.Database", "minecraft");
        config.addDefault("sql.Username", "root");
        config.addDefault("sql.Password", "");

        config.addDefault("guild.default.maxClaims", 10);
        config.addDefault("guild.default.open", false);
        config.addDefault("guild.default.friendlyFire", false);
        config.addDefault("guild.default.permissionKick", Role.Mods.name());
        config.addDefault("guild.default.permissionInvite", Role.Members.name());
        config.addDefault("guild.default.invitedOnly", true);

        config.addDefault("homes.operators.maxHomes", 50);
        config.addDefault("homes.moderators.maxHomes", 40);
        config.addDefault("homes.vip.maxHomes", 35);
        config.addDefault("homes.emerald.maxHomes", 30);
        config.addDefault("homes.diamond.maxHomes", 25);
        config.addDefault("homes.gold.maxHomes", 20);
        config.addDefault("homes.iron.maxHomes", 15);
        config.addDefault("homes.stone.maxHomes", 10);
        config.addDefault("homes.wood.maxHomes", 7);
        config.addDefault("homes.default.maxHomes", 5);


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

        config.addDefault("arena.survival.allowed-blocks", allowedBlocks);

        config.options().copyDefaults(true);
        OddJob.getInstance().saveConfig();

    }

    public static double getCurrencyInitialGuild() {
        return config.getDouble("currency.initial.guild", 200D);
    }

    public static int maxHomes(UUID uuid) {
        for (String permission : permissions) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return 0;
            if (OddJob.getInstance().getPlayerManager().getPlayer(uuid).hasPermission("homes." + permission)) {
                return config.getInt("homes." + permission + ".maxHomes");
            }
        }
        return 0;
    }
}
