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
        loadPlayers();
        loadGuilds();
        loadBalances();
        loadHomes();
        loadLocks();
        OddJob.getInstance().log("All loaded");
    }


    public UUID generateUniqueId() {
        return UUID.randomUUID();
    }


    private void loadPlayers() {
        int i = 0;
        if (playerConfig.contains("players")) {
            // Looping through players
            for (String playerUUIDString : playerConfig.getConfigurationSection("players").getKeys(false)) {
                // Increment counter of players
                i++;
                List<UUID> blacklist = new ArrayList<>();
                // Looping through blacklisted by player
                for (String a : playerConfig.getStringList("players." + playerUUIDString + ".blackList")) {
                    blacklist.add(UUID.fromString(a));
                }
                List<UUID> whitelist = new ArrayList<>();
                // Looping through whitelist of player
                for (String a : playerConfig.getStringList("players." + playerUUIDString + ".whiteList")) {
                    whitelist.add(UUID.fromString(a));
                }
                // Creating instance of OddPlayer
                /*OddJob.getInstance().getPlayerManager().create(
                        UUID.fromString(playerUUIDString),
                        playerConfig.getString("players." + playerUUIDString + ".name"),
                        playerConfig.getBoolean("players." + playerUUIDString + ".denyTPA", false),
                        blacklist,
                        whitelist);
*/
            }
            OddJob.getInstance().log("Loaded " + i + " players!");
        }
    }

    private void loadGuilds() {
        boolean run = getBoolean("run");
        int i = 0;
        int a = 0;
        int p = 0;
        if (guildConfig.contains("guild")) {
            // TODO
            // Looping through guildsID
            for (String guildUUIDString : guildConfig.getConfigurationSection("guild").getKeys(false)) {
                HashMap<String, Object> guildMap = new HashMap<>();
                // Increment counter for guilds
                i++;
                HashMap<UUID, Role> members = new HashMap<>();
                List<Chunk> chunks = new ArrayList<>();
                // If the guild has claimed any chunks
                if (guildConfig.getConfigurationSection("guild." + guildUUIDString + ".chunks") != null) {
                    // Increment counter for chunks to guilds
                    a++;
                    // Looping through the saved chunks
                    for (String guildChunk : guildConfig.getConfigurationSection("guild." + guildUUIDString + ".chunks").getKeys(false)) {
                        UUID world = UUID.fromString(guildConfig.getString("guild." + guildUUIDString + ".chunks." + guildChunk + ".world"));
                        Chunk chunk = Bukkit.getWorld(world).getChunkAt(guildConfig.getInt("guild." + guildUUIDString + ".chunks." + guildChunk + ".x"), guildConfig.getInt("guild." + guildUUIDString + ".chunks." + guildChunk + ".z"));
                        if (!run) OddJob.getInstance().getMySQLManager().addGuildChunks(guildUUIDString, chunk);
                        chunks.add(chunk);
                    }
                }
                for (String b : guildConfig.getConfigurationSection("guild." + guildUUIDString + ".members").getKeys(false)) {
                    // Increment counter for players in guilds
                    p++;
                    members.put(UUID.fromString(b), Role.valueOf(guildConfig.getString("guild." + guildUUIDString + ".members." + b)));
                    if (!run)
                        OddJob.getInstance().getMySQLManager().addGuildMember(guildUUIDString, UUID.fromString(b), Role.valueOf(guildConfig.getString("guild." + guildUUIDString + ".members." + b)));
                }
                HashMap<String, Object> settings = new HashMap<>();
                if (guildConfig.get("guild." + guildUUIDString + ".settings") != null) {
                    // Looping through the settings of the guild
                    for (String b : guildConfig.getConfigurationSection("guild." + guildUUIDString + ".settings").getKeys(false)) {
                        settings.put(b, guildConfig.get("guild." + guildUUIDString + ".settings." + b));
                    }
                }
                // Creating the instance of the Guild
                OddJob.getInstance().getGuildManager().set(
                        UUID.fromString(guildUUIDString),//id
                        guildConfig.getString("guild." + guildUUIDString + ".name"),//name
                        members,//members
                        chunks,//claims
                        Zone.valueOf(guildConfig.getString("guild." + guildUUIDString + ".zone", "GUILD")),
                        settings); //Zone
                guildMap.put("uuid", guildUUIDString);
                guildMap.put("name", guildConfig.getString("guild." + guildUUIDString + ".name"));
                guildMap.put("zone", Zone.valueOf(guildConfig.getString("guild." + guildUUIDString + ".zone", "GUILD")));
                guildMap.put("invited_only", false);

                if (!run) OddJob.getInstance().getMySQLManager().createGuild(guildMap);
                OddJob.getInstance().getConfig().set("run", true);
            }
            OddJob.getInstance().log("Loaded " + i + " guilds; " + p + " members; " + a + " chunks!");
        }
    }

    private void loadBalances() {
        int i = 0;
        if (balanceConfig.contains("balance")) {
            // Looping through the balances of players
            for (String playerUUIDString : balanceConfig.getConfigurationSection("balance").getKeys(false)) {
                // Increments counter of balances
                i++;
                // Setting the values
                OddJob.getInstance().getEconManager().setBalance(UUID.fromString(playerUUIDString), balanceConfig.getDouble("balance." + playerUUIDString));
            }
            OddJob.getInstance().log("Loaded " + i + " balances!");
        }
    }

    public void loadLocks() {
        int i = 0;
        if (locksConfig.contains("armor")) {
            HashMap<UUID, UUID> a = new HashMap<>(); // Entity -> Player
            // Looping through entities UUID
            for (String entityUUIDString : locksConfig.getConfigurationSection("armor").getKeys(false)) {
                // Incrementing counter of locked entities
                i++;
                // Putting to hash map
                a.put(UUID.fromString(entityUUIDString), UUID.fromString(locksConfig.getString("armor." + entityUUIDString)));
            }
            // Creating the list of protected armor-stands
            OddJob.getInstance().getLockManager().setArmorstand(a);
        }
        if (locksConfig.contains("locks")) {
            HashMap<Location, UUID> chest = new HashMap<>();
            // Looping through players who has locked stuff
            for (String playerUUIDString : locksConfig.getConfigurationSection("locks").getKeys(false)) {
                // Looping through their locked stuff
                for (String c : locksConfig.getConfigurationSection("locks." + playerUUIDString).getKeys(false)) {
                    // Increment counter of locked stuff
                    i++;
                    // Creating the location of the locked stuff
                    Location location = new Location(
                            Bukkit.getWorld(UUID.fromString(locksConfig.getString("locks." + playerUUIDString + "." + c + ".world"))),
                            locksConfig.getInt("locks." + playerUUIDString + "." + c + ".x"),
                            locksConfig.getInt("locks." + playerUUIDString + "." + c + ".y"),
                            locksConfig.getInt("locks." + playerUUIDString + "." + c + ".z")
                    );
                    // Putting it into the hash map
                    chest.put(location, UUID.fromString(playerUUIDString));
                }
            }
            // Setting the values
            OddJob.getInstance().getLockManager().setLocks(chest);
        }
        OddJob.getInstance().log("Loaded " + i + " locks.");
    }

    private void loadHomes() {
        int i = 0;
        if (homesConfig.contains("homes")) {
            // Looping through players who has a home
            for (String playerUUIDString : homesConfig.getConfigurationSection("homes").getKeys(false)) {
                UUID uuid = UUID.fromString(playerUUIDString);
                Set<String> setOfHomes = homesConfig.getConfigurationSection("homes." + playerUUIDString).getKeys(false);
                if (!setOfHomes.isEmpty()) {
                    // Looping through players homes
                    for (String homeName : setOfHomes) {
                        // Increment counter of homes
                        i++;
                        String worldUUID = homesConfig.getString("homes." + playerUUIDString + "." + homeName + ".world");
                        int x = homesConfig.getInt("homes." + playerUUIDString + "." + homeName + ".x");
                        int y = homesConfig.getInt("homes." + playerUUIDString + "." + homeName + ".y");
                        int z = homesConfig.getInt("homes." + playerUUIDString + "." + homeName + ".z");
                        float yaw = homesConfig.getInt("homes." + playerUUIDString + "." + homeName + ".yaw");
                        float pitch = homesConfig.getInt("homes." + playerUUIDString + "." + homeName + ".pitch");
                        // Creating the location of the home
                        Location location = new Location(Bukkit.getWorld(UUID.fromString(worldUUID)), x, y, z, yaw, pitch);
                        // Setting the value
                        //OddJob.getInstance().getHomesManager().add(uuid, homeName, location);
                    }
                }
            }
            OddJob.getInstance().log("Loaded " + i + " homes!");
        }
    }

    public void save() {
        // Saving all
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

    private void saveGuilds() {
        int i = 0, m = 0, c = 0;
        // Looping through all made guilds
        for (UUID uuid : OddJob.getInstance().getGuildManager().getGuilds().keySet()) {
            boolean run = getBoolean("run");
            HashMap<String, Object> guildMap = new HashMap<>();
            // Increment counter of guilds
            i++;
            // Getting guild information
            Guild guild = OddJob.getInstance().getGuildManager().getGuild(uuid);
            String guildUUIDString = uuid.toString();
            guildMap.put("uuid", guildUUIDString);
            // Saving name
            guildConfig.set("guild." + guildUUIDString + ".name", guild.getName());
            guildMap.put("name", guild.getName());
            // Saving zone type
            guildConfig.set("guild." + guildUUIDString + ".zone", guild.getZone().toString());
            guildMap.put("zone", guild.getZone());
            HashMap<UUID, Role> members = guild.getMembers();
            // Looping though members of this guild
            for (UUID player : members.keySet()) {
                m++;
                guildConfig.set("guild." + guildUUIDString + ".members." + player.toString(), members.get(player).toString()); //members
                if (!run)
                    OddJob.getInstance().getMySQLManager().addGuildMember(guildUUIDString, player, members.get(player));
            }
            HashMap<String, Object> settings = guild.getSettings();
            // Looping through the settings
            for (String settingName : settings.keySet()) {
                guildConfig.set("guild." + guildUUIDString + ".settings." + settingName, settings.get(settingName));
                guildMap.put(settingName, settings.get(settingName));
            }
            List<Chunk> chunks = OddJob.getInstance().getGuildManager().getChunks(uuid);
            // Looping through occupied chunks
            for (Chunk chunk : chunks) {
                // Increment counter of occupied chunks
                c++;
                if (!run) OddJob.getInstance().getMySQLManager().addGuildChunks(guildUUIDString, chunk);
                guildConfig.set("guild." + guildUUIDString + ".chunks." + c + ".world", chunk.getWorld().getUID().toString());
                guildConfig.set("guild." + guildUUIDString + ".chunks." + c + ".x", chunk.getX());
                guildConfig.set("guild." + guildUUIDString + ".chunks." + c + ".z", chunk.getZ());
            }
            if (!run) OddJob.getInstance().getMySQLManager().createGuild(guildMap);
            OddJob.getInstance().getConfig().set("run", true);
        }
        OddJob.getInstance().log("Saved " + i + " guilds; " + m + " members; " + c + " chunks!");
    }

    public void saveLocks() {
        int i = 0;
        // Removing all secured stuff before saving
        for (UUID uuid : OddJob.getInstance().getPlayerManager().getUUIDs()) {
            locksConfig.set("locks." + uuid.toString(), null);
            locksConfig.set("armor." + uuid.toString(), null);
        }
        if (OddJob.getInstance().getLockManager().getArmorstands() != null && OddJob.getInstance().getLockManager().getArmorstands().size() > 0) {

            HashMap<UUID, UUID> a = OddJob.getInstance().getLockManager().getArmorstands();
            // Looping through secured entities
            for (UUID entity : a.keySet()) {
                // Increment counter of protected entities
                i++;
                // Saving to config
                locksConfig.set("armor." + entity.toString(), a.get(entity).toString()); // Entity -> Player
            }
        }
        // Looping through Locations
        if (OddJob.getInstance().getLockManager().getLocks() != null && OddJob.getInstance().getLockManager().getLocks().size() > 0) {
            HashMap<Location, UUID> locks = OddJob.getInstance().getLockManager().getLocks();
            for (Location location : locks.keySet()) {
                // Increments counter of locks
                i++;
                String playerUUIDString = locks.get(location).toString();
                // Saving to config
                locksConfig.set("locks." + playerUUIDString + "." + i + ".world", location.getWorld().getUID().toString());
                locksConfig.set("locks." + playerUUIDString + "." + i + ".x", location.getBlockX());
                locksConfig.set("locks." + playerUUIDString + "." + i + ".y", location.getBlockY());
                locksConfig.set("locks." + playerUUIDString + "." + i + ".z", location.getBlockZ());
            }

        }
        OddJob.getInstance().log("Saved " + i + " locks");
    }

    private void saveBalances() {
        int i = 0;
        // Looping through known balances
        for (UUID uuid : OddJob.getInstance().getEconManager().getBalanceMap().keySet()) {
            // Increment counter of balances
            i++;
            // Saving to config
            balanceConfig.set("balance." + uuid.toString(), OddJob.getInstance().getEconManager().getBalanceMap().get(uuid));
        }
        OddJob.getInstance().log("Saved " + i + " balances");
    }

    private void savePlayers() {
        int i = 0;
        // Looping through the OddPlayers
        /*for (OddPlayer oddPlayer : OddJob.getInstance().getPlayerManager().getPlayersMap()) {
            // Increments counter of OddPlayers
            i++;
            String playerUUIDString = oddPlayer.getUuid().toString();
            // Saving to config
            playerConfig.set("players." + playerUUIDString + ".name", oddPlayer.getName());
            playerConfig.set("players." + playerUUIDString + ".uuid", oddPlayer.getUuid());
            playerConfig.set("players." + playerUUIDString + ".denyTPA", oddPlayer.isDenyTPA());
            playerConfig.set("players." + playerUUIDString + ".whiteList", oddPlayer.getWhiteList().toString());
            playerConfig.set("players." + playerUUIDString + ".blackList", oddPlayer.getBlackList().toString());
        }
        OddJob.getInstance().log("Saved " + i + " players");*/
    }

    private void saveHomes() {
        int i = 0;
        // Looping through OddPlayers
        /*for (OddPlayer oddPlayer : OddJob.getInstance().getPlayerManager().getPlayersMap()) {
            String playerUUIDString = oddPlayer.getUuid().toString();
            Set<String> list = OddJob.getInstance().getHomesManager().list(oddPlayer.getUuid());
            if (list != null) {
                // Looping through players homes
                for (String homeName : list) {
                    // Increments counter of homes
                    i++;
                    Location location = OddJob.getInstance().getHomesManager().get(oddPlayer.getUuid(), homeName);
                    OddJob.getInstance().getMySQLManager().saveHomes(location,homeName,oddPlayer);
                    homesConfig.set("homes." + playerUUIDString + "." + homeName + ".world", location.getWorld().getUID().toString());
                    homesConfig.set("homes." + playerUUIDString + "." + homeName + ".x", location.getBlockX());
                    homesConfig.set("homes." + playerUUIDString + "." + homeName + ".y", location.getBlockY());
                    homesConfig.set("homes." + playerUUIDString + "." + homeName + ".z", location.getBlockZ());
                    homesConfig.set("homes." + playerUUIDString + "." + homeName + ".yaw", location.getYaw());
                    homesConfig.set("homes." + playerUUIDString + "." + homeName + ".pitch", location.getPitch());
                }
            }
        }*/
        OddJob.getInstance().log("Saved " + i + " homes");
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
