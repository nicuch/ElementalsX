package ro.nicuch.elementalsx.elementals.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

import java.util.Optional;

public class GiveAllCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Optional<User> optionalUser = ElementalsX.getUser((Player) sender);
        if (!optionalUser.isPresent())
            return true;
        User user = optionalUser.get();
        if (!user.hasPermission("elementals.giveall")) {
            user.getBase().sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
            return true;
        }
        for (ItemStack item : user.getBase().getInventory().getContents())
            if (item != null)
                Bukkit.getOnlinePlayers().forEach((Player player) -> {
                    if (player != sender)
                        player.getInventory().addItem(item);
                });
        return true;
    }
}
