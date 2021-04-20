package no.helponline.Commands.Ban;

import no.helponline.OddJob;
import no.helponline.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanAddCommand extends SubCommand {
    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "Makes a player banned";
    }

    @Override
    public String getSyntax() {
        return "/ban add <name> [description]";
    }

    @Override
    public String getPermission() {
        return "ban.add";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(args[1], sender);
            return;
        }

        if (OddJob.getInstance().getBanManager().getBan(target) == null) {
            StringBuilder sb = new StringBuilder();
            if (args.length >= 3) {
                for (int i = 2; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
            } else {
                sb.append("banned");
            }
            OddJob.getInstance().log(target.toString());
            OddJob.getInstance().log(sb.toString());
            OddJob.getInstance().getBanManager().ban(target, sb.toString());
            OddJob.getInstance().getMessageManager().banAddedSuccess(args[1], sb.toString(), sender);
            return;
        }

        OddJob.getInstance().getMessageManager().banRemoveError(args[1], sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (String name : OddJob.getInstance().getPlayerManager().getNames()) {
                if (args[1].isEmpty()) {
                    list.add(name);
                } else if(name.startsWith(args[1])) {
                    list.add(name);
                }
            }
        }
        return list;
    }
}
