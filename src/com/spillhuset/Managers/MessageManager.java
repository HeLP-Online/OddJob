package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.AuctionBid;
import com.spillhuset.Utils.AuctionItem;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Enum.Types;
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
            case guilds -> tGuild;
            case players -> tPlayer;
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
        danger(type(Plugin.guilds), "Sorry, we can't find the guild: " + cValue + string, sender, false);
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

    public void insufficientFunds(UUID uuid, double price) {
        warning(type(Plugin.currency), "Insufficient funds. You will need " + cValue + price + cWarning + " to do this.", uuid, false);
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
        danger(type(Plugin.guilds), "A Guild with the name " + cGuild + string + cDanger + " already exists.", sender, false);
    }

    public void guildAlreadyAssociated(String string, CommandSender sender) {
        danger(type(Plugin.guilds), "You are already associated with the Guild " + cGuild + string, sender, false);
    }


    public void guildCreateSuccessful(String string, CommandSender sender) {
        Player p = (Player) sender;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(p.getUniqueId()))
                success(type(Plugin.guilds), "You have successfully created the Guild " + cGuild + string, sender, true);
            else
                warning(type(Plugin.guilds), "A new Guild name " + cGuild + string + cWarning + " has been created by " + cPlayer + sender.getName(), player, false);
        }
    }

    public void guildCreateError(String string, CommandSender sender) {
        danger(type(Plugin.guilds), "Something went wrong when trying to create a Guild with the name " + cValue + string, sender, true);
    }

    public void guildNotAssociated(UUID uuid) {
        info(type(Plugin.guilds), "You are not associated with any Guild yet.", uuid);
    }


    public void guildLeaveSuccessful(Guild guild, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (uuid.equals(member))
                success(type(Plugin.guilds), "You have successfully left the Guild " + cGuild + guild.getName(), member, true);
            else
                warning(type(Plugin.guilds), cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid) + cWarning + " has left the Guild", uuid, false);
        }
    }

    public void guildDisband(UUID guild, CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()) == guild) {
                danger(type(Plugin.guilds), cPlayer + sender.getName() + cDanger + " has disbanded the guild", player, false);
            } else {
                warning(type(Plugin.guilds), "Guild " + cGuild + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild) + cWarning + " disbanded!", player, false);
            }
        }
    }

    public void guildNoInvitation(UUID player) {
        warning(type(Plugin.guilds), "You have no pending Guild invites.", player, false);
    }

    public void guildsListPending(String name, List<UUID> list, CommandSender sender) {
        info(type(Plugin.guilds), "There is " + cValue + list.size() + cInfo + " pending requests to join the " + cGuild + name, sender, false);
        space(Plugin.guilds, sender);
        for (UUID pend : list) {
            info(type(Plugin.guilds), "- " + cPlayer + OddJob.getInstance().getPlayerManager().getName(pend), sender, false);
        }
    }

    public void guildsNoPending(String name, UUID player) {
        warning(type(Plugin.guilds), "There is no pending requests to join the " + cGuild + name, player, false);
    }

    public void guildAlreadyInvited(String string, UUID uuid) {
        warning(type(Plugin.guilds), cPlayer + string + cWarning + " is already invited to the Guild.", uuid, false);
    }

    public void guildAnotherInvite(String string, UUID uuid) {
        danger(type(Plugin.guilds), cPlayer + string + cDanger + " is already invited to another Guild.", uuid, false);
    }

    public void guildAlreadyPending(String string, UUID uuid) {
        warning(type(Plugin.guilds), cPlayer + string + cWarning + " has already requested to join the Guild.", uuid, false);
    }

    public void guildAnotherPending(String string, UUID uuid) {
        danger(type(Plugin.guilds), cPlayer + string + cDanger + " has already requested to join another Guild.", uuid, false);
    }

    public void guildAlreadyJoined(String string, UUID uuid) {
        warning(type(Plugin.guilds), cPlayer + string + cWarning + " is already a member of the Guild.", uuid, false);
    }

    public void guildAnotherJoined(String string, UUID uuid) {
        danger(type(Plugin.guilds), cPlayer + string + cDanger + " is already a member of another Guild.", uuid, false);
    }

    public void guildRoleNeeded(UUID uuid) {
        warning(type(Plugin.guilds), "Your role has not permission to perform this action.", uuid, false);
    }

    public void guildInvitedToGuild(Guild guild, UUID player, UUID sender) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(sender))
                success(type(Plugin.guilds), "You have successfully invited " + cPlayer + OddJob.getInstance().getPlayerManager().getName(player) + cSuccess + " to the Guild.", member, true);
            else
                success(type(Plugin.guilds), (cPlayer + OddJob.getInstance().getPlayerManager().getName(player) + cSuccess) + " has been invited to the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(sender), member, false);
        }
        success(type(Plugin.guilds), "You have been invited to the Guild " + cGuild + guild.getName(), player, false);
    }

    public void guildNotInvited(String string, UUID uuid) {
        danger(type(Plugin.guilds), cValue + string + cDanger + " were never invited to the Guild", uuid, false);
    }

    public void guildsUnInvited(Guild guild, UUID invited, CommandSender sender) {
        UUID uuid = ((Player) sender).getUniqueId();
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success(type(Plugin.guilds), "You have successfully removed invitation of " + cPlayer + OddJob.getInstance().getPlayerManager().getPlayer(invited) + cSuccess + " to the " + cGuild + guild.getName(), member, true);
            else
                warning(type(Plugin.guilds), (cPlayer + OddJob.getInstance().getPlayerManager().getName(invited) + cWarning) + " is no longer invited to the " + cGuild + guild + cWarning + ", removed by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
        danger(type(Plugin.guilds), "Your invitation to the " + cGuild + guild.getName() + cDanger + " has been revoked.", invited, false);
    }

    public void guildNotSame(UUID target, UUID uuid) {
        danger(type(Plugin.guilds), "You and " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cDanger + " is not in the same Guild.", uuid, false);
    }

    public void guildRoleHighest(UUID target, UUID uuid) {
        warning(type(Plugin.guilds), cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " has already the highest obtainable rank after GuildMaster.", uuid, false);
    }

    public void guildRoleHigher(UUID target, UUID uuid) {
        warning(type(Plugin.guilds), cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " has the same or higher rank than you.", uuid, false);
    }

    public void guildPromoted(Guild guild, UUID target, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success(type(Plugin.guilds), "You have successfully promoted " + cPlayer + OddJob.getInstance().getPlayerManager().getPlayer(target) + cSuccess + " to " + guild.getMembers().get(target).name() + " in the Guild.", member, true);
            else if (target.equals(member))
                success(type(Plugin.guilds), "You have been promoted to " + guild.getMembers().get(target).name() + " in the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), target, false);
            else
                warning(type(Plugin.guilds), (cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning) + " has been promoted to " + guild.getMembers().get(target).name() + " in the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
    }

    public void guildDemoted(Guild guild, UUID target, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success(type(Plugin.guilds), "You have successfully demoted " + cPlayer + OddJob.getInstance().getPlayerManager().getPlayer(target) + cSuccess + " to " + guild.getMembers().get(target).name() + " in the Guild.", member, true);
            else if (target.equals(member))
                danger(type(Plugin.guilds), "You have been demoted to " + guild.getMembers().get(target).name() + " in the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), target, false);
            else
                warning(type(Plugin.guilds), (cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning) + " has been demoted to " + guild.getMembers().get(target).name() + " in the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
    }

    public void guildRoleLowest(UUID target, UUID uuid) {
        warning(type(Plugin.guilds), cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " has already the lowest obtainable rank.", uuid, false);
    }

    public void guildNeedMaster(UUID uuid) {
        danger(type(Plugin.guilds), "Sorry, but only the GuildMaster may transfer the role to a new Player.", uuid, false);
    }

    public void guildAcceptPending(Guild guild, UUID target, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success(type(Plugin.guilds), "You have accepted the request from " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cSuccess + " to join the Guild", member, true);
            if (member.equals(target))
                success(type(Plugin.guilds), "Welcome to the Guild " + cGuild + guild.getName(), member, true);
            else
                warning(type(Plugin.guilds), (cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning) + "'s request to join the Guild has been accepted by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
    }

    public void guildAcceptInvite(Guild guild, CommandSender sender) {
        Player p = (Player) sender;
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(p.getUniqueId()))
                success(type(Plugin.guilds), "You have accepted the invitation to join the " + cGuild + guild.getName(), member, true);
            else
                warning(type(Plugin.guilds), cPlayer + sender.getName() + cWarning + " has accepted the invitation to join the " + cGuild + guild.getName(), member, false);
        }
    }

    public void guildsJoining(Guild guild, CommandSender sender) {
        Player p = (Player) sender;
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(p.getUniqueId()))
                success(type(Plugin.guilds), "Welcome to the " + cGuild + guild.getName(), member, true);
            else
                success(type(Plugin.guilds), "Please welcome " + cPlayer + sender.getName() + cSuccess + " to the " + cGuild + guild.getName(), member, false);
        }
    }

    public void guildsPending(Guild guild, CommandSender sender) {
        for (UUID member : guild.getMembers().keySet()) {
            warning(type(Plugin.guilds), cPlayer + sender.getName() + cWarning + " has requested to join the " + cGuild + guild.getName(), member, false);
        }
        success(type(Plugin.guilds), "You have sent a request to join the " + cGuild + guild.getName(), sender, true);
    }

    public void guildsDenyInvite(Guild guild, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            warning(type(Plugin.guilds), "Invitation sent to " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid) + cWarning + " to join the " + cGuild + guild.getName() + cWarning + " has been declined.", member, false);
        }
        success(type(Plugin.guilds), "You have declined the invitation to join the " + cGuild + guild.getName(), uuid, true);
    }

    public void guildsDenyRequest(Guild guild, UUID targetPlayer, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                warning(type(Plugin.guilds), "You have declined " + cPlayer + OddJob.getInstance().getPlayerManager().getName(targetPlayer) + cWarning + " request to join the " + cGuild + guild.getName(), member, true);
            else
                danger(type(Plugin.guilds), cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid) + cDanger + " has denied " + cPlayer + OddJob.getInstance().getPlayerManager().getName(targetPlayer) + "'s request to join the Guild", member, false);
        }
        warning(type(Plugin.guilds), "We are sorry to announce that your request to join " + cGuild + guild.getName() + cWarning + " has been declined.", targetPlayer, false);
    }

    public void guildSetConfirm(Guild guild, String key, String value, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(uuid))
                success(type(Plugin.guilds), "You have changed " + cValue + key + cSuccess + " changed to " + cValue + value + cSuccess + " for the Guild", member, false);
            else
                warning(type(Plugin.guilds), "Settings for " + cValue + key + cWarning + " changed to " + cValue + value + cWarning + " by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), member, false);
        }
    }

    public void guildMap(String string, Player player) {
        player.sendMessage(string);
    }

    public void guildNewMaster(Guild guild, UUID target, UUID next) {
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(next))
                success(type(Plugin.guilds), "You are the Chosen One to be the new GuildMaster since " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cSuccess + " left.", member, true);
            else
                warning(type(Plugin.guilds), cPlayer + OddJob.getInstance().getPlayerManager().getName(next) + cWarning + " is now your new GuildMaster.", member, false);
        }
    }

    public void guildKickPlayer(Guild guild, UUID target, UUID player, String reason) {
        String r = "";
        if (reason != null) r = " because " + cValue + reason;
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(player))
                success(type(Plugin.guilds), "You have successfully kick " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cSuccess + " from the Guild.", member, true);
            else
                warning(type(Plugin.guilds), cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cWarning + " has been kicked from the Guild by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(player) + cSuccess + r, member, false);
        }
        danger(type(Plugin.guilds), "You have been kicked from " + cGuild + guild.getName() + cDanger + " by " + cPlayer + OddJob.getInstance().getPlayerManager().getName(player) + cDanger + r, target, false);
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

    public void currencySuccessSet(String target, String amount, CommandSender sender, Types.AccountType account) {
        success(type(Plugin.currency), "You have sucessfully set " + cPlayer + target + cSuccess + "`s " + cValue + account + cSuccess + " to " + cValue + amount, sender, true);
    }

    public void currencySuccessAdded(String target, String amount, double balance, CommandSender sender, Types.AccountType account) {
        success(type(Plugin.currency), "You have successfully added " + cValue + amount + cSuccess + " to " + cPlayer + target + cSuccess + "`s " + cValue + account + cSuccess + ", new balance is " + cValue + balance, sender, false);
    }

    public void currencySuccessSubtracted(String target, String amount, double balance, CommandSender sender, Types.AccountType account) {
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
        success(type(Plugin.guilds), "Guild " + cGuild + name + cSuccess + " successfully created", sender, true);
    }

    public void infoGuildExists(String name, CommandSender sender) {
        success(type(Plugin.guilds), "Guild " + cGuild + name + cSuccess + " already exists", sender, false);
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

    public void currencyChanged(Types.AccountType type, double cost, double pocketBalance, UUID uuid, CommandSender sender) {
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
        danger(type(Plugin.locks), "Sorry, The SKELETON KEY is tooooooooo powerful!", uuid, true);
    }

    public void broken(UUID uuid) {
        warning(type(Plugin.locks), "Lock broken!", uuid, true);
    }

    public void ownedOther(UUID uuid) {
        danger(type(Plugin.locks), "This lock is owned by someone else!", uuid, false);
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
        success(type(Plugin.players), "You have added " + name + " to your Whitelist", sender, true);
    }

    public void whitelistDel(String name, CommandSender sender) {
        success(type(Plugin.players), "You have removed " + name + " from your Whitelist", sender, true);
    }

    public void blacklistAdd(String name, CommandSender sender) {
        success(type(Plugin.players), "You have added " + name + " to your Blacklist", sender, true);
    }

    public void blacklistDel(String name, CommandSender sender) {
        success(type(Plugin.players), "You have removed " + name + " from your Blacklist", sender, true);
    }

    public void playerSetDenyTPA(boolean deny, CommandSender player) {
        success(type(Plugin.players), "SET " + cValue + "denyTPA" + cSuccess + "to " + cValue + deny, player, true);
    }

    public void playerSetDenyTrade(boolean deny, CommandSender player) {
        success(type(Plugin.players), "SET " + cValue + "denyTrade" + cSuccess + "to " + cValue + deny, player, true);
    }

    public void playerSetScoreboard(String arg, String name, Player player) {
        success(type(Plugin.players), "SET " + arg + " to " + name, player, true);
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
        danger(type(Plugin.locks), "! This is a key to all your chests, keep in mind who you share it with.", sender, false);
        info(type(Plugin.locks), "! Stolen item or lost keys will not be refunded.", sender, false);
    }

    public void tpAlreadySent(OddPlayer previous, CommandSender sender) {
        danger(type(Plugin.teleport), cPlayer + sender.getName() + cDanger + " has changed his mind. Request cancelled.", previous.getPlayer(), false);
        danger(type(Plugin.teleport), "You have already sent an request, this changes your request.", sender, false);
    }

    public void tpDenied(String name, CommandSender sender) {
        warning(type(Plugin.teleport), cPlayer + name + cWarning + " has denied your request.", sender, false);
    }

    public void teleportRequestPlayer(Player topPlayer, CommandSender bottomPlayer) {
        warning(type(Plugin.teleport), cPlayer + topPlayer.getName() + cWarning + " want to be teleported to you. To accept this use '" + cValue + "/teleport accept" + cWarning + "'", bottomPlayer, false);
        success(type(Plugin.teleport), "You have requested to teleport to " + cPlayer + bottomPlayer.getName(), topPlayer, false);
    }

    public void tpAccepted(UUID destinationUUID, String name, UUID movingUUID, String movingPlayerName) {
        success(type(Plugin.teleport), "Your request has been accepted by " + cPlayer + name, movingUUID, false);
        success(type(Plugin.teleport), "You have accepted the request from " + cPlayer + movingPlayerName, destinationUUID, true);
    }

    public void tpNow(UUID movingUUID) {
        success(type(Plugin.teleport), "Teleporting now!", movingUUID, true);
    }

    public void tpInterrupt(UUID movingUUID) {
        danger(type(Plugin.teleport), "Interrupted, in combat!", movingUUID, true);
    }

    public void tpCountdown(int i, UUID movingUUID) {
        info(type(Plugin.teleport), "Teleporting in " + ChatColor.WHITE + i, movingUUID);
    }

    public void tpTeleporting(UUID movingUUID) {
        success(type(Plugin.teleport), "Teleporting!", movingUUID, true);
    }

    public void tpChanging(UUID movingUUID) {
        warning(type(Plugin.teleport), "Changing teleport location.", movingUUID, false);
    }

    public void teleportDeny(UUID playerUUID, String player, UUID targetUUID, String target) {
        danger(type(Plugin.teleport), "Your request has been denied by " + cPlayer + player, targetUUID, false);
        warning(type(Plugin.teleport), "You have denied the request from " + cPlayer + target, playerUUID, true);
    }

    public void tpTimedOut(UUID uuid) {
        danger(type(Plugin.teleport), "The teleport request has timed out", uuid, false);
    }

    public void tpError(CommandSender commandSender) {
        danger(type(Plugin.teleport), "Something went wrong when trying to teleport.", commandSender, false);
    }

    public void tpInvalid(CommandSender commandSender) {
        warning(type(Plugin.teleport), "Invalid x, y or z", commandSender, false);
    }

    public void tpPos(UUID uniqueId) {
        success(type(Plugin.teleport), "You have been teleported to a specific location", uniqueId, true);
    }

    public void tpPlayer(String destinationName, CommandSender name) {
        success(type(Plugin.teleport), "You have been teleported to " + destinationName, name, true);
    }

    public void teleportSpawn(CommandSender player) {
        success(type(Plugin.teleport), "You have been teleported to spawn", player, true);
    }

    public void tpAllTarget(CommandSender sender) {
        success(type(Plugin.teleport), "Say hi to everyone!", sender, true);
    }

    public void tpAllPlayer(Player target, String sender) {
        info(type(Plugin.teleport), "We are sorry to interrupt, but " + cPlayer + sender + cSuccess + " needs your full attension.", target, false);
    }

    public void tpPosTarget(CommandSender sender, Player player) {
        success(type(Plugin.teleport), "We 'will' successfulle teleport " + cPlayer + player.getName() + cSuccess + " to the specified location", sender, false);
        warning(type(Plugin.teleport), "You have carefully been selected to a transfer, sepcified by " + cPlayer + sender.getName(), player, false);
    }

    public void tradeRequest(Player topPlayer, Player bottomPlayer) {
        success(type(Plugin.trade), "You have sent a trade request to " + cPlayer + bottomPlayer.getName(), topPlayer, true);
        info(type(Plugin.trade), "You got a trade request from " + cPlayer + topPlayer.getName(), bottomPlayer, false);
    }

    public void tradeNotOnline(Player player) {
        warning(type(Plugin.trade), "Unfortunately your trade partner is not online", player, false);
    }

    public void tradeNone(Player player) {
        danger(type(Plugin.trade), "No players wanna trade with you", player, false);
    }

    public void guildClaiming(int count, int max, int x, int z, Player player, String guild) {
        success(type(Plugin.guilds), "Claiming:" + cValue + count + cSuccess + "/" + cValue + max + cSuccess + " chunk X:" + cValue + x + cSuccess + " Z:" + cValue + z + cSuccess + " World:" + cValue + player.getWorld().getName() + cSuccess + " to " + cGuild + guild, player, true);
    }

    public void guildClaimed(Player player) {
        danger(type(Plugin.guilds), "Already claimed", player, false);
    }

    public void guildOwnedBy(String guildNameByUUID, Player player) {
        danger(type(Plugin.guilds), "This chunk is owned by " + cGuild + guildNameByUUID, player, false);
    }

    public void guildNotAssociatedGuild(Player player) {
        danger(type(Plugin.guilds), "Sorry, you are not associated with the guild who claimed this chunk", player, false);
    }

    public void guildUnclaimed(int x, int z, Player player) {
        success(type(Plugin.guilds), "You have unclaimed X:" + cValue + x + cSuccess + " Z:" + cValue + z + cSuccess + " World:" + cValue + player.getWorld().getName(), player, true);
    }

    public void guildNotClaimed(Player player) {
        warning(type(Plugin.guilds), "Not claimed", player, false);
    }


    public void guildChangingZone(String guildNameByUUID, Player player) {
        warning(type(Plugin.guilds), "Changing Zone auto claim to " + cGuild + guildNameByUUID, player, true);
    }

    public void guildAutoOff(String guildNameByUUID, Player player) {
        warning(type(Plugin.guilds), "Turning off Zone auto claim to " + cGuild + guildNameByUUID, player, true);
    }

    public void guildAutoOn(String guildNameByUUID, Player player) {
        warning(type(Plugin.guilds), "You are now claiming zones for " + cGuild + guildNameByUUID, player, true);
    }

    public void lockEntityDamage(String name, Player damager) {
        warning(type(Plugin.locks), "Entity locked by " + cPlayer + name, damager, false);
    }

    public void lockEntityUnlock(Player damager) {
        warning(type(Plugin.locks), "Entity unlocked.", damager, true);
    }

    public void lockEntityLock(Player damager) {
        success(type(Plugin.locks), "Entity secure!", damager, true);
    }

    public void tradeAborted(String name, UUID player) {
        danger(type(Plugin.trade), "Trading aborted by " + cPlayer + name, player, false);
    }

    public void lockBlockOwned(String block, String name, Player player) {
        info(type(Plugin.locks), "The " + cValue + block + cInfo + " is owned by " + cPlayer + name, player, false);
    }

    public void lockSomeoneElse(Player player) {
        danger(type(Plugin.locks), "A lock is set by someone else.", player, false);
    }

    public void lockUnlocked(String block, Player player) {
        warning(type(Plugin.locks), "Unlocked " + cValue + block, player, true);
    }

    public void lockSkeletonOpen(Player player) {
        danger(type(Plugin.locks), "! Lock opened by the SkeletonKey", player, true);
    }

    public void lockOwned(Player player) {
        danger(type(Plugin.locks), "This block is locked by someone else.", player, false);
    }

    public void lockAlreadyBlock(Player player) {
        warning(type(Plugin.locks), "You have already locked this.", player, false);
    }

    public void lockAlreadyEntity(Player player) {
        danger(type(Plugin.locks), "A lock is already set on this.", player, false);
    }

    public void lockBlockLocked(String name, Player player) {
        success(type(Plugin.locks), "Locked " + cValue + name, player, true);
    }

    public void locksGuild(String guildNameByUUID, Player player) {
        warning(type(Plugin.locks), "Block is locked by the guild " + cGuild + guildNameByUUID, player, false);
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
        info(type(Plugin.locks), "Right click with the " + cValue + "Information tool" + cInfo + " to show the objects owner.", uniqueId);
        info(type(Plugin.locks), "Use the same command to remove the tool", uniqueId);
    }

    public void lockToolDel(UUID uniqueId) {
        info(type(Plugin.locks), "Right click with the " + cValue + "Remove material tool" + cInfo + " to remove it from the list.", uniqueId);
        info(type(Plugin.locks), "Use the same command to remove the tool", uniqueId);
    }

    public void lockToolAdd(UUID uniqueId) {
        info(type(Plugin.locks), "Right click with the " + cValue + "New material tool" + cInfo + " to add it to the list.", uniqueId);
        info(type(Plugin.locks), "Use the same command to remove the tool", uniqueId);
    }

    public void lockToolLock(UUID uniqueId) {
        info(type(Plugin.locks), "Right click with the " + cValue + "Locking tool" + cInfo + " to lock the object.", uniqueId);
        info(type(Plugin.locks), "Use the same command to remove the tool", uniqueId);
    }

    public void lockToolUnlock(UUID uniqueId) {
        info(type(Plugin.locks), "Right click with the " + cValue + "Unlocking tool" + cInfo + " to unlock the object.", uniqueId);
        info(type(Plugin.locks), "Use the same command to remove the tool", uniqueId);
    }

    public void playerDenying(String name, UUID player) {
        warning(type(Plugin.players), name + " is denying all request!", player, false);
    }

    public void lockNotCorrectPlayer(Player player) {
        warning(type(Plugin.locks), "You don't have the correct Player-Key", player, false);
    }

    public void lockNotCorrectGuild(Player player) {
        warning(type(Plugin.locks), "You don't have the correct Guild-Key", player, false);
    }

    public void lockOpened(String displayName, Player player) {
        success(type(Plugin.locks), "Lock opened with key: " + displayName, player, true);
    }

    public void errorScoreboard(CommandSender sender) {
        danger(type(Plugin.players), "Something went wrong!", sender, false);
    }

    public void cannotIdentify(String name, String account, Plugin type, CommandSender sender) {
        warning(type(type), "We can't find " + cValue + name + cWarning + " as " + cValue + account, sender, false);
    }

    public void guildMenu(Guild guild, String guildNameByUUID, Role guildMemberRole, CommandSender sender) {
        info(type(Plugin.guilds), "As a " + cValue + guildMemberRole.name() + cInfo + " of " + cGuild + guildNameByUUID, sender, false);
        info(type(Plugin.guilds), "Guild is open: " + String.valueOf(guild.isOpen()), sender, false);
        info(type(Plugin.guilds), "Friendly fire activated: " + String.valueOf(guild.getFriendlyFire()), sender, false);
        info(type(Plugin.guilds), "Claimed chunks: " + cValue + guild.getChunks() + cInfo + "/" + cValue + guild.getMaxClaims(), sender, false);
        info(type(Plugin.guilds), "Spawn set: " + cValue + (guild.getSpawn() != null), sender, false);
        info(type(Plugin.guilds), "Guild Bank account: " + cCurrency + OddJob.getInstance().getCurrencyManager().get(guild.getGuildUUID(), true).get(Types.AccountType.bank), sender, false);
        info(type(Plugin.guilds), "Players with role; " + cValue + "<player>:<role>", sender, false);
        for (UUID uuid : guild.getMembers().keySet()) {
            Role role = guild.getMembers().get(uuid);
            OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(uuid);
            info(type(Plugin.guilds), "" + cPlayer + oddPlayer.getName() + cInfo + ":" + cValue + role.name(), sender, false);
        }
    }

    public void homesCount(List<String> list, String target, int max, CommandSender sender, boolean self) {
        if (list == null) {
            homesNotSet(sender);
            return;
        }
        String count = (list.size() > max) ? ChatColor.RED + String.valueOf(list.size()) : String.valueOf(list.size());
        info(type(Plugin.home), ((self) ? "You" : cPlayer + target + cInfo) + " have assigned " + cValue + count + cInfo + " of " + cValue + max, sender, false);
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
        danger(type(Plugin.guilds), "Unknown zone " + cValue + arg, player, false);
    }

    public void guildLastOne(CommandSender sender) {
        warning(type(Plugin.guilds), "You are the last one, use " + cValue + "/guild disband" + cWarning + " instead.", sender, false);
    }

    public void changeRole(Role role, UUID target, CommandSender sender) {
        success(type(Plugin.guilds), "Your role in the guild has changed to " + cValue + role.name() + cSuccess + " by " + cPlayer + sender.getName(), target, false);
        success(type(Plugin.guilds), "You have changed the role of " + cPlayer + OddJob.getInstance().getPlayerManager().getName(target) + cSuccess + " to " + cValue + role.name(), sender, false);
    }

    public void guildWelcome(Guild guild, OfflinePlayer player) {
        for (UUID uuid : guild.getMembers().keySet()) {
            info(type(Plugin.guilds), "Please welcome " + player.getName() + " to the guild", uuid);
        }
    }

    public void pendingList(List<UUID> pending, CommandSender sender) {
        info(type(Plugin.guilds), "There are " + pending.size() + " players requesting to join your guild.", sender, false);
        space(Plugin.guilds, sender);
        int i = 1;
        for (UUID uuid : pending) {
            info(type(Plugin.guilds), i + ".) " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), sender, false);
        }
    }

    public void opSet(Player player, CommandSender sender, boolean self) {
        success(type(Plugin.op), "Successfully op'ed " + cPlayer + player.getName(), sender, false);
    }

    public void deopSet(Player player, CommandSender sender, boolean self) {
        success(type(Plugin.deop), "Successfully deop'ed " + cPlayer + player.getName(), sender, false);
    }

    public void areOp(OddPlayer target, CommandSender sender) {
        String op = target.isOp() ? cDanger + "OP" + cSuccess : cWarning + "NO OP" + cSuccess;
        success(type(Plugin.players), "You are " + op, sender, false);
    }

    public void lockList(List<String> list, CommandSender sender) {
        info(type(Plugin.locks), "You have " + cValue + list.size() + cInfo + " locked objects", sender, false);
        space(Plugin.locks, sender);
        for (int i = 1; i <= list.size(); i++) {
            info(type(Plugin.locks), i + "). " + list.get(i - 1), sender, false);
        }
    }

    public void space(Plugin type, CommandSender sender) {
        info(type(type), "------------------------------------", sender, false);
    }

    public void lockSkeleton(CommandSender sender) {
        danger(type(Plugin.locks), ChatColor.YELLOW + "!!!" + cDanger + " Danger " + ChatColor.YELLOW + "!!! " + cDanger + "Unlocks all objects for you!", sender, false);
    }

    public void locksKey(CommandSender sender) {
        success(type(Plugin.locks), "Here is your key to your locked objects, keep it safe!", sender, false);
    }

    public void homesChangedSuccess(String name, UUID uuid) {
        success(type(Plugin.home), "Location for " + cValue + name + cSuccess + " has been changed", uuid, false);
    }

    public void guildInfoSelf(Guild guild, CommandSender sender) {

    }

    public void playerErrorScoreboard(CommandSender sender, String board) {
        danger(type(Plugin.players), "Scoreboard " + cValue + board + cDanger + " not found", sender, false);
    }

    public void guildMaxClaimsReached(CommandSender sender) {
        danger(type(Plugin.guilds), "Claiming count reached maximum.", sender, false);
    }

    public void syntaxError(Plugin plugin, String s, CommandSender sender) {
        danger(type(plugin), "Unknown syntax: " + cValue + s, sender, false);
    }

    public void errorZone(Plugin plugin, String arg, CommandSender sender) {
        danger(type(plugin), "Unknown Zone: " + cValue + arg, sender, false);
    }

    public void arenaNameAlreadyExists(String arg, CommandSender sender) {
        danger(type(Plugin.arena), "Game with name " + cValue + arg + cDanger + " already exists", sender, false);
    }

    public void arenaTypeNotFound(String arg, CommandSender sender) {
        danger(type(Plugin.arena), "Arena Type " + cValue + arg + cDanger + " not found", sender, false);
    }

    public void arenaCreateSuccess(String name, CommandSender sender) {
        success(type(Plugin.arena), "Game " + cValue + name + cSuccess + " successfully created", sender, false);
    }

    public void errorCurrencyBankType(Plugin plugin, String arg, CommandSender sender) {
        danger(type(plugin), "Invalid bank-type " + cValue + arg, sender, false);
    }

    public void tpOffline(String name, Player player) {
        if (player != null) {
            danger(type(Plugin.teleport), "Player " + cPlayer + name + cDanger + " is no longer online", player, false);
        }
    }

    public void arenaList(CommandSender sender, List<String> names) {
        if (names.size() == 0) {
            warning(type(Plugin.arena), "There are no Games set", sender, false);
        } else {
            if (names.size() == 1) {
                info(type(Plugin.arena), "There is " + cValue + names.size() + cInfo + " Game set", sender, false);
            } else {
                info(type(Plugin.arena), "There are " + cValue + names.size() + cInfo + " Games set", sender, false);
            }
            space(Plugin.arena, sender);
            int i = 1;
            for (String name : names) {
                info(type(Plugin.arena), i++ + ".) " + cValue + name, sender, false);
            }
        }
    }

    public void guildFriendlyFireDisabled(Player damage) {
        warning(type(Plugin.guilds), "Friendlyfire is disabled", damage, false);
    }

    public void locksMaterialAdded(String name, Player player) {
        success(type(Plugin.locks), cValue + name + cSuccess + " added to the list of lockable materials", player, false);
    }

    public void lockMaterialRemoved(String name, Player player) {
        success(type(Plugin.locks), cValue + name + cSuccess + " removed from the list of lockable materials", player, false);
    }

    public void lockMaterialAlready(String name, Player player) {
        warning(type(Plugin.locks), cValue + name + cWarning + " already exists in the list of lockable materials", player, false);
    }

    public void teleportNoRequest(CommandSender sender) {
        warning(type(Plugin.teleport), "You have no awaiting requests", sender, false);
    }

    public void teleportRequestList(CommandSender sender, List<String> list) {
        int i = 1;
        info(type(Plugin.teleport), "There are " + cValue + list.size() + cInfo + " with teleport request", sender, false);
        space(Plugin.teleport, sender);
        for (String string : list) {
            info(type(Plugin.teleport), "" + (i++) + ".) " + cPlayer + string, sender, false);
        }
    }

    public void teleportNotOnline(UUID uuid) {
        danger(type(Plugin.teleport), "Target teleport is no longer online", uuid, false);
    }

    public void teleportDenied(OddPlayer topPlayer, OddPlayer bottomPlayer) {
        OddJob.getInstance().log("2");
        if (topPlayer != null)
            warning(type(Plugin.teleport), cPlayer + bottomPlayer.getName() + cWarning + " did not accept your request", topPlayer.getPlayer(), false);
        if (bottomPlayer != null && topPlayer != null)
            success(type(Plugin.teleport), "You have rejected teleport request from " + cPlayer + topPlayer.getName(), bottomPlayer.getPlayer(), false);
    }

    public void teleportTimedOut(UUID topUUID, UUID bottomUUID) {
        danger(type(Plugin.teleport), "Request timed out", topUUID, false);
        danger(type(Plugin.teleport), "Request timed out", bottomUUID, false);
    }

    public void tradedTopPlayer(Player topPlayer, Player bottomPlayer, double value) {
        success(type(Plugin.trade), "You have sent " + cValue + value + cSuccess + " to " + cPlayer + bottomPlayer.getUniqueId(), topPlayer, false);
        success(type(Plugin.trade), "You have recived " + cValue + value + cSuccess + " from " + cPlayer + topPlayer.getName(), bottomPlayer, false);
    }

    public void tradedBottomPlayer(Player topPlayer, Player bottomPlayer, double value) {
        success(type(Plugin.trade), "You have sent " + cValue + value + cSuccess + " to " + cPlayer + topPlayer.getUniqueId(), bottomPlayer, false);
        success(type(Plugin.trade), "You have recived " + cValue + value + cSuccess + " from " + cPlayer + bottomPlayer.getName(), topPlayer, false);
    }

    public void locksNoLocks(String name, CommandSender sender) {
        danger(type(Plugin.locks), cPlayer + name + cDanger + " has no locks set", sender, false);
    }

    public void locksUnlockedWithGuildKey(UUID uuid, String name) {
        warning(type(Plugin.locks), "Opened with key to " + cGuild + name, uuid, false);
    }

    public void locksOpenedOwnLock(UUID uuid) {
        success(type(Plugin.locks), "Opened your own lock", uuid, false);
    }

    public void locksOpenedWithPlayerKey(UUID uuid, UUID playerUUID) {
        String name = OddJob.getInstance().getPlayerManager().getName(uuid);
        warning(type(Plugin.locks), "Opened with key to " + cPlayer + name, playerUUID, false);
    }

    public void guildsListInvites(List<UUID> invites, CommandSender sender) {
        info(type(Plugin.guilds), "You have " + cValue + invites.size() + cInfo + " invitations to Guilds", sender, false);
        space(Plugin.guilds, sender);
        for (UUID uuid : invites) {
            info(type(Plugin.guilds), "- " + cGuild + OddJob.getInstance().getGuildManager().getGuildNameByUUID(uuid), sender, false);
        }
    }

    public void guildsAlreadyAssociated(String guild, String player, CommandSender sender) {
        danger(type(Plugin.guilds), "Sorry, " + cPlayer + player + cDanger + " is already associated with the " + cGuild + guild, sender, false);
    }

    public void guildsListInvitedPlayers(List<UUID> list, CommandSender sender) {
        String name = OddJob.getInstance().getGuildManager().getGuildNameByUUID(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(((Player) sender).getUniqueId()));
        if (list.size() == 0) {
            warning(type(Plugin.guilds), "There are no invitation sent from the " + cGuild + name, sender, false);
        } else if (list.size() == 1) {
            info(type(Plugin.guilds), "There is " + cValue + list.size() + cInfo + " invitation sent from the " + cGuild + name, sender, false);
            space(Plugin.guilds, sender);
            info(type(Plugin.guilds), "- " + cPlayer + OddJob.getInstance().getPlayerManager().getName(list.get(0)), sender, false);
        } else {
            info(type(Plugin.guilds), "There are " + cValue + list.size() + cInfo + " invitation sent from the " + cGuild + name, sender, false);
            space(Plugin.guilds, sender);
            for (UUID uuid : list) {
                info(type(Plugin.guilds), "- " + cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid), sender, false);
            }
        }
    }

    public void guildsSet(String name, Guild guild, boolean value, CommandSender sender) {
        success(type(Plugin.guilds), "Successfully " + cValue + name + cSuccess + " set to " + cValue + value + cSuccess + " for the " + cGuild + guild.getName(), sender, false);
    }

    public void auctionsBidSet(int item, double offer, Player player) {
        success(type(Plugin.auctions), "Bid successfully set, " + cCurrency + offer + cSuccess + " for item " + cValue + item, player, false);
    }

    public void auctionsBidNotHighEnough(double offer, double bid, Player player) {
        warning(type(Plugin.auctions), "So, the item is more expensive than " + cCurrency + offer + cWarning + ", current bid is on " + cCurrency + bid, player, false);
    }

    public void auctionsItemAlreadySold(Player player) {
        danger(type(Plugin.auctions), "Sorry, the item is already sold.", player, false);
    }

    public void auctionsCantAfford(double offer, Player player) {
        danger(type(Plugin.auctions), "Sorry, but you don't have " + cCurrency + offer + cDanger + " in your " + cValue + "pocket", player, false);
    }

    public void auctionsItemSetToSale(int id, double value, double buyout, double fee, int expire, Player player) {
        success(type(Plugin.auctions), "Auction id:" + cValue + id + cSuccess + "set for auction, start-bid:" + cCurrency + value + cSuccess + " buyout:" + cCurrency + buyout + cSuccess + ", the auction will expire in " + cValue + expire + cSuccess + " hours", player, false);
    }

    public void auctionsReceiverWon(AuctionItem auctionItem, Player receiver) {
        success(type(Plugin.auctions), "Your bid on item " + cValue + auctionItem.getId() + cSuccess + " won after " + cValue + auctionItem.getBids().size() + cSuccess + " bids, start-bid was " + cCurrency + auctionItem.getValue() + cSuccess + " and ended at " + cCurrency + auctionItem.getHighestValue(), receiver, false);
        warning(type(Plugin.auctions), "Please visit an auction-house to receive your item", receiver, false);
    }

    public void auctionsSoldWinner(AuctionItem auctionItem, AuctionBid highestBid, Player seller) {
        success(type(Plugin.auctions), "We found a winner for your action id:" + cValue + auctionItem.getId() + cSuccess + " after " + cValue + auctionItem.getBids().size() + cSuccess + " bids, " + cPlayer + OddJob.getInstance().getPlayerManager().getName(highestBid.getBidder()) + cSuccess + " won with an offer of " + cCurrency + highestBid.getBid(), seller, false);
        success(type(Plugin.auctions), "Your earning has been transferred to you " + cValue + Types.AccountType.bank.name() + cSuccess + ", please visit a bank to transfer to your " + cValue + Types.AccountType.pocket.name(), seller, false);
    }

    public void auctionsReceiverBuyout(AuctionItem auctionItem, Player receiver) {
        success(type(Plugin.auctions), "Yes, you took it right in front of everyone. Auction id:" + cValue + auctionItem.getId() + cSuccess + " sold on buyout:" + cCurrency + auctionItem.getBuyout() + cSuccess, receiver, false);
        warning(type(Plugin.auctions), "Please visit an auction-house to receive your item", receiver, false);
    }

    public void auctionsSoldBuyout(AuctionItem auctionItem, Player seller) {
        success(type(Plugin.auctions), "Ops your action id:" + cValue + auctionItem.getId() + cSuccess + " sold on buyout " + cCurrency + auctionItem.getBuyout(), seller, false);
        success(type(Plugin.auctions), "Your earning has been transferred to you " + cValue + Types.AccountType.bank.name() + cSuccess + ", please visit a bank to transfer to your " + cValue + Types.AccountType.pocket.name(), seller, false);
    }

    public void auctionsNoBidsOrBuyout(AuctionItem auctionItem, Player player) {
        warning(type(Plugin.auctions), "Sorry to say, but your auction-id:" + cValue + auctionItem.getId() + cSuccess + " didn't satisfy any players. Please visit an auction-house to receive your item", player, false);
    }

    public void teleRequestAlready(String player, CommandSender sender) {
        warning(type(Plugin.teleport), "Request to " + cPlayer + player + cWarning + " is already sent", sender, false);
    }

    public void teleportLeft(UUID sender, UUID player) {
        warning(type(Plugin.teleport), cPlayer + OddJob.getInstance().getPlayerManager().getName(player) + cWarning + " left the server, teleport aborted", sender, false);
    }

    public void teleportAccepted(Player topPlayer, Player bottomPlayer) {
        success(type(Plugin.teleport), cPlayer + topPlayer.getName() + cSuccess + " is coming for you!", bottomPlayer, false);
        success(type(Plugin.teleport), cPlayer + bottomPlayer.getName() + cSuccess + " accepted your request.", topPlayer, false);
    }

    public void errorBoolean(Plugin type, String arg, CommandSender sender) {
        danger(type(type), cValue + arg + cDanger + " is not a valid Boolean, valid values are " + cValue + "true|on|1|false|off|0", sender, false);
    }

    public void denyTPA(OddPlayer target, CommandSender sender) {
        String receiver = (target.getUuid() == ((Player)sender).getUniqueId())?"You ":cPlayer+target.getName()+cInfo+" " ;
        String deny = !target.getDenyTpa() ? cSuccess+"ACCEPTING"+cInfo : cDanger+"NOT ACCEPTING"+cInfo;
        info(type(Plugin.players),receiver+" are "+deny+" Teleport requests (tpa)",sender,false);
    }
    public void denyTrade(OddPlayer target, CommandSender sender) {
        String receiver = (target.getUuid() == ((Player)sender).getUniqueId())?"You ":cPlayer+target.getName()+cInfo+" " ;
        String deny = !target.getDenyTrade() ? cSuccess+"ACCEPTING"+cInfo : cDanger+"NOT ACCEPTING"+cInfo;
        info(type(Plugin.players),receiver+" are "+deny+" Trade requests",sender,false);
    }
}
