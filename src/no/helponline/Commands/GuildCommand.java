package no.helponline.Commands;

import no.helponline.Guilds.Guild;
import no.helponline.Guilds.Role;
import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
            // IS IT GUILDMASTE OR CONSOLE
            boolean guildMaster = false;
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId()).getMembers().get(player.getUniqueId()).equals(Role.guildMaster)) {
                    guildMaster = true;
                }
            } else {
                guildMaster = true;
            }

            if (strings.length == 0) {
                Bukkit.dispatchCommand(commandSender, command.getName() + " help");
                return true;
            }
            if (strings.length == 2 && strings[0].equalsIgnoreCase("create")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    Guild guild = OddJob.getInstance().getGuildManager().create(player.getUniqueId(), strings[1]);
                    if (guild == null) {
                        OddJob.getInstance().getMessageManager().warning("A Guild with the name `" + strings[1] + "` already exists", commandSender);
                    }
                    OddJob.getInstance().getMessageManager().success("Guild `" + guild.getName() + "` created", commandSender);
                }
            } else if (strings.length == 1 && strings[0].equalsIgnoreCase("list")) {
                StringBuilder sb = new StringBuilder();
                for (UUID uuid : OddJob.getInstance().getGuildManager().getGuilds().keySet()) {
                    Guild guild = OddJob.getInstance().getGuildManager().getGuild(uuid);
                    sb.append(guild.getName()).append(": ").append(guild.getMembers().size()).append(", ");
                }
                OddJob.getInstance().getMessageManager().console("Guilds:\n" + sb.toString());
            } else if (strings.length == 2 && strings[0].equalsIgnoreCase("promote")) {
                Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[1]));
                if (target == null || !target.isOnline()) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender);
                    return true;
                }
                Role roleT = OddJob.getInstance().getGuildManager().getGuildByMember(target.getUniqueId()).getMembers().get(target.getUniqueId());
                Role roleC;
                if (guildMaster) {
                    roleC = Role.guildMaster;
                } else {
                    Player player = (Player) commandSender;
                    roleC = OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId()).getMembers().get(player.getUniqueId());
                }
                if (roleC.level() > roleT.level()) {
                    roleT = OddJob.getInstance().getGuildManager().getGuildByMember(target.getUniqueId()).promote(target.getUniqueId());
                    OddJob.getInstance().getMessageManager().success("Promoted " + target.getName() + " to " + roleT.toString(), commandSender);
                    OddJob.getInstance().getMessageManager().success("You have been promoted to " + roleT.toString() + " by " + commandSender.getName(), target.getUniqueId());
                }
            } else if (strings.length == 2 && strings[0].equalsIgnoreCase("demote")) {
                Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[1]));
                if (target == null || !target.isOnline()) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender);
                    return true;
                }
                Role roleT = OddJob.getInstance().getGuildManager().getGuildByMember(target.getUniqueId()).getMembers().get(target.getUniqueId());
                Role roleC;
                if (guildMaster) {
                    roleC = Role.guildMaster;
                } else {
                    Player player = (Player) commandSender;
                    roleC = OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId()).getMembers().get(player.getUniqueId());
                }
                if (roleC.level() > roleT.level()) {
                    roleT = OddJob.getInstance().getGuildManager().getGuildByMember(target.getUniqueId()).demote(target.getUniqueId());
                    OddJob.getInstance().getMessageManager().success("Demoted " + target.getName() + " to " + roleT.toString(), commandSender);
                    OddJob.getInstance().getMessageManager().success("You have been demoted to " + roleT.toString() + " by " + commandSender.getName(), target.getUniqueId());
                }
            } else if (strings.length == 1 && strings[0].equalsIgnoreCase("claim")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    OddJob.getInstance().getGuildManager().claim(player);
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
            } else if (strings.length >= 3 && strings[0].equalsIgnoreCase("set")) {
                if (strings[1].equalsIgnoreCase("name")) {
                    if (commandSender instanceof Player) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 2; i < strings.length; i++) {
                            sb.append(strings[i]);
                        }
                        Player player = (Player) commandSender;
                        OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId()).setName(sb.toString());
                    }
                }
            } else if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("join")) {
                    if (commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        Guild guild = null;
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < strings.length; i++) {
                            sb.append(strings[i]);
                        }
                        for (Guild g : OddJob.getInstance().getGuildManager().getGuilds().values()) {
                            if (ChatColor.stripColor(g.getName()).equalsIgnoreCase(sb.toString())) {
                                guild = g;
                            }
                        }

                        if (guild != null) {
                            OddJob.getInstance().getGuildManager().join(guild.getId(), player.getUniqueId());
                        }
                    }
                }
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            List<String> list = new ArrayList<>();
            for (Args a : Args.values()) {
                if (a.name().startsWith(strings[0])) {
                    list.add(a.name());
                }
            }
            return list;
        }
        return null;
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
