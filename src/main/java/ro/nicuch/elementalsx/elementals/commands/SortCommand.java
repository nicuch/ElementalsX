package ro.nicuch.elementalsx.elementals.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

public class SortCommand {

    public SortCommand() {
        new CommandAPICommand("sort")
                .executes((player, args) -> {
                    this.sendHelp(player);
                })
                .register();
        new CommandAPICommand("sort")
                .withArguments(new StringArgument("type").overrideSuggestionsT(
                        StringTooltip.of("chest", ElementalsUtil.color("&bSorteaza un chest")),
                        StringTooltip.of("barrel", ElementalsUtil.color("&bSorteaza un butoi")),
                        StringTooltip.of("inventory", ElementalsUtil.color("&bSorteaza-ti inventarul")),
                        StringTooltip.of("ender_chest", ElementalsUtil.color("&bSorteaza-ti ender chest-ul")),
                        StringTooltip.of("shulker", ElementalsUtil.color("&bSorteaza un shulker box"))
                ))
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    String arg0 = (String) args[0];
                    switch (arg0.toLowerCase()) {
                        case "chest":
                        case "trapchest":
                        case "barrel":
                        case "shulker":
                            ElementalsUtil.sortInventoryHolder(user);
                            break;
                        case "inventory":
                        case "inv":
                            ElementalsUtil.sortInventory(user);
                            break;
                        case "enderchest":
                        case "ec":
                        case "ender":
                            ElementalsUtil.sortEnderChest(user);
                            break;
                        default:
                            this.sendHelp(player);
                            break;
                    }
                })
                .register();
    }

    private void sendHelp(CommandSender player) {
        player.sendMessage(ElementalsUtil.color("&a/sort chest -- &fSorteaza un cufar."));
        player.sendMessage(ElementalsUtil.color("&a/sort barrel -- &fSorteaza un butoi."));
        player.sendMessage(ElementalsUtil.color("&6/sort inventory -- &fSorteaza-ti inventarul."));
        player.sendMessage(ElementalsUtil.color("&a/sort enderchest -- &fSorteaza-ti ender chest-ul."));
        player.sendMessage(ElementalsUtil.color("&6/sort shulker -- &fSorteaza un shulker box."));
    }
}
