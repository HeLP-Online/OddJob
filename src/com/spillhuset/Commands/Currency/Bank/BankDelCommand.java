package com.spillhuset.Commands.Currency.Bank;

import org.bukkit.command.CommandSender;

import java.util.List;

public class BankDelCommand extends com.spillhuset.Utils.SubCommand {
    @Override
    public String getName() {
        return "del";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
