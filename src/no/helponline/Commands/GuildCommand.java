package no.helponline.Commands;

import no.helponline.Guilds.Guild;
import no.helponline.Guilds.Role;
import no.helponline.Guilds.Zone;
import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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
            if (strings.length == 0) {
                Bukkit.dispatchCommand(commandSender, command.getName() + " help");
                return true;
            }
            if (strings.length == 2 && strings[0].equalsIgnoreCase("create")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if (OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId()) == null) {
                        Guild guild = OddJob.getInstance().getGuildManager().create(player.getUniqueId(), strings[1]);
                        if (guild != null) {
                            OddJob.getInstance().getMessageManager().success("Guild `" + guild.getName() + "` created", commandSender);
                        }

                        OddJob.getInstance().getMessageManager().warning("A Guild with the name `" + strings[1] + "` already exists", commandSender);
                    } else {
                        OddJob.getInstance().getMessageManager().danger("You are already connected to a guild, to create a new use '/guild leave'", commandSender);
                    }
                }
            } else if (strings[0].equalsIgnoreCase("list")) {
                if (strings.length == 2 && strings[1].equalsIgnoreCase("guilds")) {
                    StringBuilder sb = new StringBuilder();
                    for (UUID uuid : OddJob.getInstance().getGuildManager().getGuilds().keySet()) {
                        Guild guild = OddJob.getInstance().getGuildManager().getGuild(uuid);
                        sb.append(guild.getName()).append(": ").append(guild.getMembers().size()).append(", ");
                    }
                    OddJob.getInstance().getMessageManager().console("Guilds:\n" + sb.toString());
                }
                if (strings.length == 3 && strings[1].equalsIgnoreCase("members")) {
                    UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildByName(strings[2]);
                    if (guildUUID == null) {
                        OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender);
                        return true;
                    }
                    StringBuilder sb = new StringBuilder();
                    for (UUID uuid : OddJob.getInstance().getGuildManager().getGuild(guildUUID).getMembers().keySet()) {
                        sb.append(OddJob.getInstance().getPlayerManager().getName(uuid)).append(", ");
                    }
                    OddJob.getInstance().getMessageManager().console("Guilds:\n" + sb.toString());
                }
            } else if (strings.length == 2 && strings[0].equalsIgnoreCase("promote")) {
                if (!(commandSender instanceof Player)) return true;
                Player player = (Player) commandSender;
                UUID targetUUID = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
                Player target = OddJob.getInstance().getPlayerManager().getPlayer(targetUUID);
                if (target == null || !target.isOnline()) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender);
                    return true;
                }
                if (OddJob.getInstance().getGuildManager().getGuildByMember(targetUUID) == OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId())) {
                    Role roleT = OddJob.getInstance().getGuildManager().getGuildByMember(targetUUID).getRole(targetUUID);
                    Role roleC = OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId()).getRole(player.getUniqueId());
                    if (roleC.level() > roleT.level()) {
                        roleT = OddJob.getInstance().getGuildManager().getGuildByMember(targetUUID).promote(targetUUID);
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
                if (OddJob.getInstance().getGuildManager().getGuildByMember(targetUUID) == OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId())) {
                    Role roleT = OddJob.getInstance().getGuildManager().getGuildByMember(targetUUID).getMembers().get(targetUUID);
                    Role roleC = OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId()).getMembers().get(player.getUniqueId());
                    if (roleC.level() > roleT.level()) {
                        roleT = OddJob.getInstance().getGuildManager().getGuildByMember(targetUUID).demote(targetUUID);
                        OddJob.getInstance().getMessageManager().success("Demoted " + target.getName() + " to " + roleT.toString(), commandSender);
                        OddJob.getInstance().getMessageManager().success("You have been demoted to " + roleT.toString() + " by " + commandSender.getName(), targetUUID);
                    }
                }
            } else if (strings[0].equalsIgnoreCase("claim")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
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
                    } else {
                        if (strings[1].equalsIgnoreCase("auto")) {
                            OddJob.getInstance().getGuildManager().toggleAutoClaim(player.getUniqueId(), Zone.GUILD);
                        }
                        OddJob.getInstance().getGuildManager().claim(player);
                    }

                }
            } else if (strings.length == 1 && strings[0].equalsIgnoreCase("claims") &&
                    commandSender instanceof Player) {
                Player player = (Player) commandSender;
                Chunk chunk = player.getLocation().getChunk();
                Guild guild = OddJob.getInstance().getGuildManager().getGuildByChunk(chunk);
                if (guild != null) {
                    OddJob.getInstance().log("This chunk is owned by " + guild.getName());
                }
                OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId()).myClaims();
            } else if (strings[0].equalsIgnoreCase("set")) {
                if (strings.length >= 3) {
                    Player player = (Player) commandSender;
                    if (strings[1].equalsIgnoreCase("name")) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 2; i < strings.length; i++) {
                            sb.append(strings[i]);
                        }
                        OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId()).setName(sb.toString());

                    } else if (strings[1].equalsIgnoreCase("invitedOnly")) {
                        try {
                            boolean bol = Boolean.parseBoolean(strings[2]);
                            OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId()).setInvitedOnly(bol);
                        } catch (Exception e) {
                        }

                    } else if (strings[1].equalsIgnoreCase("friendlyFire")) {
                        try {
                            boolean bol = Boolean.parseBoolean(strings[2]);
                            OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId()).setFriendlyFire(bol);
                        } catch (Exception e) {
                        }

                    }
                }
            } else if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("join")) {
                    if (commandSender instanceof Player) {
                        Player player = (Player) commandSender;

                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < strings.length; i++) {
                            sb.append(strings[i]);
                        }
                        UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildByName(sb.toString());
                        Guild guild = OddJob.getInstance().getGuildManager().getGuild(guildUUID);

                        if (guild != null) {
                            OddJob.getInstance().getGuildManager().join(guild.getId(), player.getUniqueId());
                        }
                    }
                } else if (strings[0].equalsIgnoreCase("leave")) {
                    if (commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < strings.length; i++) {
                            sb.append(strings[i]);
                        }
                        UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildByName(sb.toString());
                        Guild guild = OddJob.getInstance().getGuildManager().getGuild(guildUUID);
                        guild.leave(player.getUniqueId());
                        if (OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId()) == null) {
                            OddJob.getInstance().getMessageManager().success("You have successfully left the guild " + guild.getName(), player.getUniqueId());
                        }
                    }
                }
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        String[] st;
        if (strings.length == 1 || strings.length == 0) {
            st = new String[]{"create", "join", "claim", "claims", "set", "demote", "promote", "list"};
            for (String t : st) {
                if (t.startsWith(strings[0])) {
                    list.add(t);
                }
            }
        }

        if (strings[0].equalsIgnoreCase("join")) {
            // TODO List all open guilds
        } else if (strings[0].equalsIgnoreCase("claim") && strings.length >= 2) {
            // TODO permission autoclaim
            if (strings.length == 2) {
                for (Zone z : Zone.values()) {
                    if (commandSender.hasPermission("guild.claim." + z.name()) && z.name().startsWith(strings[1])) {
                        list.add(z.name());
                    }
                }
            }
            if (strings.length == 3 && commandSender.hasPermission("guild.claim." + strings[1] + ".auto")) {
                list.add("auto");
            }
        } else if (strings[0].equalsIgnoreCase("set") && strings.length == 2) {
            st = new String[]{"friendlyFire", "invitedOnly", "name"};
            for (String t : st) {
                if (t.startsWith(strings[1])) {
                    list.add(t);
                }
            }
        } else if (strings[0].equalsIgnoreCase("set") && strings.length == 3 && (strings[1].equalsIgnoreCase("friendlyFire") || strings[1].equalsIgnoreCase("invitedOnly"))) {
            st = new String[]{"TRUE", "FALSE"};
            for (String t : st) {
                if (t.startsWith(strings[2])) {
                    list.add(t);
                }
            }
        }

        return list;
    }

    enum Args {
        create("Creates a new guild"), join("Join an existing guild"), claim("Claim the chunk to your guild");

        private String help;

        Args(String help) {
            this.help = help;
        }

        public String get() {
            return this.help;
        }
    }
}
