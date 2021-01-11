package ro.nicuch.elementalsx.elementals.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Sound;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

public class AdminChatCommand {

    public AdminChatCommand() {
        new CommandAPICommand("ac")
                .withAliases("adminchat")
                .withArguments(new GreedyStringArgument("message"))
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.adminchat")) {
                        user.getBase().sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    for (User $user : ElementalsX.getOnlineUsers())
                        if ($user.hasPermission("elementals.adminchat")) {
                            $user.getBase().sendMessage(ElementalsUtil.color("&7[&aAdminChat&7] &6" + player.getName() + " &b> &a" + args[0]));
                            if ($user.hasSounds())
                                $user.getBase().playSound($user.getBase().getLocation(), Sound.BLOCK_ANVIL_HIT, 1, 1);
                        }
                })
                .executesConsole((console, args) -> {
                    for (User $user : ElementalsX.getOnlineUsers())
                        if ($user.hasPermission("elementals.adminchat")) {
                            $user.getBase().sendMessage(ElementalsUtil.color("&7[&aAdminChat&7] &cCONSOLE &b> &a" + args[0]));
                            if ($user.hasSounds())
                                $user.getBase().playSound($user.getBase().getLocation(), Sound.BLOCK_ANVIL_HIT, 1, 1);
                        }
                })
                .register();
    }
}
