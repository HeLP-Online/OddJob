package com.spillhuset.Commands.Teleport;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeleportPositionCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return true;
    }

    @Override
    public boolean allowOp() {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.teleport;
    }

    @Override
    public String getName() {
        return "position";
    }

    @Override
    public String getDescription() {
        return "Teleport to a given position";
    }

    @Override
    public String getSyntax() {
        return "/teleport position <player> <x> <y> <z> [world]";
    }

    @Override
    public String getPermission() {
        return "teleport.pos";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(5, 6, args, sender, Plugin.teleport)){
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.teleport,args[1],sender);
            return;
        }

        World world;

        if (args.length == 6) {
            world = Bukkit.getWorld(args[5]);
            if (world == null) {
                OddJob.getInstance().getMessageManager().errorWorld(args[5],sender,Plugin.teleport);
                return;
            }
        } else {
            world = player.getWorld();
        }
        int x = 0,y = 0,z = 0;
        try{
            x = Integer.parseInt(args[2]);
            y = Integer.parseInt(args[3]);
            z = Integer.parseInt(args[4]);

        }catch (NumberFormatException e) {
            OddJob.getInstance().getMessageManager().invalidNumber(Plugin.teleport,"x="+x+";y="+y+";z="+z+";",sender);
        }
        OddJob.getInstance().getMessageManager().tpPosTarget(sender,player);
        OddJob.getInstance().getTeleportManager().teleport(player,x,y,z,world,false);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (sender == player) {}
                else if (args[1].isEmpty()) {
                    list.add(player.getName());
                } else if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    list.add(player.getName());
                }
            }
        } else if (args.length == 3) {
            list.add("<X>");
        }else if (args.length == 4) {
            list.add("<Y>");
        }else if (args.length == 5) {
            list.add("<Z>");
        }else if (args.length == 6) {
            for (World world : Bukkit.getWorlds()) {
                if (args[5].isEmpty()) {
                    list.add(world.getName());
                } else if (world.getName().toLowerCase().startsWith(args[5].toLowerCase())) {
                    list.add(world.getName());
                }
            }
        }
        return list;
    }
}