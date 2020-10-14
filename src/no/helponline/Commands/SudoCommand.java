package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.UUID;

public class SudoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 2) return false;
        UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[0]);
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(strings[0], commandSender);
            return true;
        }
        CommandSender tar = Bukkit.getPlayer(target);
        if (tar != null) {
            OddJob.getInstance().log("tar = ok");
            int c = strings.length;
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < c; i++) {
                sb.append(strings[i]);
                sb.append(" ");
            }
            String cmd = sb.toString().trim();
            OddJob.getInstance().log("execute ("+cmd+")");
            Bukkit.dispatchCommand(tar,cmd);
        }
        return true;
    }
}
