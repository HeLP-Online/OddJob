package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ScoreManager {
    private Scoreboard scoreboardGuild;
    private final HashMap<UUID, BukkitTask> scores = new HashMap<>();

    public ScoreManager() {
        ScoreboardManager scoreboardManager = OddJob.getInstance().getServer().getScoreboardManager();
        if (scoreboardManager != null) scoreboardGuild = scoreboardManager.getNewScoreboard();
    }

    public void guild(Player player) {
        UUID playerGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());

        Objective objective = scoreboardGuild.getObjective("guild-" + player.getUniqueId().toString().substring(0, 8));
        //OddJob.getInstance().getMessageManager().console(objective.toString());

        Team zone,guild,playerEcon,guildEcon,rank,members;

        if (objective != null) {
            zone = scoreboardGuild.getTeam("zone-" + player.getUniqueId().toString().substring(0, 8));
            guild = scoreboardGuild.getTeam("guild-" + player.getUniqueId().toString().substring(0, 8));
            playerEcon = scoreboardGuild.getTeam("pEcon-" + player.getUniqueId().toString().substring(0, 8)); // TODO
            guildEcon = scoreboardGuild.getTeam("gEcon-" + player.getUniqueId().toString().substring(0, 8));   // TODO
            rank = scoreboardGuild.getTeam("rank-" + player.getUniqueId().toString().substring(0, 8));
            members = scoreboardGuild.getTeam("members-" + player.getUniqueId().toString().substring(0, 8));
        } else {
            objective = scoreboardGuild.registerNewObjective("guild-" + player.getUniqueId().toString().substring(0, 8), player.getUniqueId().toString().substring(0, 8), "Guild");

            zone = scoreboardGuild.registerNewTeam("zone-" + player.getUniqueId().toString().substring(0, 8));
            zone.addEntry(ChatColor.GOLD + "Zone: ");
            zone.setSuffix(OddJob.getInstance().getGuildManager().getZoneByGuild(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk(), player.getLocation().getWorld())).name());

            guild = scoreboardGuild.registerNewTeam("guild-" + player.getUniqueId().toString().substring(0, 8));
            guild.addEntry(ChatColor.GOLD + "Guild: ");
            guild.setSuffix(OddJob.getInstance().getGuildManager().getGuildNameByUUID(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk(), player.getLocation().getWorld())));

            playerEcon = scoreboardGuild.registerNewTeam("pEcon-" + player.getUniqueId().toString().substring(0, 8));
            playerEcon.addEntry(ChatColor.GOLD + "Player-Money: ");
            playerEcon.setSuffix(String.valueOf(OddJob.getInstance().getEconManager().getBalance(player.getUniqueId())));

            guildEcon = scoreboardGuild.registerNewTeam("gEcon-" + player.getUniqueId().toString().substring(0, 8));
            guildEcon.addEntry(ChatColor.GOLD + "Guild-Money: ");
            guildEcon.setSuffix(String.valueOf(OddJob.getInstance().getEconManager().getBalance(playerGuild)));

            rank = scoreboardGuild.registerNewTeam("rank-" + player.getUniqueId().toString().substring(0, 8));
            rank.addEntry(ChatColor.GOLD + "Role: ");
            rank.setSuffix(OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId()).name());

            List<UUID> mem = OddJob.getInstance().getGuildManager().getGuildMembers(playerGuild);
            int i = 0;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (mem.contains(p.getUniqueId())) {
                    i++;
                }
            }

            members = scoreboardGuild.registerNewTeam("members-" + player.getUniqueId().toString().substring(0, 8));
            members.addEntry(ChatColor.GOLD + "Members: ");
            members.setSuffix(i + "/" + mem.size());

        }

        objective.setDisplayName(OddJob.getInstance().getGuildManager().getGuildNameByUUID(playerGuild));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore(ChatColor.GOLD + "Members: ").setScore(15);
        objective.getScore(ChatColor.GOLD + "Role: ").setScore(14);
        objective.getScore(ChatColor.GOLD + "Guild-Money: ").setScore(13);
        objective.getScore("   ").setScore(12);
        objective.getScore(ChatColor.BOLD + "Yours:").setScore(11);
        objective.getScore(ChatColor.GOLD + "Player-Money: ").setScore(10);
        objective.getScore("  ").setScore(6);
        objective.getScore(ChatColor.BOLD + "You are in area of:").setScore(5);
        objective.getScore(ChatColor.GOLD + "Zone: ").setScore(4);
        objective.getScore(ChatColor.GOLD + "Guild: ").setScore(3);
        objective.getScore(" ").setScore(2);
        objective.getScore(ChatColor.GREEN + "www.mine-craft.no").setScore(1);

        scores.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                List<UUID> mem = OddJob.getInstance().getGuildManager().getGuildMembers(playerGuild);
                int i = 0;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (mem.contains(p.getUniqueId())) {
                        i++;
                    }
                }
                if (guild != null)
                    guild.setSuffix(ChatColor.WHITE + OddJob.getInstance().getGuildManager().getGuildNameByUUID(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk(), player.getLocation().getWorld())));
                if (zone != null)
                    zone.setSuffix(ChatColor.WHITE + OddJob.getInstance().getGuildManager().getZoneByGuild(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk(), player.getLocation().getWorld())).name());
                if (rank != null)
                    rank.setSuffix(ChatColor.WHITE + OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId()).name());
                if (members != null)
                    members.setSuffix(ChatColor.WHITE + "" + i + "/" + mem.size());

            }
        }.runTaskTimer(OddJob.getInstance(), 0, 10));

        player.setScoreboard(scoreboardGuild);
    }
}
