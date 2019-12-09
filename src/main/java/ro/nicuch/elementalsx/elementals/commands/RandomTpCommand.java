package ro.nicuch.elementalsx.elementals.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
    private static final Set<UUID> teleportRequest = new HashSet<>();
    private static final String[] ALIASES = {"rdtp", "rtp"};

    public static List<String> getAliases() {
        return new ArrayList<>(Arrays.asList(ALIASES));
    }

    public static boolean hasTeleportReq(User user) {
        return teleportRequest.contains(user.getBase().getUniqueId());
    }

    public static void removeTeleportReq(User user) {
        teleportRequest.remove(user.getBase().getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        User user = ElementalsX.getUser((Player) sender);
        if (teleportRequest.contains(user.getBase().getUniqueId())) {
            sender.sendMessage(ElementalsUtil.color("&cO cerere de teleportare a fost trimisa deja!"));
            return true;
        }
        if (ElementalsUtil.hasRandomTpDelay(user) && !user.getBase().isOp()) {
            sender.sendMessage(ElementalsUtil.color("&cPoti folosi din nou comanda peste 30 minute!"));
            return true;
        }
        World world = Bukkit.getWorld("world");
        int x = 0;
        int y = 64;
        int z = 0;
        Location loc = new Location(world, x, y, z);
        do {
            x = -10000 + ElementalsUtil.nextInt(20000);
            z = -10000 + ElementalsUtil.nextInt(20000);
            world.loadChunk(x, z);
            if (world.getHighestBlockAt(x, z).getType().isSolid())
                y = world.getHighestBlockYAt(x, z) + 1;
            else
                y = world.getHighestBlockYAt(x, z);
            loc.setWorld(world);
            loc.setX(x + .5);
            loc.setY(y);
            loc.setZ(z + .5);
        } while (world.getBiome(x, z) == Biome.OCEAN || world.getBiome(x, z) == Biome.DEEP_OCEAN
                || world.getBiome(x, z) == Biome.NETHER || world.getBiome(x, z) == Biome.THE_VOID
                || world.getBiome(x, z) == Biome.THE_END
                || world.getBiome(x, z) == Biome.RIVER
                || world.getBiome(x, z) == Biome.COLD_OCEAN
                || world.getBiome(x, z) == Biome.DEEP_COLD_OCEAN
                || world.getBiome(x, z) == Biome.DEEP_FROZEN_OCEAN
                || world.getBiome(x, z) == Biome.DEEP_LUKEWARM_OCEAN
                || world.getBiome(x, z) == Biome.DEEP_WARM_OCEAN
                || world.getBiome(x, z) == Biome.FROZEN_OCEAN
                || world.getBiome(x, z) == Biome.LUKEWARM_OCEAN
                || world.getBiome(x, z) == Biome.WARM_OCEAN
        || FieldUtil.isFieldAtLocation(loc));
        loc.getBlock().setType(Material.AIR);
        loc.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
        user.getBase().sendMessage(ElementalsUtil.color("&6Nu te misca pana vei fi teleportat!"));
        if (!user.getBase().isOp()) {
            teleportRequest.add(user.getBase().getUniqueId());
            int taskID = Bukkit.getScheduler().runTaskLater(ElementalsX.get(), () -> {
                user.getBase().teleport(loc);
                user.getBase().sendMessage(ElementalsUtil.color("&bAi fost teleportat la x:" + loc.getBlockX() + " y:" + loc.getBlockY()
                        + " z:" + loc.getBlockZ() + "!"));
                ElementalsUtil.delayRandomTPPlayer(user);
                ElementalsUtil.removeTandomTpCmdDelay(user);
                teleportRequest.remove(user.getBase().getUniqueId());
            }, 20L).getTaskId();
            ElementalsUtil.delayRandomTPCmdPlayer(user, taskID);
        } else {
            user.getBase().teleport(loc);
            user.getBase().sendMessage(ElementalsUtil.color("&bAi fost teleportat la x:" + loc.getBlockX() + " y:" + loc.getBlockY() + " z:"
                    + loc.getBlockZ() + "!"));
        }
        return true;
    }
}
