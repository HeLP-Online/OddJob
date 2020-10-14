package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Arena.Arena;
import no.helponline.Utils.Guild;
import no.helponline.Utils.ScoreBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.UUID;

public class ScoreManager {
    HashMap<UUID, Objective> objectives = new HashMap<>();
    ScoreboardManager scoreboardManager;
    Scoreboard scoreboard;
    int taskID = 0;

    public ScoreManager() {
        scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager != null) {
            scoreboard = scoreboardManager.getMainScoreboard();
            Objective objective = scoreboard.getObjective("server");
            if (objective == null)
                objective = scoreboard.registerNewObjective("server", "dummy", Bukkit.getServer().getName());
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.getScore(ChatColor.BOLD + "Show Scoreboard:").setScore(3);
            objective.getScore(ChatColor.GOLD + "/player set scoreboard <option>").setScore(2);
            objective.getScore(ChatColor.WHITE + "Guild or Player").setScore(1);
        }
    }

    public void clear(Player player) {
        player.setScoreboard(scoreboard);
    }

    public void create(Player player, no.helponline.Utils.Enum.ScoreBoard type) {
        // CREATING MANAGER
        if (scoreboardManager == null) {
            OddJob.getInstance().log("No manager");
            return;
        }
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        // Sending it to the player
        player.setScoreboard(scoreboard);

        String unique = player.getUniqueId().toString().substring(0, 8);

        Objective objective = objectives.getOrDefault(player.getUniqueId(), scoreboard.registerNewObjective("p" + unique, "dummy", player.getName()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        switch (type) {
            case ArenaMaker:
                int arenaId = OddJob.getInstance().getArenaManager().getEditor(player.getUniqueId());
                Arena arena = OddJob.getInstance().getArenaManager().getArena(arenaId);
                objective.setDisplayName("ArenaMaker - " + arenaId);

                Team arenaName;
                arenaName = scoreboard.getTeam("an" + arenaId);
                if (arenaName == null) arenaName = scoreboard.registerNewTeam("an" + arenaId);
                arenaName.addEntry(ChatColor.GOLD + "ID: " + ChatColor.WHITE);
                objective.getScore(ChatColor.GOLD + "ID: " + ChatColor.WHITE).setScore(15);

                Team arenaType;
                arenaType = scoreboard.getTeam("at" + arenaId);
                if (arenaType == null) arenaType = scoreboard.registerNewTeam("at" + arenaId);
                arenaType.addEntry(ChatColor.GOLD + "Type: " + ChatColor.WHITE);
                objective.getScore(ChatColor.GOLD + "Type: " + ChatColor.WHITE).setScore(15);

                Team arenaLobbySpawn;
                arenaLobbySpawn = scoreboard.getTeam("als" + arenaId);
                if (arenaLobbySpawn == null) arenaLobbySpawn = scoreboard.registerNewTeam("als" + arenaId);
                arenaLobbySpawn.addEntry(ChatColor.GOLD + "LobbySpawn: " + ChatColor.WHITE);
                objective.getScore(ChatColor.GOLD + "LobbySpawn: " + ChatColor.WHITE).setScore(15);

                Team arenaGameSpawn;
                arenaGameSpawn = scoreboard.getTeam("ags" + arenaId);
                if (arenaGameSpawn == null) arenaGameSpawn = scoreboard.registerNewTeam("ags" + arenaId);
                arenaGameSpawn.addEntry(ChatColor.GOLD + "GameSpawn: " + ChatColor.WHITE);
                objective.getScore(ChatColor.GOLD + "GameSpawn: " + ChatColor.WHITE).setScore(15);

                Team arenaSigns;
                arenaSigns = scoreboard.getTeam("as" + arenaId);
                if (arenaSigns == null) arenaSigns = scoreboard.registerNewTeam("as" + arenaId);
                arenaSigns.addEntry(ChatColor.GOLD + "Sign: " + ChatColor.WHITE);
                objective.getScore(ChatColor.GOLD + "Sign: " + ChatColor.WHITE).setScore(15);

                Team finalArenaName = arenaName;
                Team finalArenaType = arenaType;
                Team finalArenaLobbySpawn = arenaLobbySpawn;
                Team finalArenaGameSpawn = arenaGameSpawn;
                Team finalArenaSigns = arenaSigns;
                taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(OddJob.getInstance(), new Runnable() {
                    final ScoreBoard scoreBoard = new ScoreBoard(player.getUniqueId());

                    @Override
                    public void run() {
                        if (!scoreBoard.hasID()) scoreBoard.setID(taskID);
                        finalArenaName.setSuffix("" + arenaId);
                        finalArenaType.setSuffix("" + arena.getGameType());
                        finalArenaLobbySpawn.setSuffix(arena.getLobbySpawn() != null ? ChatColor.GREEN + "Set!" : ChatColor.DARK_RED + "None");
                        finalArenaGameSpawn.setSuffix("" + arena.blMat.size());
                        finalArenaSigns.setSuffix(arena.getSignLocation() != null ? ChatColor.GREEN + "Set!" : ChatColor.DARK_RED + "None");
                    }
                }, 0, 10);
                break;
            case Player:
                objective.setDisplayName("Player - " + player.getName());
                // Topic - Money
                objective.getScore(ChatColor.BOLD + "Money:").setScore(15);

                // Content - Player Bank
                Team playerBank;
                playerBank = scoreboard.getTeam("pb" + unique);
                if (playerBank == null) playerBank = scoreboard.registerNewTeam("pb" + unique);
                playerBank.addEntry(ChatColor.GOLD + "Bank: " + ChatColor.WHITE);
                playerBank.setSuffix("" + OddJob.getInstance().getEconManager().getBankBalance(player.getUniqueId(), false));
                objective.getScore(ChatColor.GOLD + "Bank: " + ChatColor.WHITE).setScore(14);

                // Content - Player Wallet
                Team playerWallet;
                playerWallet = scoreboard.getTeam("pw" + unique);
                if (playerWallet == null) playerWallet = scoreboard.registerNewTeam("pw" + unique);
                playerWallet.addEntry(ChatColor.GOLD + "Wallet: " + ChatColor.WHITE);
                playerWallet.setSuffix("" + OddJob.getInstance().getEconManager().getPocketBalance(player.getUniqueId()));
                objective.getScore(ChatColor.GOLD + "Wallet: " + ChatColor.WHITE).setScore(13);

                // Break
                objective.getScore("            ").setScore(12);

                // Topic - Location
                objective.getScore(ChatColor.BOLD + "Location:").setScore(11);

                // Content - Coords
                Team coords;
                coords = scoreboard.getTeam("co" + unique);
                if (coords == null) coords = scoreboard.registerNewTeam("co" + unique);
                coords.addEntry(ChatColor.GOLD + "Chunk: " + ChatColor.WHITE);
                coords.setSuffix("X=" + player.getLocation().getChunk().getX() + " Z=" + player.getLocation().getChunk().getZ());
                objective.getScore(ChatColor.GOLD + "Chunk: " + ChatColor.WHITE).setScore(10);

                // Content - CG
                Team cg;
                cg = scoreboard.getTeam("cg" + unique);
                if (cg == null) cg = scoreboard.registerNewTeam("cg" + unique);
                cg.addEntry(ChatColor.GOLD + "GUILD: " + ChatColor.WHITE);
                cg.setSuffix(OddJob.getInstance().getGuildManager().getGuildNameByUUID(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk())));
                objective.getScore(ChatColor.GOLD + "GUILD: " + ChatColor.WHITE).setScore(9);

                // Break
                objective.getScore("          ").setScore(5);

                objective.getScore(ChatColor.BOLD + "Community").setScore(4);
                objective.getScore(ChatColor.BLUE + "Facebook: " + ChatColor.WHITE + "@spillhusetminecraft").setScore(3);
                objective.getScore(ChatColor.RED + "Discord:   " + ChatColor.WHITE + "ZPQVHKA").setScore(2);
                objective.getScore(ChatColor.DARK_GREEN + "IP:          " + ChatColor.WHITE + "mine-craft.no").setScore(1);

                Team finalPlayerBank = playerBank;
                Team finalPlayerWallet = playerWallet;
                Team finalCoords = coords;
                Team finalCg = cg;
                taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(OddJob.getInstance(), new Runnable() {
                    final ScoreBoard scoreBoard = new ScoreBoard(player.getUniqueId());

                    @Override
                    public void run() {
                        if (!scoreBoard.hasID()) scoreBoard.setID(taskID);
                        finalPlayerBank.setSuffix("" + OddJob.getInstance().getEconManager().getBankBalance(player.getUniqueId(), false));
                        finalPlayerWallet.setSuffix("" + OddJob.getInstance().getEconManager().getPocketBalance(player.getUniqueId()));
                        finalCoords.setSuffix("X=" + player.getLocation().getChunk().getX() + " Z=" + player.getLocation().getChunk().getZ());
                        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk());
                        ChatColor color = OddJob.getInstance().getGuildManager().color(OddJob.getInstance().getGuildManager().getZoneByGuild(guild));
                        finalCg.setSuffix(color + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild));
                    }
                }, 0, 10);
                break;
            case Guild:
                objective.setDisplayName("Guild - " + ChatColor.BOLD + OddJob.getInstance().getGuildManager().getGuildNameByUUID(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId())));
                Guild guild = OddJob.getInstance().getGuildManager().getGuild(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()));

                // Content - Guild Role
                Team guildRole;
                guildRole = scoreboard.getTeam("gr" + unique);
                if (guildRole == null) guildRole = scoreboard.registerNewTeam("gr" + unique);
                guildRole.addEntry(ChatColor.GOLD + "Role: " + ChatColor.WHITE);
                guildRole.setSuffix("" + OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId()).name());
                objective.getScore(ChatColor.GOLD + "Role: " + ChatColor.WHITE).setScore(15);

                // Content - Guild Online
                Team guildOnline;
                guildOnline = scoreboard.getTeam("go" + unique);
                if (guildOnline == null) guildOnline = scoreboard.registerNewTeam("go" + unique);
                guildOnline.addEntry(ChatColor.GOLD + "Online: " + ChatColor.WHITE);
                guildOnline.setSuffix("" + OddJob.getInstance().getGuildManager().getOnline(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId())) + "/" + OddJob.getInstance().getGuildManager().getGuildMembers(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId())).size());
                objective.getScore(ChatColor.GOLD + "Online: " + ChatColor.WHITE).setScore(13);

                Team guildBank;
                guildBank = scoreboard.getTeam("gb" + unique);
                if (guildBank == null) guildBank = scoreboard.registerNewTeam("gb" + unique);
                guildBank.addEntry(ChatColor.GOLD + "Bank: " + ChatColor.WHITE);
                guildBank.setSuffix("" + OddJob.getInstance().getEconManager().getBankBalance(guild.getGuildUUID(), true));
                objective.getScore(ChatColor.GOLD + "Bank: " + ChatColor.WHITE).setScore(14);

                // Break
                objective.getScore("            ").setScore(9);

                // Topic - Location
                objective.getScore(ChatColor.BOLD + "Location:").setScore(8);

                // Content - Coords
                coords = scoreboard.getTeam("co" + unique);
                if (coords == null) coords = scoreboard.registerNewTeam("co" + unique);
                coords.addEntry(ChatColor.GOLD + "Chunk: " + ChatColor.WHITE);
                coords.setSuffix("X=" + player.getLocation().getChunk().getX() + " Z=" + player.getLocation().getChunk().getZ());
                objective.getScore(ChatColor.GOLD + "Chunk: " + ChatColor.WHITE).setScore(7);

                // Content - CG
                cg = scoreboard.getTeam("cg" + unique);
                if (cg == null) cg = scoreboard.registerNewTeam("cg" + unique);
                cg.addEntry(ChatColor.GOLD + "GUILD: " + ChatColor.WHITE);
                UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk());
                ChatColor color = OddJob.getInstance().getGuildManager().color(OddJob.getInstance().getGuildManager().getZoneByGuild(guildUUID));
                cg.setSuffix(color + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guildUUID));
                objective.getScore(ChatColor.GOLD + "GUILD: " + ChatColor.WHITE).setScore(6);

                finalCoords = coords;
                Team finalGuildBank = guildBank;
                finalCg = cg;
                Team finalGuildRole = guildRole;
                Team finalGuildOnline = guildOnline;
                taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(OddJob.getInstance(), new Runnable() {
                    final ScoreBoard scoreBoard = new ScoreBoard(player.getUniqueId());

                    @Override
                    public void run() {
                        if (!scoreBoard.hasID()) scoreBoard.setID(taskID);
                        finalGuildRole.setSuffix("" + OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId()).name());
                        finalGuildOnline.setSuffix("" + OddJob.getInstance().getGuildManager().getOnline(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId())) + "/" + OddJob.getInstance().getGuildManager().getGuildMembers(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId())).size());
                        finalGuildBank.setSuffix("" + OddJob.getInstance().getEconManager().getBankBalance(guild.getGuildUUID(), true));
                        finalCoords.setSuffix("X=" + player.getLocation().getChunk().getX() + " Z=" + player.getLocation().getChunk().getZ());
                        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk());
                        ChatColor color = OddJob.getInstance().getGuildManager().color(OddJob.getInstance().getGuildManager().getZoneByGuild(guild));
                        finalCg.setSuffix(color + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild));
                    }
                }, 0, 10);
                break;
            default:
                clear(player);
                break;
        }

        // Break
        objective.getScore("          ").setScore(5);

        objective.getScore(ChatColor.BOLD + "Community").setScore(4);
        objective.getScore(ChatColor.BLUE + "Facebook: " + ChatColor.WHITE + "@spillhusetminecraft").setScore(3);
        objective.getScore(ChatColor.RED + "Discord:   " + ChatColor.WHITE + "ZPQVHKA").setScore(2);
        objective.getScore(ChatColor.DARK_GREEN + "IP:          " + ChatColor.WHITE + "mine-craft.no").setScore(1);

    }
}
