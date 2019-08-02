package no.helponline.Commands;

import no.helponline.OddJob;
import no.helponline.Utils.Zone;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
            //guild
            if (strings.length == 0) {
                Bukkit.dispatchCommand(commandSender, command.getName() + " help");
                return true;
            } else if (strings[0].equalsIgnoreCase("create")) {
                // guild create <Name>
                if (commandSender instanceof Player) {
                    if (strings.length == 1) {
                        commandSender.sendMessage("Missing a guild name");
                        return true;
                    }
                    Player player = (Player) commandSender;
                    if (OddJob.getInstance().getGuildManager().getGuildUUIDByName(ChatColor.stripColor(strings[1])) != null) {
                        OddJob.getInstance().getMessageManager().warning("We already know a guild by that name", commandSender);
                        return true;
                    }
                    if (OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()) != null) {
                        OddJob.getInstance().getMessageManager().danger("You are already connected to a guild, to create a new use '/guild leave'", commandSender);
                        return true;
                    }
                    if (OddJob.getInstance().getGuildManager().create(player.getUniqueId(), strings[1])) {
                        OddJob.getInstance().getMessageManager().success("Guild `" + strings[1] + "` created", commandSender);
                        return true;
                    }
                    OddJob.getInstance().getMessageManager().danger("Something went wrong when creating guild!", commandSender);
                    return true;
                } else {
                    // TODO when console creating guild
                    // create guilds like SAFE,WAR,JAIL,ARENA ?
                }
            } else if (strings[0].equalsIgnoreCase("invite")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                    if (guild == null) {
                        commandSender.sendMessage("Sorry, you are not associated with any guild yet.");
                        return true;
                    }
                    if (strings.length == 1) {
                        commandSender.sendMessage("Missing a player to invite to your guild.");
                        return true;
                    }
                    if (strings.length == 2) {
                        UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                        if (target == null) {
                            OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender);
                            return true;
                        }
                        UUID invite = OddJob.getInstance().getGuildManager().getGuildInvitation(target);
                        if (invite == guild) {
                            player.sendMessage(strings[1] + " has already been invited to this guild.");
                            return true;
                        } else if (invite != null) {
                            player.sendMessage(strings[1] + " has already been invited to another guild.");
                            return true;
                        }
                        UUID accepted = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(target);
                        if (accepted == guild) {
                            player.sendMessage(strings[1] + " has already joined this guild.");
                            return true;
                        } else if (accepted != null) {
                            player.sendMessage(strings[1] + " has already joined a guild.");
                            return true;
                        }
                        if (OddJob.getInstance().getGuildManager().getGuildPermissionInvite(guild).level() <= OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId()).level()) {
                            OddJob.getInstance().getGuildManager().inviteToGuild(guild, target);
                            player.sendMessage("You have invited " + strings[1] + " to join " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild));
                            OfflinePlayer op = Bukkit.getOfflinePlayer(target);
                            if (op.isOnline()) {
                                op.getPlayer().sendMessage("You have been invited to join the guild " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild));
                            }
                        }
                    }
                } else {
                    // TODO when console creating guild
                    // create guilds like SAFE,WAR,JAIL,ARENA ?
                }
            } else if (strings[0].equalsIgnoreCase("uninvite")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                    if (guild == null) {
                        commandSender.sendMessage("Sorry, you are not associated with any guild yet.");
                        return true;
                    }
                    if (strings.length == 1) {
                        commandSender.sendMessage("Missing a player to uninvite to your guild.");
                        return true;
                    }
                    if (strings.length == 2) {
                        UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                        if (target == null) {
                            OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender);
                            return true;
                        }
                        if (OddJob.getInstance().getGuildManager().getGuildInvitation(target) == guild) {
                            OddJob.getInstance().getGuildManager().uninviteToGuild(target);
                            player.sendMessage(strings[1] + " is no longer invited to " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild));
                            return true;
                        }
                    }
                } else {
                    // TODO when console creating guild
                    // create guilds like SAFE,WAR,JAIL,ARENA ?
                }
            } else if (strings[0].equalsIgnoreCase("kick")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                    if (guild == null) {
                        commandSender.sendMessage("Sorry, you are not associated with any guild yet.");
                        return true;
                    }
                    if (strings.length == 1) {
                        commandSender.sendMessage("Missing a player to kick to your guild.");
                        return true;
                    }
                    UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                    if (target == null) {
                        OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender);
                        return true;
                    }
                    StringBuilder reason = new StringBuilder();
                    if (strings.length > 2) {
                        for (int i = 2; i < strings.length; i++) {
                            reason.append(strings[i]).append(" ");
                        }
                    }
                    OddJob.getInstance().getGuildManager().kickFromGuild(guild, target, reason.toString());
                } else {
                    // TODO when console creating guild
                    // create guilds like SAFE,WAR,JAIL,ARENA ?
                }
            } else if (strings[0].equalsIgnoreCase("list")) {
                if (strings.length == 2) {
                    if (strings[1].equalsIgnoreCase("guilds")) {
                        StringBuilder sb = new StringBuilder();
                        for (UUID uuid : OddJob.getInstance().getGuildManager().getGuilds()) {
                            sb.append(OddJob.getInstance().getGuildManager().getGuildNameByUUID(uuid)).append(": ").append(OddJob.getInstance().getGuildManager().getGuildMembers(uuid).size()).append(", ");
                        }
                        OddJob.getInstance().getMessageManager().console("Guilds:\n" + sb.toString());
                    }
                }
                if (strings.length == 3) {
                    if (strings[1].equalsIgnoreCase("members")) {
                        UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByName(strings[2]);
                        if (guildUUID == null) {
                            OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender);
                            return true;
                        }
                        StringBuilder sb = new StringBuilder();
                        for (UUID uuid : OddJob.getInstance().getGuildManager().getGuildMembers(guildUUID)) {
                            sb.append(OddJob.getInstance().getPlayerManager().getName(uuid)).append(", ");
                        }
                        OddJob.getInstance().getMessageManager().sendMessage(commandSender, "Guilds:\n" + sb.toString());
                    }
                }

            /*} else if (strings.length == 2 && strings[0].equalsIgnoreCase("promote")) {
                if (!(commandSender instanceof Player)) return true;
                Player player = (Player) commandSender;
                UUID targetUUID = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                Player target = OddJob.getInstance().getPlayerManager().getPlayer(targetUUID);
                if (target == null || !target.isOnline()) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender);
                    return true;
                }
                UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                if (OddJob.getInstance().getGuildManager().getGuildUUIDByMember(targetUUID) == guildUUID) {
                    Role roleT = OddJob.getInstance().getGuildManager().getGuildMemberRole(targetUUID);
                    Role roleC = OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId());
                    if (roleC.level() > roleT.level()) {
                        roleT = OddJob.getInstance().getGuildManager().promoteMember(guildUUID, targetUUID);
                        OddJob.getInstance().getMessageManager().success("Promoted " + target.getName() + " to " + roleT.toString(), commandSender);
                        OddJob.getInstance().getMessageManager().success("You have been promoted to " + roleT.toString() + " by " + commandSender.getName(), targetUUID);
                    }
                }
            } else if (strings.length == 2 && strings[0].equalsIgnoreCase("demote")) {
                if (!(commandSender instanceof Player)) return true;
                Player player = (Player) commandSender;
                UUID targetUUID = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                Player target = OddJob.getInstance().getPlayerManager().getPlayer(targetUUID);
                if (target == null || !target.isOnline()) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender);
                    return true;
                }
                UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                if (OddJob.getInstance().getGuildManager().getGuildUUIDByMember(targetUUID) == guildUUID) {
                    Role roleT = OddJob.getInstance().getGuildManager().getGuildMemberRole(targetUUID);
                    Role roleC = OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId());
                    if (roleC.level() > roleT.level()) {
                        roleT = OddJob.getInstance().getGuildManager().demoteMember(guildUUID, targetUUID);
                        OddJob.getInstance().getMessageManager().success("Demoted " + target.getName() + " to " + roleT.toString(), commandSender);
                        OddJob.getInstance().getMessageManager().success("You have been demoted to " + roleT.toString() + " by " + commandSender.getName(), targetUUID);
                    }
                }*/
            } else if (strings[0].equalsIgnoreCase("unclaim")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    OddJob.getInstance().getGuildManager().unclaim(player);
                } else {
                    // TODO when console creating guild
                    // create guilds like SAFE,WAR,JAIL,ARENA ?
                }
            } else if (strings[0].equalsIgnoreCase("claim")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if (strings.length == 1) {
                        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
                        if (guild == null) {
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
                        OddJob.getInstance().getGuildManager().toggleAutoClaim(player.getUniqueId(), Zone.GUILD);
                        OddJob.getInstance().getGuildManager().claim(player);
                    }

                    if (strings.length > 1) {
                        if (strings[1].equalsIgnoreCase("safe")) {
                            if (strings[2].equalsIgnoreCase("auto")) {
                                OddJob.getInstance().getGuildManager().toggleAutoClaim(player.getUniqueId(), Zone.SAFE);
                            }
                        } else if (strings[1].equalsIgnoreCase("war")) {
                            if (strings[2].equalsIgnoreCase("auto")) {
                                OddJob.getInstance().getGuildManager().toggleAutoClaim(player.getUniqueId(), Zone.WAR);
                            }
                        } else if (strings[1].equalsIgnoreCase("jail")) {
                            if (strings[2].equalsIgnoreCase("auto")) {
                                OddJob.getInstance().getGuildManager().toggleAutoClaim(player.getUniqueId(), Zone.JAIL);
                            }
                        } else if (strings[1].equalsIgnoreCase("arena")) {
                            if (strings[2].equalsIgnoreCase("auto")) {
                                OddJob.getInstance().getGuildManager().toggleAutoClaim(player.getUniqueId(), Zone.ARENA);
                            }
                        }
                    }

                } else {
                    // TODO when console creating guild
                    // create guilds like SAFE,WAR,JAIL,ARENA ?
                }
            } else if (strings[0].equalsIgnoreCase("set")) {
                if (commandSender instanceof Player) {
                    if (strings.length >= 3) {
                        Player player = (Player) commandSender;
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
                } else {
                    // TODO when console creating guild
                    // create guilds like SAFE,WAR,JAIL,ARENA ?
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
                        if (pend.equals(join)) {
                            player.sendMessage("We have already annonuced the guild about your pending request to join " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(pend));
                            return true;
                        }
                        OddJob.getInstance().getGuildManager().addGuildPending(join, player.getUniqueId());
                        player.sendMessage("We have now announced your pending interest to join the guild " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(join));
                        return true;
                    }
                } else {
                    // TODO when console creating guild
                    // create guilds like SAFE,WAR,JAIL,ARENA ?
                }
            } else if (strings[0].equalsIgnoreCase("leave")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    OddJob.getInstance().getGuildManager().leave(player.getUniqueId());
                    if (OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()) == null) {
                        OddJob.getInstance().getMessageManager().success("You have successfully left the guild", player.getUniqueId());
                    }
                } else {
                    // TODO when console creating guild
                    // create guilds like SAFE,WAR,JAIL,ARENA ?
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
                st = new String[]{"claim", "unclaim", "set", "list", "invite", "uninvite", "kick"};
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
        if (strings[0].equalsIgnoreCase("list")) {
            if (strings.length == 3) {
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
        claim("Claim the chunk to your guild");

        private String help;

        Args(String help) {
            this.help = help;
        }

        public String get() {
            return this.help;
        }
    }
}
