package com.spillhuset.Commands.Guild;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Enum.Zone;
import com.spillhuset.Utils.GuildRole;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class GuildUnclaimCommand extends SubCommand implements GuildRole {
    @Override
    public boolean allowConsole() {
        return false;
    }

    @Override
    public boolean allowOp() {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.guilds;
    }

    @Override
    public String getName() {
        return "unclaim";
    }

    @Override
    public String getDescription() {
        return "Unclaims a chunk from the guild";
    }

    @Override
    public String getSyntax() {
        return "/guild unclaim";
    }

    @Override
    public String getPermission() {
        return "guild.use";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(1, 2, args, sender, getPlugin())) {
            return;
        }

        if (!(sender instanceof Player player)) {
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
            return;
        }
        UUID guild = null;
        if (args.length == 2 && can(sender, true)) {
            try {
                guild = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.valueOf(args[1]));
                if (guild == null) {
                    OddJob.getInstance().getMessageManager().guildZoneError(args[1], player);
                    return;
                }
            } catch (Exception e) {
                OddJob.getInstance().getMessageManager().guildZoneError(args[1], player);
                return;
            }
        } else {
            guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
            if (guild == null && !player.isOp()) {
                OddJob.getInstance().getMessageManager().guildNotAssociated(player.getUniqueId());
                return;
            }
        }
        if (player.isOp() || OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk()).equals(guild)) {
            OddJob.getInstance().getGuildManager().unClaim(player);
        } else {
            OddJob.getInstance().getMessageManager().guildClaimed(player);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public Role getRole() {
        return Role.Admins;
    }

    @Override
    public boolean needGuild() {
        return true;
    }

    @Override
    public boolean needNoGuild() {
        return false;
    }
}
