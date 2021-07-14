package com.spillhuset.Commands.Guild;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.GuildRole;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class GuildLeaveCommand extends SubCommand implements GuildRole {
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
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Leaves your guild alone";
    }

    @Override
    public String getSyntax() {
        return "/guild leave";
    }

    @Override
    public String getPermission() {
        return "guild";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
            return;
        }
        if (checkArgs(1, 2, args, sender, getPlugin())) {
            return;
        }

        Player player = (Player) sender;
        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
        if (guild == null) {
            OddJob.getInstance().getMessageManager().guildNotAssociated(player.getUniqueId());
            return;
        }
        if (OddJob.getInstance().getGuildManager().getGuildMembers(guild).size() == 1) {
            OddJob.getInstance().getMessageManager().guildLastOne(sender);
            return;
        }

        if (args.length == 2) {
            UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
            if (target == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(Plugin.guild, args[1], sender);
                return;
            }
            Role role = OddJob.getInstance().getGuildManager().promoteMember(guild,target, Role.Master);
            OddJob.getInstance().getMessageManager().changeRole(role,target,sender);
            OddJob.getInstance().getGuildManager().leave(player.getUniqueId());

        }
        OddJob.getInstance().getGuildManager().leave(player.getUniqueId());
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public Role getRole() {
        return Role.Members;
    }
    @Override
    public boolean needGuild() {
        return true;
    }
}
