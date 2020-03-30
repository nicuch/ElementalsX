package ro.nicuch.elementalsx.elementals.commands;

import java.util.List;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

public class AdminChatCommand implements CommandExecutor {
    private static final String[] ALIASES = {"ac"};

    public static List<String> getAliases() {
        return Lists.newArrayList(ALIASES);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ElementalsUtil.color("Poti folosi comanda doar ca jucator!"));
            return true;
        }
        Optional<User> optionalUser = ElementalsX.getUser((Player) sender);
        if (optionalUser.isEmpty())
            return true;
        User user = optionalUser.get();
        if (!user.hasPermission("elementals.adminchat")) {
            user.getBase().sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
            return true;
        }
        if (args.length == 0) {
            user.getBase().sendMessage(ElementalsUtil.color("&cTrebuie sa scri un mesaj!"));
            return true;
        }
        String msg = ChatColor.translateAlternateColorCodes('&', ElementalsUtil.arrayToString(args, " "));
        for (User $user : ElementalsX.getOnlineUsers())
            if ($user.hasPermission("elementals.adminchat")) {
                $user.getBase().sendMessage(ElementalsUtil.color("&7[&aAdminChat&7] &6" + sender.getName() + "&b> &a" + msg));
                if ($user.hasSounds())
                    $user.getBase().playSound($user.getBase().getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
            }
        return true;
    }
}
