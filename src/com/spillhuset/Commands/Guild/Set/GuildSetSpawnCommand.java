package com.spillhuset.Commands.Guild.Set;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Guild;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class GuildSetSpawnCommand extends SubCommand {
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
        return "spawn";
    }

    @Override
    public String getDescription() {
        return "Sets a spawnpoint for the guild";
    }

    @Override
    public String getSyntax() {
        return "/guild set spawn";
    }

    @Override
    public String getPermission() {
        return "guild.use";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender,false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
            return;
        }

        if (checkArgs(2,2,args,sender,getPlugin())) {
            return;
        }
        Player player = (Player) sender;
        UUID guildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
        if (guildUUID == null) {
            OddJob.getInstance().getMessageManager().guildNotAssociated(player.getUniqueId());
            return;
        }

        Guild guild = OddJob.getInstance().getGuildManager().getGuild(guildUUID);

        Role role = OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId());
        if (role.level() < Role.Admins.level()) {
            OddJob.getInstance().getMessageManager().guildRoleNeeded(player.getUniqueId());
            return;
        }

        guild.setSpawn(player.getLocation().add(0,0.5,0));
        OddJob.getInstance().getMessageManager().guildsSet("spawn",guild,sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
