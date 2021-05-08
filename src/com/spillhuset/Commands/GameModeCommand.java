package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameModeCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    @Override
    public boolean allowOp() {
        return true;
    }

    @Override
    public boolean allowConsole() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.gamemode;
    }

    @Override
    public String getPermission() {
        return "gamemode";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (checkArgs(1,2,args,sender,getPlugin()))  {
            return true;
        }

        GameMode gm = GameMode.SURVIVAL;
        Player target = null;

        String str = args[0];

        if (str.startsWith("sp") || str.startsWith("SP") || str.equals("2")) {
            gm = GameMode.SPECTATOR;
        } else if (str.startsWith("c") || str.startsWith("C") || str.equals("1")) {
            gm = GameMode.CREATIVE;
        } else if (str.startsWith("a") || str.startsWith("A") || str.equals("3")) {
            gm = GameMode.ADVENTURE;
        }

        if (args.length == 2) {
            target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(args[1]));
            if (target == null || !target.isOnline()) {
                OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[1], sender);
                return true;
            }
            if (!can(sender,true)){
                OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
                return true;
            }
        } else if (args.length == 1 && sender instanceof Player) {
            target = (Player) sender;
        }

        if (target != null && can(sender,false)) {
            OddJob.getInstance().getPlayerManager().setGameMode(target, gm);
            OddJob.getInstance().getMessageManager().gamemmodeChanged(target.getName(), sender);
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        String name = sender.getName();
        List<String> list = new ArrayList<>();
        if (args.length == 0) {
            for (GameMode gm : GameMode.values()) {
                list.add(gm.name().toLowerCase());
            }
        } else if (args.length == 1) {
            for (GameMode gm : GameMode.values()) {
                if (gm.name().toLowerCase().startsWith(args[0]) || gm.name().startsWith(args[0])) {
                    list.add(gm.name());
                }
            }
        } else if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase()) && !player.getName().equals(name)) {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}
