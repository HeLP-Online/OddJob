package com.spillhuset.Commands.Currency.Bank;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Currency;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankAddCommand extends com.spillhuset.Utils.SubCommand {
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
        return Plugin.currency;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "Adds an amount to the given bank";
    }

    @Override
    public String getSyntax() {
        return "/currency bank add <bank_guild,bank_player> <name> <amount>";
    }

    @Override
    public String getPermission() {
        return "currency.bank.add";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {




    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
