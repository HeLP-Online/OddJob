package no.helponline;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import no.helponline.Commands.EconCommand;
import no.helponline.Commands.GuildCommand;
import no.helponline.Commands.HomesCommand;
import no.helponline.Commands.LockCommand;
import no.helponline.Events.*;
import no.helponline.Managers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class OddJob extends JavaPlugin {
    public static HashMap<UUID, Location> deathChest = new HashMap<>();
    public static List<Location> deathTrappedChest = new ArrayList<>();
    private static OddJob instance;
    private EconManager econManager;
    private GuildManager guildManager;
    private PlayerManager playerManager;
    private LockManager lockManager;
    private MessageManager messageManager;
    private HomesManager homesManager;
    private ConfigManager configManager;
    private BanManager banManager;
    private TeleportManager teleportManager;

    public static OddJob getInstance() {
        return instance;
    }

    private static Economy econ = null;
    private static Permission perms = null;

    public static HashMap<UUID, UUID> inChunk = new HashMap<>(); //player - guild
    public static boolean vault;


    public void onEnable() {
        long startingTime = System.currentTimeMillis();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            log("Needing vault");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        instance = this;
        banManager = new BanManager();
        configManager = new ConfigManager();
        econManager = new EconManager();
        guildManager = new GuildManager();
        homesManager = new HomesManager();
        lockManager = new LockManager();
        messageManager = new MessageManager();
        playerManager = new PlayerManager();
        teleportManager = new TeleportManager();

        getCommand("econ").setExecutor(new EconCommand());
        getCommand("homes").setExecutor(new HomesCommand());
        getCommand("locks").setExecutor(new LockCommand());
        getCommand("guild").setExecutor(new GuildCommand());

        configManager.load();

        setupEconomy();
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new LocksEvents(), this);
        //Bukkit.getPluginManager().registerEvents(new SofaEvent(), this);  //TODO Sleep?
        Bukkit.getPluginManager().registerEvents(new MoveEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ArmorstandEvent(), this);
        Bukkit.getPluginManager().registerEvents(new onDeath(), this);

        BukkitScheduler saver = getServer().getScheduler();
        saver.scheduleSyncRepeatingTask(this, configManager::save, 0L, 6000L);
    }


    public void onDisable() {
        configManager.save();
    }


    public void log(String message) {
        getLogger().log(Level.INFO, ChatColor.YELLOW + message);
    }


    public static Economy getEconomy() {
        return econ;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            return false;
        }
        econ = (Economy) rsp.getProvider();
        return (econ != null);
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = (Permission) rsp.getProvider();
        return (perms != null);
    }

    public GuildManager getGuildManager() {
        return guildManager;
    }

    public HomesManager getHomesManager() {
        return homesManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public EconManager getEconManager() {
        return econManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }
}