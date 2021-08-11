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

public class GuildCreateCommand extends SubCommand implements GuildRole {
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
        return "create";
    }

    @Override
    public String getDescription() {
        return "Creates a new guild";
    }

    @Override
    public String getSyntax() {
        return "/guild create <name>";
    }

    @Override
    public String getPermission() {
        return "guild.create";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // guild create <name>
        if (!(sender instanceof Player)) {
            // Sender is console
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
            return;
        }

        UUID uuid = ((Player) sender).getUniqueId();
        if (checkArgs(2, 2, args, sender, getPlugin())) {
            return;
        }

        if (OddJob.getInstance().getGuildManager().getGuildUUIDByName(args[1]) != null) {
            // Guild name already exists
            OddJob.getInstance().getMessageManager().guildNameAlreadyExsits(args[1], sender);
            return;
        }

        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(uuid);
        if (guild != null) {
            // You are associated with another guild
            OddJob.getInstance().getMessageManager().guildAlreadyAssociated(OddJob.getInstance().getGuildManager().getGuild(guild).getName(), sender);
            return;
        }

        if (OddJob.getInstance().getGuildManager().create(uuid, args[1])) {
            // Successfully created a new guild
            OddJob.getInstance().getMessageManager().guildCreateSuccessful(args[1], sender);
            return;
        }

        OddJob.getInstance().getMessageManager().guildCreateError(args[1], sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("create");
        } else if (args.length == 2) {
            list.add("<name>");
        }
        return list;
    }

    @Override
    public Role getRole() {
        return Role.all;
    }

    @Override
    public boolean needGuild() {
        return false;
    }

}
