package no.helponline.Commands.Ban;

import no.helponline.OddJob;
import no.helponline.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanRemoveCommand extends SubCommand {

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Removes a ban from the player";
    }

    @Override
    public String getSyntax() {
        return "/ban remove <player>";
    }

    @Override
    public String getPermission() {
        return "ban.remove";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(args[1], sender);
            return;
        }

        if (OddJob.getInstance().getBanManager().getBan(target) != null) {
            OddJob.getInstance().getBanManager().unban(target);
            OddJob.getInstance().getMessageManager().banRemoveSuccess(args[1], sender);
            return;
        }

        OddJob.getInstance().getMessageManager().banRemoveError(args[1], sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (UUID uuid : OddJob.getInstance().getBanManager().getBans()) {
            String name = OddJob.getInstance().getPlayerManager().getName(uuid);
            if (args[1].isEmpty()) {
                list.add(name);
            } else if (name.startsWith(args[1])) {
                list.add(name);
            }
        }
        return list;
    }
}
