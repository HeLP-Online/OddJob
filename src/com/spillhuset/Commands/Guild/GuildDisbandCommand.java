package com.spillhuset.Commands.Guild;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Enum.ScoreBoard;
import com.spillhuset.Utils.GuildRole;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildDisbandCommand extends SubCommand implements GuildRole {
    @Override
    public boolean allowConsole() {
        return true;
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
        return "disband";
    }

    @Override
    public String getDescription() {
        return "Disband your guild";
    }

    @Override
    public String getSyntax() {
        return "/guild disband";
    }

    @Override
    public String getPermission() {
        return "guild";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        UUID guild;
        boolean console = false;
        UUID player = null;
        if (!(sender instanceof Player)) {
            if (checkArgs(2, 2, args, sender, getPlugin())) {
                return;
            }
            console = true;
            guild = OddJob.getInstance().getGuildManager().getGuildUUIDByName(args[1]);
            if (guild == null) {
                OddJob.getInstance().getMessageManager().errorGuild(args[1], sender);
                return;
            }
        } else {
            if (checkArgs(1, 1, args, sender, getPlugin())) {
                return;
            }
            player = ((Player) sender).getUniqueId();
            guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player);
            if (guild == null) {
                OddJob.getInstance().getMessageManager().guildNotAssociated(player);
                return;
            }
        }

        if (!console) {
            if (OddJob.getInstance().getGuildManager().getGuildMemberRole(player) != Role.Master) {
                OddJob.getInstance().getMessageManager().guildRoleNeeded(player);
                return;
            }
        }
        OddJob.getInstance().getMessageManager().guildDisband(guild, sender);
        OddJob.getInstance().getGuildManager().disband(guild);
        if (sender instanceof Player) {
            OddJob.getInstance().getScoreManager().create((Player) sender, ScoreBoard.Player);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public Role getRole() {
        return Role.Master;
    }
}
