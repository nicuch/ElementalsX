package ro.nicuch.elementalsx.elementals.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

public class SortCommand implements TabExecutor {
    private static final String[] COMMANDS = {"chest", "inventory", "enderchest", "shulker"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 0) {
            User user = ElementalsX.getUser((Player) sender);
            switch (args[0].toLowerCase()) {
                case "chest":
                case "trapchest":
                case "barrel":
                case "shulker":
                    ElementalsUtil.sortInventoryHolder(user);
                    break;
                case "inventory":
                case "inv":
                    ElementalsUtil.sortInventory(user);
                    break;
                case "enderchest":
                case "ec":
                    ElementalsUtil.sortEnderChest(user);
                    break;
                default:
                    sender.sendMessage(ElementalsUtil.color("&a/sort chest -- &fSorteaza un cufar."));
                    sender.sendMessage(ElementalsUtil.color("&a/sort barrel -- &fSorteaza un butoi."));
                    sender.sendMessage(ElementalsUtil.color("&6/sort inventory -- &fSorteaza-ti inventarul."));
                    sender.sendMessage(ElementalsUtil.color("&a/sort enderchest -- &fSorteaza-ti ender chest-ul."));
                    sender.sendMessage(ElementalsUtil.color("&6/sort shulker -- &fSorteaza un shulker box."));
                    break;
            }
        } else {
            sender.sendMessage(ElementalsUtil.color("&a/sort chest -- &fSorteaza un cufar."));
            sender.sendMessage(ElementalsUtil.color("&a/sort barrel -- &fSorteaza un butoi."));
            sender.sendMessage(ElementalsUtil.color("&6/sort inventory -- &fSorteaza-ti inventarul."));
            sender.sendMessage(ElementalsUtil.color("&a/sort enderchest -- &fSorteaza-ti ender chest-ul."));
            sender.sendMessage(ElementalsUtil.color("&6/sort shulker -- &fSorteaza un shulker box."));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1)
            StringUtil.copyPartialMatches(args[0], Arrays.asList(COMMANDS), completions);
        Collections.sort(completions);
        return completions;
    }
}
