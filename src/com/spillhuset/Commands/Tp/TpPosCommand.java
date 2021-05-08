package com.spillhuset.Commands.Tp;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TpPosCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return false;
    }

    @Override
    public boolean allowOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.tp;
    }

    @Override
    public String getName() {
        return "pos";
    }

    @Override
    public String getDescription() {
        return "Teleport to a given position";
    }

    @Override
    public String getSyntax() {
        return "/tp pos <player> <x> <y> <z> [world]";
    }

    @Override
    public String getPermission() {
        return "tp.pos";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(5, 6, args, sender, Plugin.tp)){
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.tp,args[1],sender);
            return;
        }

        World world;

        if (args.length == 6) {
            world = Bukkit.getWorld(args[5]);
            if (world == null) {
                OddJob.getInstance().getMessageManager().errorWorld(args[5],sender,Plugin.tp);
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
            OddJob.getInstance().getMessageManager().invalidNumber(Plugin.tp,"x="+x+";y="+y+";z="+z+";",sender);
        }
        OddJob.getInstance().getMessageManager().tpPosTarget(sender,player);
        OddJob.getInstance().getTeleportManager().teleport(player,x,y,z,world,false);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}