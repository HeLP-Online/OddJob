package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.Account;
import no.helponline.Utils.Guild;
import no.helponline.Utils.Odd.OddPlayer;
import no.helponline.Utils.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
    ChatColor cHome = ChatColor.GOLD;
    ChatColor cReset = ChatColor.RESET;

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
        danger("Sorry we can't find the player: " + cValue + string, commandSender, false);
    }

    public void errorWorld(String string, CommandSender commandSender) {
        danger("Sorry we can't find the world: " + cValue + string, commandSender, false);
    }

    public void errorGuild(String string, UUID player) {
        danger("Sorry, we can't find the guild: " + cValue + string, player, false);
    }

    public void errorArena(String string, UUID player) {
        danger("Sorry, we can't find the arena: " + cValue + string, player, false);
    }

    public void errorConsole() {
        Bukkit.getConsoleSender().sendMessage(cDanger + "Only usable as a player");
    }

    public void errorMaterial(String string, Player player) {
        danger("Unknown material " + cValue + string, player, false);
    }

    public void errorNumber(String string, Player player) {
        danger("Invalid number " + cValue + string, player, false);
    }

    public void errorHome(String name, CommandSender player) {
        player.sendMessage(ChatColor.RED + "Unknown home '" + ChatColor.YELLOW + name + ChatColor.RED + "'");
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
        target.sendMessage(cInfo + "[Guild - " + cGuild + name + cInfo + "] " + cReset + message);
    }


    public void insufficientItems(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Insufficient number of items.");
    }

    public void insufficientFunds(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Insufficient funds.");
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

    public void guildNameAlreadyExsits(String string, CommandSender sender) {
        danger("A Guild with the name " + cGuild + string + cDanger + " already exists.", sender, false);
    }

    public void guildAlreadyAssociated(String string, CommandSender sender) {
        danger("You are already associated with the Guild " + cGuild + string, sender, false);
    }


    public void guildCreateSuccessful(String string, CommandSender sender) {
        Player p = (Player) sender;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(p.getUniqueId()))
                success("You have successfully created the Guild " + cGuild + string, sender, true);
            else
                warning("A new Guild name " + cGuild + string + cWarning + " has been created by " + cPlayer + sender.getName(), player, false);
        }
    }

    public void guildCreateError(String string, CommandSender sender) {
        danger("Something went wrong when trying to create a Guild with the name " + cValue + string, sender, true);
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

    public void guildAcceptInvite(Guild guild, CommandSender sender) {
        Player p = (Player) sender;
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(p.getUniqueId()))
                success("You have accepted the invitation to join the " + cGuild + guild.getName(), member, true);
            else
                warning(cPlayer + sender.getName() + cWarning + " has accepted the invitation to join the " + cGuild + guild.getName(), member, false);
        }
    }

    public void guildJoining(Guild guild, CommandSender sender) {
        Player p = (Player) sender;
        for (UUID member : guild.getMembers().keySet()) {
            if (member.equals(p.getUniqueId()))
                success("Welcome to the Guild " + cGuild + guild.getName(), member, true);
            else
                success("Please welcome " + cPlayer + sender.getName() + cSuccess + " to the " + cGuild + guild.getName(), member, false);
        }
    }

    public void guildPending(Guild guild, CommandSender sender) {
        for (UUID member : guild.getMembers().keySet()) {
            warning(cPlayer + sender.getName() + cWarning + " has requested to join the " + cGuild + guild.getName(), member, false);
        }
        success("You have sent a request to join the " + cGuild + guild.getName(), sender, true);
    }

    public void guildDenyInvite(Guild guild, UUID uuid) {
        for (UUID member : guild.getMembers().keySet()) {
            warning(cPlayer + OddJob.getInstance().getPlayerManager().getName(uuid) + cWarning + "'s invitation to join the " + cGuild + guild.getName() + cWarning + " has been declined.", member, false);
        }
        success("You have declined the invitation to join the " + cGuild + guild.getName(), uuid, true);
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
        success("Area for Arena set!", player.getUniqueId(), false);
    }

    public void errorHomeMaximal(Player player) {
        danger("You have reached th maximal amout of homes.", player, false);
    }

    public void errorMissingArgs(CommandSender sender) {
        danger("Missing arguments", sender, false);
    }

    public void errorTooManyArgs(CommandSender sender) {
        danger("Too many args", sender, false);
    }

    public void infoCurrencyBalance(UUID uuid, double pocket, double bank) {
        info("You are holding " + cValue + pocket + cInfo + " in your " + cValue + "pocket" + cInfo + ", and have " + cValue + bank + cInfo + " in your " + cValue + "bank" + cInfo + " account.", uuid, false);
    }

    public void invalidNumber(String arg, CommandSender sender) {
        danger("'" + cWarning + arg + cDanger + "' is not a number", sender, false);
    }

    public void sendSyntax(String syntax, CommandSender sender) {
        warning("syntax: " + syntax, sender, false);
    }

    public void currencySuccessSet(String target, String amount, CommandSender sender, Account account) {
        success("You have sucessfully set " + cPlayer + target + cSuccess + "`s " + cValue + account + cSuccess + " to " + cValue + amount, sender, true);
    }

    public void currencySuccessAdded(String target, String amount, double balance, CommandSender sender, Account account) {
        success("You have successfully added " + cValue + amount + cSuccess + " to " + cPlayer + target + cSuccess + "`s " + cValue + account + cSuccess + ", new balance is " + cValue + balance, sender, true);
    }

    public void currencySuccessSubtracted(String target, String amount, double balance, CommandSender sender, Account account) {
        success("You have successfully subtracted " + cValue + amount + cSuccess + " from " + cPlayer + target + cSuccess + "`s " + cValue + account + cSuccess + ", new balance is " + cValue + balance, sender, true);
    }

    public void banRemoveSuccess(String name, CommandSender sender) {
        success("Ban removed from " + cValue + name, sender, true);
    }

    public void banRemoveError(String name, CommandSender sender) {
        danger("Player " + cValue + name + cDanger + " was never banned", sender, true);
    }

    public void banAddedSuccess(String name, String text, CommandSender sender) {
        success("Player " + cPlayer + name + cSuccess + " was successfully banned with the text '" + cValue + text + cSuccess + "'", sender, true);
    }

    public void banList(HashMap<OddPlayer, String> bans, CommandSender sender) {
        // There are ** players banned
        // ---------------------------
        // 1. <player> : <reason>
        info("There are " + cValue + bans.size() + cInfo + " players banned", sender, false);
        info("----------------------------------", sender, false);
        int i = 0;
        for (OddPlayer name : bans.keySet()) {
            i++;
            String reason = bans.get(name);
            info(cValue + "" + i + cInfo + ". " + cPlayer + name.getName() + cInfo + " : " + cValue + reason, sender, false);
        }
    }


    public void infoGuildCreated(String name, CommandSender sender) {
        success("Guild " + cGuild + name + cSuccess + " successfully created", sender, true);
    }

    public void infoGuildExists(String name, CommandSender sender) {
        success("Guild " + cGuild + name + cSuccess + " already exists", sender, false);
    }

    public void errorWrongArgs(CommandSender sender) {
        danger("Error wrong arguments", sender, false);
    }

    public void infoArgs(String args, CommandSender sender) {
        warning("Valid arguments are: " + cValue + args, sender, false);
    }
}
