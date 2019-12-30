package ro.nicuch.elementalsx.elementals.commands;

import java.util.*;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

public class SoundCommand implements TabExecutor {
    private static final String[] COMMANDS = {"on", "off"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Optional<User> optionalUser = ElementalsX.getUser((Player) sender);
        if (!optionalUser.isPresent())
            return true;
        User user = optionalUser.get();
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "on":
                    if (user.hasSounds()) {
                        user.getBase().sendMessage(ElementalsUtil.color("&6Ai deja sunetele activate!"));
                    } else {
                        user.toggleSounds(true);
                        user.getBase().sendMessage(ElementalsUtil.color("&6Sunetele au fost activate."));
                    }
                    break;
                case "off":
                    if (!user.hasSounds()) {
                        user.getBase().sendMessage(ElementalsUtil.color("&6Ai deja sunetele dezactivate!"));
                    } else {
                        user.toggleSounds(false);
                        user.getBase().sendMessage(ElementalsUtil.color("&6Sunetele au fost dezactivate."));
                    }
                    break;
                default:
                    user.getBase().sendMessage(ElementalsUtil.color("&c/sound off -- &fDezactiveaza sunetele din chat."));
                    user.getBase().sendMessage(ElementalsUtil.color("&5/sound on -- &fActiveaza sunetele din chat."));
                    break;
            }
        } else {
            user.getBase().sendMessage(ElementalsUtil.color("&c/sound off -- &fDezactiveaza sunetele din chat."));
            user.getBase().sendMessage(ElementalsUtil.color("&5/sound on -- &fActiveaza sunetele din chat."));
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
