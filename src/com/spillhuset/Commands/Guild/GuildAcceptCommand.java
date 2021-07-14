package com.spillhuset.Commands.Guild;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Guild;
import com.spillhuset.Utils.GuildRole;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildAcceptCommand extends SubCommand implements GuildRole {
    @Override
    public boolean allowConsole() {
        return false;
    }

    @Override
    public boolean allowOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.guild;
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public String getDescription() {
        return "Accepts an invitation to a guild, or accepts a pending request to join the guild";
    }

    @Override
    public String getSyntax() {
        return "/guild accept <name>|<guild>";
    }

    @Override
    public String getPermission() {
        return "guild";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // guild accept <name>  -- pending
        // guild accept <guild>  -- invite

        // Check console
        if (!(sender instanceof Player player)) {
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
            return;
        }

        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
        UUID invitation = OddJob.getInstance().getGuildManager().getGuildInvitation(player.getUniqueId());
        UUID target = null;

        // Find guild
        if (guild != null) {
            Guild Guild = OddJob.getInstance().getGuildManager().getGuild(guild);

            // Check Role
            Role role = OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId());
            if (role.level() >= Guild.getPermissionInvite().level()) {

                // Check pending - want to join the guild!
                List<UUID> pending = OddJob.getInstance().getGuildManager().getGuildPendingList(guild);
                if (pending.size() == 0) {
                    OddJob.getInstance().getMessageManager().guildNoPending(player.getUniqueId());
                } else if (pending.size() == 1 && args.length == 1) {
                    invitation = pending.get(0);
                    OddJob.getInstance().getGuildManager().join(guild, invitation);
                    OddJob.getInstance().getMessageManager().guildWelcome(Guild, Bukkit.getOfflinePlayer(invitation));
                } else {
                    if (args.length == 1) {
                        // Send a list of requests
                        OddJob.getInstance().getMessageManager().pendingList(pending, sender);
                    } else if (args.length == 2) {
                        // Accepts a request
                        for (UUID uuid : pending) {
                            if (OddJob.getInstance().getPlayerManager().getPlayer(uuid).getName().equalsIgnoreCase(args[1])) {
                                target = uuid;
                                break;
                            }
                        }
                        if (target != null) {
                            OddJob.getInstance().getGuildManager().join(guild, invitation);
                            OddJob.getInstance().getMessageManager().guildWelcome(Guild, Bukkit.getOfflinePlayer(invitation));
                        }
                    }
                }

            } else {
                OddJob.getInstance().getMessageManager().permissionDenied(Plugin.guild, sender);
            }
        } else {
            // Not belonging to any guild
            guild = OddJob.getInstance().getGuildManager().getGuildInvitation(player.getUniqueId());
            if (guild != null) {
                // Is invited
                Guild Guild = OddJob.getInstance().getGuildManager().getGuild(guild);
                OddJob.getInstance().getGuildManager().join(guild, player.getUniqueId());
                OddJob.getInstance().getMessageManager().guildWelcome(Guild, player);
            } else {
                // Has no invite
                OddJob.getInstance().getMessageManager().guildNoInvitation(player.getUniqueId());
            }
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        // Check console
        if (!(sender instanceof Player player)) {
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
            return list;
        }

        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());

        // Find guild
        if (guild != null) {
            Guild Guild = OddJob.getInstance().getGuildManager().getGuild(guild);

            // Check Role
            Role role = OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId());
            if (role.level() >= Guild.getPermissionInvite().level()) {

                // Check pending - want to join the guild!
                List<UUID> pending = OddJob.getInstance().getGuildManager().getGuildPendingList(guild);

                if (args.length == 1) {
                    for (UUID uuid : pending) {
                        list.add(OddJob.getInstance().getPlayerManager().getName(uuid));
                    }
                } else if (args.length == 2) {
                    for (UUID uuid : pending) {
                        String name = OddJob.getInstance().getPlayerManager().getName(uuid);
                        if (name.startsWith(args[1])) {
                            list.add(name);
                        }
                    }
                }

            }
        }
        return list;
    }

    @Override
    public Role getRole() {
        return Role.all;
    }

    public boolean needGuild() {
        return false;
    }
}
