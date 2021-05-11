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

public class GuildJoinCommand extends SubCommand implements GuildRole {
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
        return Plugin.guild;
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Joins a guild";
    }

    @Override
    public String getSyntax() {
        return "/guild join <name>";
    }

    @Override
    public String getPermission() {
        return "guild.join";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // guild create <name>
        if (!(sender instanceof Player)) {
            // Sender is console
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.guild);
            return;
        }

        UUID uuid = ((Player) sender).getUniqueId();
        if (checkArgs(2, 2, args, sender, getPlugin())) {
            return;
        }

        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(uuid);
        if (guild != null) {
            // You are associated with another guild
            OddJob.getInstance().getMessageManager().guildAlreadyAssociated(OddJob.getInstance().getGuildManager().getGuild(guild).getName(), sender);
            return;
        }

        guild = OddJob.getInstance().getGuildManager().getGuildUUIDByName(args[1]);
        if (guild == null) {
            OddJob.getInstance().getMessageManager().errorGuild(args[1], sender);
            return;
        }

        if (OddJob.getInstance().getGuildManager().isGuildOpen(guild)) {
            OddJob.getInstance().getGuildManager().join(guild, uuid);
            OddJob.getInstance().getMessageManager().guildJoining(OddJob.getInstance().getGuildManager().getGuild(guild), sender);
            return;
        }

        OddJob.getInstance().getGuildManager().addGuildPending(guild, uuid);
        OddJob.getInstance().getMessageManager().guildPending(OddJob.getInstance().getGuildManager().getGuild(guild), sender);
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
