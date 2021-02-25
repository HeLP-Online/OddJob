package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Guild;
import no.helponline.Utils.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class MessageManager {
    ChatColor cDanger = ChatColor.RED;
    ChatColor cWarning = ChatColor.YELLOW;
    ChatColor cSuccess = ChatColor.GREEN;
    ChatColor cInfo = ChatColor.AQUA;

    ChatColor cGuild = ChatColor.DARK_AQUA;
    ChatColor cPlayer = ChatColor.GOLD;
    ChatColor cValue = ChatColor.GRAY;

    public void success(String message, CommandSender sender, boolean console) {
        sender.sendMessage(ChatColor.GREEN + message);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.GREEN + message);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + message);
        }
    }

    public void success(String message, UUID sender, boolean console) {
        Player player = Bukkit.getPlayer(sender);
        if (player != null) {
            player.sendMessage(ChatColor.GREEN + message);
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.GREEN + message);
        }
    }

    public void warning(String message, CommandSender sender, boolean console) {
        sender.sendMessage(ChatColor.YELLOW + message);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.YELLOW + message);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + message);
        }
    }

    public void warning(String message, UUID sender, boolean console) {
        Player player = Bukkit.getPlayer(sender);
        if (player != null) {
            player.sendMessage(ChatColor.YELLOW + message);
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.YELLOW + message);
        }
    }

    public void danger(String message, CommandSender sender, boolean console) {
        sender.sendMessage(ChatColor.RED + message);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.RED + message);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + message);
        }
    }

    public void danger(String message, UUID sender, boolean console) {
        Player player = Bukkit.getPlayer(sender);
        if (player != null) {
            player.sendMessage(ChatColor.RED + message);
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.RED + message);
        }
    }

    public void console(String s) {
        Bukkit.getConsoleSender().sendMessage(s);
    }

    public void info(String message, CommandSender sender, boolean console) {
        sender.sendMessage(ChatColor.DARK_AQUA + message);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.DARK_AQUA + message);
        } else {
            if (console) Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + message);
        }
    }

    public void info(String message, UUID sender, boolean console) {
        Player player = Bukkit.getPlayer(sender);
        if (player != null) {
            player.sendMessage(ChatColor.DARK_AQUA + message);
            if (console) Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + ChatColor.DARK_AQUA + message);
        }
    }

    public void errorPlayer(String string, CommandSender commandSender) {
        warning("Sorry we can't find the player: " + string, commandSender, false);
    }

    public void errorWorld(String string, CommandSender commandSender) {
        warning("Sorry we can't find the world: " + string, commandSender, false);
    }

    public void broadcastAchievement(String string) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            pl.sendMessage(string);
        }
    }

    public void broadcast(String s) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(s);
        }
    }

    public void guild(String s, UUID guild, UUID sender) {
        for (UUID uuid : OddJob.getInstance().getGuildManager().getGuildMembers(guild)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline() && player.getUniqueId() != sender) {
                player.sendMessage(s);
            }
        }
    }

    public void errorGuild(String name, UUID player) {
        danger("Sorry, we can't find the guild " + ChatColor.GOLD + name, player, false);
    }
    public void errorArena(String name, UUID player) {
        danger("Sorry, we can't find the arena " + ChatColor.GOLD + name, player, false);
    }

    public void insufficientItems(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Insufficient number of items.");
    }

    public void insufficientFunds(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Insufficient funds.");
    }

    public void errorConsole() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Only usable as a player");
    }

    public void errorMaterial(String string, Player player) {
        player.sendMessage(ChatColor.RED + "Unknown material '" + ChatColor.YELLOW + string + ChatColor.RED + "'");
    }

    public void errorNumber(String string, Player player) {
        player.sendMessage(ChatColor.RED + "Invalid number '" + ChatColor.YELLOW + string + ChatColor.RED + "'");
    }

    public void errorHome(String name, CommandSender player) {
        player.sendMessage(ChatColor.RED + "Unknown home '" + ChatColor.YELLOW + name + ChatColor.RED + "'");
    }

    public void infoListPlayers(String string, Collection<UUID> list, CommandSender commandSender) {
        StringBuilder builder = new StringBuilder();
        builder.append(string).append("\n");
        builder.append("---------------------\n");
        int i = 0;
        for (UUID uuid : list) {
            builder.append(i).append(".) ").append(OddJob.getInstance().getPlayerManager().getName(uuid)).append("\n");
        }
        commandSender.sendMessage(builder.toString());
    }

    public void infoHashmap(String string, HashMap<String, String> info, CommandSender commandSender) {
        StringBuilder builder = new StringBuilder();
        builder.append(string).append("\n");
        builder.append("---------------------\n");
        for (String st : info.keySet()) {
            builder.append(st).append(" ").append(info.get(st)).append("\n");
        }
        commandSender.sendMessage(builder.toString());
    }

    public void infoListWarps(String string, HashMap<String, Warp> warps, Player player) {
        StringBuilder builder = new StringBuilder();
        builder.append(string).append("\n");
        builder.append("---------------------\n");
        int i = 0;
        for (String st : warps.keySet()) {
            String name = warps.get(st).getName();
            ChatColor color = (player.hasPermission("oddjob.warps." + name)) ? ChatColor.GREEN : ChatColor.RED;
            builder.append(i).append(".) ").append(color).append(name);
            if (warps.get(st).hasPassword()) builder.append("*");
            builder.append("\n");
        }
        player.sendMessage(builder.toString());
    }

    public void save(String string, int i, int u) {
        console("Saved " + string + ": inserted:" + i + "; updated:" + u + ";");
    }

    public void load(String string, int l) {
        console("Loaded " + string + ": " + l);
    }

    public void update(String string, int u) {
        console("Updated " + string + ": " + u);
    }

    public void listHomes(String string, Set<String> list, Player player) {
        StringBuilder builder = new StringBuilder();
        builder.append(string).append("\n");
        builder.append("---------------------\n");
        for (String name : list) {
            builder.append(name).append("\n");
        }
        player.sendMessage(builder.toString());
    }

    public void homesSetSuccess(String name, UUID uuid) {
        success("Home " + ChatColor.GOLD + name + ChatColor.GREEN + " successfully set", uuid, true);
    }

    public void homesInsideGuild(UUID uuid) {
        danger("Home set failed, you are inside another guild.", uuid, false);
    }

    public void homesDelSuccess(String name, UUID uuid) {
        warning("Home " + ChatColor.GOLD + name + ChatColor.YELLOW + " successfully deleted", uuid, true);
    }

    public void homesTeleportSuccess(UUID uuid, String name) {
        success("Teleport to " + ChatColor.GOLD + name + ChatColor.GREEN + " successful", uuid, true);
    }

    /******/
    public void guildTooFewArguments(UUID uuid) {
        danger("Too few arguments supplied.", uuid, false);
    }

    public void guildTooManyArguments(UUID uuid) {
        danger("Too many arguments supplied.", uuid, false);
    }

    public void guildNameAlreadyExsits(String string, UUID uuid) {
        danger("A Guild with the name " + cGuild + string + cDanger + " already exists.", uuid, false);
    }

    public void guildAlreadyAssociated(String string, UUID uuid) {
        danger("You are already associated with the Guild " + cGuild + string, uuid, false);
    }

    public void guildCreateSuccessful(String string, UUID uuid) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(uuid))
                success("You have successfully created the Guild " + cGuild + string, uuid, true);
            else
                warning("A new Guild name " + cGuild + string + cWarning + " has been created by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), player, false);
        }
    }

    public void guildCreateError(String string, UUID uuid) {
        danger("Something went wrong when trying to create a Guild with the name " + cValue + string, uuid, true);
    }

    public void guildNotAssociated(UUID uuid) {
        warning("You are not associated with any Guild yet.", uuid, false);
    }

    public void guildLeaveSuccessful(Guild guild, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (uuid.equals(member))
                success("You have successfully left the Guild " + cGuild + guild.getName(), member, true);
            else
                warning(cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid) + cWarning + " has left the Guild", uuid, false);
        }
    }

    public void guildDisband(String string) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            warning("Noone left in the Guilld " + cGuild + string + cWarning + ", disbanding", player.getUniqueId(), false);
        }
    }

    public void guildNoInvitation(UUID uuid) {
        warning("You have no pending Guild invites.", uuid, false);
    }

    public void guildListPending(String string, List<UUID> list, UUID uuid) {
        StringBuilder builder = new StringBuilder();
        builder.append(string).append("\n");
        builder.append("---------------------\n");
        for (UUID pend : list) {
            builder.append(cPlayer).append(OddJob.getInstance().getPlayerManager().getName(pend)).append("\n");
        }
        OddJob.getInstance().getPlayerManager().getPlayer(uuid).sendMessage(builder.toString());
    }

    public void guildNoPending(UUID uuid) {
        warning("There is no pending requests to join the Guild.", uuid, false);
    }

    public void guildAlreadyInvited(String string, UUID uuid) {
        warning(cPlayer + string + cWarning + " is already invited to the Guild.", uuid, false);
    }

    public void guildAnotherInvite(String string, UUID uuid) {
        danger(cPlayer + string + cDanger + " is already invited to another Guild.", uuid, false);
    }

    public void guildAlreadyPending(String string, UUID uuid) {
        warning(cPlayer + string + cWarning + " has already requested to join the Guild.", uuid, false);
    }

    public void guildAnotherPending(String string, UUID uuid) {
        danger(cPlayer + string + cDanger + " has already requested to join another Guild.", uuid, false);
    }

    public void guildAlreadyJoined(String string, UUID uuid) {
        warning(cPlayer + string + cWarning + " is already a member of the Guild.", uuid, false);
    }

    public void guildAnotherJoined(String string, UUID uuid) {
        danger(cPlayer + string + cDanger + " is already a member of another Guild.", uuid, false);
    }

    public void guildRoleNeeded(UUID uuid) {
        warning("Your role has not permission to perform this action.", uuid, false);
    }

    public void guildInvitedToGuild(Guild guild, UUID player, UUID sender) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(sender))
                success("You have successfully invited " + cPlayer + OddJob.getInstance().getPlayerManager().getName(player) + cSuccess + " to the Guild.", member, true);
            else
                success((cPlayer + OddJob.getInstance().getPlayerManager().getName(player) + cSuccess) + " has been invited to the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(sender), member, false);
        }
        success("You have been invited to the Guild " + cGuild + guild.getName(), player, false);
    }

    public void guildNotInvited(String string, UUID uuid) {
        danger(cValue + string + cDanger + " were never invited to the Guild", uuid, false);
    }

    public void guildUninvited(Guild guild, UUID invited, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success("You have successfully removed invitation of " + cPlayer + OddJob.getInstance().getPlayerManager().getPlayer(invited) + cSuccess + " to the Guild.", member, true);
            else
                warning((cPlayer + OddJob.getInstance().getPlayerManager().getName(invited) + cWarning) + " is no longer invited to the Guild, removed by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
        danger("Your invitation to the Guild " + cGuild + guild.getName() + cDanger + " has been revoked.", invited, false);
    }

    public void guildNotSame(UUID target, UUID uuid) {
        danger("You and " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cDanger + " is not in the same Guild.", uuid, false);
    }

    public void guildRoleHighest(UUID target, UUID uuid) {
        warning(cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " has already the highest obtainable rank after GuildMaster.", uuid, false);
    }

    public void guildRoleHigher(UUID target, UUID uuid) {
        warning(cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " has the same or higher rank than you.", uuid, false);
    }

    public void guildPromoted(Guild guild, UUID target, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success("You have successfully promoted " + cPlayer + OddJob.getInstance().getPlayerManager().getPlayer(target) + cSuccess + " to " + guild.getMembers().get(target).name() + " in the Guild.", member, true);
            else if (target.equals(member))
                success("You have been promoted to " + guild.getMembers().get(target).name() + " in the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), target, false);
            else
                warning((cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning) + " has been promoted to " + guild.getMembers().get(target).name() + " in the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
    }

    public void guildDemoted(Guild guild, UUID target, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success("You have successfully demoted " + cPlayer + OddJob.getInstance().getPlayerManager().getPlayer(target) + cSuccess + " to " + guild.getMembers().get(target).name() + " in the Guild.", member, true);
            else if (target.equals(member))
                danger("You have been demoted to " + guild.getMembers().get(target).name() + " in the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), target, false);
            else
                warning((cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning) + " has been demoted to " + guild.getMembers().get(target).name() + " in the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
    }

    public void guildRoleLowest(UUID target, UUID uuid) {
        warning(cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " has already the lowest obtainable rank.", uuid, false);
    }

    public void guildNeedMaster(UUID uuid) {
        danger("Sorry, but only the GuildMaster may transfer the role to a new Player.", uuid, false);
    }

    public void guildAcceptPending(Guild guild, UUID target, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success("You have accepted the request from " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cSuccess + " to join the Guild", member, true);
            if (member.equals(target))
                success("Welcome to the Guild " + cGuild + guild.getName(), member, true);
            else
                warning((cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning) + "'s request to join the Guild has been accepted by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
    }

    public void guildAcceptInvite(Guild guild, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success("You have accepted the invitation to join the Guild " + cGuild + guild.getName(), member, true);
            else
                warning(cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid) + cWarning + " has accepted the invitation to join the Guild", member, false);
        }
    }

    public void guildJoining(Guild guild, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success("Welcome to the Guild " + cGuild + guild.getName(), member, true);
            else
                success("Please welcome " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid) + cSuccess + " to the Guild", member, false);
        }
    }

    public void guildPending(Guild guild, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {

            warning(cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid) + cWarning + " has requested to join the Guild", member, false);
        }
        success("You have sent a request to join the Guild " + cGuild + guild.getName(), uuid, true);
    }

    public void guildDenyInvite(Guild guild, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            warning(cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid) + cWarning + "'s invitation to join the Guild has been declined.", member, false);
        }
        success("You have declined the invitation to join the Guild " + cGuild + guild.getName(), uuid, true);
    }

    public void guildDenyRequest(Guild guild, UUID target, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                warning("You have declined " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " request to join the Guild", member, true);
            else
                danger(cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid) + cDanger + " has denied " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + "'s request to join the Guild", member, false);
        }
        danger("We are sorry to announce that your request to join " + cGuild + guild.getName() + cDanger + " has been declined.", target, false);
    }

    public void guildSetConfirm(Guild guild, String key, String value, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success("You have changed " + cValue + key + cSuccess + " changed to " + cValue + value + cSuccess + " for the Guild", member, false);
            else
                warning("Settings for " + cValue + key + cWarning + " changed to " + cValue + value + cWarning + " by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
    }

    public void guildMap(String string, Player player) {
        player.sendMessage(string);
    }

    public void guildNewMaster(Guild guild, UUID target, UUID next) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(next))
                success("You are the Chosen One to be the new GuildMaster since " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cSuccess + " left.", member, true);
            else
                warning(cPlayer + OddJob.getInstance().getPlayerManager().getName(next) + cWarning + " is now your new GuildMaster.", member, false);
        }
    }

    public void guildKickPlayer(Guild guild, UUID target, UUID player, String reason) {
        String r = "";
        if (reason != null) r = " because " + cValue + reason;
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(player))
                success("You have successfully kick " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cSuccess + " from the Guild.", member, true);
            else
                warning(cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " has been kicked from the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(player) + cSuccess + r, member, false);
        }
        danger("You have been kicked from " + cGuild + guild.getName() + cDanger + " by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(player) + cDanger + r, target, false);
    }

    public void arenaSetSpawnTeleport(String s, UUID uniqueId) {
    }

    public void arenaSetSpawnRemove(String s, UUID uniqueId) {
    }

    public void arenaAreaSet(Player player) {
        success("Area for Arena set!",player.getUniqueId(),false);
    }
}
