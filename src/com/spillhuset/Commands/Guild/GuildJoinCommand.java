package com.spillhuset.Commands.Guild;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Enum.Zone;
import com.spillhuset.Utils.Guild;
import com.spillhuset.Utils.GuildRole;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildJoinCommand extends SubCommand implements GuildRole {
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
        return "guild.use";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // guild create <name>
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
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
            // Guild is not found
            OddJob.getInstance().getMessageManager().errorGuild(args[1], sender);
            return;
        }

        // Free to join
        if (OddJob.getInstance().getGuildManager().isGuildOpen(guild)) {
            OddJob.getInstance().getGuildManager().join(guild, uuid);
            OddJob.getInstance().getMessageManager().guildsJoining(OddJob.getInstance().getGuildManager().getGuild(guild), sender);
            return;
        }

        // Make a request to join
        OddJob.getInstance().getGuildManager().addPending(guild, uuid);
        OddJob.getInstance().getMessageManager().guildsPending(OddJob.getInstance().getGuildManager().getGuild(guild), sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> temp = new ArrayList<>();
        if (args.length == 2) {
            for (UUID uuid : OddJob.getInstance().getGuildManager().getGuilds().keySet()) {
                Guild guild = OddJob.getInstance().getGuildManager().getGuild(uuid);
                if (args[1].isEmpty() || guild.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    if (guild.getZone() == Zone.GUILD) {
                        if (guild.isOpen()) {
                            temp.add(guild.getName() + "*");
                        } else {
                            temp.add(guild.getName());
                        }
                    }
                }
            }
        }
        return temp;
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
        return true;
    }
}
