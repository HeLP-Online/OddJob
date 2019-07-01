package no.helponline;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import no.helponline.Commands.EconCommand;
import no.helponline.Commands.GuildCommand;
import no.helponline.Commands.HomesCommand;
import no.helponline.Commands.LockCommand;
import no.helponline.Events.ArmorstandEvent;
import no.helponline.Events.LocksEvents;
import no.helponline.Events.MoveEvent;
import no.helponline.Events.PlayerJoin;
import no.helponline.Managers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class OddJob extends JavaPlugin {
    private static OddJob instance;

    public static OddJob getInstance() {
        return instance;
    }

    private static GuildManager guildManager;
    private static LockManager lockManager;
    private static Economy econ;
    private static Permission perms = null;

    public static boolean vault;


    public void onEnable() {
        long startingTime = System.currentTimeMillis();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            log("Needing vault");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        instance = this;

        new ConfigManager();
        lockManager = new LockManager();
        new PlayerManager();
        new HomesManager();
        new MessageManager(this);
        guildManager = new GuildManager();

        getCommand("econ").setExecutor(new EconCommand());
        getCommand("homes").setExecutor(new HomesCommand());
        getCommand("locks").setExecutor(new LockCommand());
        getCommand("guild").setExecutor(new GuildCommand());

        ConfigManager.load();

        setupEconomy();
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new LocksEvents(), this);
        //Bukkit.getPluginManager().registerEvents(new SofaEvent(), this);  //TODO Sleep?
        Bukkit.getPluginManager().registerEvents(new MoveEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ArmorstandEvent(), this);
    }


    public void onDisable() {
        ConfigManager.save();
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

    public static Permission getPermissions() {
        return perms;
    }


    public GuildManager getGuildManager() {
        return guildManager;
    }


    public LockManager getLockManager() {
        return lockManager;
    }
}