package no.helponline.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public interface SubCommandInterface {
    ArrayList<SubCommand> subCommands = new ArrayList<>();

    boolean onCommand(CommandSender sender, Command command, String label, String[] args);

}
