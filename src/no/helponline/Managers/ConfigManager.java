package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class ConfigManager {
    private YamlConfiguration guildConfig;
    private File guildFile;
    private YamlConfiguration balanceConfig;
    private File balanceFile;
    private YamlConfiguration playerConfig;
    private File playerFile;
    private YamlConfiguration homesConfig;
    private File homesFile;
    private YamlConfiguration locksConfig;
    private File locksFile;

    public void load() {
        if (!OddJob.getInstance().getDataFolder().exists()) {
            OddJob.getInstance().getDataFolder().mkdirs();
        }

        // Creating file variable
        guildFile = new File(OddJob.getInstance().getDataFolder(), "guilds.yml");
        balanceFile = new File(OddJob.getInstance().getDataFolder(), "balances.yml");
        playerFile = new File(OddJob.getInstance().getDataFolder(), "players.yml");
        homesFile = new File(OddJob.getInstance().getDataFolder(), "homes.yml");
        locksFile = new File(OddJob.getInstance().getDataFolder(), "locks.yml");

        // Creating non-existing files
        if (!guildFile.exists()) {
            try {
                guildFile.createNewFile();
                Bukkit.getConsoleSender().sendMessage("Guilds file created");
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not create the guilds.yml");
            }
        }
        if (!balanceFile.exists()) {
            try {
                balanceFile.createNewFile();
                Bukkit.getConsoleSender().sendMessage("Balance file created!");
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not create the balances.yml");
            }
        }
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
                Bukkit.getConsoleSender().sendMessage("Players list created!");
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not create the players.yml file");
            }
        }
        if (!homesFile.exists()) {
            try {
                homesFile.createNewFile();
                Bukkit.getConsoleSender().sendMessage("List of homes created!");
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not create the homes.yml file");
            }
        }
        if (!locksFile.exists()) {
            try {
                locksFile.createNewFile();
                Bukkit.getConsoleSender().sendMessage("List of secured locks created!");
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not create the locks.yml file");
            }
        }

        // Instansiate the config files
        guildConfig = YamlConfiguration.loadConfiguration(guildFile);
        balanceConfig = YamlConfiguration.loadConfiguration(balanceFile);
        playerConfig = YamlConfiguration.loadConfiguration(playerFile);
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
        locksConfig = YamlConfiguration.loadConfiguration(locksFile);

        // Loading all
        //loadPlayers();
        //loadGuilds();
        //loadBalances();
        //loadHomes();
        //loadLocks();
        //OddJob.getInstance().log("All loaded");
    }


    public int getInt(String string) {
        return OddJob.getInstance().getConfig().getInt(string);
    }

    public String getString(String string) {
        return OddJob.getInstance().getConfig().getString(string);
    }

    public ConfigurationSection getConfigurationSection(String name) {
        return OddJob.getInstance().getConfig().getConfigurationSection(name);
    }

    public List<String> getStringList(String name) {
        return OddJob.getInstance().getConfig().getStringList(name);
    }

    public double getDouble(String name) {
        return OddJob.getInstance().getConfig().getDouble(name);
    }

    public boolean getBoolean(String plugin, UUID uuid, String string, boolean def) {
        return guildConfig.getBoolean(plugin + "." + uuid + ".set." + string, def);
    }

    public boolean getBoolean(String name) {
        return OddJob.getInstance().getConfig().getBoolean(name);
    }
}
