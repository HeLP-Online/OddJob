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

public class GuildClaimCommand extends SubCommand implements GuildRole {
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
        return "claim";
    }

    @Override
    public String getDescription() {
        return "Claims a chunk to your guild";
    }

    @Override
    public String getSyntax() {
        return "/guild claim [zone]";
    }

    @Override
    public String getPermission() {
        return "guild";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(1, 3, args, sender, getPlugin())) {
            return;
        }

        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
            return;
        }

        Player player = (Player) sender;
        UUID guild;
        if (args.length == 2 && can(sender, true)) {
            guild = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.valueOf(args[1]));
            if (guild == null) {
                OddJob.getInstance().getMessageManager().guildZoneError(args[1], player);
                return;
            }
        } else {
            guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
            if (guild == null) {
                OddJob.getInstance().getMessageManager().guildNotAssociated(player.getUniqueId());
                return;
            }
        }
        Guild gg = OddJob.getInstance().getGuildManager().getGuild(guild);
        if (OddJob.getInstance().getGuildManager().getSumChunks(guild) >= gg.getMaxClaims()) {
            OddJob.getInstance().getGuildManager().claim(guild, player.getLocation().getChunk(), player);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {

        return new ArrayList<>();
    }

    @Override
    public Role getRole() {
        return Role.Admins;
    }
}
