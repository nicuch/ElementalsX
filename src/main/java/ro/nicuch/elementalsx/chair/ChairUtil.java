package ro.nicuch.elementalsx.chair;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChairUtil {
    public final static Map<UUID, ArmorStand> chairs = new HashMap<>();
    public final static Map<UUID, ArmorStand> sits = new HashMap<>();
    public final static Map<UUID, Block> occupiedChairs = new HashMap<>();
    public final static Map<UUID, Block> occupiedSits = new HashMap<>();

    public static void sitPlayerOnGround(Player player) {
        if (player.isSneaking() || player.isGliding() || player.isRiptiding())
            return;
        Block above = player.getLocation().getBlock();
        if (!above.getType().isAir()) {
            player.sendMessage(ElementalsUtil.color("&cNu te poti aseza aici!"));
            return;
        }
        Block block = above.getRelative(BlockFace.DOWN);
        if (sits.containsKey(player.getUniqueId())) {
            player.sendMessage(ElementalsUtil.color("&cStai deja jos!"));
            return;
        }
        if (chairs.containsKey(player.getUniqueId())) {
            player.sendMessage(ElementalsUtil.color("&cTe aflii deja intr-un scaun!"));
            return;
        }
        if (player.isInsideVehicle() || player.isSneaking()) {
            player.sendMessage(ElementalsUtil.color("&cNu te poti aseza jos acum!"));
            return;
        }
        if (occupiedSits.containsValue(block)) {
            player.sendMessage(ElementalsUtil.color("&cCineva sta deja jos!"));
            return;
        }
        if (occupiedChairs.containsValue(block)) {
            player.sendMessage(ElementalsUtil.color("&cCineva sta deja pe scaun!"));
            return;
        }
        if (!player.isOnGround()) {
            player.sendMessage(ElementalsUtil.color("&cNu te poti aseza cat timp esti in aer!"));
            return;
        }
        Location armorStandLocation = block.getLocation().clone().add(.5, .05, .5);
        ArmorStand chair = createChair(armorStandLocation);
        sits.put(player.getUniqueId(), chair);
        occupiedSits.put(player.getUniqueId(), block);
        chair.addPassenger(player);
        player.sendMessage(ElementalsUtil.color("&cAcum stai jos!"));
    }

    public static void sitPlayerOnCarpet(Player player, Block block) {
        if (player.isSneaking() || player.isGliding() || player.isRiptiding())
            return;
        Block above = block.getRelative(BlockFace.UP);
        if (!above.getType().isAir()) {
            player.sendMessage(ElementalsUtil.color("&cNu te poti aseza aici!"));
            return;
        }
        if (sits.containsKey(player.getUniqueId())) {
            player.sendMessage(ElementalsUtil.color("&cStai deja jos!"));
            return;
        }
        if (chairs.containsKey(player.getUniqueId())) {
            player.sendMessage(ElementalsUtil.color("&cTe aflii deja intr-un scaun!"));
            return;
        }
        if (player.isInsideVehicle() || player.isSneaking()) {
            player.sendMessage(ElementalsUtil.color("&cNu te poti aseza jos acum!"));
            return;
        }
        if (occupiedSits.containsValue(block)) {
            player.sendMessage(ElementalsUtil.color("&cCineva sta deja jos!"));
            return;
        }
        if (occupiedChairs.containsValue(block)) {
            player.sendMessage(ElementalsUtil.color("&cCineva sta deja pe scaun!"));
            return;
        }
        if (!player.isOnGround()) {
            player.sendMessage(ElementalsUtil.color("&cNu te poti aseza cat timp esti in aer!"));
            return;
        }
        Location armorStandLocation = block.getLocation().clone().add(.5, -.9, .5);
        ArmorStand chair = createChair(armorStandLocation);
        sits.put(player.getUniqueId(), chair);
        occupiedSits.put(player.getUniqueId(), block);
        chair.addPassenger(player);
        player.sendMessage(ElementalsUtil.color("&cAcum stai jos!"));
    }

    public static void sitPlayerOnStairs(Player player, Block block) {
        if (player.isSneaking() || player.isGliding() || player.isRiptiding())
            return;
        Block above = block.getRelative(BlockFace.UP);
        if (!above.getType().isAir()) {
            return;
        }
        if (chairs.containsKey(player.getUniqueId())) {
            player.sendMessage(ElementalsUtil.color("&cTe aflii deja intr-un scaun!"));
            return;
        }
        if (sits.containsKey(player.getUniqueId())) {
            player.sendMessage(ElementalsUtil.color("&cStai deja jos!"));
            return;
        }
        if (player.isInsideVehicle() || player.isSneaking()) {
            player.sendMessage(ElementalsUtil.color("&cNu te poti aseza jos acum!"));
            return;
        }
        if (occupiedSits.containsValue(block)) {
            player.sendMessage(ElementalsUtil.color("&cCineva sta deja jos!"));
            return;
        }
        if (occupiedChairs.containsValue(block)) {
            player.sendMessage(ElementalsUtil.color("&cCineva sta deja pe scaun!"));
            return;
        }
        if (!player.isOnGround()) {
            player.sendMessage(ElementalsUtil.color("&cNu te poti aseza cat timp esti in aer!"));
            return;
        }
        if (!(block.getBlockData() instanceof Directional))
            return;
        Directional directional = (Directional) block.getBlockData();
        Location armorStandLocation = block.getLocation().clone();
        switch (directional.getFacing()) {
            case EAST:
                armorStandLocation = armorStandLocation.add(.3, -.45, .5);
                armorStandLocation.setPitch(0f);
                armorStandLocation.setYaw(90f);
                break;
            case SOUTH:
                armorStandLocation = armorStandLocation.add(.5, -.45, .3);
                armorStandLocation.setPitch(0f);
                armorStandLocation.setYaw(180f);
                break;
            case WEST:
                armorStandLocation = armorStandLocation.add(.7, -.45, .5);
                armorStandLocation.setPitch(0f);
                armorStandLocation.setYaw(-90f);
                break;
            case NORTH:
                armorStandLocation = armorStandLocation.add(.5, -.45, .7);
                armorStandLocation.setPitch(0f);
                armorStandLocation.setYaw(0f);
                break;
            default:
                break;
        }
        ArmorStand chair = createChair(armorStandLocation);
        chairs.put(player.getUniqueId(), chair);
        occupiedChairs.put(player.getUniqueId(), block);
        chair.addPassenger(player);
        player.sendMessage(ElementalsUtil.color("&cAcum stai pe scaun!"));
    }

    public static void sitPlayerOnSlabs(Player player, Block block) {
        if (player.isSneaking() || player.isGliding() || player.isRiptiding())
            return;
        Block above = block.getRelative(BlockFace.UP);
        if (!above.getType().isAir()) {
            return;
        }
        if (chairs.containsKey(player.getUniqueId())) {
            player.sendMessage(ElementalsUtil.color("&cTe aflii deja intr-un scaun!"));
            return;
        }
        if (sits.containsKey(player.getUniqueId())) {
            player.sendMessage(ElementalsUtil.color("&cStai deja jos!"));
            return;
        }
        if (player.isInsideVehicle() || player.isSneaking()) {
            player.sendMessage(ElementalsUtil.color("&cNu te poti aseza jos acum!"));
            return;
        }
        if (occupiedSits.containsValue(block)) {
            player.sendMessage(ElementalsUtil.color("&cCineva sta deja jos!"));
            return;
        }
        if (occupiedChairs.containsValue(block)) {
            player.sendMessage(ElementalsUtil.color("&cCineva sta deja pe scaun!"));
            return;
        }
        if (!player.isOnGround()) {
            player.sendMessage(ElementalsUtil.color("&cNu te poti aseza cat timp esti in aer!"));
            return;
        }
        Location armorStandLocation = block.getLocation().clone().add(.5, -.45, .5);
        armorStandLocation.setPitch(0f);
        armorStandLocation.setYaw(0f);
        ArmorStand chair = createChair(armorStandLocation);
        chairs.put(player.getUniqueId(), chair);
        occupiedChairs.put(player.getUniqueId(), block);
        chair.addPassenger(player);
        player.sendMessage(ElementalsUtil.color("&cAcum stai pe scaun!"));
    }

    public static void unsitPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (chairs.containsKey(uuid)) {
            chairs.remove(uuid).remove();
            occupiedChairs.remove(uuid);
            player.sendMessage(ElementalsUtil.color("&cTe-ai ridicat din scaun!"));
        } else if (sits.containsKey(uuid)) {
            sits.remove(uuid).remove();
            occupiedSits.remove(uuid);
            player.sendMessage(ElementalsUtil.color("&cTe-ai ridicat!"));
        }
    }

    public static void unsitAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            unsitPlayer(player);
        }
    }

    public static ArmorStand createChair(Location location) {
        ArmorStand chair = location.getWorld().spawn(location, ArmorStand.class);
        chair.setVisible(false);
        chair.setGravity(false);
        chair.setAI(false);
        chair.setBasePlate(false);
        chair.setSmall(true);
        chair.setInvulnerable(true);
        return chair;
    }
}
