package com.spillhuset.Commands.Guild;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.GuildRole;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildInviteCommand extends SubCommand implements GuildRole {
    @Override
    public Role getRole() {
        return null;
    }

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
        return "invite";
    }

    @Override
    public String getDescription() {
        return "Invites a player without a guild to the guild";
    }

    @Override
    public String getSyntax() {
        return "/guild invite [player]";
    }

    @Override
    public String getPermission() {
        return "guild.use";
    }

    @Override
    public boolean needNoGuild() {
        return false;
    }

    @Override
    public boolean needGuild() {
        return true;
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
        UUID playerUUID = ((Player) sender).getUniqueId();
        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(playerUUID);
        if (args.length == 1) {
            OddJob.getInstance().getGuildManager().listInvitedPlayers(guild,sender);
        } else if (args.length == 2) {
            UUID target = null;
            for (UUID uuid : OddJob.getInstance().getPlayerManager().getUUIDs()) {
                if (OddJob.getInstance().getPlayerManager().getName(uuid).equalsIgnoreCase(args[1])) {
                    target = uuid;
                }
            }

            if (target == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[1], sender);
                return;
            }

            String name = OddJob.getInstance().getPlayerManager().getName(target);
            UUID test = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(target);
            if (test != null) {
                OddJob.getInstance().getMessageManager().guildsAlreadyAssociated(OddJob.getInstance().getGuildManager().getGuildNameByUUID(test), name, sender);
                return;
            }

            OddJob.getInstance().getGuildManager().inviteToGuild(guild, target, playerUUID);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (UUID uuid : OddJob.getInstance().getPlayerManager().getUUIDs()) {
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(uuid);
            if (guild == null) {
                String name = OddJob.getInstance().getPlayerManager().getName(uuid);
                if (args.length == 2 && (args[1].isEmpty() || name.toLowerCase().startsWith(args[1].toLowerCase())) && !name.equalsIgnoreCase(sender.getName())) {
                    list.add(name);
                }
            }
        }
        return list;
    }
}
