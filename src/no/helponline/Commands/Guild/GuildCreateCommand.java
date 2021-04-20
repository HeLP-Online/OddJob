package no.helponline.Commands.Guild;
import no.helponline.Managers.GuildManager;
import no.helponline.OddJob;
import no.helponline.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            OddJob.getInstance().getMessageManager().errorConsole();
            return;
        }
        UUID uuid = ((Player) sender).getUniqueId();
        if (args.length < 2) {
            OddJob.getInstance().getMessageManager().errorMissingArgs(sender);
            return;
        } else if (args.length > 2) {
            OddJob.getInstance().getMessageManager().errorTooManyArgs(sender);
            return;
        }
        if (OddJob.getInstance().getGuildManager().getGuildUUIDByName(args[1]) != null) {
            OddJob.getInstance().getMessageManager().guildNameAlreadyExsits(args[1],sender);
            return;
        }
        UUID guild =OddJob.getInstance().getGuildManager().getGuildUUIDByMember(uuid) ;
        if(guild != null) {
            OddJob.getInstance().getMessageManager().guildAlreadyAssociated(OddJob.getInstance().getGuildManager().getGuild(guild).getName(),sender);
            return;
        }
        if(OddJob.getInstance().getGuildManager().create(uuid,args[1])) {
            OddJob.getInstance().getMessageManager().guildCreateSuccessful(args[1],sender);
            return;
        }
        OddJob.getInstance().getMessageManager().guildCreateError(args[1],sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
