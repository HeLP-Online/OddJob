package no.helponline;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import no.helponline.Commands.*;
import no.helponline.Events.*;
import no.helponline.Managers.*;
import no.helponline.Utils.ArenaPlayer;
import no.helponline.Utils.Broadcaster;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class OddJob extends JavaPlugin {
    //public static HashMap<UUID, Location> deathChest = new HashMap<>();
    public HashMap<UUID, ArenaPlayer> arenaPlayer = new HashMap<>();
    private static OddJob instance;
    private ArenaManager arenaManager;
    private BanManager banManager;
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
    private TeleportManager teleportManager;
    private WorldManger worldManager;
    private WarpManager warpManager;

    public static OddJob getInstance() {
        return instance;
    }

    private static final Economy econ = null;
    private static final Permission perms = null;

    //public static HashMap<UUID, UUID> inChunk = new HashMap<>(); //player - guild
    public static boolean vault;

    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            log("Needing vault");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        instance = this;

        arenaManager = new ArenaManager();
        banManager = new BanManager();
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
        teleportManager = new TeleportManager();
        warpManager = new WarpManager();
        worldManager = new WorldManger();

        getCommand("econ").setExecutor(new EconCommand());
        getCommand("guild").setExecutor(new GuildCommand());
        getCommand("homes").setExecutor(new HomesCommand());
        getCommand("invsee").setExecutor(new InvseeCommand());
        getCommand("locks").setExecutor(new LockCommand());
        getCommand("suicide").setExecutor(new SuicideCommand());
        getCommand("kill").setExecutor(new KillCommand());
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

        configManager.load();

        Bukkit.getPluginManager().registerEvents(new BlockBreak(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new LocksEvents(), this);
        //Bukkit.getPluginManager().registerEvents(new SofaEvent(), this);  //TODO Sleep?
        Bukkit.getPluginManager().registerEvents(new MoveEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ArmorstandEvent(), this);
        Bukkit.getPluginManager().registerEvents(new onDeath(), this);
        Bukkit.getPluginManager().registerEvents(new ArenaMechanics(), this);
        Bukkit.getPluginManager().registerEvents(new PortalEvents(), this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            OddJob.getInstance().log(playerManager.getGamemode(player, player.getWorld()).name());
            if (!player.getGameMode().equals(playerManager.getGamemode(player, player.getWorld()))) {
                playerManager.setGameMode(player, playerManager.getGamemode(player, player.getWorld()));
            }
        }

        OddJob.getInstance().getArenaManager().loadArenas();

        Server server = getServer();
        Broadcaster broadcaster = new Broadcaster(Broadcaster.createSocket(), server.getPort(), server.getMotd(), server.getIp());
        getServer().getScheduler().runTaskAsynchronously(this, broadcaster);
    }

    public void onDisable() {

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
    public ScoreManager getScoreManager() { return scoreManager; }
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