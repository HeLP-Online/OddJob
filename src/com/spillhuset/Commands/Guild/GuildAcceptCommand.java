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
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
            return;
        }
        Player player = (Player) sender;
        Guild guild = null;
        UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
        UUID invitation;
        List<UUID> pending;
        if (guildUUID != null) {
            // Player has a guild
            guild = OddJob.getInstance().getGuildManager().getGuild(guildUUID);
            pending = OddJob.getInstance().getGuildManager().getGuildPendingList(guildUUID);
            if (pending.size() == 0) {
                OddJob.getInstance().getMessageManager().guildNoPending(player.getUniqueId());
                return;
            } else if(pending.size() == 1) {
                invitation = pending.get(0);
                OddJob.getInstance().getGuildManager().join(guildUUID,invitation);
                OddJob.getInstance().getMessageManager().guildWelcome(guild, Bukkit.getOfflinePlayer(invitation));
            } else {
                if (args.length == 1) {
                    OddJob.getInstance().getMessageManager().pendingList(pending,sender);
                    return;
                } else if(args.length == 2){
                    invitation = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
                    OddJob.getInstance().getGuildManager().join(guildUUID,invitation);
                    OddJob.getInstance().getMessageManager().guildWelcome(guild, Bukkit.getOfflinePlayer(invitation));
                }
            }
        } else {
            // Has no guild
            guildUUID = OddJob.getInstance().getGuildManager().getGuildInvitation(player.getUniqueId());
            if (guildUUID != null) {
                // An invitation exists
                guild = OddJob.getInstance().getGuildManager().getGuild(guildUUID);
                OddJob.getInstance().getGuildManager().join(guildUUID,player.getUniqueId());
                OddJob.getInstance().getMessageManager().guildWelcome(guild,player);
                return;
            } else {
                OddJob.getInstance().getMessageManager().guildNoInvitation(player.getUniqueId());
                return;
            }
        }



    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public Role getRole() {
        return Role.all;
    }
}
