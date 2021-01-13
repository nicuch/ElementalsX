package ro.nicuch.elementalsx.elementals.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.StringArgument;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.chair.ChairUtil;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

public class ChairCommand {

    public ChairCommand() {
        new CommandAPICommand("sit")
                .executesPlayer((player, args) -> {
                    ChairUtil.sitPlayerOnGround(player);
                })
                .register();
        new CommandAPICommand("unsit")
                .executesPlayer((player, args) -> {
                    ChairUtil.unsitPlayer(player);
                })
                .register();
        new CommandAPICommand("chair")
                .withArguments(new StringArgument("toggle").overrideSuggestionsT(
                        StringTooltip.of("on", "Stai jos pe scaune"),
                        StringTooltip.of("off", "Nu vei mai sta jos pe scaune"))
                )
                .executesPlayer((player, args) -> {
                    User user = ElementalsX.getUserUnsafe(player);
                    switch ((String) args[0]) {
                        case "on":
                            user.canClickOnChairs(true);
                            player.sendMessage(ElementalsUtil.color("&bAcum vei sta jos pe scaune!"));
                            break;
                        case "off":
                            user.canClickOnChairs(false);
                            player.sendMessage(ElementalsUtil.color("&bAcum nu vei mai sta jos pe scaune!"));
                            break;
                        default:
                            player.sendMessage(ElementalsUtil.color("&cFoloseste /chair <on/off>"));
                            break;
                    }
                })
                .register();
    }
}
