package no.helponline.Commands;

import no.helponline.OddJob;
import no.helponline.Utils.Role;
import no.helponline.Utils.Zone;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        if (command.getName().equalsIgnoreCase("guild")) {
            // Command '/guild'
            if (strings.length == 0) {
                Bukkit.dispatchCommand(commandSender, command.getName() + " help");
                return true;
            }
            // Command '/guild create <Name>'
            else if (strings[0].equalsIgnoreCase("create")) {
                if (commandSender instanceof Player) {
                    if (strings.length == 1) {
                        // Missing Guild name
                        OddJob.getInstance().getMessageManager().danger("Missing a guild name", commandSender, false);
                        return true;
                    }

                    Player player = (Player) commandSender;

                    if (OddJob.getInstance().getGuildManager().getGuildUUIDByName(ChatColor.stripColor(strings[1])) != null) {
                        // Guild name already exists
                        OddJob.getInstance().getMessageManager().warning("We already know a guild by that name", commandSender, false);
                        return true;
                    }

                    if (OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()) != null) {
                        // Player is already associated with a Guild
                        OddJob.getInstance().getMessageManager().warning("You are already connected to a guild, to create a new use '/guild leave'", commandSender, false);
                        return true;
                    }

                    if (OddJob.getInstance().getGuildManager().create(player.getUniqueId(), strings[1])) {
                        // Creating Guild
                        OddJob.getInstance().getMessageManager().success("Guild `" + ChatColor.DARK_AQUA + strings[1] + ChatColor.GREEN + "` created", commandSender, true);
                        return true;
                    }

                    // ERROR
                    OddJob.getInstance().getMessageManager().danger("Something went wrong when creating guild!", commandSender, false);
                    return true;
                }
                // guild create executed by console, creating default zones
                else {
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
            }
            // Command '/guild leave'
            else if (strings[0].equalsIgnoreCase("leave")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;

                    UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());

                    if (guild != null) {
                        // The Player is associated with a Guild
                        OddJob.getInstance().getGuildManager().leave(player.getUniqueId());

                        if (OddJob.getInstance().getGuildManager().getGuildMembers(guild).size() > 0) {
                            // There are more members left in the Guild
                            OddJob.getInstance().getMessageManager().success("You have now left " + ChatColor.DARK_AQUA + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild), player, true);
                            OddJob.getInstance().getMessageManager().guild(ChatColor.YELLOW + "We are sorry to announce that " + ChatColor.DARK_AQUA + player.getName() + ChatColor.YELLOW + " has left the Guild", guild);
                        } else {
                            // The Guild is empty, disband it.
                            OddJob.getInstance().getGuildManager().disband(guild);
                            OddJob.getInstance().getMessageManager().broadcast(ChatColor.YELLOW + "No one left in " + ChatColor.DARK_AQUA + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild) + ChatColor.YELLOW + ", Guild disbanded!");
                        }
                    } else {
                        OddJob.getInstance().getMessageManager().errorGuild(commandSender);
                    }
                }
            }
            // Command '/guild accept'
            else if (strings[0].equalsIgnoreCase("accept")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());

                    if (guild == null) {
                        // The Player is NOT associated with any Guild
                        guild = OddJob.getInstance().getGuildManager().getGuildInvitation(player.getUniqueId());
                        if (guild != null) {
                            // The Player HAS an invitation to a Guild
                            OddJob.getInstance().getGuildManager().accept(guild, player.getUniqueId());
                        } else {
                            // The Player does NOT have any invitation to any Guilds
                            OddJob.getInstance().getMessageManager().warning("Nothing to accept.", player, false);
                        }
                        return true;
                    } else {
                        // The Player IS associated with a Guild
                        if (strings.length == 1) {
                            // Command '/guild accept'
                            List<UUID> pending = OddJob.getInstance().getGuildManager().getGuildPendingList(guild);
                            int c = 0;
                            StringBuilder sb = new StringBuilder();
                            player.sendMessage("List of pending request to join the Guild:");
                            if (pending.size() > 0) {
                                // One or more pending
                                for (UUID uuid : pending) {
                                    c++;
                                    sb.append(c).append(".) ").append(OddJob.getInstance().getPlayerManager().getName(uuid));
                                }
                            } else {
                                // NO pending
                                sb.append("No pending request.");
                            }
                            player.sendMessage(sb.toString());
                        } else {
                            // Command '/guild accept <player>'
                            UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                            if (target == null) {
                                OddJob.getInstance().getMessageManager().errorPlayer("Sorry, we can't find " + strings[1], player);
                                return true;
                            }

                            // Accept the pending request by Player to join the Guild
                            OddJob.getInstance().getGuildManager().accept(guild, target);
                        }
                    }
                }
            }
            // Command '/guild invite'
            else if (strings[0].equalsIgnoreCase("invite")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                    if (guild == null) {
                        OddJob.getInstance().getMessageManager().errorGuild(commandSender);
                        return true;
                    }
                    if (strings.length == 1) {
                        // Not enough parameters
                        commandSender.sendMessage("Missing a player to invite to your guild.");
                        return true;
                    }
                    if (strings.length == 2) {
                        // Command '/guild invite <player>'
                        UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                        if (target == null) {
                            OddJob.getInstance().getMessageManager().errorPlayer(strings[1], commandSender);
                            return true;
                        }

                        UUID invite = OddJob.getInstance().getGuildManager().getGuildInvitation(target);
                        if (guild.equals(invite)) {
                            // Invite the Player to your Guild
                            player.sendMessage(strings[1] + " has already been invited to this guild.");
                            return true;
                        } else if (invite != null) {
                            // The invited Player is already invited to another Guild
                            player.sendMessage(strings[1] + " has already been invited to another guild.");
                            return true;
                        }

                        UUID accepted = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(target);
                        if (guild.equals(accepted)) {
                            // The Player has already joined your Guild
                            player.sendMessage(strings[1] + " has already joined this guild.");
                            return true;
                        } else if (accepted != null) {
                            // The Player has already joined another Guild
                            player.sendMessage(strings[1] + " has already joined a guild.");
                            return true;
                        }

                        UUID pending = OddJob.getInstance().getGuildManager().getGuildPending(target);
                        if (guild.equals(pending)) {
                            // Player has a pending invitation to your Guild
                            player.sendMessage(strings[1] + " has an pending request to your guild. use '/guild accept <playername>' to accept the players request, or '/guild deny <playername>' to deny it.");
                            return true;
                        } else if (pending != null) {
                            // Player has a pending invitation to another Guild
                            player.sendMessage(strings[1] + " has an pending request to another guild, either the other guild have to decline it, or the player has to revoke the request.");
                            return true;
                        }

                        if (OddJob.getInstance().getGuildManager().getGuildPermissionInvite(guild).level() <= OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId()).level()) {
                            // The Player have access to invite new Players as members to the Guild

                            // Invite Player to the Guild
                            OddJob.getInstance().getGuildManager().inviteToGuild(guild, target);

                            OddJob.getInstance().getMessageManager().info("You have invited " + strings[1] + " to join " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild), player, false);
                            Player op = Bukkit.getPlayer(target);
                            if (op != null) {
                                OddJob.getInstance().getMessageManager().info("You have been invited to join the guild " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild), target, false);
                            }
                        }
                    }
                }
            }
            // Command '/guild uninvite'
            else if (strings[0].equalsIgnoreCase("uninvite")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                    if (guild == null) {
                        OddJob.getInstance().getMessageManager().errorGuild(commandSender);
                        return true;
                    }
                    if (strings.length == 1) {
                        commandSender.sendMessage("Missing a player to uninvite to your guild.");
                        return true;
                    }
                    if (strings.length == 2) {
                        UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                        if (target == null) {
                            OddJob.getInstance().getMessageManager().errorPlayer("Sorry, we can't find " + strings[1], commandSender);
                            return true;
                        }
                        if (guild.equals(OddJob.getInstance().getGuildManager().getGuildInvitation(target))) {
                            // Player has an invitation to the Guild

                            // Remove the invitation to the Guild
                            OddJob.getInstance().getGuildManager().unInviteToGuild(target);

                            OddJob.getInstance().getMessageManager().info(strings[1] + " is no longer invited to " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild), player, false);
                            OddJob.getInstance().getMessageManager().success("You are no longer invited to the guild " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild), target, false);
                            return true;
                        } else {
                            // Player has never been invited to the Guild
                            player.sendMessage(strings[1] + " has never been invited to your guild.");
                        }
                    }
                }
            }
            // Command '/guild kick'
            else if (strings[0].equalsIgnoreCase("kick")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                    if (guild == null) {
                        OddJob.getInstance().getMessageManager().errorGuild(commandSender);
                        return true;
                    }
                    if (strings.length == 1) {
                        commandSender.sendMessage("Missing a player to kick to your guild.");
                        return true;
                    }
                    UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                    if (target == null) {
                        OddJob.getInstance().getMessageManager().errorPlayer(strings[1], commandSender);
                        return true;
                    }
                    StringBuilder reason = new StringBuilder();
                    if (strings.length > 2) {
                        for (int i = 2; i < strings.length; i++) {
                            reason.append(strings[i]).append(" ");
                        }
                    }
                    OddJob.getInstance().getGuildManager().kickFromGuild(guild, target, reason.toString());
                    player.sendMessage(strings[1] + " has left the guild.");
                }
            } else if (strings[0].equalsIgnoreCase("list")) {
                if (strings.length == 2) {
                    if (strings[1].equalsIgnoreCase("guilds")) {
                        StringBuilder sb = new StringBuilder();
                        for (UUID uuid : OddJob.getInstance().getGuildManager().getGuilds()) {
                            if (OddJob.getInstance().getGuildManager().getZoneByGuild(uuid).equals(Zone.GUILD)) {
                                sb.append(OddJob.getInstance().getGuildManager().getGuildNameByUUID(uuid)).append(": ").append(OddJob.getInstance().getGuildManager().getGuildMembers(uuid).size()).append(", ");
                            }
                        }
                        commandSender.sendMessage("Guilds:\n" + sb.toString());
                    }
                }
                if (strings.length == 3) {
                    if (strings[1].equalsIgnoreCase("members")) {
                        UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByName(strings[2]);
                        if (guildUUID == null) {
                            OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender, false);
                            return true;
                        }
                        StringBuilder sb = new StringBuilder();
                        for (UUID uuid : OddJob.getInstance().getGuildManager().getGuildMembers(guildUUID)) {
                            sb.append(OddJob.getInstance().getPlayerManager().getName(uuid)).append(", ");
                        }
                        commandSender.sendMessage("Members of " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guildUUID) + ":\n" + sb.toString());
                    }
                }
            } else if (strings.length == 2 && strings[0].equalsIgnoreCase("promote")) {
                if (!(commandSender instanceof Player)) return true;

                Player player = (Player) commandSender;
                UUID targetUUID = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                if (targetUUID == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(strings[1], commandSender);
                    return true;
                }
                UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                if (OddJob.getInstance().getGuildManager().getGuildUUIDByMember(targetUUID).equals(guildUUID)) {
                    Role targetRole = OddJob.getInstance().getGuildManager().getGuildMemberRole(targetUUID);
                    Role triggerRole = OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId());

                    if (targetRole.equals(Role.Admins)) {
                        OddJob.getInstance().getMessageManager().danger(OddJob.getInstance().getPlayerManager().getName(targetUUID) + " already have the highest rank.", commandSender, false);
                        return true;
                    }
                    if (targetRole.level() >= triggerRole.level()) {
                        OddJob.getInstance().getMessageManager().danger(OddJob.getInstance().getPlayerManager().getName(targetUUID) + " have same or higher rank than you.", commandSender, false);
                        return true;
                    }
                    targetRole = OddJob.getInstance().getGuildManager().promoteMember(guildUUID, targetUUID);
                    OddJob.getInstance().getMessageManager().success("Promoted " + OddJob.getInstance().getPlayerManager().getName(targetUUID) + " to " + ChatColor.GOLD + targetRole.name(), commandSender, true);
                    if (OddJob.getInstance().getPlayerManager().getPlayer(targetUUID).isOnline()) {
                        OddJob.getInstance().getMessageManager().success("You have been promoted to " + ChatColor.GOLD + targetRole.name() + ChatColor.RESET + " by " + commandSender.getName(), targetUUID, false);
                    }

                }
            } else if (strings.length == 2 && strings[0].equalsIgnoreCase("demote")) {
                if (!(commandSender instanceof Player)) return true;

                Player player = (Player) commandSender;
                UUID targetUUID = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                if (targetUUID == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender, false);
                    return true;
                }
                UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                if (OddJob.getInstance().getGuildManager().getGuildUUIDByMember(targetUUID).equals(guildUUID)) {
                    Role targetRole = OddJob.getInstance().getGuildManager().getGuildMemberRole(targetUUID);
                    Role triggerRole = OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId());

                    if (targetRole.equals(Role.Members)) {
                        OddJob.getInstance().getMessageManager().danger(OddJob.getInstance().getPlayerManager().getName(targetUUID) + " already have the lowest rank.", commandSender, false);
                        return true;
                    }
                    if (targetRole.level() >= triggerRole.level()) {
                        OddJob.getInstance().getMessageManager().danger(OddJob.getInstance().getPlayerManager().getName(targetUUID) + " have same or higher rank than you.", commandSender, false);
                        return true;
                    }
                    targetRole = OddJob.getInstance().getGuildManager().demoteMember(guildUUID, targetUUID);
                    OddJob.getInstance().getMessageManager().success("Demoted " + OddJob.getInstance().getPlayerManager().getName(targetUUID) + " to " + ChatColor.GOLD + targetRole.name(), commandSender, true);
                    if (OddJob.getInstance().getPlayerManager().getPlayer(targetUUID).isOnline()) {
                        OddJob.getInstance().getMessageManager().success("You have been demoted to " + ChatColor.GOLD + targetRole.name() + ChatColor.RESET + " by " + commandSender.getName(), targetUUID, false);
                    }

                }
            } else if (strings[0].equalsIgnoreCase("unclaim")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                    if (guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
                        commandSender.sendMessage("Sorry, you are not associated with any guild yet.");
                        return true;
                    }
                    OddJob.getInstance().getGuildManager().unClaim(player);
                }
            } else if (strings[0].equalsIgnoreCase("claim")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if (strings.length == 1) {
                        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                        if (guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
                            commandSender.sendMessage("Sorry, you are not associated with any guild yet.");
                            return true;
                        }
                        OddJob.getInstance().getGuildManager().claim(player);
                    } else if (strings.length == 2 && strings[1].equalsIgnoreCase("auto")) {
                        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                        if (guild == null) {
                            commandSender.sendMessage("Sorry, you are not associated with any guild yet.");
                            return true;
                        }
                        OddJob.getInstance().getGuildManager().toggleAutoClaim(player, Zone.GUILD);
                    }

                    if (strings.length > 1) {
                        if (strings[1].equalsIgnoreCase("safe")) {
                            if (strings.length == 3 && strings[2].equalsIgnoreCase("auto")) {
                                OddJob.getInstance().getGuildManager().toggleAutoClaim(player, Zone.SAFE);
                            } else {
                                OddJob.getInstance().getGuildManager().claim(player, Zone.SAFE);
                            }
                        } else if (strings[1].equalsIgnoreCase("war")) {
                            if (strings.length == 3 && strings[2].equalsIgnoreCase("auto")) {
                                OddJob.getInstance().getGuildManager().toggleAutoClaim(player, Zone.WAR);
                            } else {
                                OddJob.getInstance().getGuildManager().claim(player, Zone.WAR);
                            }
                        } else if (strings[1].equalsIgnoreCase("jail")) {
                            if (strings.length == 3 && strings[2].equalsIgnoreCase("auto")) {
                                OddJob.getInstance().getGuildManager().toggleAutoClaim(player, Zone.JAIL);
                            } else {
                                OddJob.getInstance().getGuildManager().claim(player, Zone.JAIL);
                            }
                        } else if (strings[1].equalsIgnoreCase("arena")) {
                            if (strings.length == 3 && strings[2].equalsIgnoreCase("auto")) {
                                OddJob.getInstance().getGuildManager().toggleAutoClaim(player, Zone.ARENA);
                            } else {
                                OddJob.getInstance().getGuildManager().claim(player, Zone.ARENA);
                            }
                        }
                    }
                }
            } else if (strings[0].equalsIgnoreCase("set")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if (strings.length >= 3) {
                        if (strings[1].equalsIgnoreCase("name")) {
                            OddJob.getInstance().getGuildManager().changeName(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()), strings[2]);
                        } else if (strings[1].equalsIgnoreCase("invited_only")) {
                            try {
                                boolean bol = Boolean.parseBoolean(strings[2]);
                                OddJob.getInstance().getGuildManager().changeInvitedOnly(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()), bol);
                            } catch (Exception e) {
                            }

                        } else if (strings[1].equalsIgnoreCase("friendly_fire")) {
                            try {
                                boolean bol = Boolean.parseBoolean(strings[2]);
                                OddJob.getInstance().getGuildManager().changeFriendlyFire(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()), bol);
                            } catch (Exception e) {
                            }

                        }
                    }
                }
            } else if (strings[0].equalsIgnoreCase("join")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    UUID invite = OddJob.getInstance().getGuildManager().getGuildInvitation(player.getUniqueId());
                    if (invite != null) {
                        OddJob.getInstance().getGuildManager().join(invite, player.getUniqueId());
                        player.sendMessage("You have successfully joined " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(invite));
                        //TODO announce to the rest of the guild
                    }
                    if (strings.length == 2) {
                        UUID join = OddJob.getInstance().getGuildManager().getGuildUUIDByName(strings[1]);
                        if (join == null) {
                            player.sendMessage("Sorry, we don't know about " + strings[1]);
                            return true;
                        }
                        UUID pend = OddJob.getInstance().getGuildManager().getGuildPending(player.getUniqueId());
                        if (join.equals(pend)) {
                            player.sendMessage("We have already annonuced the guild about your pending request to join " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(pend));
                            return true;
                        }
                        OddJob.getInstance().getGuildManager().addGuildPending(join, player.getUniqueId());
                        player.sendMessage("We have now announced your pending interest to join the guild " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(join));
                        OddJob.getInstance().log(join.toString());
                        OddJob.getInstance().log("members: " + OddJob.getInstance().getGuildManager().getGuildMembers(join).size());
                        for (UUID member : OddJob.getInstance().getGuildManager().getGuildMembers(join)) {
                            OddJob.getInstance().getMessageManager().info("Your guild has got a new member request from " + player.getName(), member, false);
                        }
                        return true;
                    }
                }
            } else if (strings.length == 2 && strings[0].equalsIgnoreCase("save")) {
                switch (strings[1]) {
                    case "guilds":
                        OddJob.getInstance().getGuildManager().saveGuilds();
                        break;
                    case "chunks":
                        OddJob.getInstance().getGuildManager().saveChunks();
                        break;
                }
            } else if (strings.length == 2 && strings[0].equalsIgnoreCase("load")) {
                switch (strings[1]) {
                    case "guilds":
                        OddJob.getInstance().getGuildManager().loadGuilds();
                        break;
                    case "chunks":
                        OddJob.getInstance().getGuildManager().loadChunks();
                        break;
                }
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length == 1 || strings.length == 0) {
            String[] st;
            if ((commandSender instanceof Player) && OddJob.getInstance().getGuildManager().getGuildUUIDByMember(((Player) commandSender).getUniqueId()) != null) {
                st = new String[]{"claim", "unclaim", "set", "list", "invite", "uninvite", "kick", "promote", "demote", "accept", "deny"};
            } else if (commandSender instanceof Player) {
                st = new String[]{"create", "join"};
            } else {
                st = new String[]{"create", "set", "list", "invite", "uninvite", "kick"};
            }

            for (String t : st) {
                if (t.startsWith(strings[0])) {
                    list.add(t);
                }
            }
        }
        if (strings[0].equalsIgnoreCase("promote")) {
            String[] st;
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                Role role = OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId());
                UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                if (guild != null) {
                    for (UUID uuid : OddJob.getInstance().getGuildManager().getGuildMembers(guild)) {
                        Role rule = OddJob.getInstance().getGuildManager().getGuildMemberRole(uuid);
                        if (role.level() > rule.level() && !rule.equals(Role.Admins)) {
                            list.add(OddJob.getInstance().getPlayerManager().getName(uuid));
                        }
                    }
                }
            }
        }
        if (strings[0].equalsIgnoreCase("demote")) {
            String[] st;
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                Role role = OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId());
                UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                if (guild != null) {
                    for (UUID uuid : OddJob.getInstance().getGuildManager().getGuildMembers(guild)) {
                        Role rule = OddJob.getInstance().getGuildManager().getGuildMemberRole(uuid);
                        if (role.level() > rule.level() && !rule.equals(Role.Members)) {
                            list.add(OddJob.getInstance().getPlayerManager().getName(uuid));
                        }
                    }
                }
            }
        }
        if (strings[0].equalsIgnoreCase("list")) {
            if (strings.length == 2) {
                list.add("members");
                list.add("guilds");
            } else if (strings.length == 3) {
                if (strings[1].equalsIgnoreCase("members")) {
                    for (UUID guild : OddJob.getInstance().getGuildManager().getGuilds()) {
                        list.add(OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild));
                    }
                }
            }
        }
        if (strings[0].equalsIgnoreCase("uninvite")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                for (UUID uuid : OddJob.getInstance().getGuildManager().getGuildInvitations(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()))) {
                    list.add(OddJob.getInstance().getPlayerManager().getName(uuid));
                }
            }
        }
        if (strings[0].equalsIgnoreCase("invite")) {
            for (UUID player : OddJob.getInstance().getPlayerManager().getUUIDs()) {
                if (OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player) == null
                        && OddJob.getInstance().getGuildManager().getGuildInvitation(player) == null
                        && OddJob.getInstance().getGuildManager().getGuildPending(player) == null) {
                    list.add(OddJob.getInstance().getPlayerManager().getName(player));
                }
            }
        } else if (strings[0].equalsIgnoreCase("join")) {
            OddJob.getInstance().log("join");
            if (strings.length == 2) {
                if (commandSender instanceof Player) {
                    list.addAll(OddJob.getInstance().getGuildManager().listGuildsToJoin(((Player) commandSender).getUniqueId()));
                }
            }
        } else if (strings[0].equalsIgnoreCase("claim")) {
            if (strings.length == 2) {
                for (Zone z : Zone.values()) {
                    if (commandSender.hasPermission("guild.claim." + z.name()) && z.name().startsWith(strings[1])) {
                        list.add(z.name());
                    }
                }
            } else if (strings.length == 3) {
                if (commandSender.hasPermission("guild.claim." + strings[1] + ".auto")) {
                    list.add("auto");
                }
            }
        } else if (strings[0].equalsIgnoreCase("set")) {
            if (strings.length == 2) {
                String[] st = new String[]{"friendly_fire", "invited_only", "name"};
                for (String t : st) {
                    if (t.startsWith(strings[1])) {
                        list.add(t);
                    }
                }
            } else if (strings.length == 3) {
                if (strings[1].equalsIgnoreCase("friendly_fire") || strings[1].equalsIgnoreCase("invited_only")) {
                    String[] st = new String[]{"TRUE", "FALSE"};
                    for (String t : st) {
                        if (t.startsWith(strings[2])) {
                            list.add(t);
                        }
                    }
                }
            }
        }

        return list;
    }

    enum Args {
        create("Creates a new guild"),
        join("Join an existing guild"),
        claim("Claim the chunk to your guild"),
        leave("Leave your guild");

        private String help;

        Args(String help) {
            this.help = help;
        }

        public String get() {
            return this.help;
        }
    }
}
