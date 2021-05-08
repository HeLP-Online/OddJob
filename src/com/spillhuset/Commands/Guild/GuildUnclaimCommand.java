package com.spillhuset.Commands.Guild;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Zone;
import com.spillhuset.Utils.Guild;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class GuildUnclaimCommand extends SubCommand {
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
        return "guild";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(1, 2, args, sender, getPlugin())) {
            return;
        }

        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
            return;
        }
        Player player = (Player) sender;
        UUID guild;
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
            if (guild == null) {
                OddJob.getInstance().getMessageManager().guildNotAssociated(player);
                return;
            }
        }
        OddJob.getInstance().log("Chunk:"+OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk())+"    Guild:"+guild);
        if (OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getLocation().getChunk()) == guild) {
            OddJob.getInstance().getGuildManager().unClaim(player);
        }
    }
    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
