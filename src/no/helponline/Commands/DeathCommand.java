package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeathCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        int timeCheck = 0;
        try {
            timeCheck = Integer.parseInt(strings[0]);
        } catch (Exception e) {
            OddJob.getInstance().log("int error");
        }
        int i = OddJob.getInstance().getDeathManager().timeCheck(timeCheck);
        OddJob.getInstance().getMessageManager().console("Replaced " + i + " chests");
        return true;
    }
}
