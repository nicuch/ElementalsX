package ro.nicuch.elementalsx.elementals.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

import java.util.Optional;

public class ChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Optional<User> optionalUser = ElementalsX.getUser((Player) sender);
            if (!optionalUser.isPresent())
                return true;
            User user = optionalUser.get();
            if (!user.hasPermission("elementals.clearchat")) {
                user.getBase().sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                return true;
            }
        }
        if (args.length == 0) {
            sender.sendMessage(ElementalsUtil.color("&cPrea putine argumente"));
            return true;
        }
        switch (args[0]) {
            case "stop":
                ElementalsUtil.toggleChat(true);
                Bukkit.broadcastMessage(ElementalsUtil.color("&9Chatul a fost &coprit&9!"));
                break;
            case "start":
                ElementalsUtil.toggleChat(false);
                Bukkit.broadcastMessage(ElementalsUtil.color("&9Chatul a fost &apornit&9!"));
                break;
            case "clear":
                for (int i = 0; i < 50; i++) {
                    Bukkit.broadcastMessage("");
                }
                Bukkit.broadcastMessage(ElementalsUtil.color("&9Chat-ul a fost sters!"));
                break;
            default:
                sender.sendMessage(ElementalsUtil.color("/chat clear"));
                sender.sendMessage(ElementalsUtil.color("/chat stop"));
                sender.sendMessage(ElementalsUtil.color("/chat start"));
                break;
        }
        return true;
    }
}
