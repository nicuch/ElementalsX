package ro.nicuch.elementalsx.elementals.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.enchants.EnchantUtil;
import ro.nicuch.elementalsx.enchants.EnchantUtil.CEnchantType;

public class CustomEnchantCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        User user = ElementalsX.getUser((Player) sender);
        if (!user.hasPermission("elementals.customenchant")) {
            sender.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ElementalsUtil.color("&cTrebuie sa definesti enchantul!"));
            return true;
        }
        if (user.getBase().getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            sender.sendMessage(ElementalsUtil.color("&cTrebuie sa ai un item in mana!"));
            return true;
        }
        try {
            CEnchantType type = CEnchantType.valueOf(args[0].toUpperCase());
            user.getBase().getInventory().setItemInMainHand(
                    EnchantUtil.enchantItem(user.getBase().getInventory().getItemInMainHand(), type));
        } catch (Exception ex) {
            sender.sendMessage(ElementalsUtil.color("&cEnchant invalid!"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> commands = new ArrayList<>();
            for (CEnchantType type : CEnchantType.values())
                commands.add(type.name().toLowerCase());
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
