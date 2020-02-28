package no.helponline.Commands;

import no.helponline.Managers.JailManager.Slot;
import no.helponline.OddJob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.UUID;

public class JailCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (strings.length == 1 && strings[0].equalsIgnoreCase("status")) {
            if (OddJob.getInstance().getJailManager().has(player.getWorld().getUID())) {
                OddJob.getInstance().getJailManager().revertPoints();
                OddJob.getInstance().getMessageManager().console("All set!");
            }
            return true;
        }
        if (strings.length == 2 && strings[0].equalsIgnoreCase("set")) {
            switch (Slot.valueOf(strings[1])) {
                case warden:
                case free:
                case lobby:
                    OddJob.getInstance().getJailManager().set(Slot.valueOf(strings[1]), player);
                    OddJob.getInstance().getMessageManager().success("Point of " + Slot.valueOf(strings[1]).name() + " set!", player, false);
                    break;
                default:
                    OddJob.getInstance().getMessageManager().danger("Syntax error", player, false);
            }
            return true;
        }
        if (strings.length == 1 && strings[0].equalsIgnoreCase("warden")) {
            if (OddJob.getInstance().getJailManager().has(player.getWorld().getUID())) {
                OddJob.getInstance().getTeleportManager().teleport(player,OddJob.getInstance().getJailManager().jailWarden(player.getWorld().getUID()),0, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
            return true;
        }

        if (strings.length == 2 && strings[0].equalsIgnoreCase("catch")) {
            UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
            if (target == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(strings[1],commandSender);
                return true;
            }

            OddJob.getInstance().getJailManager().setInJail(target,OddJob.getInstance().getPlayerManager().getPlayer(target).getWorld().getUID(),commandSender);
            return true;
        }
        if (strings.length == 2 && strings[0].equalsIgnoreCase("free")) {
            UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
            if (target == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(strings[1],commandSender);
                return true;
            }

            OddJob.getInstance().getJailManager().freeFromJail(target,commandSender);
        }
        return true;
    }
}
