package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.ScoreBoard;
import no.helponline.Utils.Enum.Zone;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import javax.management.openmbean.TabularDataSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class ScoreManager {
    private ScoreboardManager scoreboardManager;
    private Scoreboard scoreboard;
    private boolean enabled = false;
    private HashMap<UUID,BukkitRunnable> scores = new HashMap<>();

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
        final String unique = playerUUID.toString().substring(0, 8);

        final UUID playerGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(playerUUID);
        Team zone, guild, playerPocket, playerBank, guildBank, rank, members;
        Objective objective = null;

        // Make or Get Scoreboard
        objective = scoreboard.getObjective("guild-" + unique);
        if (objective == null) {
            objective = scoreboard.registerNewObjective("guild-" + unique, "dummy", OddJob.getInstance().getGuildManager().getGuildNameByUUID(playerGuild), RenderType.INTEGER);
        }

        zone = scoreboard.getTeam("zone-" + unique);
        if (zone == null) zone = scoreboard.registerNewTeam("zone-" + unique);
        zone.addEntry(ChatColor.GOLD + "Zone: ");
        zone.setSuffix(OddJob.getInstance().getGuildManager().getZoneByGuild(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk())).name());

        guild = scoreboard.getTeam("guild-" + unique);
        if (guild == null) guild = scoreboard.registerNewTeam("guild-" + unique);
        guild.addEntry(ChatColor.GOLD + "Guild: ");
        guild.setSuffix(OddJob.getInstance().getGuildManager().getGuildNameByUUID(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk())));

        playerPocket = scoreboard.getTeam("pPocket-" + unique);
        if (playerPocket == null)playerPocket = scoreboard.registerNewTeam("pPocket-" + unique);
        playerPocket.addEntry(ChatColor.GOLD + "Player-Pocket: ");
        playerPocket.setSuffix(String.valueOf(OddJob.getInstance().getEconManager().getPocketBalance(player.getUniqueId())));

        playerBank = scoreboard.registerNewTeam("pBank-" + unique);
        playerBank.addEntry(ChatColor.GOLD + "Player-Bank: ");
        playerBank.setSuffix(String.valueOf(OddJob.getInstance().getEconManager().getBankBalance(player.getUniqueId(), false)));

        guildBank = scoreboard.registerNewTeam("gBank-" + unique);
        guildBank.addEntry(ChatColor.GOLD + "Guild-Bank: ");
        guildBank.setSuffix(String.valueOf(OddJob.getInstance().getEconManager().getBankBalance(playerGuild, true)));

        rank = scoreboard.registerNewTeam("role-" + unique);
        rank.addEntry(ChatColor.GOLD + "Role: ");
        rank.setSuffix(OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId()).name());

        Set<UUID> mem = OddJob.getInstance().getGuildManager().getGuildMembers(playerGuild);
        int i = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (mem.contains(p.getUniqueId())) {
                i++;
            }
        }

        members = scoreboard.registerNewTeam("members-" + unique);
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
        scores.put(player.getUniqueId(), (BukkitRunnable) new BukkitRunnable() {
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
                rank.setSuffix(ChatColor.WHITE + OddJob.getInstance().getGuildManager().getGuildMemberRole(playerUUID).name());
                members.setSuffix(ChatColor.WHITE + "" + i + "/" + mem.size());

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
