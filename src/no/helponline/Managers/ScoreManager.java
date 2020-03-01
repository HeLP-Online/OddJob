package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class ScoreManager {
    private Scoreboard scoreboardGuild;
    private final HashMap<UUID, BukkitTask> scores = new HashMap<UUID, BukkitTask>();

    public ScoreManager() {
        ScoreboardManager scoreboardManager = OddJob.getInstance().getServer().getScoreboardManager();
        if (scoreboardManager != null) scoreboardGuild = scoreboardManager.getNewScoreboard();
    }

    public void guild(Player player) {
        UUID playerGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());

        Objective objective = scoreboardGuild.getObjective("guild-"+player.getUniqueId().toString().substring(0,8));
        //OddJob.getInstance().getMessageManager().console(objective.toString());

        Team zone;
        Team guild;
        Team playerEcon;
        Team guildEcon;
        Team rank;

        if (objective != null) {
            zone = scoreboardGuild.getTeam("zone");
            guild = scoreboardGuild.getTeam("guild");
            playerEcon = scoreboardGuild.getTeam("playerEcon");
            guildEcon = scoreboardGuild.getTeam("guildEcon");
            rank = scoreboardGuild.getTeam("rank");
        } else {
            objective = scoreboardGuild.registerNewObjective("guild-" + player.getUniqueId().toString().substring(0,8), player.getUniqueId().toString().substring(0,8), "Guild");

            zone = scoreboardGuild.registerNewTeam("zone");
            zone.addEntry("Zone: ");
            zone.setSuffix(OddJob.getInstance().getGuildManager().getZoneByGuild(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk(), player.getLocation().getWorld())).name());

            guild = scoreboardGuild.registerNewTeam("guild");
            guild.addEntry("Guild: ");
            guild.setSuffix(OddJob.getInstance().getGuildManager().getGuildNameByUUID(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk(), player.getLocation().getWorld())));

            playerEcon = scoreboardGuild.registerNewTeam("playerEcon");
            playerEcon.addEntry("PEcon: ");
            playerEcon.setSuffix(String.valueOf(OddJob.getInstance().getEconManager().getBalance(player.getUniqueId())));

            guildEcon = scoreboardGuild.registerNewTeam("guildEcon");
            guildEcon.addEntry("GEcon: ");
            guildEcon.setSuffix(String.valueOf(OddJob.getInstance().getEconManager().getBalance(playerGuild)));

            rank = scoreboardGuild.registerNewTeam("rank");
            rank.addEntry("Rank: ");
            rank.setSuffix(OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId()).name());

        }

        objective.setDisplayName(OddJob.getInstance().getGuildManager().getGuildNameByUUID(playerGuild));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore("Members: ").setScore(15);
        objective.getScore("Rank: ").setScore(14);
        objective.getScore("GEcon: ").setScore(13);
        objective.getScore("   ").setScore(12);
        objective.getScore(ChatColor.BOLD + "Yours:").setScore(11);
        objective.getScore("PEcon: ").setScore(10);
        objective.getScore("  ").setScore(6);
        objective.getScore(ChatColor.BOLD + "You are in area of:").setScore(5);
        objective.getScore("Zone: ").setScore(4);
        objective.getScore("Guild: ").setScore(3);
        objective.getScore(" ").setScore(2);
        objective.getScore("www.mine-craft.no").setScore(1);

        scores.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                if (guild != null)
                    guild.setSuffix(OddJob.getInstance().getGuildManager().getGuildNameByUUID(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk(), player.getLocation().getWorld())));
                if (zone != null)
                    zone.setSuffix(OddJob.getInstance().getGuildManager().getZoneByGuild(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk(), player.getLocation().getWorld())).name());

            }
        }.runTaskTimer(OddJob.getInstance(), 0, 10));

        player.setScoreboard(scoreboardGuild);
    }
}
