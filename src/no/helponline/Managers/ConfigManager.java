package no.helponline.Managers;

import no.helponline.Guilds.Guild;
import no.helponline.Guilds.Role;
import no.helponline.Guilds.Zone;
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
    private int generatedID = 100;
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
        OddJob.getInstance().log("All loaded");
    }


    public UUID generateUniqueId() {
        return UUID.randomUUID();
    }


    private void loadPlayers() {
        if (playerConfig.contains("players")) {
            for (String s : playerConfig.getConfigurationSection("players").getKeys(false)) {
                OddJob.getInstance().getPlayerManager().create(
                        UUID.fromString(s),
                        playerConfig.getString("players." + s + ".uuid"),
                        playerConfig.getBoolean("players." + s + ".denyTPA", false),
                        playerConfig.getStringList("players." + s + ".blackList"),
                        playerConfig.getStringList("players." + s + ".whiteLits"));

            }
            Bukkit.getConsoleSender().sendMessage("Players loaded!");
        }
    }

    private void loadGuilds() {
        if (guildConfig.contains("guild")) {
            // TODO
            for (String s : guildConfig.getConfigurationSection("guild").getKeys(false)) {
                HashMap<UUID, Role> members = new HashMap<>();
                List<Chunk> chunks = new ArrayList<>();
                if (guildConfig.getConfigurationSection("guild." + s + ".chunks") != null) {
                    for (String c : guildConfig.getConfigurationSection("guild." + s + ".chunks").getKeys(false)) {
                        UUID world = UUID.fromString(guildConfig.getString("guild." + s + ".chunks." + c + ".world"));
                        Chunk chunk = Bukkit.getWorld(world).getChunkAt(guildConfig.getInt("guild." + s + ".chunks." + c + ".x"), guildConfig.getInt("guild." + s + ".chunks." + c + ".z"));
                        chunks.add(chunk);
                    }
                }
                for (String i : guildConfig.getConfigurationSection("guild." + s + ".members").getKeys(false)) {
                    members.put(UUID.fromString(i), Role.valueOf(guildConfig.getString("guild." + s + ".members." + i)));
                }
                HashMap<String, Object> settings = new HashMap<>();
                if (guildConfig.get("guild." + s + ".settings") != null) {
                    for (String i : guildConfig.getConfigurationSection("guild." + s + ".settings").getKeys(false)) {
                        settings.put(i, guildConfig.get("guild." + s + ".settings." + i));
                    }
                }
                OddJob.getInstance().getGuildManager().set(
                        UUID.fromString(s),//id
                        guildConfig.getString("guild." + s + ".name"),//name
                        members,//members
                        chunks,//claims
                        Zone.valueOf(guildConfig.getString("guild." + s + ".zone", "GUILD")),
                        settings); //Zone
            }
        }
    }

    private void loadBalances() {
        if (balanceConfig.contains("balance")) {
            for (String s : balanceConfig.getConfigurationSection("balance").getKeys(false)) {
                OddJob.getInstance().getEconManager().setBalance(UUID.fromString(s), balanceConfig.getDouble("balance." + s));
            }
            Bukkit.getConsoleSender().sendMessage("Balances loaded!");
        }
    }

    public void loadLocks() {
        int i = 0;
        if (locksConfig.contains("armor")) {
            HashMap<UUID, UUID> a = new HashMap<>();
            for (String s : locksConfig.getConfigurationSection("armor").getKeys(false)) {
                i++;
                a.put(UUID.fromString(s), UUID.fromString(locksConfig.getString("armor." + s)));
            }
            OddJob.getInstance().getLockManager().setArmorstand(a);
        }
        if (locksConfig.contains("locks")) {
            HashMap<Location, UUID> chest = new HashMap<>();

            for (String s : locksConfig.getConfigurationSection("locks").getKeys(false)) {
                for (String c : locksConfig.getConfigurationSection("locks." + s).getKeys(false)) {
                    Location l = new Location(Bukkit.getWorld(UUID.fromString(locksConfig.getString("locks." + s + "." + c + ".world"))), locksConfig.getInt("locks." + s + "." + c + ".x"), locksConfig.getInt("locks." + s + "." + c + ".y"), locksConfig.getInt("locks." + s + "." + c + ".z"));
                    chest.put(l, UUID.fromString(s));
                    i++;
                }
            }
            OddJob.getInstance().getLockManager().setLocks(chest);
        }
        OddJob.getInstance().log("Loaded " + i + " locks.");
    }

    private void loadHomes() {
        if (homesConfig.contains("homes")) {
            for (String s : homesConfig.getConfigurationSection("homes").getKeys(false)) {
                UUID uuid = UUID.fromString(s);
                Set<String> st = homesConfig.getConfigurationSection("homes." + uuid.toString()).getKeys(false);
                if (!st.isEmpty()) {
                    for (String str : st) {
                        String worldUUID = homesConfig.getString("homes." + uuid.toString() + "." + str + ".world");
                        int x = homesConfig.getInt("homes." + uuid.toString() + "." + str + ".x");
                        int y = homesConfig.getInt("homes." + uuid.toString() + "." + str + ".y");
                        int z = homesConfig.getInt("homes." + uuid.toString() + "." + str + ".z");
                        float yaw = homesConfig.getInt("homes." + uuid.toString() + "." + str + ".yaw");
                        float pitch = homesConfig.getInt("homes." + uuid.toString() + "." + str + ".pitch");
                        Location location = new Location(Bukkit.getWorld(UUID.fromString(worldUUID)), x, y, z, yaw, pitch);
                        OddJob.getInstance().getHomesManager().add(uuid, str, location);
                    }
                }
            }
            Bukkit.getConsoleSender().sendMessage("Homes loaded!");
        }
    }

    public void save() {
        OddJob.getInstance().getConfig().set("generatedID", generatedID);
        saveBalances();
        savePlayers();
        saveHomes();
        saveLocks();
        saveGuilds();
        try {
            guildConfig.save(guildFile);
            balanceConfig.save(balanceFile);
            playerConfig.save(playerFile);
            homesConfig.save(homesFile);
            locksConfig.save(locksFile);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while saving!");
        }
        OddJob.getInstance().saveConfig();
        OddJob.getInstance().log("All saved!");
    }

    public void saveGuilds() {
        int i = 0;
        for (UUID uuid : OddJob.getInstance().getGuildManager().getGuilds().keySet()) {

            i++;
            Guild guild = OddJob.getInstance().getGuildManager().getGuild(uuid);
            guildConfig.set("guild." + uuid.toString() + ".name", guild.getName()); //name
            guildConfig.set("guild." + uuid.toString() + ".zone", guild.getZone().toString());
            HashMap<UUID, Role> members = guild.getMembers();
            for (UUID player : members.keySet()) {
                guildConfig.set("guild." + uuid.toString() + ".members." + player.toString(), members.get(player).toString()); //members
            }
            HashMap<String, Object> settings = guild.getSettings();
            for (String s : settings.keySet()) {
                guildConfig.set("guild." + uuid.toString() + ".settings." + s, settings.get(s));
            }
            List<Chunk> chunks = OddJob.getInstance().getGuildManager().getChunks(uuid);
            int c = 0;
            for (Chunk chunk : chunks) {
                c++;
                guildConfig.set("guild." + uuid.toString() + ".chunks." + c + ".world", chunk.getWorld().getUID().toString());
                guildConfig.set("guild." + uuid.toString() + ".chunks." + c + ".x", chunk.getX());
                guildConfig.set("guild." + uuid.toString() + ".chunks." + c + ".z", chunk.getZ());
            }
        }
    }

    public void saveLocks() {
        int i = 0;
        for (UUID uuid : OddJob.getInstance().getPlayerManager().getUUIDs()) {
            locksConfig.set("locks." + uuid.toString(), null);
            locksConfig.set("armor." + uuid.toString(), null);
        }
        if (OddJob.getInstance().getLockManager().getArmorstands() != null && OddJob.getInstance().getLockManager().getArmorstands().size() > 0) {

            HashMap<UUID, UUID> a = OddJob.getInstance().getLockManager().getArmorstands();
            for (UUID entity : a.keySet()) {
                i++;
                locksConfig.set("armor." + entity.toString(), a.get(entity).toString());
            }
        }
        if (OddJob.getInstance().getLockManager().getLocks() != null && OddJob.getInstance().getLockManager().getLocks().size() > 0) {
            HashMap<Location, UUID> locks = OddJob.getInstance().getLockManager().getLocks();
            for (Location location : locks.keySet()) {
                i++;
                UUID uuid = locks.get(location);
                locksConfig.set("locks." + uuid.toString() + "." + i + ".world", location.getWorld().getUID().toString());
                locksConfig.set("locks." + uuid.toString() + "." + i + ".x", location.getBlockX());
                locksConfig.set("locks." + uuid.toString() + "." + i + ".y", location.getBlockY());
                locksConfig.set("locks." + uuid.toString() + "." + i + ".z", location.getBlockZ());
            }

        }
        Bukkit.getConsoleSender().sendMessage("Secured " + i + " locks saved!");
    }

    private void saveBalances() {
        for (UUID uuid : OddJob.getInstance().getEconManager().getBalanceMap().keySet()) {
            balanceConfig.set("balance." + uuid.toString(), OddJob.getInstance().getEconManager().getBalanceMap().get(uuid));
        }
        Bukkit.getConsoleSender().sendMessage("Balances saved!");
    }

    private void savePlayers() {
        for (UUID uuid : OddJob.getInstance().getPlayerManager().getPlayersMap().keySet()) {
            playerConfig.set("players." + uuid.toString(), OddJob.getInstance().getPlayerManager().getPlayersMap().get(uuid));
        }
        Bukkit.getConsoleSender().sendMessage("Players saved!");
    }

    private void saveHomes() {
        for (UUID uuid : OddJob.getInstance().getPlayerManager().getPlayersMap().keySet()) {
            Set<String> list = OddJob.getInstance().getHomesManager().list(uuid);
            if (list != null) {
                for (String st : list) {
                    Location location = OddJob.getInstance().getHomesManager().get(uuid, st);
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
