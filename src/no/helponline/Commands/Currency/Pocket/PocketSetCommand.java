package no.helponline.Commands.Currency.Pocket;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.Account;
import no.helponline.Utils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PocketSetCommand extends SubCommand {
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Sets a value to a players pocket";
    }

    @Override
    public String getSyntax() {
        return "/currency pocket set <player> <amount>";
    }

    @Override
    public String getPermission() {
        return "currency.pocket.set";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length != 4) {
            OddJob.getInstance().getMessageManager().errorMissingArgs(sender);
            OddJob.getInstance().getMessageManager().sendSyntax(getSyntax(), sender);
            sender.sendMessage(ChatColor.GOLD + "args: " + ChatColor.RESET + "<player> <amount>");
            return;
        }
        UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[2]);
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(args[2], sender);
            return;
        }
        double amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (Exception e) {
            OddJob.getInstance().getMessageManager().invalidNumber(args[3],sender);
            return;
        }

        OddJob.getInstance().getCurrencyManager().setPocketBalance(target, amount);
        OddJob.getInstance().getMessageManager().currencySuccessSet(args[2], args[3], sender, Account.POCKET);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 3) {
            for (String name : OddJob.getInstance().getPlayerManager().getNames()) {
                if (args[2].isEmpty()) {
                    list.add(name);
                } else if (name.startsWith(args[2])) {
                    list.add(name);
                }
            }
        }
        return list;
    }
}
