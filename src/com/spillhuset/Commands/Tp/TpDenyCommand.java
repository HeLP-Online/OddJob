package com.spillhuset.Commands.Tp;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TpDenyCommand extends SubCommand {
    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public String getDescription() {
        return "Denies an incoming teleport request";
    }

    @Override
    public String getSyntax() {
        return "/tp deny";
    }

    @Override
    public String getPermission() {
        return "tp";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.tp);
            return;
        }
        Player player =(Player) sender;

        if (!checkArgs(1,1,args,sender,Plugin.tp)) {
            return;
        }

        OddJob.getInstance().getTeleportManager().deny(player.getUniqueId());
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
