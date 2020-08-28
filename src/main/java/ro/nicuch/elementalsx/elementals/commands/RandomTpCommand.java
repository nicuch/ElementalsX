package ro.nicuch.elementalsx.elementals.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.protection.FieldUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RandomTpCommand implements CommandExecutor {
    private static final String[] ALIASES = {"rdtp", "rtp"};

    public static List<String> getAliases() {
        return new ArrayList<>(Arrays.asList(ALIASES));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Optional<User> optionalUser = ElementalsX.getUser((Player) sender);
        if (optionalUser.isEmpty())
            return true;
        User user = optionalUser.get();
        if (!user.canRandomTeleport()) {
            sender.sendMessage(ElementalsUtil.color("&cPoti folosi din nou comanda peste 30 minute!"));
            return true;
        }
        World world = Bukkit.getWorld("world");
        int x = 0;
        int y = 64;
        int z = 0;
        Location loc = new Location(world, x, y, z);
        int tries = 0;
        Biome biome;
        do {
            x = -20000 + ElementalsUtil.nextInt(40000);
            z = -20000 + ElementalsUtil.nextInt(40000);
            if (world.getHighestBlockAt(x, z).getType().isSolid())
                y = world.getHighestBlockYAt(x, z) + 1;
            else
                y = world.getHighestBlockYAt(x, z);
            loc.setWorld(world);
            loc.setX(x + .5);
            loc.setY(y);
            loc.setZ(z + .5);
            tries++;
            biome = world.getBiome(x, y, z);
        } while ((biome == Biome.OCEAN
                || biome == Biome.DEEP_OCEAN
                || biome == Biome.NETHER_WASTES
                || biome == Biome.SOUL_SAND_VALLEY
                || biome == Biome.BASALT_DELTAS
                || biome == Biome.CRIMSON_FOREST
                || biome == Biome.WARPED_FOREST
                || biome == Biome.THE_VOID
                || biome == Biome.THE_END
                || biome == Biome.RIVER
                || biome == Biome.COLD_OCEAN
                || biome == Biome.DEEP_COLD_OCEAN
                || biome == Biome.DEEP_FROZEN_OCEAN
                || biome == Biome.DEEP_LUKEWARM_OCEAN
                || biome == Biome.DEEP_WARM_OCEAN
                || biome == Biome.FROZEN_OCEAN
                || biome == Biome.LUKEWARM_OCEAN
                || biome == Biome.WARM_OCEAN
                || FieldUtil.isFieldAtLocation(loc)) && tries < 1);
        loc.getBlock().setType(Material.AIR);
        if (!user.hasPermission("elementals.randomtp.override")) user.toggleRandomTeleport();
        user.getBase().teleport(loc);
        user.getBase().sendMessage(ElementalsUtil.color("&b&oAi fost teleportat la x:" + loc.getBlockX() + " y:" + loc.getBlockY() + " z:"
                + loc.getBlockZ() + "!"));
        return true;
    }
}
