package no.helponline.Commands;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.Role;
import no.helponline.Utils.Enum.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildCommand implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            if (strings.length == 0) {
                return true;
            }
            if (strings.length == 1 && strings[0].equalsIgnoreCase("load")) {
                OddJob.getInstance().getGuildManager().loadChunks();
            } else if (strings.length == 1 && strings[0].equalsIgnoreCase("save")) {
                OddJob.getInstance().getGuildManager().saveChunks();
            } else if (strings.length == 1 && strings[0].equalsIgnoreCase("create")) {
                UUID safe = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.SAFE);
                if (safe == null) {
                    OddJob.getInstance().getGuildManager().create("SafeZone", Zone.SAFE, true, false);
                    commandSender.sendMessage("SafeZone created!");
                } else {
                    commandSender.sendMessage("SafeZone exists");
                }
                UUID war = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WAR);
                if (war == null) {
                    OddJob.getInstance().getGuildManager().create("WarZone", Zone.WAR, true, false);
                    commandSender.sendMessage("WarZone created!");
                } else {
                    commandSender.sendMessage("WarZone exists");
                }
                UUID jail = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.JAIL);
                if (jail == null) {
                    OddJob.getInstance().getGuildManager().create("JailZone", Zone.JAIL, true, false);
                    commandSender.sendMessage("JailZone created!");
                } else {
                    commandSender.sendMessage("JailZone exists");
                }
                UUID arena = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.ARENA);
                if (arena == null) {
                    OddJob.getInstance().getGuildManager().create("WarZone", Zone.ARENA, true, false);
                    commandSender.sendMessage("ArenaZone created!");
                } else {
                    commandSender.sendMessage("ArenaZone exists");
                }
                UUID wild = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD);
                if (wild == null) {
                    OddJob.getInstance().getGuildManager().create("WildZone", Zone.WILD, true, false);
                    commandSender.sendMessage("WildZone created!");
                } else {
                    commandSender.sendMessage("WildZone exists");
                }
            }
            OddJob.getInstance().getMessageManager().errorConsole();
            return true;
        }

        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();
        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
        UUID target = null;
        UUID invite = null;
        UUID targetGuild = null;
        Role targetRole = null;
        Role triggerRole = null;
        UUID zone = null;
        boolean bol;

        if (command.getName().equalsIgnoreCase("guild")) {
            if (strings.length == 0) {
                return true;
            }
            switch (strings[0].toLowerCase()) {
                case "map":
                    OddJob.getInstance().getGuildManager().map(player);
                    return true;
                // Command '/guild create <name>'
                case "create":
                    if (strings.length < 2) {
                        OddJob.getInstance().getMessageManager().guildTooFewArguments(uuid);
                        return true;
                    }
                    if (strings.length > 2) {
                        OddJob.getInstance().getMessageManager().guildTooManyArguments(uuid);
                        return true;
                    }
                    if (OddJob.getInstance().getGuildManager().getGuildUUIDByName(strings[1]) != null) {
                        OddJob.getInstance().getMessageManager().guildNameAlreadyExsits(strings[1], uuid);
                        return true;
                    }
                    if (guild != null) {
                        OddJob.getInstance().getMessageManager().guildAlreadyAssociated(OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild), uuid);
                        return true;
                    }
                    if (OddJob.getInstance().getGuildManager().create(player.getUniqueId(), strings[1])) {
                        OddJob.getInstance().getMessageManager().guildCreateSuccessful(strings[1], uuid);
                        return true;
                    }
                    OddJob.getInstance().getMessageManager().guildCreateError(strings[1], uuid);
                    return true;
                // Command '/guild leave'
                case "leave":
                    if (strings.length > 1) {
                        OddJob.getInstance().getMessageManager().guildTooManyArguments(uuid);
                        return true;
                    }
                    if (guild == null) {
                        OddJob.getInstance().getMessageManager().guildNotAssociated(uuid);
                        return true;
                    }
                    OddJob.getInstance().getGuildManager().leave(uuid);
                    if (OddJob.getInstance().getGuildManager().getGuildMembers(guild).size() > 0) {
                        OddJob.getInstance().getMessageManager().guildLeaveSuccessful(OddJob.getInstance().getGuildManager().getGuild(guild), uuid);
                    } else {
                        OddJob.getInstance().getMessageManager().guildDisband(OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild));
                        OddJob.getInstance().getGuildManager().disband(guild);
                    }
                    return true;
                case "accept":
                    // Command '/guild accept'
                    if (guild == null) {
                        if (strings.length > 1) {
                            OddJob.getInstance().getMessageManager().guildTooManyArguments(uuid);
                            return true;
                        }
                        invite = OddJob.getInstance().getGuildManager().getGuildInvitation(uuid);
                        if (invite == null) {
                            OddJob.getInstance().getMessageManager().guildNoInvitation(uuid);
                            return true;
                        }
                        OddJob.getInstance().getGuildManager().acceptInvite(uuid);
                    } else {
                        if (strings.length == 1) {
                            List<UUID> pending = OddJob.getInstance().getGuildManager().getGuildPendingList(guild);
                            if (pending.size() > 0) {
                                OddJob.getInstance().getMessageManager().guildListPending("Pending requests to join the Guild", pending, uuid);
                                return true;
                            } else {
                                OddJob.getInstance().getMessageManager().guildNoPending(uuid);
                            }
                        } else {
                            target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                            if (target == null) {
                                OddJob.getInstance().getMessageManager().errorPlayer(strings[1], player);
                                return true;
                            }
                            OddJob.getInstance().getGuildManager().acceptPending(target, uuid);
                        }
                    }
                    return true;
                case "invite":
                    // Command '/guild invite <player>'
                    if (guild == null) {
                        OddJob.getInstance().getMessageManager().guildNotAssociated(uuid);
                        return true;
                    }
                    if (strings.length > 2) {
                        OddJob.getInstance().getMessageManager().guildTooManyArguments(uuid);
                        return true;
                    }
                    if (strings.length < 2) {
                        OddJob.getInstance().getMessageManager().guildTooFewArguments(uuid);
                        return true;
                    }
                    target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                    if (target == null) {
                        OddJob.getInstance().getMessageManager().errorPlayer(strings[1], player);
                        return true;
                    }

                    invite = OddJob.getInstance().getGuildManager().getGuildInvitation(target);
                    OddJob.getInstance().getMessageManager().console("Invitations " + OddJob.getInstance().getGuildManager().getGuildInvitations(guild).size());
                    if (guild.equals(invite)) {
                        OddJob.getInstance().getMessageManager().guildAlreadyInvited(strings[1], uuid);
                        return true;
                    } else if (invite != null) {
                        OddJob.getInstance().getMessageManager().guildAnotherInvite(strings[1], uuid);
                        return true;
                    }

                    UUID accepted = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(target);
                    if (guild.equals(accepted)) {
                        OddJob.getInstance().getMessageManager().guildAlreadyJoined(strings[1], uuid);
                        return true;
                    } else if (accepted != null) {
                        OddJob.getInstance().getMessageManager().guildAnotherJoined(strings[1], uuid);
                        return true;
                    }

                    UUID pending = OddJob.getInstance().getGuildManager().getGuildPending(target);
                    OddJob.getInstance().getMessageManager().console("Pending " + OddJob.getInstance().getGuildManager().getGuildPendingList(guild).size());
                    if (guild.equals(pending)) {
                        OddJob.getInstance().getMessageManager().guildAlreadyPending(strings[1], uuid);
                        //TODO player.sendMessage(strings[1] + " has an pending request to your guild. use '/guild accept <playername>' to accept the players request, or '/guild deny <playername>' to deny it.");
                        return true;
                    } else if (pending != null) {
                        OddJob.getInstance().getMessageManager().guildAnotherPending(strings[1], uuid);
                        //TODO player.sendMessage(strings[1] + " has an pending request to another guild, either the other guild have to decline it, or the player has to revoke the request.");
                        return true;
                    }

                    Role role = OddJob.getInstance().getGuildManager().getGuildMemberRole(uuid);
                    Role needed = OddJob.getInstance().getGuildManager().getGuildPermissionInvite(guild);

                    if (needed.level() <= role.level()) {
                        OddJob.getInstance().getGuildManager().inviteToGuild(guild, target, uuid);
                        return true;
                    }
                    OddJob.getInstance().getMessageManager().guildRoleNeeded(uuid);
                    return true;
                case "uninvite":
                    // Command '/guild uninvite <player>'
                    if (guild == null) {
                        OddJob.getInstance().getMessageManager().guildNotAssociated(uuid);
                        return true;
                    }
                    if (strings.length > 2) {
                        OddJob.getInstance().getMessageManager().guildTooManyArguments(uuid);
                        return true;
                    }
                    if (strings.length < 2) {
                        OddJob.getInstance().getMessageManager().guildTooFewArguments(uuid);
                        return true;
                    }
                    target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                    if (target == null) {
                        OddJob.getInstance().getMessageManager().errorPlayer(strings[1], player);
                        return true;
                    }

                    invite = OddJob.getInstance().getGuildManager().getGuildInvitation(target);
                    if (invite == null) {
                        OddJob.getInstance().getMessageManager().guildNotInvited(strings[1], uuid);
                    } else {
                        OddJob.getInstance().getGuildManager().unInviteToGuild(target);
                        OddJob.getInstance().getMessageManager().guildUninvited(OddJob.getInstance().getGuildManager().getGuild(guild), target, uuid);
                    }
                    return true;
                case "kick":
                    // Command '/guild kick <player> [reason]'
                    if (guild == null) {
                        OddJob.getInstance().getMessageManager().guildNotAssociated(uuid);
                        return true;
                    }
                    if (strings.length < 2) {
                        OddJob.getInstance().getMessageManager().guildTooFewArguments(uuid);
                        return true;
                    }
                    target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                    if (target == null) {
                        OddJob.getInstance().getMessageManager().errorPlayer(strings[1], player);
                        return true;
                    }

                    StringBuilder reason = new StringBuilder();
                    if (strings.length > 2) {
                        for (int i = 2; i < strings.length; i++) {
                            reason.append(strings[i]).append(" ");
                        }
                    }
                    OddJob.getInstance().getGuildManager().kickFromGuild(guild, target, player.getUniqueId(),reason.toString());
                    return true;
                case "list":
                    if (strings.length >= 2) {
                        switch (strings[1]) {
                            case "guilds":
                                OddJob.getInstance().getGuildManager().listGuilds();
                                return true;
                        }
                    }
                    break;
                case "promote":
                    // Command '/guild promote <player>'
                    if (guild == null) {
                        OddJob.getInstance().getMessageManager().guildNotAssociated(uuid);
                        return true;
                    }
                    if (strings.length > 2) {
                        OddJob.getInstance().getMessageManager().guildTooManyArguments(uuid);
                        return true;
                    }
                    if (strings.length < 2) {
                        OddJob.getInstance().getMessageManager().guildTooFewArguments(uuid);
                        return true;
                    }
                    target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                    if (target == null) {
                        OddJob.getInstance().getMessageManager().errorPlayer(strings[1], player);
                        return true;
                    }
                    targetGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(target);
                    if (!targetGuild.equals(guild)) {
                        OddJob.getInstance().getMessageManager().guildNotSame(target, uuid);
                        return true;
                    }

                    targetRole = OddJob.getInstance().getGuildManager().getGuildMemberRole(target);
                    triggerRole = OddJob.getInstance().getGuildManager().getGuildMemberRole(uuid);

                    if (targetRole.equals(Role.Admins)) {
                        OddJob.getInstance().getMessageManager().guildRoleHighest(target, uuid);
                        return true;
                    }
                    if (targetRole.level() >= triggerRole.level()) {
                        OddJob.getInstance().getMessageManager().guildRoleHigher(target, uuid);
                        return true;
                    }
                    targetRole = OddJob.getInstance().getGuildManager().promoteMember(guild, target);
                    OddJob.getInstance().getMessageManager().guildPromoted(OddJob.getInstance().getGuildManager().getGuild(guild), target, uuid);
                    return true;
                case "demote":
                    target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                    if (target == null) {
                        OddJob.getInstance().getMessageManager().errorPlayer(strings[1], player);
                        return true;
                    }
                    targetGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(target);
                    if (!targetGuild.equals(guild)) {
                        OddJob.getInstance().getMessageManager().guildNotSame(target, uuid);
                        return true;
                    }

                    targetRole = OddJob.getInstance().getGuildManager().getGuildMemberRole(target);
                    triggerRole = OddJob.getInstance().getGuildManager().getGuildMemberRole(uuid);

                    if (targetRole.equals(Role.Members)) {
                        OddJob.getInstance().getMessageManager().guildRoleLowest(target, uuid);
                        return true;
                    }
                    if (targetRole.level() >= triggerRole.level()) {
                        OddJob.getInstance().getMessageManager().guildRoleHigher(target, uuid);
                        return true;
                    }
                    targetRole = OddJob.getInstance().getGuildManager().demoteMember(guild, target);
                    OddJob.getInstance().getMessageManager().guildDemoted(OddJob.getInstance().getGuildManager().getGuild(guild), target, uuid);
                    return true;
                case "unclaim":
                    // Command '/guild unclaim'
                    if (guild == null) {
                        OddJob.getInstance().getMessageManager().guildNotAssociated(uuid);
                        return true;
                    }
                    if (strings.length != 1) {
                        OddJob.getInstance().getMessageManager().guildTooManyArguments(uuid);
                        return true;
                    }
                    OddJob.getInstance().getGuildManager().unClaim(player);
                    return true;
                case "claim":
                    // Command '/guild claim ...'
                    if (guild == null) {
                        OddJob.getInstance().getMessageManager().guildNotAssociated(uuid);
                        return true;
                    }
                    if (strings.length == 2 && strings[1].equalsIgnoreCase("auto")) {
                        OddJob.getInstance().getGuildManager().toggleAutoClaim(player, guild);
                        OddJob.getInstance().getGuildManager().claim(player, guild);
                        return true;
                    }
                    if (strings.length >= 2) {
                        guild = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.valueOf(strings[1].toUpperCase()));
                        if (guild != null) {
                            if (strings.length == 3 && strings[2].equalsIgnoreCase("auto")) {
                                OddJob.getInstance().getGuildManager().toggleAutoClaim(player, guild);
                            }
                            if (strings.length == 2 && !strings[1].equalsIgnoreCase("auto")) {
                                OddJob.getInstance().getGuildManager().claim(player, guild);
                            }
                        }
                    }
                    if (guild == null) guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(uuid);
                    OddJob.getInstance().getGuildManager().claim(player, guild);
                    return true;
                case "set":
                    // Command '/guild set <attr> <value>'
                    if (guild == null) {
                        OddJob.getInstance().getMessageManager().guildNotAssociated(uuid);
                        return true;
                    }
                    if (strings.length > 3) {
                        OddJob.getInstance().getMessageManager().guildTooManyArguments(uuid);
                        return true;
                    }
                    if (strings.length < 3) {
                        OddJob.getInstance().getMessageManager().guildTooFewArguments(uuid);
                        return true;
                    }

                    if (strings[1].equalsIgnoreCase("name")) {
                        triggerRole = OddJob.getInstance().getGuildManager().getGuildMemberRole(uuid);
                        if (OddJob.getInstance().getGuildManager().getGuildUUIDByName(strings[2]) != null && triggerRole.equals(Role.Admins)) {
                            OddJob.getInstance().getMessageManager().guildNameAlreadyExsits(strings[2], uuid);
                            return true;
                        }
                        OddJob.getInstance().getGuildManager().changeName(guild, strings[2]);
                        OddJob.getInstance().getMessageManager().guildSetConfirm(OddJob.getInstance().getGuildManager().getGuild(guild), strings[1], strings[2], uuid);
                        return true;
                    } else if (strings[1].equalsIgnoreCase("invited_only")) {
                        bol = Boolean.parseBoolean(strings[2]);
                        OddJob.getInstance().getGuildManager().changeInvitedOnly(guild, bol);
                        OddJob.getInstance().getMessageManager().guildSetConfirm(OddJob.getInstance().getGuildManager().getGuild(guild), strings[1], strings[2], uuid);
                        return true;
                    } else if (strings[1].equalsIgnoreCase("friendly_fire")) {
                        bol = Boolean.parseBoolean(strings[2]);
                        OddJob.getInstance().getGuildManager().changeFriendlyFire(guild, bol);
                        OddJob.getInstance().getMessageManager().guildSetConfirm(OddJob.getInstance().getGuildManager().getGuild(guild), strings[1], strings[2], uuid);
                        return true;
                    } else if (strings[1].equalsIgnoreCase("guild_master")) {
                        target = OddJob.getInstance().getPlayerManager().getUUID(strings[2]);
                        if (target == null) {
                            OddJob.getInstance().getMessageManager().errorPlayer(strings[2], player);
                            return true;
                        }
                        triggerRole = OddJob.getInstance().getGuildManager().getGuildMemberRole(uuid);
                        if (!triggerRole.equals(Role.Master)) {
                            OddJob.getInstance().getMessageManager().guildNeedMaster(uuid);
                            return true;
                        }
                        OddJob.getInstance().getGuildManager().changeGuildMaster(target, uuid);
                        OddJob.getInstance().getMessageManager().guildSetConfirm(OddJob.getInstance().getGuildManager().getGuild(guild), strings[1], strings[2], uuid);
                        return true;
                    }
                    return true;
                case "join":
                    // Command '/guild join <guild>'
                    if (guild != null) {
                        OddJob.getInstance().getMessageManager().guildAlreadyAssociated(OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild), uuid);
                        return true;
                    }
                    if (strings.length < 2) {
                        OddJob.getInstance().getMessageManager().guildTooFewArguments(uuid);
                        return true;
                    }
                    if (strings.length > 2) {
                        OddJob.getInstance().getMessageManager().guildTooManyArguments(uuid);
                        return true;
                    }
                    guild = OddJob.getInstance().getGuildManager().getGuildUUIDByName(strings[1]);
                    if (guild == null) {
                        OddJob.getInstance().getMessageManager().errorGuild(strings[1], uuid);
                        return true;
                    }
                    invite = OddJob.getInstance().getGuildManager().getGuildInvitation(uuid);
                    if (guild.equals(invite)) {
                        OddJob.getInstance().getGuildManager().join(guild, uuid);
                        OddJob.getInstance().getMessageManager().guildJoining(OddJob.getInstance().getGuildManager().getGuild(guild), uuid);
                        return true;
                    }
                    pending = OddJob.getInstance().getGuildManager().getGuildPending(uuid);
                    if (guild.equals(pending)) {
                        OddJob.getInstance().getMessageManager().guildAlreadyPending(strings[1], uuid);
                        return true;
                    }
                    OddJob.getInstance().getGuildManager().addGuildPending(guild, uuid);
                    OddJob.getInstance().getMessageManager().guildPending(OddJob.getInstance().getGuildManager().getGuild(guild), uuid);
                    return true;
                case "deny":
                    if (guild == null) {
                        invite = OddJob.getInstance().getGuildManager().getGuildInvitation(uuid);
                        if (invite != null) {
                            OddJob.getInstance().getGuildManager().denyInvite(uuid);
                            OddJob.getInstance().getMessageManager().guildDenyInvite(OddJob.getInstance().getGuildManager().getGuild(invite), uuid);
                            return true;
                        } else {
                            OddJob.getInstance().getMessageManager().guildNoInvitation(uuid);
                        }
                    } else {
                        //TODO permission
                        target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                        if (target == null) {
                            OddJob.getInstance().getMessageManager().errorPlayer(strings[1], player);
                            return true;
                        }
                        pending = OddJob.getInstance().getGuildManager().getGuildPending(target);
                        if (guild.equals(pending)) {
                            OddJob.getInstance().getGuildManager().denyRequest(target);
                            OddJob.getInstance().getMessageManager().guildDenyRequest(OddJob.getInstance().getGuildManager().getGuild(pending), target, uuid);
                            return true;
                        }
                    }
                default:
            }
        }
        return true;
    }


    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        String[] sub;
        if (strings.length == 1) {
            if (!(commandSender instanceof Player)) sub = new String[]{"create"};
            else {
                Player player = (Player) commandSender;
                UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                if (guild != null) {
                    sub = new String[]{"claim", "unclaim", "set", "list", "invite", "uninvite", "kick", "promote", "demote", "accept", "deny", "leave"};
                } else {
                    if (OddJob.getInstance().getGuildManager().getGuildInvitation(player.getUniqueId()) != null)
                        sub = new String[]{"create", "join", "map", "accept", "deny"};
                    else
                        sub = new String[]{"create", "join", "map"};
                }

            }
            for (String st : sub) {
                if (st.startsWith(strings[0].toLowerCase()) || strings[0].isEmpty()) {
                    list.add(st);
                }
            }
        } else if (strings.length == 2) {
            Player player = (Player) commandSender;
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
            if (guild == null) {
                if (strings[0].equalsIgnoreCase("join")) {
                    for (String name : OddJob.getInstance().getGuildManager().listGuildsToJoin(player.getUniqueId())) {
                        if (name.toLowerCase().startsWith(strings[1])) {
                            list.add(name);
                        }
                    }
                }
            } else {
                Role role = OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId());
                switch (strings[0]) {
                    case "claim":
                        list.add("auto");
                        break;
                    case "set":
                        list.add("name");
                        list.add("friendly_fire");
                        list.add("invited_only");
                        list.add("guild_master");
                        break;
                    case "invite":
                        for (UUID uuid : OddJob.getInstance().getPlayerManager().getUUIDs()) {
                            if (OddJob.getInstance().getGuildManager().getGuildUUIDByMember(uuid) != null) continue;
                            if (OddJob.getInstance().getPlayerManager().getName(uuid).toLowerCase().startsWith(strings[1].toLowerCase()))
                                list.add(OddJob.getInstance().getPlayerManager().getName(uuid));
                        }
                        break;
                    case "uninvite":
                        for (UUID uuid : OddJob.getInstance().getGuildManager().getGuildInvitations(guild)) {
                            if (OddJob.getInstance().getPlayerManager().getName(uuid).toLowerCase().startsWith(strings[1].toLowerCase()))
                                list.add(OddJob.getInstance().getPlayerManager().getName(uuid));
                        }
                        break;
                    case "kick":
                    case "promote":
                    case "demote":
                        for (UUID uuid : OddJob.getInstance().getGuildManager().getGuildMembers(guild)) {
                            Role targetRole = OddJob.getInstance().getGuildManager().getGuildMemberRole(uuid);
                            if (targetRole.level() < role.level()) {
                                if (OddJob.getInstance().getPlayerManager().getName(uuid).toLowerCase().startsWith(strings[1].toLowerCase()))
                                    list.add(OddJob.getInstance().getPlayerManager().getName(uuid));
                            }
                        }
                        break;
                    case "accept":
                    case "deny":
                        OddJob.getInstance().getMessageManager().console("Pendings: " + OddJob.getInstance().getGuildManager().getGuildPendingList(guild).size());
                        for (UUID uuid : OddJob.getInstance().getGuildManager().getGuildPendingList(guild)) {
                            if (OddJob.getInstance().getPlayerManager().getName(uuid).toLowerCase().startsWith(strings[1].toLowerCase()))
                                list.add(OddJob.getInstance().getPlayerManager().getName(uuid));
                        }
                        break;
                    default:
                        break;
                }
            }


        } else if (strings.length == 3 && strings[0].equalsIgnoreCase("set")) {
            Player player = (Player) commandSender;
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
            switch (strings[1]) {
                case "friendly_fire":
                case "invited_only":
                    list.add(Boolean.FALSE.toString());
                    list.add(Boolean.TRUE.toString());
                    break;
                case "guild_master":
                    for (UUID uuid : OddJob.getInstance().getGuildManager().getGuild(guild).getMembers().keySet()) {
                        if (!uuid.equals(player.getUniqueId())) {
                            list.add(OddJob.getInstance().getPlayerManager().getName(uuid));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return list;
    }

    enum Args {
        create("Creates a new guild"),
        join("Join an existing guild"),
        claim("Claim the chunk to your guild"),
        leave("Leave your guild");

        private final String help;

        Args(String help) {
            this.help = help;
        }

        public String get() {
            return this.help;
        }
    }
}
