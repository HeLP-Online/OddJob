package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("ban")) {
            UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[0]);
            if (target == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(strings[0], commandSender);
                return true;
            }

            String text = OddJob.getInstance().getConfig().getString("default.kick_message");
            if (strings.length >= 2) {
                int length = strings.length;
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= length - 1; i++) {
                    sb.append(strings[i]);
                }
                text = sb.toString();
            }

            OddJob.getInstance().getBanManager().ban(target, text);
            OddJob.getInstance().getMessageManager().success("Banned "+ ChatColor.AQUA+OddJob.getInstance().getPlayerManager().getName(target) +ChatColor.GREEN+ " banned with message: "+ChatColor.RESET + text,commandSender,true);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length == 1) {
            List<UUID> bans = OddJob.getInstance().getBanManager().getBans();
            for (UUID player : OddJob.getInstance().getPlayerManager().getUUIDs()) {
                if (!bans.contains(player)) list.add(OddJob.getInstance().getPlayerManager().getName(player));
            }
        }
        return list;
    }
}
