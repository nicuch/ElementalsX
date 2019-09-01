package ro.nicuch.elementalsx.elementals.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.enchants.EnchantUtil;
import ro.nicuch.elementalsx.enchants.EnchantUtil.CCurseEnchType;

public class CurseEnchantCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ElementalsUtil.color("Nu poti folosi comanda!"));
            return true;
        }
        OfflinePlayer offline = Bukkit.getPlayerExact(args[1]);
        if (!offline.isOnline())
            return true;
        Player player = offline.getPlayer();
        player.getInventory().addItem(EnchantUtil.getCurseBook(CCurseEnchType.valueOf(args[0].toUpperCase())));
        return true;
    }
}
