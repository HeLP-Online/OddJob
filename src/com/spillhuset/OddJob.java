package com.spillhuset;


import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.spillhuset.Commands.Arena.ArenaCommand;
import com.spillhuset.Commands.Auction.AuctionCommand;
import com.spillhuset.Commands.Ban.BanCommand;
import com.spillhuset.Commands.*;
import com.spillhuset.Commands.Guild.GuildCommand;
import com.spillhuset.Commands.Homes.HomesCommand;
import com.spillhuset.Commands.Lock.LockCommand;
import com.spillhuset.Commands.Money.MoneyCommand;
import com.spillhuset.Commands.Player.PlayerCommand;
import com.spillhuset.Commands.Teleport.*;
import com.spillhuset.Commands.Trade.TradeCommand;
import com.spillhuset.Commands.Warp.WarpCommand;
import com.spillhuset.Events.*;
import com.spillhuset.Managers.*;
import com.spillhuset.Utils.SignManager;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.ezapi.inventory.EzInventory;

import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class OddJob extends JavaPlugin {
    private static OddJob instance;

    private ArenaManager arenaManager;
    private BanManager banManager;
    //private ChestManager chestManager;
    private DeathManager deathManager;
    private MoneyManager moneyManager;
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
    private int saver;
    private AuctionManager auctionManager;
    // API's
    private LuckPerms luckPermsApi;

    public static OddJob getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPermsApi = provider.getProvider();
        }

        messageManager = new MessageManager();
        arenaManager = new ArenaManager();
        auctionManager = new AuctionManager();
        banManager = new BanManager();
        //chestManager = new ChestManager();
        deathManager = new DeathManager();
        moneyManager = new MoneyManager();
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

        try {
            mySQLManager.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getCommand("teleport").setExecutor(new TeleportCommand());
        getCommand("auction").setExecutor(new AuctionCommand());
        getCommand("currency").setExecutor(new MoneyCommand()); // SubCommand
        getCommand("guild").setExecutor(new GuildCommand()); // SubCommand
        getCommand("homes").setExecutor(new HomesCommand()); // SubCommand
        getCommand("invsee").setExecutor(new InvseeCommand());
        getCommand("locks").setExecutor(new LockCommand());
        getCommand("suicide").setExecutor(new SuicideCommand()); // Cleaned
        getCommand("kick").setExecutor(new KickCommand());
        getCommand("ban").setExecutor(new BanCommand()); // SubCommand
        getCommand("feed").setExecutor(new FeedCommand());
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("give").setExecutor(new GiveCommand());
        getCommand("gamemode").setExecutor(new GameModeCommand());
        getCommand("player").setExecutor(new PlayerCommand());
        getCommand("warp").setExecutor(new WarpCommand()); // SubCommand
        getCommand("arena").setExecutor(new ArenaCommand());
        getCommand("freeze").setExecutor(new FreezeCommand()); // Cleaned
        getCommand("backup").setExecutor(new RollbackCommand());
        getCommand("rollback").setExecutor(new RollbackCommand());
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("trade").setExecutor(new TradeCommand()); // SubCommand
        getCommand("spawnmob").setExecutor(new SpawnMobCommand());
        getCommand("map").setExecutor(new MapCommand());
        getCommand("sudo").setExecutor(new SudoCommand());
        getCommand("op").setExecutor(new OpCommand());
        getCommand("deop").setExecutor(new DeopCommand());
        getCommand("tpa").setExecutor(new TpaCommand());
        getCommand("tpaccept").setExecutor(new TpAcceptCommand());
        getCommand("tpdeny").setExecutor(new TpDenyCommand());
        getCommand("tpall").setExecutor(new TpAllCommand());

        ConfigManager.load(); // Checked
        playerManager.load();
        moneyManager.load();
        guildManager.load();
        lockManager.load();
        worldManager.load();
        //arenaManager.load();


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
        Bukkit.getPluginManager().registerEvents(new PlayerBucketEmpty(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChangesWorld(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItem(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteract(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMove(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPortal(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerSpawnLocation(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkLoad(), this);


        if (Bukkit.getPluginManager().isPluginEnabled("worldedit")) {
            Bukkit.getConsoleSender().sendMessage("WorldEdit found");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            Bukkit.getConsoleSender().sendMessage("WorldGuard found");
        }
        if (Bukkit.getVersion().toLowerCase().contains("spigot")) {
            Bukkit.getConsoleSender().sendMessage("Spigot found");
        }
        if (Bukkit.getVersion().toLowerCase().contains("paper")) {
            Bukkit.getConsoleSender().sendMessage("Paper found");
        }
        if (Bukkit.getVersion().toLowerCase().contains("craftbukkit")) {
            Bukkit.getConsoleSender().sendMessage("Craftbukkit found");
        }


        saver = Bukkit.getScheduler().scheduleSyncRepeatingTask(OddJob.getInstance(), () -> {
            OddJob.getInstance().save();
            OddJob.getInstance().getAuctionManager().checkExpiredBids();
        }, 7200, 7200);

        signManager.updateSigns();
    }

    private void save() {
        playerManager.save();
        moneyManager.save();
        warpManager.save();
        guildManager.save();
    }


    public void onDisable() {
        getGuildManager().saveGuilds();
        getPlayerManager().save();
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

    public DeathManager getDeathManager() {
        return deathManager;
    }

    public MoneyManager getCurrencyManager() {
        return moneyManager;
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

    public LockManager getLocksManager() {
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

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public WorldManger getWorldManager() {
        return worldManager;
    }

    public UUID getServerId() {
        UUID id = null;
        if (getConfig().get("server_unique_id") != null) {
            id = UUID.fromString(getConfig().getString("server_unique_id", null));
        }
        if (id == null) {
            id = UUID.randomUUID();
            getConfig().set("server_unique_id", id.toString());
            saveConfig();
        }
        return id;
    }

    public WorldEditPlugin getWorldEdit() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        return (p instanceof WorldEditPlugin) ? (WorldEditPlugin) p : null;
    }

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }

    public LuckPerms getLuckPermsApi() {
        return luckPermsApi;
    }
}