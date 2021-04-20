package no.helponline.Commands.Currency.Pocket;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.Account;
import no.helponline.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PocketAddCommand extends SubCommand {
    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "Adds an amount to the players pocket";
    }

    @Override
    public String getSyntax() {
        return "/currency pocket add <player> <amount>";
    }

    public String getPermission() {
        return "currency.pocket.add";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length != 4) {
            OddJob.getInstance().getMessageManager().errorMissingArgs(sender);
            OddJob.getInstance().getMessageManager().sendSyntax(getSyntax(), sender);
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
            OddJob.getInstance().getMessageManager().invalidNumber(args[3], sender);
            return;
        }
        OddJob.getInstance().getCurrencyManager().addPocketBalance(target, amount);
        double balance = OddJob.getInstance().getCurrencyManager().getPocketBalance(target);
        OddJob.getInstance().getMessageManager().currencySuccessAdded(args[2], args[3], balance, sender, Account.POCKET);
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
