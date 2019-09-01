package ro.nicuch.elementalsx.elementals.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

public class PointsCommand implements CommandExecutor, TabCompleter {
    private static final String[] COMMANDS = {"give", "take", "set", "reset"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (!sender.isOp())
                return true;
            switch (args[0]) {
                case "give":
                    if (args.length < 2) {
                        sender.sendMessage(ElementalsUtil.color("&cTrebuie sa scri numele unui jucator!"));
                        break;
                    } else {
                        if (args.length < 3)
                            sender.sendMessage(ElementalsUtil.color("&cTrebuie sa scri suma!"));
                        else
                            ElementalsUtil.changePoints(sender, args[1], args[2], true);
                    }
                    break;
                case "take":
                    if (args.length < 2) {
                        sender.sendMessage(ElementalsUtil.color("&cTrebuie sa scri numele unui jucator!"));
                        break;
                    } else {
                        if (args.length < 3)
                            sender.sendMessage(ElementalsUtil.color("&cTrebuie sa scri suma!"));
                        else
                            ElementalsUtil.changePoints(sender, args[1], args[2], false);
                    }
                    break;
                case "set":
                    if (args.length < 2) {
                        sender.sendMessage(ElementalsUtil.color("&cTrebuie sa scri numele unui jucator!"));
                        break;
                    } else {
                        if (args.length < 3)
                            sender.sendMessage(ElementalsUtil.color("&cTrebuie sa scri suma!"));
                        else
                            ElementalsUtil.setPoints(sender, args[1], args[2], true);
                    }
                    break;
                case "reset":
                    if (args.length < 2) {
                        sender.sendMessage(ElementalsUtil.color("&cTrebuie sa scri numele unui jucator!"));
                        break;
                    } else
                        ElementalsUtil.setPoints(sender, args[1], "0", false);
                    break;
                default:
                    break;
            }
        } else {
            User user = ElementalsX.getUser((Player) sender);
            sender.sendMessage(ElementalsUtil.color("&6&lP&9&lika&6&lP&9&loints&a&l: &c" + user.getPoints()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1)
            StringUtil.copyPartialMatches(args[0], Arrays.asList(COMMANDS), completions);
        if (args.length == 2)
            StringUtil.copyPartialMatches(args[1], ElementalsUtil.getPlayersNames(), completions);
        Collections.sort(completions);
        if (sender.isOp())
            return completions;
        else
            return null;
    }
}
