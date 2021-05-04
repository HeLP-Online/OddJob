package com.spillhuset.Commands.Guild;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildCreateCommand extends SubCommand {
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
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.guild);
            return;
        }

        UUID uuid = ((Player) sender).getUniqueId();
        if (args.length < 2) {
            // To few arguments
            OddJob.getInstance().getMessageManager().errorMissingArgs(Plugin.guild, sender);
            return;
        } else if (args.length > 2) {
            // To many arguments
            OddJob.getInstance().getMessageManager().errorTooManyArgs(Plugin.guild, sender);
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
}
