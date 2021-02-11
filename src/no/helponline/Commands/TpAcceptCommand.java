package no.helponline.Commands;

import no.helponline.OddJob;
import no.helponline.Utils.Odd.OddPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpAcceptCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            if (!(commandSender instanceof Player)) return true;
            UUID uuid;
            try {
                uuid = OddJob.getInstance().getPlayerManager().getUUID(strings[0]);
            } catch (Exception ex) {
                OddJob.getInstance().getMessageManager().danger("Wrong accept ID",commandSender,false);
                return true;
            }
            OddJob.getInstance().getTeleportManager().accept(uuid);
        }
        return true;
    }
}
