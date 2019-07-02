package no.helponline.Managers;

import no.helponline.Guilds.Role;
import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ConfigManager {
    private static int generatedID = 100;
    private static YamlConfiguration guildConfig;
    private static File guildFile;
    private static YamlConfiguration balanceConfig;
    private static File balanceFile;
    private static YamlConfiguration playerConfig;
    private static File playerFile;
    private static YamlConfiguration homesConfig;
    private static File homesFile;
    private static YamlConfiguration locksConfig;
    private static File locksFile;

    public static void load() {
        if (!OddJob.getInstance().getDataFolder().exists()) {
            OddJob.getInstance().getDataFolder().mkdirs();
        }

        if (OddJob.getInstance().getConfig().contains("generatedID")) {
            generatedID = OddJob.getInstance().getConfig().getInt("generatedID");
        }

        guildFile = new File(OddJob.getInstance().getDataFolder(), "guilds.yml");
        balanceFile = new File(OddJob.getInstance().getDataFolder(), "balances.yml");
        playerFile = new File(OddJob.getInstance().getDataFolder(), "players.yml");
        homesFile = new File(OddJob.getInstance().getDataFolder(), "homes.yml");
        locksFile = new File(OddJob.getInstance().getDataFolder(), "locks.yml");

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

        guildConfig = YamlConfiguration.loadConfiguration(guildFile);
        balanceConfig = YamlConfiguration.loadConfiguration(balanceFile);
        playerConfig = YamlConfiguration.loadConfiguration(playerFile);
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
        locksConfig = YamlConfiguration.loadConfiguration(locksFile);

        loadGuilds();
        loadBalances();
        loadPlayers();
        loadHomes();
        loadLocks();
    }


    public static UUID generateUniqueId() {
        return UUID.randomUUID();
    }


    private static void loadPlayers() {
        if (playerConfig.contains("players")) {
            for (String s : playerConfig.getConfigurationSection("players").getKeys(false)) {
                PlayerManager.updatePlayer(UUID.fromString(s), playerConfig.getString("players." + s));
            }
            Bukkit.getConsoleSender().sendMessage("Players loaded!");
        }
    }

    private static void loadGuilds() {
        if (guildConfig.contains("guild")) {
            // TODO
            for (String s : guildConfig.getConfigurationSection("guild").getKeys(false)) {
                HashMap<UUID, Role> members = new HashMap<>();
                List<Chunk> chunks = new ArrayList<>();
                for (String c : guildConfig.getConfigurationSection("guild." + s + ".chunks").getKeys(false)) {
                    UUID world = UUID.fromString(guildConfig.getString("guild." + s + ".chunks." + c + ".world"));
                    Chunk chunk = Bukkit.getWorld(world).getChunkAt(guildConfig.getInt("guild." + s + ".chunks." + c + ".x"), guildConfig.getInt("guild." + s + ".chunks." + c + ".z"));
                    chunks.add(chunk);
                }
                for (String i : guildConfig.getConfigurationSection("guild." + s + ".members").getKeys(false)) {
                    members.put(UUID.fromString(i), Role.valueOf(guildConfig.getString("guild." + s + ".members." + i)));
                }
                OddJob.getInstance().getGuildManager().set(
                        UUID.fromString(s),//id
                        guildConfig.getString("guild." + s + ".name"),//name
                        members,//members
                        chunks);//claims
            }
        }
    }

    private static void loadBalances() {
        if (balanceConfig.contains("balance")) {
            for (String s : balanceConfig.getConfigurationSection("balance").getKeys(false)) {
                EconManager.setBalance(UUID.fromString(s), balanceConfig.getDouble("balance." + s));
            }
            Bukkit.getConsoleSender().sendMessage("Balances loaded!");
        }
    }

    public static void loadLocks() {
        if (locksConfig.contains("locks")) {
            int iLocks = 0;
            HashMap<Location, UUID> chest = new HashMap<>();

            for (String s : locksConfig.getConfigurationSection("locks").getKeys(false)) {
                UUID uuid = UUID.fromString(s);
                for (String c : locksConfig.getConfigurationSection("locks." + uuid).getKeys(false)) {
                    Location l = new Location(Bukkit.getWorld(UUID.fromString(locksConfig.getString("locks." + uuid + "." + c + ".world"))), locksConfig.getInt("locks." + uuid + "." + c + ".x"), locksConfig.getInt("locks." + uuid + "." + c + ".y"), locksConfig.getInt("locks." + uuid + "." + c + ".z"));
                    chest.put(l, uuid);
                    iLocks++;
                }
            }
            OddJob.getInstance().log("loaded " + iLocks + " locks.");
            LockManager.setLocks(chest);
        }
    }

    private static void loadHomes() {
        if (homesConfig.contains("homes")) {
            for (String s : homesConfig.getConfigurationSection("homes").getKeys(false)) {
                UUID uuid = UUID.fromString(s);
                Set<String> st = homesConfig.getConfigurationSection("homes." + uuid).getKeys(false);
                if (!st.isEmpty()) {
                    for (String str : st) {
                        String worldUUID = homesConfig.getString("homes." + uuid + "." + str + ".world");
                        int x = homesConfig.getInt("homes." + uuid + "." + str + ".x");
                        int y = homesConfig.getInt("homes." + uuid + "." + str + ".y");
                        int z = homesConfig.getInt("homes." + uuid + "." + str + ".z");
                        float yaw = homesConfig.getInt("homes." + uuid + "." + str + ".yaw");
                        float pitch = homesConfig.getInt("homes." + uuid + "." + str + ".pitch");
                        Location location = new Location(Bukkit.getWorld(UUID.fromString(worldUUID)), x, y, z, yaw, pitch);
                        HomesManager.add(uuid, str, location);
                    }
                }
            }
            Bukkit.getConsoleSender().sendMessage("Homes loaded!");
        }
    }

    public static void save() {
        OddJob.getInstance().getConfig().set("generatedID", generatedID);
        saveBalances();
        savePlayers();
        saveHomes();
        saveLocks();
        try {
            balanceConfig.save(balanceFile);
            playerConfig.save(playerFile);
            homesConfig.save(homesFile);
            locksConfig.save(locksFile);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while saving!");
        }
        OddJob.getInstance().saveConfig();
    }

    public static void saveLocks() {
        for (UUID uuid : PlayerManager.getUUIDs()) {
            locksConfig.set("locks." + uuid, null);
        }
        if (LockManager.getLocks() != null && LockManager.getLocks().size() > 0) {
            int i = 0;
            HashMap<Location, UUID> locks = LockManager.getLocks();
            for (Location location : locks.keySet()) {
                UUID uuid = locks.get(location);
                locksConfig.set("locks." + uuid + "." + i + ".world", location.getWorld().getUID().toString());
                locksConfig.set("locks." + uuid + "." + i + ".x", location.getBlockX());
                locksConfig.set("locks." + uuid + "." + i + ".y", location.getBlockY());
                locksConfig.set("locks." + uuid + "." + i + ".z", location.getBlockZ());
                i++;
            }
            Bukkit.getConsoleSender().sendMessage("Secured " + i + " locks saved!");
        }

    }

    private static void saveBalances() {
        for (UUID uuid : EconManager.getBalanceMap().keySet()) {
            balanceConfig.set("balance." + uuid.toString(), EconManager.getBalanceMap().get(uuid));
        }
        Bukkit.getConsoleSender().sendMessage("Balances saved!");
    }

    private static void savePlayers() {
        for (UUID uuid : PlayerManager.getPlayersMap().keySet()) {
            playerConfig.set("players." + uuid.toString(), PlayerManager.getPlayersMap().get(uuid));
        }
        Bukkit.getConsoleSender().sendMessage("Players saved!");
    }

    private static void saveHomes() {
        for (UUID uuid : PlayerManager.getPlayersMap().keySet()) {
            Set<String> list = HomesManager.list(uuid);
            if (list != null) {
                for (String st : list) {
                    Location location = HomesManager.get(uuid, st);
                    homesConfig.set("homes." + uuid + "." + st + ".world", location.getWorld().getUID().toString());
                    homesConfig.set("homes." + uuid + "." + st + ".x", location.getBlockX());
                    homesConfig.set("homes." + uuid + "." + st + ".y", location.getBlockY());
                    homesConfig.set("homes." + uuid + "." + st + ".z", location.getBlockZ());
                    homesConfig.set("homes." + uuid + "." + st + ".yaw", location.getYaw());
                    homesConfig.set("homes." + uuid + "." + st + ".pitch", location.getPitch());
                }
            }
        }
        Bukkit.getConsoleSender().sendMessage("Homes saved!");
    }

    public static int getInt(String string) {
        return OddJob.getInstance().getConfig().getInt(string);
    }

    public static String getString(String string) {
        return OddJob.getInstance().getConfig().getString(string);
    }

    public static ConfigurationSection getConfigurationSection(String name) {
        return OddJob.getInstance().getConfig().getConfigurationSection(name);
    }

    public static List<String> getStringList(String name) {
        return OddJob.getInstance().getConfig().getStringList(name);
    }

    public static double getDouble(String name) {
        return OddJob.getInstance().getConfig().getDouble(name);
    }

    public static boolean getBoolean(String name) {
        return OddJob.getInstance().getConfig().getBoolean(name);
    }
}
