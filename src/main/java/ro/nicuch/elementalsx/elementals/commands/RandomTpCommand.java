package ro.nicuch.elementalsx.elementals.commands;

import java.util.*;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.protection.FieldUtil;

public class RandomTpCommand implements CommandExecutor {
    private static final String[] ALIASES = {"rdtp", "rtp"};

    public static List<String> getAliases() {
        return new ArrayList<>(Arrays.asList(ALIASES));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Optional<User> optionalUser = ElementalsX.getUser((Player) sender);
        if (!optionalUser.isPresent())
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
        do {
            x = -10000 + ElementalsUtil.nextInt(20000);
            z = -10000 + ElementalsUtil.nextInt(20000);
            if (world.getHighestBlockAt(x, z).getType().isSolid())
                y = world.getHighestBlockYAt(x, z) + 1;
            else
                y = world.getHighestBlockYAt(x, z);
            loc.setWorld(world);
            loc.setX(x + .5);
            loc.setY(y);
            loc.setZ(z + .5);
            tries++;
        } while ((world.getBiome(x, y, z) == Biome.OCEAN || world.getBiome(x, y, z) == Biome.DEEP_OCEAN
                || world.getBiome(x, y, z) == Biome.NETHER || world.getBiome(x, y, z) == Biome.THE_VOID
                || world.getBiome(x, y, z) == Biome.THE_END
                || world.getBiome(x, y, z) == Biome.RIVER
                || world.getBiome(x, y, z) == Biome.COLD_OCEAN
                || world.getBiome(x, y, z) == Biome.DEEP_COLD_OCEAN
                || world.getBiome(x, y, z) == Biome.DEEP_FROZEN_OCEAN
                || world.getBiome(x, y, z) == Biome.DEEP_LUKEWARM_OCEAN
                || world.getBiome(x, y, z) == Biome.DEEP_WARM_OCEAN
                || world.getBiome(x, y, z) == Biome.FROZEN_OCEAN
                || world.getBiome(x, y, z) == Biome.LUKEWARM_OCEAN
                || world.getBiome(x, y, z) == Biome.WARM_OCEAN
                || FieldUtil.isFieldAtLocation(loc)) && tries <= 10);
        loc.getBlock().setType(Material.AIR);
        loc.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
        loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.GRASS_BLOCK);
        user.getBase().sendMessage(ElementalsUtil.color("&6Nu te misca pana vei fi teleportat!"));
        if (!user.hasPermission("elementals.randomtp.override")) user.toggleRandomTeleport();
        user.getBase().teleport(loc);
        user.getBase().sendMessage(ElementalsUtil.color("&b&oAi fost teleportat la x:" + loc.getBlockX() + " y:" + loc.getBlockY() + " z:"
                + loc.getBlockZ() + "!"));
        return true;
    }
}
