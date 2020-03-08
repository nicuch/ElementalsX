package ro.nicuch.elementalsx.elementals.commands;

import java.util.*;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.protection.FieldUtil;

public class ProtectionCommand implements TabExecutor {
    private static final String[] COMMANDS = {"info", "allow", "allowall", "remove", "removeall", "disable", "enable",
            "visualise", "loc", "fun", "list", "help"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Optional<User> optionalUser = ElementalsX.getUser((Player) sender);
        if (!optionalUser.isPresent())
            return true;
        User user = optionalUser.get();
        if (args.length > 0) {
            switch (args[0]) {
                case "allow":
                    if (args.length < 2)
                        sender.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oTrebuie sa scri si numele unui jucator!"));
                    else
                        FieldUtil.allowInField(user, args[1], false, true);
                    break;
                case "allowall":
                    if (args.length < 2)
                        sender.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oTrebuie sa scri si numele unui jucator!"));
                    else
                        FieldUtil.allowInField(user, args[1], true, true);
                    break;
                case "remove":
                    if (args.length < 2)
                        sender.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oTrebuie sa scri si numele unui jucator!"));
                    else
                        FieldUtil.allowInField(user, args[1], false, false);
                    break;
                case "removeall":
                    if (args.length < 2)
                        sender.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oTrebuie sa scri si numele unui jucator!"));
                    else
                        FieldUtil.allowInField(user, args[1], true, false);
                    break;
                case "disable":
                    if (user.isIgnoringPlacingFields()) {
                        sender.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oAi dezactivat transformarea blocurilor de diamant din protectii deja."));
                        break;
                    }
                    user.setIgnorePlacingFields(true);
                    sender.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &b&oAcum poti pune blocuri de diamant fara a se transforma in protectii."));
                    break;
                case "enable":
                    if (!user.isIgnoringPlacingFields()) {
                        sender.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu ai dezactivat transformarea blocurilor de diamant in protectii."));
                        break;
                    }
                    user.setIgnorePlacingFields(false);
                    sender.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &b&oAcum blocurile de diamant pe care le pui se vor transforma in protectii."));
                    break;
                case "visualise":
                    FieldUtil.visualiseField(user);
                    break;
                case "info":
                    FieldUtil.infoField(user);
                    break;
                case "loc":
                case "location":
                    FieldUtil.locateField(user);
                    break;
                case "list":
                    FieldUtil.listFields(user);
                    break;
                case "take":
                    FieldUtil.takeProtection(user);
                    break;
                case "fun":
                    FieldUtil.toggleFun(user);
                    break;
                case "help":
                    if (args.length < 2) {
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
                    } else {
                        switch (args[1]) {
                            case "1":
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
                            case "2":
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
                                sender.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oPagina nu a fost gasita!"));
                                break;
                        }
                    }
                    break;
                default:
                    sender.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &bFoloseste &c/ps help <pagina> &b&o!"));
                    break;
            }
        } else {
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
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1)
            StringUtil.copyPartialMatches(args[0], Arrays.asList(COMMANDS), completions);
        if (args.length == 2) {
            if (ElementalsUtil.getPlayersNames().size() == 0)
                return completions;
            StringUtil.copyPartialMatches(args[1], ElementalsUtil.getPlayersNames(), completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
