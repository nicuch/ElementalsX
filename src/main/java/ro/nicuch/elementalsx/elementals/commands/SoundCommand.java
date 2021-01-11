package ro.nicuch.elementalsx.elementals.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.StringArgument;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

public class SoundCommand {

    public SoundCommand() {
        new CommandAPICommand("sound")
                .withArguments(new StringArgument("type").overrideSuggestionsT(
                        StringTooltip.of("on", ElementalsUtil.color("&cActiveaza sunetele")), StringTooltip.of("off", ElementalsUtil.color("&cDezactiveaza sunetele"))
                ))
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    switch ((String) args[0]) {
                        case "on":
                            if (user.hasSounds()) {
                                user.getBase().sendMessage(ElementalsUtil.color("&6Ai deja sunetele activate!"));
                            } else {
                                user.toggleSounds(true);
                                user.getBase().sendMessage(ElementalsUtil.color("&6Sunetele au fost activate."));
                            }
                            break;
                        case "off":
                            if (!user.hasSounds()) {
                                user.getBase().sendMessage(ElementalsUtil.color("&6Ai deja sunetele dezactivate!"));
                            } else {
                                user.toggleSounds(false);
                                user.getBase().sendMessage(ElementalsUtil.color("&6Sunetele au fost dezactivate."));
                            }
                            break;
                        default:
                            user.getBase().sendMessage(ElementalsUtil.color("&6Foloseste /sound <on/off>."));
                            break;
                    }
                })
                .register();
    }
}
