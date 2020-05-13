package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Arena.GameState;
import no.helponline.Utils.Arena.YMLLoader;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    public FileConfiguration scoreboard,messages,database,signs,reset,chestLoot,kits;

    public void load() {
        reloadConfig();
        reloadMessages();
        reloadDatabase();
        reloadSigns();
        //reloadReset();
        reloadChests();
        reloadScoreboard();
        reloadKits();
    }

    private void reloadConfig() {
        OddJob.getInstance().reloadConfig();
        FileConfiguration config = OddJob.getInstance().getConfig();

        config.addDefault("use-permission", true);
        config.addDefault("broadcast-win", true);

        config.addDefault("SQL.Type", "MYSQL");
        config.addDefault("SQL.TablePrefix", "mine_");
        config.addDefault("SQL.Hostname", "localhost");
        config.addDefault("SQL.Port", 3306);
        config.addDefault("SQL.Database", "minecraft");
        config.addDefault("SQL.Username", "root");
        config.addDefault("SQL.Password", "");

        config.addDefault("Lightning.on-death", true);
        config.addDefault("Lightning.on-few-players", true);
        config.addDefault("Lightning.few-players", 3);
        config.addDefault("Lightning.few-players-time", 45);

        config.addDefault("Default.Lobby-Time", 120);
        config.addDefault("Default.Required-Players-to-start", 2);

        config.addDefault("Default.Arena.Chests.TypeID", 54);
        config.addDefault("Default.Arena.Chests.Data", -1);
        config.addDefault("Default.Arena.Grace-Period", 30);

        config.addDefault("Default.Arena.Automaticly-Deathmatch-Time", 1800);
        config.addDefault("Default.Arena.Player-Deathmatch-Start", 3);

        config.addDefault("Default.Money-on-Kill", 2.5);
        config.addDefault("Default.Money-on-Win", 20.0);
        config.addDefault("Default.Midnight-chest-refill", true);

        ArrayList<Material> allowedBlocks = new ArrayList<>();

        allowedBlocks.add(Material.OAK_LEAVES); // 18
        allowedBlocks.add(Material.SPRUCE_LEAVES); // 18
        allowedBlocks.add(Material.BIRCH_LEAVES);  // 18
        allowedBlocks.add(Material.JUNGLE_LEAVES); // 18
        allowedBlocks.add(Material.DEAD_BUSH);   // 32
        allowedBlocks.add(Material.GRASS);  // 31:1
        allowedBlocks.add(Material.FERN);  // 31:2
        allowedBlocks.add(Material.CAKE);  // 92
        allowedBlocks.add(Material.MELON); // 103
        allowedBlocks.add(Material.BROWN_MUSHROOM); // 39
        allowedBlocks.add(Material.RED_MUSHROOM); // 40
        allowedBlocks.add(Material.PUMPKIN); // 86
        allowedBlocks.add(Material.TNT);  // 46
        allowedBlocks.add(Material.FIRE); // 51
        allowedBlocks.add(Material.COBWEB); // 30

        config.addDefault("Default.Arena.Allowed-Blocks", allowedBlocks);

        if (config.contains("Chest"))
            config.set("Chest", null);
        if (config.contains("Chestloot"))
            config.set("Chestloot", null);

        ArrayList<String> allowedCmds = new ArrayList<>();
        allowedCmds.add("/arena");
        config.addDefault("Allowed-Commands", allowedCmds);


        config.addDefault("Voting.Item", Material.CHEST + " name:&eVote_for_an_arena lore:&7Rightclick_to_open//&7the_voting_menu!");
        config.addDefault("Voting.InventoryTitle", "Vote for an arena!");
        config.addDefault("Voting.ArenaItem", Material.MAP + " 0 lore:&7Click_to_vote//&7for_this_arena!");
        config.addDefault("Leave-Item", Material.MAGMA_CREAM + " name:&eLeave_the_lobby lore:&7Rightclick_to_leave//&7the_lobby!");

        config.addDefault("Spectating.Enabled", true);
        config.addDefault("Spectating.Max-Spectators-Per-Arena", 8);
        config.addDefault("Spectating.Player-Navigator.Item", Material.COMPASS + " name:&ePlayer Navigator lore:&7Rightclick_to_open//&7the_player_navigator!");
        config.addDefault("Spectating.Player-Navigator.Inventory-Title", "Click on a item to spectate!");

        if (config.contains("Separating.Chat.Enabled"))
            config.set("Separating.Chat.Enabled", null);


        config.addDefault("Chat.Enabled", true);
        config.addDefault("Chat.Design", "{STATE}{PREFIX}{PLAYERNAME}{SUFFIX}{MESSAGE}");
        config.addDefault("Chat.Spectator-State", "&8[&4✖&8] &r");

        List<String> joinfull = new ArrayList<>();
        joinfull.add("sg.donator.vip.iron");
        joinfull.add("sg.donator.vip.gold");
        joinfull.add("sg.donator.moderator");
        joinfull.add("sg.donator.admin");
        config.addDefault("Donator-Permissions.Join-Full-Arena", joinfull);

        List<String> votePower = new ArrayList<>();
        votePower.add("sg.donator.vip.iron//2");
        votePower.add("sg.donator.vip.gold//2");
        config.addDefault("Donator-Permissions.Extra-Vote-Power", votePower);
        config.addDefault("TNT-Extra-Damage", 7.0);

        config.addDefault("Enable-Arena-Reset", true);

        config.options().copyDefaults(true);
        OddJob.getInstance().saveConfig();

        if (!config.getBoolean("Enable-Arena-Reset")) {
            System.out.println("[SurvivalGames] Warning: Arena map reset is disabled.");
        }
    }

    private void reloadChests() {
        chestLoot = new YMLLoader("plugins/SurvivalGames", "chestLoot.yml").getFileConfiguration();

        List<String> lvl1 = new ArrayList<>();

        lvl1.add(Material.WOODEN_AXE + "");
        lvl1.add(Material.LEATHER_BOOTS + "");
        lvl1.add(Material.GOLDEN_HELMET + "");
        lvl1.add(Material.APPLE + " 3");
        lvl1.add(Material.ARROW + " 5");
        chestLoot.addDefault("Chestloot.Level 1", lvl1);


        List<String> lvl2 = new ArrayList<>();

        lvl2.add(Material.COOKED_BEEF + "");
        lvl2.add(Material.CHICKEN + " 2");
        lvl2.add(Material.COOKED_CHICKEN + "");
        lvl2.add(Material.MUSHROOM_STEW + "");
        lvl2.add(Material.WOODEN_SWORD + "");
        lvl2.add(Material.GOLDEN_HELMET + "");
        lvl2.add(Material.GOLDEN_LEGGINGS + "");
        lvl2.add(Material.LEATHER_BOOTS + "");
        lvl2.add(Material.PORKCHOP + " 2");
        lvl2.add(Material.BOWL + "");
        lvl2.add(Material.MELON + " 2");
        lvl2.add(Material.CHICKEN + "");

        chestLoot.addDefault("Chestloot.Level 2", lvl2);


        List<String> lvl3 = new ArrayList<>();

        lvl3.add(Material.MELON + "");
        lvl3.add(Material.IRON_HELMET + "");
        lvl3.add(Material.MELON_SLICE + " 4");
        lvl3.add(Material.GOLDEN_SWORD + "");
        lvl3.add(Material.COBWEB + " 3");
        lvl3.add(Material.CHAINMAIL_CHESTPLATE + "");
        lvl3.add(Material.CHAINMAIL_BOOTS + "");
        lvl3.add(Material.FISHING_ROD + "");
        lvl3.add(Material.LEATHER_LEGGINGS + "");
        lvl3.add(Material.ARROW + " 4");
        lvl3.add(Material.GOLD_INGOT + " 2");
        lvl3.add(Material.TNT + " name:&eInstant_ignition_bomb");
        lvl3.add(Material.DEAD_BUSH + "");

        chestLoot.addDefault("Chestloot.Level 3", lvl3);


        List<String> lvl4 = new ArrayList<>();

        lvl4.add(Material.GOLD_INGOT + " 5");
        lvl4.add(Material.IRON_CHESTPLATE + "");
        lvl4.add(Material.IRON_BOOTS + "");
        lvl4.add(Material.CHAINMAIL_HELMET + "");
        lvl4.add(Material.FLINT_AND_STEEL + "");
        lvl4.add(Material.GOLDEN_BOOTS + "");
        lvl4.add(Material.STONE_SWORD + "");
        lvl4.add(Material.WOODEN_SWORD + "");
        lvl4.add(Material.STRING + " 2");

        chestLoot.addDefault("Chestloot.Level 4", lvl4);


        List<String> lvl5 = new ArrayList<>();
        lvl5.add(Material.DIAMOND + " 2");
        lvl5.add(Material.IRON_INGOT + "");
        lvl5.add(Material.STICK + " 2");
        lvl5.add(Material.CAKE + "");
        lvl5.add(Material.FERMENTED_SPIDER_EYE + "");
        lvl5.add(Material.BOW + ":168");
        lvl4.add(Material.STONE_SWORD + " name:&eSword_of_Herobrine enchant:KNOCKBACK,1 enchant:DAMAGE_ALL,1");
        lvl5.add(Material.POTION + " effect:regeneration,10,1 name:&cRegeneration");
        lvl5.add(Material.POTION + " effect:jump,18,1 effect:speed,18,2 name:&ePotion_of_a_rabbit lore:&7Give_you_the//&7abilities_of_a_rabbit!");
        chestLoot.addDefault("Chestloot.Level 5", lvl5);

        chestLoot.addDefault("Chest-Title", "Survival Chest");
        chestLoot.options().header(
                "##### UltimateSurvivalGames Chestloot Configuration #####\n" +
                        "\n" +
                        "## How does this work? ##\n" +
                        "The chestloot is splitted into 5 lists. You can add unlimited items to each list.\n" +
                        "In one chest can spawn up to 8 different items. For each itemstack, the plugin chooses from\n" +
                        "one list. Each list has different chances for items spawning in that list:\n" +
                        "\n" +
                        "Level 1: 40 %\n" +
                        "Level 2: 30 %\n" +
                        "Level 3: 15 %\n" +
                        "Level 4: 10 %\n" +
                        "Level 5: 5 %\n" +
                        "\n" +
                        "If the plugin has choosed a list for an itemstack, it takes an item random from the list.\n" +
                        "\n" +
                        "## How can I modify the items? ##\n" +
                        "You can add or remove items from all lists. But at least one item has to be on each list.\n" +
                        "\n" +
                        "## How do I format the items? ##\n" +
                        "MATERIAL/ITEMID[:SUBID] [AMOUNT] [SPECIAL THINGS]\n" +
                        "Here are some examples:\n" +
                        "\n" +
                        "# Normal Item:\n" +
                        "\"BREAD\" - is the same like \"BREAD 1\", \"BREAD:0 1\" or \"297:0 1\"\n" +
                        "\n" +
                        "# If you want to set a predefined durability-level, just change the subid:\n" +
                        "\"STONE_SWORD:10\" - This tool has already 10 uses lost.\n" +
                        "\n" +
                        "# You can also add enchantments to an item:\n" +
                        "\"STONE_SWORD enchant:KNOCKBACK,2 enchant:DAMAGE_ALL,3\" - This item has knockback 2 and sharpness 3! Note: Only the vanilla level of an enchantment can be used!\n" +
                        "\n" +
                        "# You can also set a custom name and lore for an item:\n" +
                        "\"EGG name:&eEaster_Egg lore:&7Throw//&7me!\" - This is an egg with a displayname \"Easter Egg\" and the lore \"Throw me\"! Note: Spaces are \"_\" and line breaks in lore the charakters \"//\"\n");

        chestLoot.options().copyDefaults(true);
        saveChests();
    }

    private void saveChests() {
        try {
            chestLoot.save("plugins/SurvivalGames/chestLoot.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadScoreboard() {
        scoreboard = new YMLLoader("plugins/SurvivalGames", "scoreboard.yml").getFileConfiguration();

        String path = "Phase.Waiting.";
        scoreboard.addDefault(path + "Enabled", true);
        scoreboard.addDefault(path + "Title", "&b&lWaiting for players");
        List<String> content = new ArrayList<>();
        content.add("&eRequired players to start&7://%requiredplayers%");
        content.add("&eCurrent player amount&7://%playing%");
        scoreboard.addDefault(path + "Scores", content);

        path = "Phase.Voting.";
        scoreboard.addDefault(path + "Enabled", true);
        scoreboard.addDefault(path + "Title", "&b&lArena Voting");
        content = new ArrayList<>();
        content.add("&e%arena%//%votecount%");
        content.add("&e%arena%//%votecount%");
        content.add("&e%arena%//%votecount%");
        scoreboard.addDefault(path + "Scores", content);

        path = "Phase.Cooldown.";
        scoreboard.addDefault(path + "Enabled", true);
        scoreboard.addDefault(path + "Title", "&b&lCooldown");
        content = new ArrayList<>();
        content.add("&eTime remaining&7://%time%");
        content.add("&eTributes&7://%playing%");
        scoreboard.addDefault(path + "Scores", content);

        path = "Phase.Ingame.";
        scoreboard.addDefault(path + "Enabled", true);
        scoreboard.addDefault(path + "Title", "&b&lIngame");
        content = new ArrayList<>();
        content.add("&e&lAlive&7://%playing%");
        content.add("&c&lDead&7://%death%");
        scoreboard.addDefault(path + "Scores", content);

        path = "Phase.Deathmatch.";
        scoreboard.addDefault(path + "Enabled", true);
        scoreboard.addDefault(path + "Title", "&b&lDeathmatch");
        content = new ArrayList<>();
        content.add("&eTime remaining&7://%time%");
        scoreboard.addDefault(path + "Scores", content);

        scoreboard.options().header(
                "##### UltimateSurvivalGames Scoreboard Configuration #####\n" +
                        "\n" +
                        "How does this work?\n" +
                        "For each game phase (WAITING,VOTING,COOLDOWN,INGAME and DEATHMATHCH) is a scoreboard design.\n" +
                        "If you set \"Enabled\" for a phase to false, no scoreboard will shown!\n" +
                        "The title can be maximal 32 charakters long and cannot contain variables.\n" +
                        "\n" +
                        "In the \"Scores\" part, you can modify the content of the scoreboard. \"//\" splits the line in name and score.\n" +
                        "The left part is the name which can be maximal 48 charalters long.\n" +
                        "The right part is the amount of a score. Here you have to write the variables.\n" +
                        "\n" +
                        "What are the variables?\n" +
                        "You can use many variables. Here is a list:\n" +
                        "\n" +
                        "  %playing% - The current amount of players in a lobby!\n" +
                        "  %requiredplayers% - The amount of required players to start a game automaticly!\n" +
                        "  %death% - The amount of deaths in a round!\n" +
                        "  %spectators% - The amount of spectators in a round!\n" +
                        "  %time% - The remaining time of a game phase!\n" +
                        "  %votecount% - The amount of votes of an arena (Only works in the voting phase)\n" +
                        "  %arena% - The name of the arena (Only works in the score name)\n" +
                        "\n" +
                        "More help on http://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/\n");


        scoreboard.options().copyDefaults(true);
        saveScoreboard();
    }

    private void saveScoreboard() {
        try {
            scoreboard.save("plugins/SurvivalGames/scoreboard.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadSigns() {
        signs = new YMLLoader("plugins/SurvivalGames", "signs.yml").getFileConfiguration();

        signs.addDefault("Sign.LeftClick.Show current arena", true);
        signs.addDefault("Sign.LeftClick.Show players remain", true);

        signs.addDefault("Sign.Line.1", "&bSurvivalGames");
        signs.addDefault("Sign.Line.2", "&8[&e%name%&8]");
        signs.addDefault("Sign.Line.3", "&o%state%");
        signs.addDefault("Sign.Line.4", "%currentplayers%/&7%requiredplayers%&r/%maxplayers%");

        signs.addDefault("Sign.LeavePrefix", "&bSurvivalGames");
        signs.addDefault("Sign.Leave.Line.2", "");
        signs.addDefault("Sign.Leave.Line.3", "&oRightclick");
        signs.addDefault("Sign.Leave.Line.4", "&oto leave!");

        for (GameState state : GameState.values()) {
            signs.addDefault("Translations." + state.toString(), state.toString());
        }


        signs.options().copyDefaults(true);
        saveSigns();
    }

    private void reloadDatabase() {
        database = new YMLLoader("plugins/SurvivalGames", "database.yml").getFileConfiguration();
    }

    private void reloadKits() {
        kits = new YMLLoader("plugins/SurvivalGames", "kits.yml").getFileConfiguration();
        kits.addDefault("Enabled",true);
        kits.options().copyDefaults(true);
        saveKits();
    }



    private void reloadMessages() {
        messages = new YMLLoader("plugins/SurvivalGames", "messages.yml").getFileConfiguration();
        // TODO messages
        messages.options().copyDefaults(true);
        saveMessages();
    }

    private void saveMessages(){
        try {
            messages.save("plugins/SurvivalGames/messages.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveKits() {
        try {
            kits.save("plugins/SurvivalGames/kits.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveSigns() {
        try {
            signs.save("plugins/SurvivalGames/signs.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
