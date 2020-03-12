package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Zone;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class ScoreManager {
    private final ScoreboardManager scoreboardManager;
    private Scoreboard scoreboardGuild;
    public final HashMap<UUID, BukkitTask> scores = new HashMap<>();
    public final HashMap<UUID, Scoreboard> boards = new HashMap<>();

    public ScoreManager() {
        scoreboardManager = OddJob.getInstance().getServer().getScoreboardManager();

    }

    public void clear(Player player) {
        if (scores.containsKey(player.getUniqueId())) scores.get(player.getUniqueId()).cancel();
        scores.remove(player.getUniqueId());

        player.setScoreboard(scoreboardManager.getMainScoreboard());
    }

    public void guild(Player player) {
        UUID playerGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
        final UUID playerUUID = player.getUniqueId();
        if (!boards.containsKey(playerUUID)) {
            if (scoreboardManager != null) scoreboardGuild = scoreboardManager.getNewScoreboard();
        } else {
            scoreboardGuild = boards.get(playerUUID);
        }
        Objective objective = scoreboardGuild.getObjective("guild-" + player.getUniqueId().toString().substring(0, 8));
        //OddJob.getInstance().getMessageManager().console(objective.toString());

        Team zone, guild, playerEcon, guildEcon, rank, members;

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
            OddJob.getInstance().getMessageManager().console("chunk: " + player.getLocation().getChunk().toString());
            OddJob.getInstance().getMessageManager().console("world: " + player.getLocation().getWorld().getName());
            OddJob.getInstance().getMessageManager().console("guild-uuid: " + OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk()));
            zone.setSuffix(OddJob.getInstance().getGuildManager().getZoneByGuild(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk())).name());

            guild = scoreboardGuild.registerNewTeam("guild-" + player.getUniqueId().toString().substring(0, 8));
            guild.addEntry(ChatColor.GOLD + "Guild: ");
            guild.setSuffix(OddJob.getInstance().getGuildManager().getGuildNameByUUID(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk())));

            playerEcon = scoreboardGuild.registerNewTeam("pEcon-" + player.getUniqueId().toString().substring(0, 8));
            playerEcon.addEntry(ChatColor.GOLD + "Player-Money: ");
            playerEcon.setSuffix(String.valueOf(OddJob.getInstance().getEconManager().getBalance(player.getUniqueId())));

            guildEcon = scoreboardGuild.registerNewTeam("gEcon-" + player.getUniqueId().toString().substring(0, 8));
            guildEcon.addEntry(ChatColor.GOLD + "Guild-Money: ");
            guildEcon.setSuffix(String.valueOf(OddJob.getInstance().getEconManager().getBalance(playerGuild)));

            rank = scoreboardGuild.registerNewTeam("rank-" + player.getUniqueId().toString().substring(0, 8));
            rank.addEntry(ChatColor.GOLD + "Role: ");
            rank.setSuffix(OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId()).name());

            Set<UUID> mem = OddJob.getInstance().getGuildManager().getGuildMembers(playerGuild);
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
                if (!player.isOnline()) cancel();
                if (playerGuild == null || playerGuild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD)))
                    cancel();
                UUID playerUUID = player.getUniqueId();
                if (boards.containsKey(playerUUID)) scoreboardGuild = boards.get(playerUUID);

                Team zzone = scoreboardGuild.getTeam("zone-" + playerUUID.toString().substring(0, 8));
                Team gguild = scoreboardGuild.getTeam("guild-" + playerUUID.toString().substring(0, 8));
                //playerEcon = scoreboardGuild.getTeam("pEcon-" + playerUUID.toString().substring(0, 8)); // TODO
                //guildEcon = scoreboardGuild.getTeam("gEcon-" + playerUUID.toString().substring(0, 8));   // TODO
                Team rrank = scoreboardGuild.getTeam("rank-" + playerUUID.toString().substring(0, 8));
                Team mmembers = scoreboardGuild.getTeam("members-" + playerUUID.toString().substring(0, 8));
                Set<UUID> mem = OddJob.getInstance().getGuildManager().getGuildMembers(playerGuild);
                int i = 0;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (mem.contains(p.getUniqueId())) {
                        i++;
                    }
                }
                if (gguild != null)
                    gguild.setSuffix(ChatColor.WHITE + OddJob.getInstance().getGuildManager().getGuildNameByUUID(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk())));
                if (zzone != null)
                    zzone.setSuffix(ChatColor.WHITE + OddJob.getInstance().getGuildManager().getZoneByGuild(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk())).name());
                if (rrank != null)
                    rrank.setSuffix(ChatColor.WHITE + OddJob.getInstance().getGuildManager().getGuildMemberRole(playerUUID).name());
                if (mmembers != null)
                    mmembers.setSuffix(ChatColor.WHITE + "" + i + "/" + mem.size());

            }
        }.runTaskTimer(OddJob.getInstance(), 0, 10));
        boards.put(playerUUID, scoreboardGuild);
        player.setScoreboard(scoreboardGuild);

    }
}
