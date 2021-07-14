package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Currency;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Guild;
import com.spillhuset.Utils.Odd.OddPlayer;
import com.spillhuset.Utils.Warp;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class MessageManager {
    ChatColor cDanger = ChatColor.RED;
    ChatColor cWarning = ChatColor.YELLOW;
    ChatColor cSuccess = ChatColor.GREEN;
    ChatColor cInfo = ChatColor.AQUA;

    ChatColor cCurrency = ChatColor.DARK_AQUA;
    ChatColor cGuild = ChatColor.DARK_AQUA;
    ChatColor cPlayer = ChatColor.GOLD;
    ChatColor cValue = ChatColor.GRAY;
    ChatColor cHome = ChatColor.GOLD;
    ChatColor cReset = ChatColor.RESET;
    ChatColor cWarp = ChatColor.DARK_AQUA;
    ChatColor cBan = ChatColor.DARK_AQUA;

    String tGuild = "[" + cGuild + "G" + cReset + "] ";
    String tWarp = "[" + cWarp + "W" + cReset + "] ";
    String tHome = "[" + cHome + "H" + cReset + "] ";
    String tPlayer = "[" + cPlayer + "P" + cReset + "] ";
    String tCurrency = "[" + cCurrency + "C" + cReset + "] ";
    String tBan = "[" + cBan + "B" + cReset + "] ";

    public String type(Plugin type) {
        return switch (type) {
            case ban -> tBan;
            case currency -> tCurrency;
            case home -> tHome;
            case warp -> tWarp;
            case guild -> tGuild;
            case player -> tPlayer;
            default -> "[*] ";
        };
    }

    public void console(String text) {
        Bukkit.getConsoleSender().sendMessage(text);
    }

    private void success(String type, String text, UUID sender, boolean console) {
        Player player = Bukkit.getPlayer(sender);
        if (player != null) {
            success(type, text, player, console);
        }
    }

    private void info(String type, String text, UUID sender) {
        Player player = Bukkit.getPlayer(sender);
        if (player != null) {
            info(type, text, player, false);
        }
    }

    public void danger(String type, String text, UUID sender, boolean console) {
        Player player = Bukkit.getPlayer(sender);
        if (player != null) {
            danger(type, text, player, console);
        }
    }

    public void warning(String type, String text, UUID sender, boolean console) {
        Player player = Bukkit.getPlayer(sender);
        if (player != null) {
            warning(type, text, player, console);
        }
    }

    public void success(String type, String text, CommandSender sender, boolean console) {
        if (sender instanceof Player player) {
            sender.sendMessage(type + cSuccess + text);
            if (console) console(player.getName() + ": " + cSuccess + text);
        } else {
            console(cSuccess + text);
        }
    }

    public void warning(String type, String text, CommandSender sender, boolean console) {
        if (sender instanceof Player player) {
            sender.sendMessage(type + cWarning + text);
            if (console) console(player.getName() + ": " + cWarning + text);
        } else {
            console(cWarning + text);
        }
    }

    public void info(String type, String text, CommandSender sender, boolean console) {
        if (sender instanceof Player player) {
            sender.sendMessage(type + cInfo + text);
            if (console) console(player.getName() + ": " + cInfo + text);
        } else {
            console(cInfo + text);
        }
    }

    public void danger(String type, String text, CommandSender sender, boolean console) {
        if (sender instanceof Player player) {
            sender.sendMessage(type + cDanger + text);
            if (console) console(player.getName() + ": " + cDanger + text);
        } else {
            console(cDanger + text);
        }
    }

    public void errorPlayer(Plugin type, String string, CommandSender commandSender) {
        danger(type(type), "Sorry we can't find the player: " + cValue + string, commandSender, false);
    }

    public void errorWorld(String string, CommandSender commandSender, Plugin type) {
        danger(type(type), "Sorry we can't find the world: " + cValue + string, commandSender, false);
    }

    public void errorGuild(String string, CommandSender sender) {
        danger(type(Plugin.guild), "Sorry, we can't find the guild: " + cValue + string, sender, false);
    }

    public void errorArena(String string, UUID player) {
        danger(type(Plugin.arena), "Sorry, we can't find the arena: " + cValue + string, player, false);
    }

    public void errorConsole(Plugin type) {
        console(type(type) + cDanger + "Only usable as a player");
    }

    public void errorMaterial(Plugin type, String string, CommandSender player) {
        danger(type(type), "Unknown material " + cValue + string, player, false);
    }

    public void errorNumber(Plugin type, String string, CommandSender sender) {
        danger(type(type), "Invalid number " + cValue + string, sender, false);
    }

    public void errorHome(String name, CommandSender sender) {
        danger(type(Plugin.home), "Unknown home " + cHome + name, sender, false);
    }

    //TODO broadcastAchievement
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

    public void broadcastGuild(String message, String guildName, List<UUID> players, CommandSender sender) {
        for (UUID uuid : players) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
            if (target.isOnline()) {
                Player player = (Player) target;
                guildBroadcast(message, guildName, player);
            }
        }
    }

    private void guildBroadcast(String message, String name, Player target) {
        target.sendMessage("[" + cGuild + "G" + cReset + " - " + cGuild + name + cReset + "] " + message);
    }


    public void insufficientItems(Player player) {
        warning(type(Plugin.shop), cWarning + "Insufficient number of items.", player, false);
    }

    public void insufficientFunds(CommandSender player) {
        player.sendMessage(tCurrency + cWarning + " Insufficient funds.");
    }

    public void insufficientFunds(UUID target, CommandSender player) {
        player.sendMessage(tCurrency + cWarning + " Insufficient funds.");
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

    public void infoListWarps(HashMap<UUID, Warp> warps, CommandSender sender) {
        if (warps.size() == 0) {
            warpsNotSet(sender);
            return;
        }

        info(type(Plugin.warp), "There " + ((warps.size() == 1) ? "is " : "are ") + cValue + warps.size() + cInfo + " warps", sender, false);
        space(Plugin.warp, sender);
        int i = 1;
        for (UUID uuid : warps.keySet()) {
            info(type(Plugin.warp), i + ".) " + cValue + warps.get(uuid).getName() + ((warps.get(uuid).hasPassword()) ? "*" : "") + ((warps.get(uuid).getCost() > 0) ? "$" : ""), sender, false);
            i++;
        }
    }

    private void warpsNotSet(CommandSender sender) {
        warning(type(Plugin.warp), "No warps set on this server!", sender, false);
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
        success(type(Plugin.home), "Successfully set home " + cHome + name + cSuccess, uuid, true);
    }

    public void homesInsideGuild(UUID uuid) {
        danger(type(Plugin.home), "Home set failed, you are inside another guild.", uuid, false);
    }

    public void homesDelSuccess(String name, UUID uuid) {
        warning(type(Plugin.home), "Home " + ChatColor.GOLD + name + ChatColor.YELLOW + " successfully deleted", uuid, true);
    }

    public void homesTeleportSuccess(UUID uuid, String name) {
        success(type(Plugin.home), "Teleport to " + ChatColor.GOLD + name + ChatColor.GREEN + " successful", uuid, true);
    }

    public void guildNameAlreadyExsits(String string, CommandSender sender) {
        danger(type(Plugin.guild), "A Guild with the name " + cGuild + string + cDanger + " already exists.", sender, false);
    }

    public void guildAlreadyAssociated(String string, CommandSender sender) {
        danger(type(Plugin.guild), "You are already associated with the Guild " + cGuild + string, sender, false);
    }


    public void guildCreateSuccessful(String string, CommandSender sender) {
        Player p = (Player) sender;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(p.getUniqueId()))
                success(type(Plugin.guild), "You have successfully created the Guild " + cGuild + string, sender, true);
            else
                warning(type(Plugin.guild), "A new Guild name " + cGuild + string + cWarning + " has been created by " + cPlayer + sender.getName(), player, false);
        }
    }

    public void guildCreateError(String string, CommandSender sender) {
        danger(type(Plugin.guild), "Something went wrong when trying to create a Guild with the name " + cValue + string, sender, true);
    }

    public void guildNotAssociated(UUID uuid) {
        info(type(Plugin.guild), "You are not associated with any Guild yet.", uuid);
    }


    public void guildLeaveSuccessful(Guild guild, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (uuid.equals(member))
                success(type(Plugin.guild), "You have successfully left the Guild " + cGuild + guild.getName(), member, true);
            else
                warning(type(Plugin.guild), cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid) + cWarning + " has left the Guild", uuid, false);
        }
    }

    public void guildDisband(UUID guild, CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()) == guild) {
                danger(type(Plugin.guild), cPlayer + sender.getName() + cDanger + " has disbanded the guild", player, false);
            } else {
                warning(type(Plugin.guild), "Guild " + cGuild + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild) + cWarning + " disbanded!", player, false);
            }
        }
    }

    public void guildNoInvitation(UUID player) {
        warning(type(Plugin.guild), "You have no pending Guild invites.", player, false);
    }

    public void guildListPending(String string, List<UUID> list, UUID uuid) {
        StringBuilder builder = new StringBuilder();
        builder.append(type(Plugin.guild)).append(string).append("\n");
        builder.append("---------------------\n");
        for (UUID pend : list) {
            builder.append(cPlayer).append(OddJob.getInstance().getPlayerManager().getName(pend)).append("\n");
        }
        OddJob.getInstance().getPlayerManager().getPlayer(uuid).sendMessage(builder.toString());
    }

    public void guildNoPending(UUID player) {
        warning(type(Plugin.guild), "There is no pending requests to join the Guild.", player, false);
    }

    public void guildAlreadyInvited(String string, UUID uuid) {
        warning(type(Plugin.guild), cPlayer + string + cWarning + " is already invited to the Guild.", uuid, false);
    }

    public void guildAnotherInvite(String string, UUID uuid) {
        danger(type(Plugin.guild), cPlayer + string + cDanger + " is already invited to another Guild.", uuid, false);
    }

    public void guildAlreadyPending(String string, UUID uuid) {
        warning(type(Plugin.guild), cPlayer + string + cWarning + " has already requested to join the Guild.", uuid, false);
    }

    public void guildAnotherPending(String string, UUID uuid) {
        danger(type(Plugin.guild), cPlayer + string + cDanger + " has already requested to join another Guild.", uuid, false);
    }

    public void guildAlreadyJoined(String string, UUID uuid) {
        warning(type(Plugin.guild), cPlayer + string + cWarning + " is already a member of the Guild.", uuid, false);
    }

    public void guildAnotherJoined(String string, UUID uuid) {
        danger(type(Plugin.guild), cPlayer + string + cDanger + " is already a member of another Guild.", uuid, false);
    }

    public void guildRoleNeeded(UUID uuid) {
        warning(type(Plugin.guild), "Your role has not permission to perform this action.", uuid, false);
    }

    public void guildInvitedToGuild(Guild guild, UUID player, UUID sender) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(sender))
                success(type(Plugin.guild), "You have successfully invited " + cPlayer + OddJob.getInstance().getPlayerManager().getName(player) + cSuccess + " to the Guild.", member, true);
            else
                success(type(Plugin.guild), (cPlayer + OddJob.getInstance().getPlayerManager().getName(player) + cSuccess) + " has been invited to the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(sender), member, false);
        }
        success(type(Plugin.guild), "You have been invited to the Guild " + cGuild + guild.getName(), player, false);
    }

    public void guildNotInvited(String string, UUID uuid) {
        danger(type(Plugin.guild), cValue + string + cDanger + " were never invited to the Guild", uuid, false);
    }

    public void guildUninvited(Guild guild, UUID invited, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success(type(Plugin.guild), "You have successfully removed invitation of " + cPlayer + OddJob.getInstance().getPlayerManager().getPlayer(invited) + cSuccess + " to the Guild.", member, true);
            else
                warning(type(Plugin.guild), (cPlayer + OddJob.getInstance().getPlayerManager().getName(invited) + cWarning) + " is no longer invited to the Guild, removed by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
        danger(type(Plugin.guild), "Your invitation to the Guild " + cGuild + guild.getName() + cDanger + " has been revoked.", invited, false);
    }

    public void guildNotSame(UUID target, UUID uuid) {
        danger(type(Plugin.guild), "You and " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cDanger + " is not in the same Guild.", uuid, false);
    }

    public void guildRoleHighest(UUID target, UUID uuid) {
        warning(type(Plugin.guild), cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " has already the highest obtainable rank after GuildMaster.", uuid, false);
    }

    public void guildRoleHigher(UUID target, UUID uuid) {
        warning(type(Plugin.guild), cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " has the same or higher rank than you.", uuid, false);
    }

    public void guildPromoted(Guild guild, UUID target, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success(type(Plugin.guild), "You have successfully promoted " + cPlayer + OddJob.getInstance().getPlayerManager().getPlayer(target) + cSuccess + " to " + guild.getMembers().get(target).name() + " in the Guild.", member, true);
            else if (target.equals(member))
                success(type(Plugin.guild), "You have been promoted to " + guild.getMembers().get(target).name() + " in the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), target, false);
            else
                warning(type(Plugin.guild), (cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning) + " has been promoted to " + guild.getMembers().get(target).name() + " in the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
    }

    public void guildDemoted(Guild guild, UUID target, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success(type(Plugin.guild), "You have successfully demoted " + cPlayer + OddJob.getInstance().getPlayerManager().getPlayer(target) + cSuccess + " to " + guild.getMembers().get(target).name() + " in the Guild.", member, true);
            else if (target.equals(member))
                danger(type(Plugin.guild), "You have been demoted to " + guild.getMembers().get(target).name() + " in the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), target, false);
            else
                warning(type(Plugin.guild), (cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning) + " has been demoted to " + guild.getMembers().get(target).name() + " in the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
    }

    public void guildRoleLowest(UUID target, UUID uuid) {
        warning(type(Plugin.guild), cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " has already the lowest obtainable rank.", uuid, false);
    }

    public void guildNeedMaster(UUID uuid) {
        danger(type(Plugin.guild), "Sorry, but only the GuildMaster may transfer the role to a new Player.", uuid, false);
    }

    public void guildAcceptPending(Guild guild, UUID target, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success(type(Plugin.guild), "You have accepted the request from " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cSuccess + " to join the Guild", member, true);
            if (member.equals(target))
                success(type(Plugin.guild), "Welcome to the Guild " + cGuild + guild.getName(), member, true);
            else
                warning(type(Plugin.guild), (cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning) + "'s request to join the Guild has been accepted by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
    }

    public void guildAcceptInvite(Guild guild, CommandSender sender) {
        Player p = (Player) sender;
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(p.getUniqueId()))
                success(type(Plugin.guild), "You have accepted the invitation to join the " + cGuild + guild.getName(), member, true);
            else
                warning(type(Plugin.guild), cPlayer + sender.getName() + cWarning + " has accepted the invitation to join the " + cGuild + guild.getName(), member, false);
        }
    }

    public void guildJoining(Guild guild, CommandSender sender) {
        Player p = (Player) sender;
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(p.getUniqueId()))
                success(type(Plugin.guild), "Welcome to the Guild " + cGuild + guild.getName(), member, true);
            else
                success(type(Plugin.guild), "Please welcome " + cPlayer + sender.getName() + cSuccess + " to the " + cGuild + guild.getName(), member, false);
        }
    }

    public void guildPending(Guild guild, CommandSender sender) {
        for (UUID member : guild.getMembers().keySet()) {
            warning(type(Plugin.guild), cPlayer + sender.getName() + cWarning + " has requested to join the " + cGuild + guild.getName(), member, false);
        }
        success(type(Plugin.guild), "You have sent a request to join the " + cGuild + guild.getName(), sender, true);
    }

    public void guildDenyInvite(Guild guild, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            warning(type(Plugin.guild), cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid) + cWarning + "'s invitation to join the " + cGuild + guild.getName() + cWarning + " has been declined.", member, false);
        }
        success(type(Plugin.guild), "You have declined the invitation to join the " + cGuild + guild.getName(), uuid, true);
    }

    public void guildDenyRequest(Guild guild, UUID target, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                warning(type(Plugin.guild), "You have declined " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " request to join the Guild", member, true);
            else
                danger(type(Plugin.guild), cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid) + cDanger + " has denied " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + "'s request to join the Guild", member, false);
        }
        danger(type(Plugin.guild), "We are sorry to announce that your request to join " + cGuild + guild.getName() + cDanger + " has been declined.", target, false);
    }

    public void guildSetConfirm(Guild guild, String key, String value, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success(type(Plugin.guild), "You have changed " + cValue + key + cSuccess + " changed to " + cValue + value + cSuccess + " for the Guild", member, false);
            else
                warning(type(Plugin.guild), "Settings for " + cValue + key + cWarning + " changed to " + cValue + value + cWarning + " by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
    }

    public void guildMap(String string, Player player) {
        player.sendMessage(string);
    }

    public void guildNewMaster(Guild guild, UUID target, UUID next) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(next))
                success(type(Plugin.guild), "You are the Chosen One to be the new GuildMaster since " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cSuccess + " left.", member, true);
            else
                warning(type(Plugin.guild), cPlayer + OddJob.getInstance().getPlayerManager().getName(next) + cWarning + " is now your new GuildMaster.", member, false);
        }
    }

    public void guildKickPlayer(Guild guild, UUID target, UUID player, String reason) {
        String r = "";
        if (reason != null) r = " because " + cValue + reason;
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(player))
                success(type(Plugin.guild), "You have successfully kick " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cSuccess + " from the Guild.", member, true);
            else
                warning(type(Plugin.guild), cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " has been kicked from the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(player) + cSuccess + r, member, false);
        }
        danger(type(Plugin.guild), "You have been kicked from " + cGuild + guild.getName() + cDanger + " by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(player) + cDanger + r, target, false);
    }

    public void arenaSetSpawnTeleport(String s, UUID uniqueId) {
    }

    public void arenaSetSpawnRemove(String s, UUID uniqueId) {
    }

    public void arenaAreaSet(Player player) {
        success(type(Plugin.arena), "Area for Arena set!", player.getUniqueId(), false);
    }

    public void errorHomeMaximal(CommandSender player) {
        danger(type(Plugin.home), "You have reached th maximal amout of homes.", player, false);
    }

    public void errorMissingArgs(Plugin type, CommandSender sender) {
        danger(type(type), "Missing arguments", sender, false);
    }

    public void errorTooManyArgs(Plugin type, CommandSender sender) {
        danger(type(type), "Too many args", sender, false);
    }

    public void infoCurrencyBalance(UUID uuid, double pocket, double bank) {
        info(type(Plugin.currency), "You are holding " + cValue + pocket + cInfo + " in your " + cValue + "pocket" + cInfo + ", and have " + cValue + bank + cInfo + " in your " + cValue + "bank" + cInfo + " account.", uuid);
    }

    public void invalidNumber(Plugin type, String number, CommandSender sender) {
        danger(type(type), "'" + cValue + number + cDanger + "' is not a number", sender, false);
    }

    public void sendSyntax(Plugin type, String syntax, CommandSender sender) {
        warning(type(type), "syntax: " + syntax, sender, false);
    }

    public void currencySuccessSet(String target, String amount, CommandSender sender, Currency account) {
        success(type(Plugin.currency), "You have sucessfully set " + cPlayer + target + cSuccess + "`s " + cValue + account + cSuccess + " to " + cValue + amount, sender, true);
    }

    public void currencySuccessAdded(String target, String amount, double balance, CommandSender sender, Currency account) {
        success(type(Plugin.currency), "You have successfully added " + cValue + amount + cSuccess + " to " + cPlayer + target + cSuccess + "`s " + cValue + account + cSuccess + ", new balance is " + cValue + balance, sender, false);
    }

    public void currencySuccessSubtracted(String target, String amount, double balance, CommandSender sender, Currency account) {
        success(type(Plugin.currency), "You have successfully subtracted " + cValue + amount + cSuccess + " from " + cPlayer + target + cSuccess + "`s " + cValue + account + cSuccess + ", new balance is " + cValue + balance, sender, true);
    }

    public void banRemoveSuccess(String name, CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("ban")) {
                success(type(Plugin.ban), "Ban removed from " + cValue + name + cSuccess + " by " + cPlayer + sender.getName(), player, true);
            }
        }

    }

    public void banRemoveError(String name, CommandSender sender) {
        danger(type(Plugin.ban), "Player " + cValue + name + cDanger + " was never banned", sender, true);
    }

    public void banAddedSuccess(String name, String text, String send, CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("ban")) {
                success(type(Plugin.ban), "Player " + cPlayer + name + cSuccess + " was successfully banned with the text '" + cValue + text + cSuccess + "' by " + cPlayer + send, sender, true);
            }
        }

    }

    public void banList(HashMap<OddPlayer, String> bans, CommandSender sender) {
        // There are ** players banned
        // ---------------------------
        // 1. <player> : <reason>
        info(type(Plugin.ban), "There are " + cValue + bans.size() + cInfo + " players banned", sender, false);
        if (bans.size() > 0) {
            info(type(Plugin.ban), "----------------------------------", sender, false);
            int i = 0;
            for (OddPlayer name : bans.keySet()) {
                i++;
                String reason = bans.get(name);
                info(type(Plugin.ban), cValue + "" + i + cInfo + ". " + cPlayer + name.getName() + cInfo + " : " + cValue + reason, sender, false);
            }
        }
    }


    public void infoGuildCreated(String name, CommandSender sender) {
        success(type(Plugin.guild), "Guild " + cGuild + name + cSuccess + " successfully created", sender, true);
    }

    public void infoGuildExists(String name, CommandSender sender) {
        success(type(Plugin.guild), "Guild " + cGuild + name + cSuccess + " already exists", sender, false);
    }

    public void errorWrongArgs(Plugin type, CommandSender sender) {
        danger(type(type), "Error wrong arguments", sender, false);
    }

    public void infoArgs(Plugin type, String args, CommandSender sender) {

        warning(type(type), "Valid syntax are: " + cValue + args.replace(",", cWarning + "," + cValue), sender, false);
    }

    public void saved(Plugin type, CommandSender sender) {
        success(type(type), "Successfully saved " + cValue + type.name(), sender, true);
    }

    public void loaded(Plugin type, CommandSender sender) {
        success(type(type), "Successfully saved " + cValue + type.name(), sender, true);
    }

    public void successWarpAdded(String name, Player player) {
        success(type(Plugin.warp), "Successfully added Warp " + cWarp + name, player, true);
    }

    public void successWarpDeleted(String name, CommandSender sender) {
        success(type(Plugin.warp), "Successfully deleted Warp " + cWarp + name, sender, true);
    }

    public void errorWarpExists(String name, CommandSender sender) {
        danger(type(Plugin.warp), "Warp " + cWarp + name + cDanger + " already exists", sender, false);
    }

    public void warpWrongPassword(String name, CommandSender sender) {
        warning(type(Plugin.warp), " Wrong password for " + cWarp + name, sender, false);
    }


    public void successWarpSetLocation(String name, CommandSender sender) {
        success(type(Plugin.warp), "Successfully set a new location for warp " + cWarp + name, sender, true);
    }

    public void errorWarpNotExists(String name, CommandSender sender) {
        danger(type(Plugin.warp), "Warp " + cWarp + name + cDanger + " doesn't exists", sender, false);
    }

    public void successWarpSetCost(double cost, String name, CommandSender sender) {
        success(type(Plugin.warp), "Successfully set cost " + cValue + cost + cSuccess + " at warp " + cWarp + name, sender, true);
    }

    public void successWarpSetPasswd(String newPass, String name, CommandSender sender) {
        success(type(Plugin.warp), "Successfully set password for warp " + cWarp + name + cSuccess + " with " + cValue + newPass, sender, true);
    }

    public void successWarpSetName(String newName, String oldName, CommandSender sender) {
        success(type(Plugin.warp), "Successfully changed warp name from " + cWarp + oldName + cSuccess + " to " + cWarp + newName, sender, true);
    }

    public void currencyChanged(Currency type, double cost, double pocketBalance, UUID uuid, CommandSender sender) {
        if (sender == null) {
            info(type(Plugin.currency), "Subtracted " + cCurrency + cost + cInfo + " from your " + cValue + type.name() + cInfo + ", new balance is " + cValue + pocketBalance, uuid);
        } else {
            info(type(Plugin.currency), "Subtracted " + cCurrency + cost + cInfo + " from " + cPlayer + Bukkit.getPlayer(uuid).getName() + cInfo + "`s " + cValue + type.name() + cInfo + ", new balance is " + cValue + pocketBalance, uuid);
        }
    }


    public void warpError(String name, CommandSender sender) {
        danger(type(Plugin.warp), "An unexpected error has occured with " + cValue + name, sender, false);
    }

    public void sold(int amount, Material material, double cost, Player player) {
        success(type(Plugin.shop), "You sold " + amount + " of " + material.name() + " for " + cost, player, false);
    }

    public void buy(int amount, Material material, double cost, Player player) {
        success(type(Plugin.shop), "You bought " + amount + " of " + material.name() + " for " + cost, player, false);
    }

    public void skeleton(UUID uuid) {
        danger(type(Plugin.lock), "Sorry, The SKELETON KEY is tooooooooo powerful!", uuid, true);
    }

    public void broken(UUID uuid) {
        warning(type(Plugin.lock), "Lock broken!", uuid, true);
    }

    public void ownedOther(UUID uuid) {
        danger(type(Plugin.lock), "This lock is owned by someone else!", uuid, false);
    }

    public void alreadyJailed(CommandSender sender) {
        danger(type(Plugin.jail), "Is already in jail!", sender, false);
    }

    public void jailNoWorld(CommandSender sender) {
        danger(type(Plugin.jail), "This world has no finished jail!", sender, false);
    }

    public void jailIn(UUID uuidPlayer) {
        info(type(Plugin.jail), "You are now in jail, serving your time!", uuidPlayer);
    }

    public void jailInsert(UUID uuidPlayer, CommandSender sender) {
        success(type(Plugin.jail), "You have successfully set " + OddJob.getInstance().getPlayerManager().getName(uuidPlayer) + " in jail!", sender, true);
    }

    public void jailNot(CommandSender sender) {
        danger(type(Plugin.jail), "Is not in jail!", sender, false);
    }

    public void jailServed(UUID uuidPlayer) {
        info(type(Plugin.jail), "You have served your time, be more careful!", uuidPlayer);
    }

    public void jailFree(UUID uuidPlayer, CommandSender sender) {
        success(type(Plugin.jail), "You have successfully set " + OddJob.getInstance().getPlayerManager().getName(uuidPlayer) + " free from jail!", sender, true);
    }

    public void killed(String name, CommandSender sender) {
        success(type(Plugin.kick), "Killed " + name, sender, true);
    }

    public void unbanned(String name, CommandSender sender, boolean b) {
        success(type(Plugin.ban), "Unbanned " + name, sender, true);
    }

    public void whitelistAdd(String name, CommandSender sender) {
        success(type(Plugin.player), "You have added " + name + " to your Whitelist", sender, true);
    }

    public void whitelistDel(String name, CommandSender sender) {
        success(type(Plugin.player), "You have removed " + name + " from your Whitelist", sender, true);
    }

    public void blacklistAdd(String name, CommandSender sender) {
        success(type(Plugin.player), "You have added " + name + " to your Blacklist", sender, true);
    }

    public void blacklistDel(String name, CommandSender sender) {
        success(type(Plugin.player), "You have removed " + name + " from your Blacklist", sender, true);
    }

    public void playerSetDenyTPA(String arg, boolean deny, Player player) {
        success(type(Plugin.player), "SET " + arg + " to " + deny, player, true);
    }

    public void playerSetDenyTrade(String arg, boolean deny, Player player) {
        success(type(Plugin.player), "SET " + arg + " to " + deny, player, true);
    }

    public void playerSetScoreboard(String arg, String name, Player player) {
        success(type(Plugin.player), "SET " + arg + " to " + name, player, true);
    }


    public void banAddError(String arg, CommandSender sender) {
        danger(type(Plugin.ban), "Error while banning " + cPlayer + arg, sender, false);
    }

    public void feedPlayer(String name, CommandSender commandSender) {
        success(type(Plugin.feed), name + " has been feed.", commandSender, false);
    }

    public void feedTarget(UUID uniqueId) {
        success(type(Plugin.feed), "You have been feed", uniqueId, false);
    }

    public void frozenPlayer(String name, CommandSender commandSender) {
        warning(type(Plugin.freeze), name + " is no longer frozen.", commandSender, true);
    }

    public void frozenTarget(UUID uniqueId) {
        success(type(Plugin.freeze), "The bonechill has left your body.", uniqueId, false);
    }

    public void unfreezePlayer(String name, CommandSender commandSender) {
        success(type(Plugin.freeze), name + " is now frozen to the spot.", commandSender, true);
    }

    public void unfreezeTarget(UUID uniqueId) {
        warning(type(Plugin.freeze), "You feel a bonefreezing cold, that freezes your body, stuck at this location.", uniqueId, false);
    }

    public void gamemmodeChanged(Player target, GameMode gameMode, CommandSender sender, boolean self) {
        if (!self) {
            success(type(Plugin.gamemode), "Gamemode of " + cPlayer + target.getName() + cSuccess + " changed to " + cValue + gameMode.name(), sender, false);
        }
        success(type(Plugin.gamemode), "Gamemode changed to " + cValue + gameMode.name(), sender, false);
    }

    public void healPlayer(String name, CommandSender commandSender) {
        success(type(Plugin.heal), name + " has been healed.", commandSender, true);
    }

    public void healTarget(UUID uniqueId) {
        success(type(Plugin.heal), "You have been healed", uniqueId, false);
    }

    public void kickPlayer(String name, String message, CommandSender commandSender) {
        success(type(Plugin.kick), "Player " + cPlayer + name + cSuccess + " kicked, reason: " + cReset + message, commandSender, true);
    }

    public void lockKey(CommandSender sender) {
        danger(type(Plugin.lock), "! This is a key to all your chests, keep in mind who you share it with.", sender, false);
        info(type(Plugin.lock), "! Stolen item or lost keys will not be refunded.", sender, false);
    }

    public void tpAlreadySent(String arg, CommandSender sender) {
        danger(type(Plugin.tp), "You have already sent an request to " + cPlayer + arg, sender, false);
    }

    public void tpDenied(String name, CommandSender sender) {
        warning(type(Plugin.tp), cPlayer + name + cWarning + " has denied your request.", sender, false);
    }

    public void tpRequestPlayer(String name, CommandSender sender) {
        warning(type(Plugin.tp), cPlayer + name + cWarning + " want to be teleported to you. To accept this use '" + cValue + "/tp accept" + cWarning + "'", sender, false);
    }

    public void tpAccepted(UUID destinationUUID, String name, UUID movingUUID, String movingPlayerName) {
        success(type(Plugin.tp), "Your request has been accepted by " + cPlayer + name, movingUUID, false);
        success(type(Plugin.tp), "You have accepted the request from " + cPlayer + movingPlayerName, destinationUUID, true);
    }

    public void tpNow(UUID movingUUID) {
        success(type(Plugin.tp), "Teleporting now!", movingUUID, true);
    }

    public void tpInterrupt(UUID movingUUID) {
        danger(type(Plugin.tp), "Interrupted, in combat!", movingUUID, true);
    }

    public void tpCountdown(int i, UUID movingUUID) {
        info(type(Plugin.tp), "Teleporting in " + ChatColor.WHITE + i, movingUUID);
    }

    public void tpTeleporting(UUID movingUUID) {
        success(type(Plugin.tp), "Teleporting!", movingUUID, true);
    }

    public void tpChanging(UUID movingUUID) {
        warning(type(Plugin.tp), "Changing teleport location.", movingUUID, false);
    }

    public void tpDeny(UUID playerUUID, String player, UUID targetUUID, String target) {
        danger(type(Plugin.tp), "Your request has been denied by " + cPlayer + player, targetUUID, false);
        danger(type(Plugin.tp), "You have denied the request from " + cPlayer + target, playerUUID, true);
    }

    public void tpTimedOut(UUID uuid) {
        danger(type(Plugin.tp), "The teleport request has timed out", uuid, false);
    }

    public void tpError(CommandSender commandSender) {
        danger(type(Plugin.tp), "Something went wrong when trying to teleport.", commandSender, false);
    }

    public void tpInvalid(CommandSender commandSender) {
        warning(type(Plugin.tp), "Invalid x, y or z", commandSender, false);
    }

    public void tpPos(UUID uniqueId) {
        success(type(Plugin.tp), "You have been teleported to a specific location", uniqueId, true);
    }

    public void tpPlayer(String destinationName, CommandSender name) {
        success(type(Plugin.tp), "You have been teleported to " + destinationName, name, true);
    }

    public void tpSpawn(CommandSender player) {
        success(type(Plugin.tp), "You have been teleported to spawn", player, true);
    }

    public void tpAllTarget(CommandSender sender) {
        success(type(Plugin.tp), "Say hi to everyone!", sender, true);
    }

    public void tpAllPlayer(Player target, String sender) {
        info(type(Plugin.tp), "We are sorry to interrupt, but " + cPlayer + sender + cSuccess + " needs your full attension.", target, false);
    }

    public void tpPosTarget(CommandSender sender, Player player) {
        success(type(Plugin.tp), "We 'will' successfulle teleport " + cPlayer + player.getName() + cSuccess + " to the specified location", sender, false);
        warning(type(Plugin.tp), "You have carefully been selected to a transfer, sepcified by " + cPlayer + sender.getName(), player, false);
    }

    public void tradeRequest(Player tradeWith, Player player) {
        success(type(Plugin.trade), "You have sent a trade request to " + cPlayer + tradeWith.getName(), player, true);
        info(type(Plugin.trade), "You got a trade request from " + cPlayer + player.getName(), tradeWith, false);
    }

    public void tradeNotOnline(Player player) {
        warning(type(Plugin.trade), "Unfortunately your trade partner is not online", player, false);
    }

    public void tradeNone(Player player) {
        danger(type(Plugin.trade), "No players wanna trade with you", player, false);
    }

    public void guildClaiming(int count, int max, int x, int z, Player player, String guild) {
        success(type(Plugin.guild), "Claiming:" + cValue + count + cSuccess + "/" + cValue + max + cSuccess + " chunk X:" + cValue + x + cSuccess + " Z:" + cValue + z + cSuccess + " World:" + cValue + player.getWorld().getName() + cSuccess + " to " + cGuild + guild, player, true);
    }

    public void guildClaimed(Player player) {
        danger(type(Plugin.guild), "Already claimed", player, false);
    }

    public void guildOwnedBy(String guildNameByUUID, Player player) {
        danger(type(Plugin.guild), "This chunk is owned by " + cGuild + guildNameByUUID, player, false);
    }

    public void guildNotAssociatedGuild(Player player) {
        danger(type(Plugin.guild), "Sorry, you are not associated with the guild who claimed this chunk", player, false);
    }

    public void guildUnclaimed(int x, int z, Player player) {
        success(type(Plugin.guild), "You have unclaimed X:" + cValue + x + cSuccess + " Z:" + cValue + z + cSuccess + " World:" + cValue + player.getWorld().getName(), player, true);
    }

    public void guildNotClaimed(Player player) {
        warning(type(Plugin.guild), "Not claimed", player, false);
    }


    public void guildChangingZone(String guildNameByUUID, Player player) {
        warning(type(Plugin.guild), "Changing Zone auto claim to " + cGuild + guildNameByUUID, player, true);
    }

    public void guildAutoOff(String guildNameByUUID, Player player) {
        warning(type(Plugin.guild), "Turning off Zone auto claim to " + cGuild + guildNameByUUID, player, true);
    }

    public void guildAutoOn(String guildNameByUUID, Player player) {
        warning(type(Plugin.guild), "You are now claiming zones for " + cGuild + guildNameByUUID, player, true);
    }

    public void lockEntityDamage(String name, Player damager) {
        warning(type(Plugin.lock), "Entity locked by " + cPlayer + name, damager, false);
    }

    public void lockEntityUnlock(Player damager) {
        warning(type(Plugin.lock), "Entity unlocked.", damager, true);
    }

    public void lockEntityLock(Player damager) {
        success(type(Plugin.lock), "Entity secure!", damager, true);
    }

    public void tradeAborted(String name, UUID player) {
        danger(type(Plugin.trade), "Trading aborted by " + cPlayer + name, player, false);
    }

    public void lockBlockOwned(String block, String name, Player player) {
        info(type(Plugin.lock), "The " + cValue + block + cInfo + " is owned by " + cPlayer + name, player, false);
    }

    public void lockSomeoneElse(Player player) {
        danger(type(Plugin.lock), "A lock is set by someone else.", player, false);
    }

    public void lockUnlocked(String block, Player player) {
        warning(type(Plugin.lock), "Unlocked " + cValue + block, player, true);
    }

    public void lockSkeletonOpen(Player player) {
        danger(type(Plugin.lock), "! Lock opened by the SkeletonKey", player, true);
    }

    public void lockOwned(Player player) {
        danger(type(Plugin.lock), "This block is locked by someone else.", player, false);
    }

    public void lockAlreadyBlock(Player player) {
        warning(type(Plugin.lock), "You have already locked this.", player, false);
    }

    public void lockAlreadyEntity(Player player) {
        danger(type(Plugin.lock), "A lock is already set on this.", player, false);
    }

    public void lockBlockLocked(String name, Player player) {
        success(type(Plugin.lock), "Locked " + cValue + name, player, true);
    }

    public void lockGuild(String guildNameByUUID, Player player) {
        warning(type(Plugin.lock), "Block is locked by the guild " + cGuild + guildNameByUUID, player, false);
    }

    public void spiritFoundSomeone(UUID player) {
        danger(type(Plugin.spirit), "Somebody found your spirit.", player, false);
    }

    public void spiritFound(String finder, String owner) {
        console(type(Plugin.spirit) + cPlayer + finder + cReset + " found the spirit of " + cPlayer + owner);
    }

    public void spiritFoundSelf(UUID findingPlayer) {
        success(type(Plugin.spirit), "You got lucky this time", findingPlayer, true);
    }

    public void death1200(Player player) {
        info(type(Plugin.spirit), "Sorry to hear about your death. Your spirit will disappear in " + cValue + "20" + cInfo + " min, if you don't get it!", player, true);
    }

    public void death600(Player player) {
        info(type(Plugin.spirit), "Spirit disappearing in " + cValue + "10" + cInfo + " min.", player, false);
    }

    public void death60(Player player) {
        info(type(Plugin.spirit), "Spirit disappearing in " + cValue + "1" + cInfo + " min.", player, false);
    }

    public void death10(int i, Player player) {
        danger(type(Plugin.spirit), "Spirit disappearing in " + cValue + i + cInfo + " sec.", player, false);
    }

    public void death0(Player player) {
        danger(type(Plugin.spirit), "All your items is gone, sorry.", player, true);
    }

    public void lockToolInfo(UUID uniqueId) {
        info(type(Plugin.lock), "Right click with the " + cValue + "Information tool" + cInfo + " to show the objects owner.", uniqueId);
        info(type(Plugin.lock), "Use the same command to remove the tool", uniqueId);
    }

    public void lockToolLock(UUID uniqueId) {
        info(type(Plugin.lock), "Right click with the " + cValue + "Locking tool" + cInfo + " to lock the object.", uniqueId);
        info(type(Plugin.lock), "Use the same command to remove the tool", uniqueId);
    }

    public void lockToolUnlock(UUID uniqueId) {
        info(type(Plugin.lock), "Right click with the " + cValue + "Unlocking tool" + cInfo + " to unlock the object.", uniqueId);
        info(type(Plugin.lock), "Use the same command to remove the tool", uniqueId);
    }

    public void playerDenying(String name, UUID player) {
        warning(type(Plugin.player), name + " is denying all request!", player, false);
    }

    public void lockNotCorrectPlayer(Player player) {
        warning(type(Plugin.lock), "You don't have the correct Player-Key", player, false);
    }

    public void lockNotCorrectGuild(Player player) {
        warning(type(Plugin.lock), "You don't have the correct Guild-Key", player, false);
    }

    public void lockOpened(String displayName, Player player) {
        success(type(Plugin.lock), "Lock opened with key: " + displayName, player, true);
    }

    public void errorScoreboard(CommandSender sender) {
        danger(type(Plugin.player), "Something went wrong!", sender, false);
    }

    public void cannotIdentify(String name, String account, Plugin type, CommandSender sender) {
        warning(type(type), "We can't find " + cValue + name + cWarning + " as " + cValue + account, sender, false);
    }

    public void guildMenu(Guild guild, String guildNameByUUID, Role guildMemberRole, CommandSender sender) {
        info(type(Plugin.guild), "As a " + cValue + guildMemberRole.name() + cInfo + " of " + cGuild + guildNameByUUID, sender, false);
        info(type(Plugin.guild), "Guild is open: " + String.valueOf(guild.getOpen()), sender, false);
        info(type(Plugin.guild), "Friendly fire activated: " + String.valueOf(guild.getFriendlyFire()), sender, false);
        info(type(Plugin.guild), "" + cValue + guild.getChunks() + cInfo + "/" + cValue + guild.getMaxClaims(), sender, false);
        info(type(Plugin.guild), "Players with role; " + cValue + "<player>:<role>", sender, false);
        for (UUID uuid : guild.getMembers().keySet()) {
            Role role = guild.getMembers().get(uuid);
            OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(uuid);
            info(type(Plugin.guild), oddPlayer.getName() + ":" + role.name(), sender, false);
        }
    }

    public void homesCount(List<String> list, String target, int max, CommandSender sender, boolean self) {
        if (list == null) {
            homesNotSet(sender);
            return;
        }
        info(type(Plugin.home), ((self) ? "You" : cPlayer + target + cInfo) + " have assigned " + cValue + list.size() + cInfo + " of " + cValue + max, sender, false);
        space(Plugin.home, sender);
        int i = 1;
        for (String name : list) {
            info(type(Plugin.home), i + ".) " + cValue + name, sender, false);
            i++;
        }
    }

    public void homesNotSet(CommandSender sender) {
        warning(type(Plugin.home), "No home is set yet!", sender, false);
    }

    public void permissionDenied(Plugin type, CommandSender sender) {
        danger(type(type), "Permission denied!", sender, false);
    }

    public void guildZoneError(String arg, Player player) {
        danger(type(Plugin.guild), "Unknown zone " + cValue + arg, player, false);
    }

    public void guildLastOne(CommandSender sender) {
        warning(type(Plugin.guild), "You are the last one, use " + cValue + "/guild disband" + cWarning + " instead.", sender, false);
    }

    public void changeRole(Role role, UUID target, CommandSender sender) {
        success(type(Plugin.guild), "Your role in the guild has changed to " + cValue + role.name() + cSuccess + " by " + cPlayer + sender.getName(), target, false);
        success(type(Plugin.guild), "You have changed the role of " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cSuccess + " to " + cValue + role.name(), sender, false);
    }

    public void guildWelcome(Guild guild, OfflinePlayer player) {
        for (UUID uuid : guild.getMembers().keySet()) {
            info(type(Plugin.guild), "Please welcome " + player.getName() + " to the guild", uuid);
        }
    }

    public void pendingList(List<UUID> pending, CommandSender sender) {
        info(type(Plugin.guild), "There are " + pending.size() + " players requesting to join your guild.", sender, false);
        space(Plugin.guild, sender);
        int i = 1;
        for (UUID uuid : pending) {
            info(type(Plugin.guild), i + ".) " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), sender, false);
        }
    }

    public void opSet(Player player, CommandSender sender, boolean self) {
        success(type(Plugin.op), "Successfully op'ed " + cPlayer + player, sender, false);
    }

    public void deopSet(Player player, CommandSender sender, boolean self) {
        success(type(Plugin.deop), "Successfully deop'ed " + cPlayer + player, sender, false);
    }

    public void areOp(CommandSender sender) {
        danger(type(Plugin.player), "You are an " + cValue + "OP", sender, false);
    }

    public void notOp(CommandSender sender) {
        success(type(Plugin.player), "You are " + cDanger + "NOT" + cSuccess + " an " + cValue + "OP", sender, false);
    }

    public void lockList(List<String> list, CommandSender sender) {
        info(type(Plugin.lock), "You have " + cValue + list.size() + cInfo + " locked objects", sender, false);
        space(Plugin.lock, sender);
        for (int i = 1; i <= list.size(); i++) {
            info(type(Plugin.lock), i + "). " + list.get(i - 1), sender, false);
        }
    }

    public void space(Plugin type, CommandSender sender) {
        info(type(type), "------------------------------------", sender, false);
    }

    public void lockSkeleton(CommandSender sender) {
        danger(type(Plugin.lock), ChatColor.YELLOW + "!!!" + cDanger + " Danger " + ChatColor.YELLOW + "!!! " + cDanger + "Unlocks all objects for you!", sender, false);
    }

    public void locksKey(CommandSender sender) {
        success(type(Plugin.lock), "Here is your key to your locked objects, keep it safe!", sender, false);
    }

    public void homesChangedSuccess(String name, UUID uuid) {
        success(type(Plugin.home), "Location for " + cValue + name + cSuccess + " has been changed", uuid, false);
    }

    public void guildInfoSelf(Guild guild, CommandSender sender) {

    }

    public void playerErrorScoreboard(CommandSender sender, String board) {
        danger(type(Plugin.player), "Scoreboard " + cValue + board + cDanger + " not found", sender, false);
    }

    public void guildMaxClaimsReached(CommandSender sender) {
        danger(type(Plugin.guild), "Claiming count reached maximum.", sender, false);
    }

    public void syntaxError(Plugin plugin, String s, CommandSender sender) {
        danger(type(plugin), "Unknown syntax: " + s, sender, false);
    }

    public void errorZone(Plugin plugin, String arg, CommandSender sender) {
        danger(type(plugin), "Unknown Zone: " + cValue + arg, sender, false);
    }
}
