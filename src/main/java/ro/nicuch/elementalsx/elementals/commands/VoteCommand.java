package ro.nicuch.elementalsx.elementals.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

public class VoteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ElementalsUtil.color("&aVoteaza serverul pentru a primi un premiu!"));
        sender.sendMessage(ElementalsUtil.color("&6Click pe link-ul de mai jos pentru a vota."));
        sender.sendMessage(ElementalsUtil.color("&bhttps://minecraft-mp.com/server/239808/vote/"));
        sender.sendMessage(ElementalsUtil.color("&cNu te deconecta cat timp votezi, altfel vei pierde premiul!"));
        return true;
    }
}
