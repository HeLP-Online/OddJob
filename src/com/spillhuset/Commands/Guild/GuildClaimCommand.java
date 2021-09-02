package com.spillhuset.Commands.Guild;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Enum.Zone;
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
        return Plugin.guilds;
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
        return "/guild claim [zone=<zone>|auto=<auto>]";
    }

    @Override
    public String getPermission() {
        return "guild.use";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

        // Check player
        if (!(sender instanceof Player player)) {
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
            return;
        }

        Zone zone = Zone.GUILD;
        boolean auto = false;
        UUID guild = null;

        // Check args
        if (checkArgs(1, 3, args, sender, getPlugin())) {
            return;
        }

        if (args.length >= 2) {
            for (int i = 1; i <= (args.length - 1); i++) {
                if (args[i].startsWith("zone") && can(sender, true)) {
                    // Setting zone
                    String[] value = args[i].split("=");
                    if (value.length != 2) {
                        OddJob.getInstance().getMessageManager().syntaxError(Plugin.guilds, "args=value", sender);
                        return;
                    }
                    for (Zone z : Zone.values()) {
                        if (z.name().toLowerCase().startsWith(value[1].toLowerCase())) {
                            zone = z;
                        }
                    }
                    if (zone == Zone.GUILD) {
                        OddJob.getInstance().getMessageManager().errorZone(Plugin.guilds, args[1], sender);
                        return;
                    }

                    guild = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(zone);
                } else if (args[i].startsWith("auto")) {
                    // Setting auto claim
                    String[] value = args[i].split("=");
                    if (value.length != 2) {
                        OddJob.getInstance().getMessageManager().syntaxError(Plugin.guilds, "args=value", sender);
                        return;
                    }
                    auto = value[1].equalsIgnoreCase("1") || value[1].equalsIgnoreCase("on") || value[1].equalsIgnoreCase("true");
                }
            }
        }

        if (zone == Zone.GUILD) {
            guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
            if (OddJob.getInstance().getGuildManager().getSumChunks(guild) >= OddJob.getInstance().getGuildManager().getGuild(guild).getMaxClaims()) {
                OddJob.getInstance().getMessageManager().guildMaxClaimsReached(sender);
                return;
            }
        }
        OddJob.getInstance().log("Claiming for: " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild));
        OddJob.getInstance().getGuildManager().claim(player, guild);
        if ((OddJob.getInstance().getGuildManager().hasAutoClaim(player.getUniqueId()) == null && auto) || (OddJob.getInstance().getGuildManager().hasAutoClaim(player.getUniqueId()) != null && !auto)) {
            OddJob.getInstance().getGuildManager().toggleAutoClaim(player, guild, auto);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> auto = new ArrayList<>();
        auto.add("auto=on");
        auto.add("auto=off");

        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (String string : auto) {
                if (string.toLowerCase().startsWith(args[1].toLowerCase())) {
                    list.add(string);
                }
            }
            for (Zone zone : Zone.values()) {
                if (zone.name().toLowerCase().startsWith(args[1].toLowerCase()) && can(sender, true)) {
                    list.add("zone=" + zone.name());
                }
            }
        }
        return list;
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
