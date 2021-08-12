package com.spillhuset.Commands.Auction;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class AuctionBuyoutCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return false;
    }

    @Override
    public boolean allowOp() {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.auction;
    }

    @Override
    public String getName() {
        return "buy";
    }

    @Override
    public String getDescription() {
        return "Buy a listed stack of items";
    }

    @Override
    public String getSyntax() {
        return "/auction buyout <id>";
    }

    @Override
    public String getPermission() {
        return "auction";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender,false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
            return;
        }

        if (checkArgs(2,2,args,sender,getPlugin())){
            return;
        }

        HashMap<String,Object> ret = OddJob.getInstance().getAuctionManager().buyout(Integer.parseInt(args[1]));
        ItemStack item = ((ItemStack) ret.get("item"));
        OddJob.getInstance().log("You bought id:"+args[1]+" for "+ret.get("buyout")+" item:"+item.getType().name());
        Player player = (Player) sender;
        player.getInventory().addItem(item);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
