package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.ScoreBoard;
import no.helponline.Utils.Enum.Zone;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class ScoreManager {
    private ScoreboardManager scoreboardManager;
    private Scoreboard scoreboard;
    private boolean enabled = false;
    private HashMap<UUID, BukkitTask> scores = new HashMap<>();

    public ScoreManager() {
        scoreboardManager = OddJob.getInstance().getServer().getScoreboardManager();
        if (scoreboardManager != null) {
            if (scoreboard == null) {
                scoreboard = scoreboardManager.getNewScoreboard();
            }
            enabled = true;
        }
    }

    public void clear(Player player) {
        if (scores.containsKey(player.getUniqueId())) scores.get(player.getUniqueId()).cancel();
        scores.remove(player.getUniqueId());

        player.setScoreboard(scoreboardManager.getMainScoreboard());
    }

    public void guild(Player player) {
        final UUID playerUUID = player.getUniqueId();
        final String pUnique = playerUUID.toString().substring(0, 8);
        final UUID playerGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(playerUUID);
        final String gUnique = playerGuild.toString().substring(0,8);

        Team zone, guild, playerPocket, playerBank, guildBank, rank, members;
        Objective objective = null;

        // Make or Get Scoreboard
        objective = scoreboard.getObjective("guild-" + pUnique);
        if (objective == null) {
            objective = scoreboard.registerNewObjective("guild-" + pUnique, "dummy", OddJob.getInstance().getGuildManager().getGuildNameByUUID(playerGuild), RenderType.INTEGER);
        }

        zone = scoreboard.getTeam("zone-" + pUnique);
        if (zone == null) zone = scoreboard.registerNewTeam("zone-" + pUnique);
        zone.addEntry(ChatColor.GOLD + "Zone: ");
        zone.setSuffix(OddJob.getInstance().getGuildManager().getZoneByGuild(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk())).name());

        guild = scoreboard.getTeam("guild-" + gUnique);
        if (guild == null) guild = scoreboard.registerNewTeam("guild-" + gUnique);
        guild.addEntry(ChatColor.GOLD + "Guild: ");
        guild.setSuffix(OddJob.getInstance().getGuildManager().getGuildNameByUUID(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk())));

        playerPocket = scoreboard.getTeam("pPocket-" + pUnique);
        if (playerPocket == null) playerPocket = scoreboard.registerNewTeam("pPocket-" + pUnique);
        playerPocket.addEntry(ChatColor.GOLD + "Player-Pocket: ");
        playerPocket.setSuffix(String.valueOf(OddJob.getInstance().getEconManager().getPocketBalance(player.getUniqueId())));

        playerBank = scoreboard.getTeam("pBank-" + pUnique);
        if (playerBank == null) playerBank = scoreboard.registerNewTeam("pBank-" + pUnique);
        playerBank.addEntry(ChatColor.GOLD + "Player-Bank: ");
        playerBank.setSuffix(String.valueOf(OddJob.getInstance().getEconManager().getBankBalance(player.getUniqueId(), false)));

        guildBank = scoreboard.getTeam("gBank-" + gUnique);
        if (guildBank == null) guildBank = scoreboard.registerNewTeam("gBank-" + gUnique);
        guildBank.addEntry(ChatColor.GOLD + "Guild-Bank: ");
        guildBank.setSuffix(String.valueOf(OddJob.getInstance().getEconManager().getBankBalance(playerGuild, true)));

        rank = scoreboard.getTeam("role-" + pUnique);
        if (rank == null) rank = scoreboard.registerNewTeam("role-" + pUnique);
        rank.addEntry(ChatColor.GOLD + "Role: ");
        rank.setSuffix(OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId()).name());

        Set<UUID> mem = OddJob.getInstance().getGuildManager().getGuildMembers(playerGuild);
        int i = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (mem.contains(p.getUniqueId())) {
                i++;
            }
        }

        members = scoreboard.getTeam("members-" + gUnique);
        OddJob.getInstance().log("Members "+members);
        if (members == null) members = scoreboard.registerNewTeam("members-" + gUnique);
        members.addEntry(ChatColor.GOLD + "Members: ");
        members.setSuffix(i + "/" + mem.size());


        objective.setDisplayName(OddJob.getInstance().getGuildManager().getGuildNameByUUID(playerGuild));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore(ChatColor.GOLD + "Members: ").setScore(15);
        objective.getScore(ChatColor.GOLD + "Role: ").setScore(14);
        objective.getScore(ChatColor.GOLD + "Guild-Bank: ").setScore(13);
        objective.getScore("   ").setScore(12);
        objective.getScore(ChatColor.BOLD + "Yours:").setScore(11);
        objective.getScore(ChatColor.GOLD + "Player-Pocket: ").setScore(10);
        objective.getScore(ChatColor.GOLD + "Player-Bank: ").setScore(9);
        objective.getScore("  ").setScore(6);
        objective.getScore(ChatColor.BOLD + "You are in area of:").setScore(5);
        objective.getScore(ChatColor.GOLD + "Zone: ").setScore(4);
        objective.getScore(ChatColor.GOLD + "Guild: ").setScore(3);
        objective.getScore(" ").setScore(2);
        objective.getScore(ChatColor.GREEN + "www.mine-craft.no").setScore(1);

        Team finalGuild = guild;
        Team finalZone = zone;
        Team finalRank = rank;
        Team finalMembers = members;
        scores.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) cancel();
                if (playerGuild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD)))
                    cancel();
                final UUID playerUUID = player.getUniqueId();

                if (scoreboard == null) return;

                Set<UUID> mem = OddJob.getInstance().getGuildManager().getGuildMembers(playerGuild);
                int i = 0;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (mem.contains(p.getUniqueId())) {
                        i++;
                    }
                }
                Chunk playerInChunk = player.getLocation().getChunk();
                UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(playerInChunk);
                if (guildUUID == null) guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD);
                String guildName = OddJob.getInstance().getGuildManager().getGuildNameByUUID(guildUUID);
                Zone inZone = OddJob.getInstance().getGuildManager().getZoneByGuild(guildUUID);
                String guildZone = inZone.name();

                finalGuild.setSuffix(ChatColor.WHITE + guildName);
                finalZone.setSuffix(ChatColor.WHITE + guildZone);
                finalRank.setSuffix(ChatColor.WHITE + OddJob.getInstance().getGuildManager().getGuildMemberRole(playerUUID).name());
                finalMembers.setSuffix(ChatColor.WHITE + "" + i + "/" + mem.size());

            }
        }.runTaskTimer(OddJob.getInstance(), 0, 10));

        player.setScoreboard(scoreboard);

    }

    public void set(UUID uuid, ScoreBoard scoreBoard) {
        Player player = OddJob.getInstance().getPlayerManager().getPlayer(uuid);
        switch (scoreBoard) {

            case Guild:
                guild(player);
                break;
            case None:
            default:
                none(player);
                break;

        }
    }

    public void none(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }
}
