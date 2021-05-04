package com.spillhuset.Commands.Ban;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Odd.OddPlayer;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BanListCommand extends SubCommand {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "List all bans";
    }

    @Override
    public String getSyntax() {
        return "/ban list";
    }

    @Override
    public String getPermission() {
        return "ban.list";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermission())) {
            // There are ** players banned
            // ---------------------------
            // 1. <player> : <reason>
            HashMap<OddPlayer,String> bans = new HashMap<>();
            for (UUID uuid : OddJob.getInstance().getBanManager().getBans()) {
                bans.put(OddJob.getInstance().getPlayerManager().getOddPlayer(uuid),OddJob.getInstance().getBanManager().getBan(uuid));
            }
            OddJob.getInstance().getMessageManager().banList(bans,sender);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
