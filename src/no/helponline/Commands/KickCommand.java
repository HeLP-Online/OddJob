package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class KickCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("kick")) {
            // COMMAND KICK
            String message = OddJob.getInstance().getConfigManager().getString("default.kick_message");
            if (strings.length >= 2) {
                int length = strings.length;
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= length - 1; i++) {
                    sb.append(strings[i]);
                }
                message = sb.toString();
            }
            Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target == null || !target.isOnline()) {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                return true;
            }
            OddJob.getInstance().getBanManager().kick(target, message);
            commandSender.sendMessage("Player " + target.getName() + " kicked, reason: " + message);
            // todo permission
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        // TODO
        return null;
    }
}
