package ro.nicuch.elementalsx.elementals.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.protection.FieldUtil;

public class ProtectionCommand {

    public ProtectionCommand() {
        new CommandAPICommand("ps")
                .withAliases("protection")
                .withSubcommand(
                        new CommandAPICommand("help")
                                .executes((sender, args) -> {
                                    sender.sendMessage(ElementalsUtil.color("&f[&bProtectie &f- &cPagina 1&f]&e&m                                                "));
                                    sender.sendMessage(ElementalsUtil.color("&b/ps take"));
                                    sender.sendMessage(ElementalsUtil.color("&8> &f&oDistruge protectia fara a folosi un pickaxe."));
                                    sender.sendMessage(ElementalsUtil.color("&b/ps allow <nume>"));
                                    sender.sendMessage(ElementalsUtil.color("&8> &f&oAdauga un jucator in protectie."));
                                    sender.sendMessage(ElementalsUtil.color("&b/ps allowall <nume>"));
                                    sender.sendMessage(ElementalsUtil.color("&8> &f&oAdauga un jucator in toate protectiile tale."));
                                    sender.sendMessage(ElementalsUtil.color("&b/ps remove <nume>"));
                                    sender.sendMessage(ElementalsUtil.color("&8> &f&oSterge un jucator din protectie."));
                                    sender.sendMessage(ElementalsUtil.color("&b/ps removall <nume>"));
                                    sender.sendMessage(ElementalsUtil.color("&8> &f&oSterge un jucator din toate protectiile tale."));
                                    sender.sendMessage(ElementalsUtil.color("&b/ps help <pagina>"));
                                    sender.sendMessage(ElementalsUtil.color("&8> &f&oAfiseaza o lista cu comenzi."));
                                    sender.sendMessage(ElementalsUtil.color("&e&m                                                                      "));
                                }))
                .withSubcommand(
                        new CommandAPICommand("help")
                                .withArguments(new IntegerArgument("page", 1, 2))
                                .executes((sender, args) -> {
                                    int page = (int) args[0];
                                    switch (page) {
                                        case 1:
                                            sender.sendMessage(ElementalsUtil.color("&f[&bProtectie &f- &cPagina 1&f]&e&m                                                "));
                                            sender.sendMessage(ElementalsUtil.color("&b/ps take"));
                                            sender.sendMessage(ElementalsUtil.color("&8> &f&oDistruge protectia fara a folosi un pickaxe."));
                                            sender.sendMessage(ElementalsUtil.color("&b/ps allow <nume>"));
                                            sender.sendMessage(ElementalsUtil.color("&8> &f&oAdauga un jucator in protectie."));
                                            sender.sendMessage(ElementalsUtil.color("&b/ps allowall <nume>"));
                                            sender.sendMessage(ElementalsUtil.color("&8> &f&oAdauga un jucator in toate protectiile tale."));
                                            sender.sendMessage(ElementalsUtil.color("&b/ps remove <nume>"));
                                            sender.sendMessage(ElementalsUtil.color("&8> &f&oSterge un jucator din protectie."));
                                            sender.sendMessage(ElementalsUtil.color("&b/ps removall <nume>"));
                                            sender.sendMessage(ElementalsUtil.color("&8> &f&oSterge un jucator din toate protectiile tale."));
                                            sender.sendMessage(ElementalsUtil.color("&b/ps help <pagina>"));
                                            sender.sendMessage(ElementalsUtil.color("&8> &f&oAfiseaza o lista cu comenzi."));
                                            sender.sendMessage(ElementalsUtil.color("&e&m                                                                      "));
                                            break;
                                        case 2:
                                            sender.sendMessage(ElementalsUtil.color("&f[&bProtectie &f- &cPagina 2&f]&e&m                                                "));
                                            sender.sendMessage(ElementalsUtil.color("&b/ps list"));
                                            sender.sendMessage(ElementalsUtil.color("&8> &f&oAfiseaza o lista cu toate protectiile tale."));
                                            sender.sendMessage(ElementalsUtil.color("&b/ps enable"));
                                            sender.sendMessage(ElementalsUtil.color("&8> &f&oCand pui blocuri de diamant, acestea se vor transforma in protectii."));
                                            sender.sendMessage(ElementalsUtil.color("&b/ps disable"));
                                            sender.sendMessage(ElementalsUtil.color("&8> &f&oCand pui blocuri de diamant, acestea &c&oNU &f&ose vor transforma in protectii."));
                                            sender.sendMessage(ElementalsUtil.color("&b/ps visualise"));
                                            sender.sendMessage(ElementalsUtil.color("&8> &f&oVizualizeaza perimetrul protectiei tale."));
                                            sender.sendMessage(ElementalsUtil.color("&b/ps info"));
                                            sender.sendMessage(ElementalsUtil.color("&8> &f&oAfiseaza informatii despre protectia ta."));
                                            sender.sendMessage(ElementalsUtil.color("&b/ps loc"));
                                            sender.sendMessage(ElementalsUtil.color("&8> &f&oAfla unde se afla protectia ta."));
                                            sender.sendMessage(ElementalsUtil.color("&b/ps fun"));
                                            sender.sendMessage(ElementalsUtil.color("&8> &f&oJucatorii fara acces vor putea folosi redstone, minecart-uri si barci in protectia ta."));
                                            sender.sendMessage(ElementalsUtil.color("&e&m                                                                      "));
                                            break;
                                        default:
                                            sender.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &bPagina nu a fost gasita!"));
                                            break;
                                    }
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("allow")
                                .executesPlayer((player, args) -> {
                                    player.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oFoloseste /ps allow <jucator>!"));
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("allow")
                                .withArguments(new StringArgument("player").overrideSuggestionsT(sender ->
                                        Bukkit.getOnlinePlayers().stream()
                                                .map(Player::getName)
                                                .filter(playerName -> !playerName.equals(sender.getName()))
                                                .map(playerName -> StringTooltip.of(playerName, ElementalsUtil.color("&bAdauga-l pe &e" + playerName + " &bin protectie")))
                                                .toArray(StringTooltip[]::new)))
                                .executesPlayer((player, args) -> {
                                    User user = ElementalsX.getUserUnsafe(player);
                                    FieldUtil.allowInField(user, (String) args[0], false, true);
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("allowall")
                                .executesPlayer((player, args) -> {
                                    player.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oFoloseste /ps allowall <jucator>!"));
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("allowall")
                                .withArguments(new StringArgument("player").overrideSuggestionsT(sender ->
                                        Bukkit.getOnlinePlayers().stream()
                                                .map(Player::getName)
                                                .filter(playerName -> !playerName.equals(sender.getName()))
                                                .map(playerName -> StringTooltip.of(playerName, ElementalsUtil.color("&bAdauga-l pe &e" + playerName + " &bin protectii")))
                                                .toArray(StringTooltip[]::new)))
                                .executesPlayer((player, args) -> {
                                    User user = ElementalsX.getUserUnsafe(player);
                                    FieldUtil.allowInField(user, (String) args[0], true, true);
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("remove")
                                .executesPlayer((player, args) -> {
                                    player.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oFoloseste /ps remove <jucator>!"));
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("remove")
                                .withArguments(new StringArgument("player").overrideSuggestionsT(sender ->
                                        Bukkit.getOnlinePlayers().stream()
                                                .map(Player::getName)
                                                .filter(playerName -> !playerName.equals(sender.getName()))
                                                .map(playerName -> StringTooltip.of(playerName, ElementalsUtil.color("&bSterge-l pe &e" + playerName + " &bdin protectie")))
                                                .toArray(StringTooltip[]::new)))
                                .executesPlayer((player, args) -> {
                                    User user = ElementalsX.getUserUnsafe(player);
                                    FieldUtil.allowInField(user, (String) args[0], false, false);
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("removeall")
                                .executesPlayer((player, args) -> {
                                    player.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oFoloseste /ps removeall <jucator>!"));
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("removeall")
                                .withArguments(new StringArgument("player").overrideSuggestionsT(sender ->
                                        Bukkit.getOnlinePlayers().stream()
                                                .map(Player::getName)
                                                .filter(playerName -> !playerName.equals(sender.getName()))
                                                .map(playerName -> StringTooltip.of(playerName, ElementalsUtil.color("&bSterge-l pe &e" + playerName + " &bdin protectii")))
                                                .toArray(StringTooltip[]::new)))
                                .executesPlayer((player, args) -> {
                                    User user = ElementalsX.getUserUnsafe(player);
                                    FieldUtil.allowInField(user, (String) args[0], true, false);
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("disable")
                                .executesPlayer((player, args) -> {
                                    User user = ElementalsX.getUserUnsafe(player);
                                    if (user.isIgnoringPlacingFields()) {
                                        player.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oAi dezactivat transformarea blocurilor de diamant din protectii deja."));
                                        return;
                                    }
                                    user.setIgnorePlacingFields(true);
                                    player.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &b&oAcum poti pune blocuri de diamant fara a se transforma in protectii."));
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("enable")
                                .executesPlayer((player, args) -> {
                                    User user = ElementalsX.getUserUnsafe(player);
                                    if (!user.isIgnoringPlacingFields()) {
                                        player.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu ai dezactivat transformarea blocurilor de diamant in protectii."));
                                        return;
                                    }
                                    user.setIgnorePlacingFields(false);
                                    player.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &b&oAcum blocurile de diamant pe care le pui se vor transforma in protectii."));
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("visualise")
                                .executesPlayer((player, args) -> {
                                    User user = ElementalsX.getUserUnsafe(player);
                                    FieldUtil.visualiseField(user);
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("info")
                                .executesPlayer((player, args) -> {
                                    User user = ElementalsX.getUserUnsafe(player);
                                    FieldUtil.infoField(user);
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("location")
                                .withAliases("loc")
                                .executesPlayer((player, args) -> {
                                    User user = ElementalsX.getUserUnsafe(player);
                                    FieldUtil.locateField(user);
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("list")
                                .executesPlayer((player, args) -> {
                                    FieldUtil.listFields(player.getUniqueId(), player);
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("list")
                                .withArguments(new StringArgument("player").overrideSuggestions(sender ->
                                        Bukkit.getOnlinePlayers().stream()
                                                .map(Player::getName)
                                                .filter(playerName -> !playerName.equals(sender.getName()))
                                                .toArray(String[]::new)))
                                .executesPlayer((player, args) -> {
                                    User user = ElementalsX.getUserUnsafe(player);
                                    if (!user.hasPermission("elementals.protection.list.others")) {
                                        user.getBase().sendMessage(ElementalsUtil.color("&cNu ai permisiunea la comanda!"));
                                        return;
                                    }
                                    OfflinePlayer offline = Bukkit.getOfflinePlayer((String) args[0]);
                                    FieldUtil.listFields(offline.getUniqueId(), player);
                                })
                                .executesConsole((console, args) -> {
                                    OfflinePlayer offline = Bukkit.getOfflinePlayer((String) args[0]);
                                    FieldUtil.listFields(offline.getUniqueId(), console);
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("take")
                                .executesPlayer((player, args) -> {
                                    User user = ElementalsX.getUserUnsafe(player);
                                    FieldUtil.takeProtection(user);
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("fun")
                                .executesPlayer((player, args) -> {
                                    User user = ElementalsX.getUserUnsafe(player);
                                    FieldUtil.toggleFun(user);
                                })
                )
                .executes((sender, args) -> {
                    sender.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &bFoloseste &c/ps help [pagina] &b&o!"));
                })
                .register();
    }
}
