package ro.nicuch.elementalsx.elementals.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.hover.HoverUtil;

public class ShowCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        User user = ElementalsX.getUser((Player) sender);
        /*
         * TODO Add later ess com.earth2me.essentials.User essUser =
         * Elementals.getEssentials().getUserMap() .getUser(user.getBase().getName());
         * if (essUser.isMuted()) {
         * sender.sendMessage("&cNu poti folosi comanda cat timp ai mute!"); return
         * true; }
         */
        ItemStack item = user.getBase().getInventory().getItemInMainHand();
        if (item.getType().equals(Material.AIR)) {
            sender.sendMessage(ElementalsUtil.color("&cTrebuie sa ti un obiect in mana!"));
            return true;
        }
        ElementalsUtil.broadcastRawMessage(
                "[\"\",{\"text\":\"[\",\"color\":\"yellow\"},{\"text\":\"/show\",\"color\":\"blue\"},{\"text\":\"]\",\"color\":\"yellow\"},{\"text\":\" " + user.getBase().getDisplayName() +  " \",\"color\":\"none\"},{\"text\":\">\",\"color\":\"red\"},{\"text\":\"" + "the item" + "\",\"hoverEvent\":{\"action\":\"show_item\",\"value\":\"" + HoverUtil.rawMessage(item) + "\"},\"color\":\"none\"}]"
        );
        // HoverUtil.convertItemToHover("&c[&b/show&c] &r" +
        // user.getBase().getDisplayName() + " &e&l> &r", null,
        // user.getBase().getInventory().getItemInMainHand()));
        return true;
    }
}
