package com.spillhuset.Commands.Guild;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Guild;
import com.spillhuset.Utils.GuildRole;
import com.spillhuset.Utils.SubCommand;
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
        return Plugin.guilds;
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public String getDescription() {
        return "Accept an incoming invite to a guild or Accept an request to join the guild";
    }

    @Override
    public String getSyntax() {
        return "/guild accept [player|guild]";
    }

    @Override
    public String getPermission() {
        return "guild.use";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return;
        }

        if (checkArgs(1, 2, args, sender, getPlugin())) {
            return;
        }

        UUID uuid = ((Player) sender).getUniqueId();
        UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(uuid);

        if (guildUUID != null) {
            Guild guild = OddJob.getInstance().getGuildManager().getGuild(guildUUID);
            List<UUID> pending = OddJob.getInstance().getGuildManager().getGuildPendingList(guildUUID);
            if (pending.size() == 0) {
                OddJob.getInstance().getMessageManager().guildsNoPending(guild.getName(), uuid);
            } else if (pending.size() == 1) {
                if ((args.length == 2 && args[1].equalsIgnoreCase(OddJob.getInstance().getPlayerManager().getName(pending.get(0)))) || args.length == 1) {
                    OddJob.getInstance().getGuildManager().acceptPending(pending.get(0), uuid);
                } else {
                    OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[1], sender);
                }
            } else {
                OddJob.getInstance().getMessageManager().guildsListPending(guild.getName(), pending, sender);
            }
        } else {
            List<UUID> invites = OddJob.getInstance().getGuildManager().getGuildInvites(uuid);
            if (invites.size() == 0) {
                OddJob.getInstance().getMessageManager().guildNoInvitation(uuid);
            } else if (invites.size() == 1) {
                if ((args.length == 2 && args[1].equalsIgnoreCase(OddJob.getInstance().getGuildManager().getGuildNameByUUID(invites.get(0)))) || args.length == 1) {
                    OddJob.getInstance().getGuildManager().acceptInvite(sender);
                } else {
                    OddJob.getInstance().getMessageManager().errorGuild(args[1], sender);
                }
            } else {
                OddJob.getInstance().getMessageManager().guildsListInvites(invites, sender);
            }
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public Role getRole() {
        return Role.all;
    }

    @Override
    public boolean needGuild() {
        return false;
    }

    @Override
    public boolean needNoGuild() {
        return false;
    }
}
