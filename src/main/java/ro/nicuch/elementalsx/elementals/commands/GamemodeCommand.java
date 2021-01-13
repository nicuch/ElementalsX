package ro.nicuch.elementalsx.elementals.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

public class GamemodeCommand {

    public GamemodeCommand() {
        new CommandAPICommand("gamemode")
                .withAliases("gm")
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.gamemode")) {
                        player.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    player.sendMessage(ElementalsUtil.color("&cFoloseste /gm <id/mod>"));
                })
                .executesConsole((console, args) -> {
                    console.sendMessage(ElementalsUtil.color("&cFoloseste /gm <id/mod> <player>"));
                })
                .register();
        new CommandAPICommand("gamemode")
                .withAliases("gm")
                .withArguments(new StringArgument("mod").overrideSuggestionsT(
                        StringTooltip.of("creative", "Schimba gamemode-ul in creative"),
                        StringTooltip.of("survival", "Schimba gamemode-ul in survival"),
                        StringTooltip.of("adventure", "Schimba gamemode-ul in adventure"),
                        StringTooltip.of("spectator", "Schimba gamemode-ul in spectator")
                ))
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.gamemode")) {
                        player.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    switch ((String) args[0]) {
                        case "creative":
                        case "c":
                            player.setGameMode(GameMode.CREATIVE);
                            player.sendMessage(ElementalsUtil.color("&6Ti-ai schimbat gamemode-ul in creative"));
                            break;
                        case "survival":
                        case "s":
                        case "surv":
                            player.setGameMode(GameMode.SURVIVAL);
                            player.sendMessage(ElementalsUtil.color("&6Ti-ai schimbat gamemode-ul in survival"));
                            break;
                        case "adventure":
                        case "a":
                        case "adv":
                            player.setGameMode(GameMode.ADVENTURE);
                            player.sendMessage(ElementalsUtil.color("&6Ti-ai schimbat gamemode-ul in adventure"));
                            break;
                        case "spectator":
                        case "spec":
                            player.setGameMode(GameMode.SPECTATOR);
                            player.sendMessage(ElementalsUtil.color("&6Ti-ai schimbat gamemode-ul in spectator"));
                            break;
                        default:
                            player.sendMessage(ElementalsUtil.color("&cGamemode-uri posibile: &acreative&c, &asurvival&c, &aadventure&c, &aspectator"));
                            break;
                    }
                })
                .register();
        new CommandAPICommand("gamemode")
                .withAliases("gm")
                .withArguments(new StringArgument("mod").overrideSuggestionsT(
                        StringTooltip.of("creative", "Schimba gamemode-ul in creative"),
                        StringTooltip.of("survival", "Schimba gamemode-ul in survival"),
                        StringTooltip.of("adventure", "Schimba gamemode-ul in adventure"),
                        StringTooltip.of("spectator", "Schimba gamemode-ul in spectator")
                ), new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.gamemode.other")) {
                        player.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    Player other = (Player) args[1];
                    switch ((String) args[0]) {
                        case "creative":
                        case "c":
                            other.setGameMode(GameMode.CREATIVE);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in creative"));
                            player.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in creative jucatorului &e" + other.getName()));
                            break;
                        case "survival":
                        case "s":
                        case "surv":
                            other.setGameMode(GameMode.SURVIVAL);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in survival"));
                            player.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in survival jucatorului &e" + other.getName()));
                            break;
                        case "adventure":
                        case "a":
                        case "adv":
                            other.setGameMode(GameMode.ADVENTURE);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in adventure"));
                            player.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in adventure jucatorului &e" + other.getName()));
                            break;
                        case "spectator":
                        case "spec":
                            other.setGameMode(GameMode.SPECTATOR);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in spectator"));
                            player.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in spectator jucatorului &e" + other.getName()));
                            break;
                        default:
                            player.sendMessage(ElementalsUtil.color("&cGamemode-uri posibile: &acreative&c, &asurvival&c, &aadventure&c, &aspectator"));
                            break;
                    }
                })
                .executesConsole((console, args) -> {
                    Player other = (Player) args[1];
                    switch ((String) args[0]) {
                        case "creative":
                        case "c":
                            other.setGameMode(GameMode.CREATIVE);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in creative"));
                            console.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in creative jucatorului &e" + other.getName()));
                            break;
                        case "survival":
                        case "s":
                        case "surv":
                            other.setGameMode(GameMode.SURVIVAL);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in survival"));
                            console.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in survival jucatorului &e" + other.getName()));
                            break;
                        case "adventure":
                        case "a":
                        case "adv":
                            other.setGameMode(GameMode.ADVENTURE);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in adventure"));
                            console.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in adventure jucatorului &e" + other.getName()));
                            break;
                        case "spectator":
                        case "spec":
                            other.setGameMode(GameMode.SPECTATOR);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in spectator"));
                            console.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in spectator jucatorului &e" + other.getName()));
                            break;
                        default:
                            console.sendMessage(ElementalsUtil.color("&cGamemode-uri posibile: &acreative&c, &asurvival&c, &aadventure&c, &aspectator"));
                            break;
                    }
                })
                .register();
        new CommandAPICommand("gamemode")
                .withAliases("gm")
                .withArguments(new IntegerArgument("id", 0, 3))
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.gamemode")) {
                        player.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    switch ((int) args[0]) {
                        case 1:
                            player.setGameMode(GameMode.CREATIVE);
                            player.sendMessage(ElementalsUtil.color("&6Ti-ai schimbat gamemode-ul in creative"));
                            break;
                        case 0:
                            player.setGameMode(GameMode.SURVIVAL);
                            player.sendMessage(ElementalsUtil.color("&6Ti-ai schimbat gamemode-ul in survival"));
                            break;
                        case 2:
                            player.setGameMode(GameMode.ADVENTURE);
                            player.sendMessage(ElementalsUtil.color("&6Ti-ai schimbat gamemode-ul in adventure"));
                            break;
                        case 3:
                            player.setGameMode(GameMode.SPECTATOR);
                            player.sendMessage(ElementalsUtil.color("&6Ti-ai schimbat gamemode-ul in spectator"));
                            break;
                        default:
                            player.sendMessage(ElementalsUtil.color("&cGamemode-uri posibile: &acreative&c, &asurvival&c, &aadventure&c, &aspectator"));
                            break;
                    }
                })
                .register();
        new CommandAPICommand("gamemode")
                .withAliases("gm")
                .withArguments(new IntegerArgument("id", 0, 3), new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.gamemode.other")) {
                        player.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    Player other = (Player) args[1];
                    switch ((int) args[0]) {
                        case 1:
                            other.setGameMode(GameMode.CREATIVE);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in creative"));
                            player.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in creative jucatorului &e" + other.getName()));
                            break;
                        case 0:
                            other.setGameMode(GameMode.SURVIVAL);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in survival"));
                            player.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in survival jucatorului &e" + other.getName()));
                            break;
                        case 2:
                            other.setGameMode(GameMode.ADVENTURE);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in adventure"));
                            player.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in adventure jucatorului &e" + other.getName()));
                            break;
                        case 3:
                            other.setGameMode(GameMode.SPECTATOR);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in spectator"));
                            player.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in spectator jucatorului &e" + other.getName()));
                            break;
                        default:
                            player.sendMessage(ElementalsUtil.color("&cGamemode-uri posibile: &acreative&c, &asurvival&c, &aadventure&c, &aspectator"));
                            break;
                    }
                })
                .executesConsole((console, args) -> {
                    Player other = (Player) args[1];
                    switch ((int) args[0]) {
                        case 1:
                            other.setGameMode(GameMode.CREATIVE);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in creative"));
                            console.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in creative jucatorului &e" + other.getName()));
                            break;
                        case 0:
                            other.setGameMode(GameMode.SURVIVAL);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in survival"));
                            console.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in survival jucatorului &e" + other.getName()));
                            break;
                        case 2:
                            other.setGameMode(GameMode.ADVENTURE);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in adventure"));
                            console.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in adventure jucatorului &e" + other.getName()));
                            break;
                        case 3:
                            other.setGameMode(GameMode.SPECTATOR);
                            other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in spectator"));
                            console.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in spectator jucatorului &e" + other.getName()));
                            break;
                        default:
                            console.sendMessage(ElementalsUtil.color("&cGamemode-uri posibile: &acreative&c, &asurvival&c, &aadventure&c, &aspectator"));
                            break;
                    }
                })
                .register();
        new CommandAPICommand("gmc")
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.gamemode")) {
                        player.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    player.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(ElementalsUtil.color("&6Ti-ai schimbat gamemode-ul in creative"));
                })
                .executesConsole((console, args) -> {
                    console.sendMessage(ElementalsUtil.color("&cFoloseste /gmc <player>"));
                })
                .register();
        new CommandAPICommand("gms")
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.gamemode")) {
                        player.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(ElementalsUtil.color("&6Ti-ai schimbat gamemode-ul in survival"));
                })
                .executesConsole((console, args) -> {
                    console.sendMessage(ElementalsUtil.color("&cFoloseste /gmc <player>"));
                })
                .register();
        new CommandAPICommand("gma")
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.gamemode")) {
                        player.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    player.setGameMode(GameMode.ADVENTURE);
                    player.sendMessage(ElementalsUtil.color("&6Ti-ai schimbat gamemode-ul in adventure"));
                })
                .executesConsole((console, args) -> {
                    console.sendMessage(ElementalsUtil.color("&cFoloseste /gmc <player>"));
                })
                .register();
        new CommandAPICommand("gmsp")
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.gamemode")) {
                        player.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(ElementalsUtil.color("&6Ti-ai schimbat gamemode-ul in spectator"));
                })
                .executesConsole((console, args) -> {
                    console.sendMessage(ElementalsUtil.color("&cFoloseste /gmc <player>"));
                })
                .register();
        new CommandAPICommand("gmc")
                .withArguments(new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.gamemode.other")) {
                        player.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    Player other = (Player) args[0];
                    other.setGameMode(GameMode.CREATIVE);
                    other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in creative"));
                    player.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in creative jucatorului &e" + other.getName()));
                })
                .executesConsole((console, args) -> {
                    Player other = (Player) args[0];
                    other.setGameMode(GameMode.CREATIVE);
                    other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in creative"));
                    console.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in creative jucatorului &e" + other.getName()));
                })
                .register();
        new CommandAPICommand("gms")
                .withArguments(new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.gamemode.other")) {
                        player.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    Player other = (Player) args[0];
                    other.setGameMode(GameMode.SURVIVAL);
                    other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in survival"));
                    player.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in survival jucatorului &e" + other.getName()));
                })
                .executesConsole((console, args) -> {
                    Player other = (Player) args[0];
                    other.setGameMode(GameMode.SURVIVAL);
                    other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in survival"));
                    console.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in survival jucatorului &e" + other.getName()));
                })
                .register();
        new CommandAPICommand("gma")
                .withArguments(new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.gamemode.other")) {
                        player.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    Player other = (Player) args[0];
                    other.setGameMode(GameMode.ADVENTURE);
                    other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in adventure"));
                    player.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in adventure jucatorului &e" + other.getName()));
                })
                .executesConsole((console, args) -> {
                    Player other = (Player) args[0];
                    other.setGameMode(GameMode.ADVENTURE);
                    other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in adventure"));
                    console.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in adventure jucatorului &e" + other.getName()));
                })
                .register();
        new CommandAPICommand("gmsp")
                .withArguments(new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    if (!user.hasPermission("elementals.gamemode.other")) {
                        player.sendMessage(ElementalsUtil.color("&cNu ai permisiunea!"));
                        return;
                    }
                    Player other = (Player) args[0];
                    other.setGameMode(GameMode.SPECTATOR);
                    other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in spectator"));
                    player.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in spectator jucatorului &e" + other.getName()));
                })
                .executesConsole((console, args) -> {
                    Player other = (Player) args[0];
                    other.setGameMode(GameMode.SPECTATOR);
                    other.sendMessage(ElementalsUtil.color("&6Ti-a fost schimbat gamemode-ul in spectator"));
                    console.sendMessage(ElementalsUtil.color("&6Ai schimbat gamemode-ul in spectator jucatorului &e" + other.getName()));
                })
                .register();
    }
}
