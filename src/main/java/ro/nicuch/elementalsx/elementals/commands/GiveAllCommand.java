package ro.nicuch.elementalsx.elementals.commands;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

import java.util.List;
import java.util.stream.Collectors;

public class GiveAllCommand {

    public GiveAllCommand() {
        new CommandAPICommand("giveall")
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.giveall")) {
                        user.getBase().sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    List<Player> onlinePlayers = Bukkit.getOnlinePlayers().stream().filter(p -> p.equals(player)).collect(Collectors.toList());
                    for (ItemStack item : user.getBase().getInventory().getContents())
                        if (item != null)
                            onlinePlayers.forEach(p -> p.getInventory().addItem(item));
                })
                .register();
    }
}
