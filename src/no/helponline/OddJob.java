package no.helponline;

import net.milkbowl.vault.economy.Economy;
import no.helponline.Commands.*;
import no.helponline.Events.*;
import no.helponline.Managers.*;
import no.helponline.Utils.Arena.*;
import no.helponline.Utils.Broadcaster;
import no.helponline.Utils.SignManager;
import org.bukkit.*;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class OddJob extends JavaPlugin {
    private static OddJob instance;

    private ArenaManager arenaManager;
    private BanManager banManager;
    private ChestManager chestManager;
    private ConfigManager configManager;
    private DeathManager deathManager;
    private EconManager econManager;
    private FreezeManager freezeManager;
    private GuildManager guildManager;
    private HomesManager homesManager;
    private JailManager jailManager;
    private LockManager lockManager;
    private MessageManager messageManager;
    private MySQLManager mySQLManager;
    private PlayerManager playerManager;
    private ScoreManager scoreManager;
    private ShopManager shopManager;
    private SignManager signManager;
    private TeleportManager teleportManager;
    private WorldManger worldManager;
    private WarpManager warpManager;
    private Economy econ;

    public static OddJob getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        arenaManager = new ArenaManager();
        banManager = new BanManager();
        chestManager = new ChestManager();
        configManager = new ConfigManager();
        deathManager = new DeathManager();
        econManager = new EconManager();
        freezeManager = new FreezeManager();
        guildManager = new GuildManager();
        homesManager = new HomesManager();
        jailManager = new JailManager();
        lockManager = new LockManager();
        messageManager = new MessageManager();
        mySQLManager = new MySQLManager();
        playerManager = new PlayerManager();
        scoreManager = new ScoreManager();
        shopManager = new ShopManager();
        signManager = new SignManager();
        teleportManager = new TeleportManager();
        warpManager = new WarpManager();
        worldManager = new WorldManger();

        getCommand("econ").setExecutor(new EconCommand());
        getCommand("guild").setExecutor(new GuildCommand());
        getCommand("homes").setExecutor(new HomesCommand());
        getCommand("invsee").setExecutor(new InvseeCommand());
        getCommand("locks").setExecutor(new LockCommand());
        getCommand("suicide").setExecutor(new SuicideCommand());
        //getCommand("kill").setExecutor(new KillCommand());
        getCommand("tp").setExecutor(new TpCommand());
        getCommand("tpall").setExecutor(new TpAllCommand());
        getCommand("kick").setExecutor(new KickCommand());
        getCommand("tpa").setExecutor(new TpACommand());
        getCommand("tpaccept").setExecutor(new TpAcceptCommand());
        getCommand("tpdeny").setExecutor(new TpDenyCommand());
        getCommand("ban").setExecutor(new BanCommand());
        getCommand("unban").setExecutor(new UnbanCommand());
        getCommand("feed").setExecutor(new FeedCommand());
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("give").setExecutor(new GiveCommand());
        getCommand("gamemode").setExecutor(new GameModeCommand());
        getCommand("tppos").setExecutor(new TpPosCommand());
        getCommand("player").setExecutor(new PlayerCommand());
        getCommand("addwarp").setExecutor(new WarpCommand());
        getCommand("delwarp").setExecutor(new WarpCommand());
        getCommand("warp").setExecutor(new WarpCommand());
        getCommand("arena").setExecutor(new ArenaCommand());
        getCommand("back").setExecutor(new BackCommand());
        getCommand("freeze").setExecutor(new FreezeCommand());
        getCommand("death").setExecutor(new DeathCommand());
        getCommand("backup").setExecutor(new RollbackCommand());
        getCommand("rollback").setExecutor(new RollbackCommand());
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("jail").setExecutor(new JailCommand());
        getCommand("trade").setExecutor(new TradeCommand());
        getCommand("shop").setExecutor(new ShopCommand());

        configManager.load();
        econManager.load();
        lockManager.load();
        homesManager.load();
        playerManager.load();
        guildManager.loadGuilds();
        guildManager.loadChunks();
        warpManager.load();
        worldManager.load();
        deathManager.load();
        arenaManager.load();

        Bukkit.getPluginManager().registerEvents(new BlockBreak(), this);
        Bukkit.getPluginManager().registerEvents(new BlockExplode(), this);
        Bukkit.getPluginManager().registerEvents(new BlockIgnite(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlace(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntity(), this);
        Bukkit.getPluginManager().registerEvents(new EntityExplode(), this);
        Bukkit.getPluginManager().registerEvents(new EntityInteract(), this);
        Bukkit.getPluginManager().registerEvents(new EntityPickupItem(), this);
        Bukkit.getPluginManager().registerEvents(new EntitySpawn(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClose(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerBedEnter(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerBucketEmpty(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChangesWorld(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItem(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteract(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMove(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPortal(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);

        Server server = getServer();
        Broadcaster broadcaster = new Broadcaster(Broadcaster.createSocket(), server.getPort(), server.getMotd(), server.getIp());
        getServer().getScheduler().runTaskAsynchronously(this, broadcaster);

        if (setupEconomy()) Bukkit.getConsoleSender().sendMessage("Vault found");
        if (!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            Bukkit.getConsoleSender().sendMessage("WorldEdit found");
        }
        if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            Bukkit.getConsoleSender().sendMessage("WorldGuard found");
        }
        if (!Bukkit.getVersion().toLowerCase().contains("spigot")) {
            Bukkit.getConsoleSender().sendMessage("Spigot found");
        }
        if (!Bukkit.getVersion().toLowerCase().contains("paper")) {
            Bukkit.getConsoleSender().sendMessage("Paper found");
        }
        if (!Bukkit.getVersion().toLowerCase().contains("craftbukkit")) {
            Bukkit.getConsoleSender().sendMessage("Craftbukkit found");
        }
        signManager.updateSigns();
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                econ = economyProvider.getProvider();
            }
        }

        return (econ != null);
    }

    public void onDisable() {
        getGuildManager().saveChunks();
        getGuildManager().saveGuilds();
        for (World world : Bukkit.getWorlds()) {
            getDeathManager().cleanUp(world);
        }
    }

    public Location getSpawn() {
        return getServer().getWorld("world").getSpawnLocation();
    }

    public void log(String message) {
        getLogger().log(Level.INFO, ChatColor.YELLOW + message);
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public BanManager getBanManager() {
        return banManager;
    }

    public ChestManager getChestManager() {
        return chestManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DeathManager getDeathManager() {
        return deathManager;
    }

    public EconManager getEconManager() {
        return econManager;
    }

    public FreezeManager getFreezeManager() {
        return freezeManager;
    }

    public GuildManager getGuildManager() {
        return guildManager;
    }

    public HomesManager getHomesManager() {
        return homesManager;
    }

    public JailManager getJailManager() {
        return jailManager;
    }

    public LockManager getLockManager() {
        return lockManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public MySQLManager getMySQLManager() {
        return mySQLManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public WorldManger getWorldManager() {
        return worldManager;
    }

}