package ro.nicuch.elementalsx.protection;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleCollisionEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.projectiles.BlockProjectileSource;

import net.citizensnpcs.api.CitizensAPI;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.elementals.NanoBlockBreakEvent;

public class FieldListener implements Listener {

    @EventHandler
    public void event(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (!block.getType().equals(Material.DIAMOND_BLOCK))
                continue;
            if (FieldUtil.isFieldBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void event(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (!block.getType().equals(Material.DIAMOND_BLOCK))
                continue;
            if (FieldUtil.isFieldBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void event(PlayerFishEvent event) {
        if (!event.getState().equals(State.CAUGHT_ENTITY))
            return;
        if (!FieldUtil.isFieldAtLocation(event.getCaught().getLocation()))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        UUID uuid = event.getPlayer().getUniqueId();
        Field field = FieldUtil.getFieldByLocation(event.getCaught().getLocation());
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (!block.getType().equals(Material.DIAMOND_BLOCK))
            return;
        String world_name = block.getWorld().getName();
        if (!(world_name.equals("world") || world_name.equals("world_nether") || world_name.equals("world_the_end")))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (user.isIgnoringPlacingFields())
            return;
        if (FieldUtil.isFieldNerby(user, block.getLocation())) {
            event.getPlayer().sendMessage(ElementalsUtil.color("&3Nu poti pune protectia ta daca se intersecteaza cu protectia altui jucator!"));
            event.setCancelled(true);
            return;
        }
        if (!user.hasPermission("elementals.protection.override"))
            for (Entity entity : event.getPlayer().getNearbyEntities(25, 64, 25))
                if (entity.getType().equals(EntityType.PLAYER)) {
                    User entityuser = ElementalsX.getUser((Player) entity);
                    if (!entityuser.hasPermission("elementals.protection.override")) {
                        event.getPlayer().sendMessage(ElementalsUtil.color("&3Nu poti pune protectii cat timp sunt jucatori in zona!"));
                        event.setCancelled(true);
                        return;
                    }
                }
        if (FieldUtil.areThereEnoughProtections(block.getChunk()) && (!user.hasPermission("elementals.protection.override"))) {
            event.getPlayer().sendMessage(ElementalsUtil.color("&3Limita de 4 protectii intr-un chunk a fost atinsa! Nu mai poti pune alte protectii."));
            event.setCancelled(true);
            return;
        }
        String id = FieldUtil.getFieldIdByBlock(block);
        Location loc = block.getLocation();
        Block maxLoc = loc.clone().add(25, 0, 25).getBlock();
        Block minLoc = loc.clone().add(-25, 0, -25).getBlock();
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            try {
                ElementalsX.getBase()
                        .prepareStatement(
                                "INSERT INTO protection (id, x, y, z, world, owner, maxx, maxz, minx, minz, chunkx, chunkz) VALUES ('"
                                        + id + "', '" + block.getX() + "', '" + block.getY() + "', '" + block.getZ()
                                        + "', '" + block.getWorld().getName() + "', '"
                                        + user.getBase().getUniqueId().toString() + "', '" + maxLoc.getX() + "', '"
                                        + maxLoc.getZ() + "', '" + minLoc.getX() + "', '" + minLoc.getZ() + "', '"
                                        + block.getChunk().getX() + "', '" + block.getChunk().getZ() + "');")
                        .executeUpdate();
                Bukkit.getScheduler().runTask(ElementalsX.get(), () -> {
                    FieldUtil.registerField(user, block, id, maxLoc, minLoc);
                    event.getPlayer().sendMessage(ElementalsUtil.color("&aAi pus protectia cu succes!"));
                });
            } catch (SQLException exception) {
                event.getPlayer().sendMessage(ElementalsUtil.color("&cEroare. &aContacteaza un admin!"));
                event.setCancelled(true);
                exception.printStackTrace();
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.DIAMOND_BLOCK))
            return;
        String world_name = event.getBlock().getWorld().getName();
        if (!(world_name.equals("world") || world_name.equals("world_nether") || world_name.equals("world_the_end")))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (!FieldUtil.isFieldBlock(event.getBlock()))
            return;
        if (!(FieldUtil.getFieldById(FieldUtil.getFieldIdByBlock(event.getBlock()))
                .isOwner(user.getBase().getUniqueId()) || event.getPlayer().isOp())) {
            event.getPlayer().sendMessage(ElementalsUtil.color("&bNu esti detinatorul protectiei!"));
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlock();
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            try {
                ElementalsX.getBase()
                        .prepareStatement("DELETE FROM protection WHERE x='" + block.getX() + "' AND y='" + block.getY()
                                + "' AND z='" + block.getZ() + "' AND world='" + block.getWorld().getName() + "';")
                        .executeUpdate();
                Bukkit.getScheduler().runTask(ElementalsX.get(), () -> {
                    FieldUtil.unregisterField(block);
                    event.getPlayer().sendMessage(ElementalsUtil.color("&aProtectia a fost distrusa!"));
                });
            } catch (SQLException exception) {
                event.getPlayer().sendMessage(ElementalsUtil.color("&cEroare. &aContacteaza un admin!"));
                event.setCancelled(true);
                exception.printStackTrace();
            }
        });
    }

    @EventHandler
    public void event(BlockBurnEvent event) {
        if (!FieldUtil.isFieldAtLocation(event.getBlock().getLocation()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(ChunkLoadEvent event) {
        if (event.getWorld().getName().equals("spawn"))
            return;
        FieldUtil.loadFieldsInChunk(event.getChunk());
    }

    @EventHandler
    public void event(ChunkUnloadEvent event) {
        if (event.getWorld().getName().equals("spawn"))
            return;
        FieldUtil.unloadFieldsInChunk(event.getChunk());
    }

    @EventHandler
    public void event(BlockFromToEvent event) {
        if (!FieldUtil.isFieldAtLocation(event.getBlock().getLocation())
                && FieldUtil.isFieldAtLocation(event.getToBlock().getLocation()))
            event.setCancelled(true);
        else if (FieldUtil.isFieldAtLocation(event.getBlock().getLocation())
                && FieldUtil.isFieldAtLocation(event.getToBlock().getLocation()))
            if (!FieldUtil.getFieldByLocation(event.getBlock().getLocation())
                    .isOwner(FieldUtil.getFieldByLocation(event.getToBlock().getLocation()).getOwner()))
                event.setCancelled(true);
    }

    @EventHandler
    public void event(BlockSpreadEvent event) {
        if (!event.getSource().getType().equals(Material.FIRE))
            return;
        if (!FieldUtil.isFieldAtLocation(event.getBlock().getLocation()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event0(EntityChangeBlockEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (!event.getEntity().getType().equals(EntityType.WITHER))
            return;
        if (!FieldUtil.isFieldAtLocation(event.getBlock().getLocation()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(EntityChangeBlockEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (!event.getEntity().getType().equals(EntityType.PLAYER))
            return;
        User user = ElementalsX.getUser((Player) event.getEntity());
        if (!event.getBlock().getType().equals(Material.FARMLAND))
            return;
        Location loc = event.getBlock().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = event.getEntity().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(EntityDamageByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (!FieldUtil.isFieldAtLocation(event.getEntity().getLocation()))
            return;
        Entity entity = event.getEntity();
        if (!entity.getType().equals(EntityType.PLAYER))
            return;
        Entity damager;
        if (event.getDamager() instanceof Projectile) {
            Projectile proj = (Projectile) event.getDamager();
            if (proj.getShooter() == null)
                return;
            if (proj.getShooter() instanceof BlockProjectileSource) {
                event.setCancelled(true);
                return;
            }
            damager = (Entity) proj.getShooter();
        } else
            damager = event.getDamager();
        if (!(damager instanceof Player))
            return;
        User user = ElementalsX.getUser((Player) damager);
        if (user.hasPermission("elementals.protection.override"))
            return;
        user.getBase().sendMessage(ElementalsUtil.color("&cNu poti face PvP cu un jucator aflat in protectie!"));
        event.setCancelled(true);
        if (event.getDamager().hasMetadata("flame_ench"))
            entity.setFireTicks(0);
    }

    @EventHandler
    public void event(EntityExplodeEvent event) {
        if (event.getLocation().getWorld().getName().equals("spawn"))
            return;
        if (!FieldUtil.isFieldAtLocation(event.getLocation()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(HangingBreakByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Location loc = event.getEntity().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        Entity remover;
        if (event.getRemover() instanceof Projectile) {
            Projectile proj = (Projectile) event.getRemover();
            if (proj.getShooter() == null)
                return;
            if (proj.getShooter() instanceof BlockProjectileSource) {
                event.setCancelled(true);
                return;
            }
            remover = (Entity) proj.getShooter();
        } else
            remover = event.getRemover();
        if (!(remover instanceof Player))
            return;
        User user = ElementalsX.getUser((Player) remover);
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = user.getBase().getUniqueId();
        if (!(field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override")))
            event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerArmorStandManipulateEvent event) {
        User user = ElementalsX.getUser(event.getPlayer());
        if (CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked()))
            return;
        Location loc = event.getRightClicked().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = user.getBase().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(HangingPlaceEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Location loc = event.getEntity().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = user.getBase().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerBucketEmptyEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        Location loc = event.getBlockClicked().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = user.getBase().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.getPlayer().sendMessage(ElementalsUtil.color("&bNu poti folosi galeata aici!"));
        event.setCancelled(true);
    }

    @EventHandler
    public void event(NanoBlockBreakEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        Location loc = event.getBlock().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = user.getBase().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerBucketFillEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        Location loc = event.getBlockClicked().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = user.getBase().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.getPlayer().sendMessage(ElementalsUtil.color("&bNu poti folosi galeata aici!"));
        event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerInteractEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked()))
            return;
        Location loc = event.getRightClicked().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        UUID uuid = user.getBase().getUniqueId();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        EntityType entityType = event.getRightClicked().getType();
        if (entityType.equals(EntityType.PARROT)
                || entityType.equals(EntityType.LLAMA)
                || entityType.equals(EntityType.TRADER_LLAMA)
                || entityType.equals(EntityType.ZOMBIE_HORSE)
                || entityType.equals(EntityType.MULE)
                || entityType.equals(EntityType.HORSE)
                || entityType.equals(EntityType.DONKEY)
                || entityType.equals(EntityType.SKELETON_HORSE)
                || entityType.equals(EntityType.PIG)
                || entityType.equals(EntityType.COW)
                || entityType.equals(EntityType.OCELOT)
                || entityType.equals(EntityType.MUSHROOM_COW)
                || entityType.equals(EntityType.BAT)
                || entityType.equals(EntityType.BOAT)
                || entityType.equals(EntityType.CHICKEN)
                || entityType.equals(EntityType.WOLF)
                || entityType.equals(EntityType.VILLAGER)
                || entityType.equals(EntityType.IRON_GOLEM)
                || entityType.equals(EntityType.LEASH_HITCH)
                || entityType.equals(EntityType.RABBIT)
                || entityType.equals(EntityType.SHEEP)
                || entityType.equals(EntityType.SNOWMAN)
                || entityType.equals(EntityType.SQUID)
                || entityType.equals(EntityType.ARMOR_STAND)
                || entityType.equals(EntityType.ITEM_FRAME)) {
            if (entityType.equals(EntityType.ZOMBIE_HORSE)
                    || entityType.equals(EntityType.MULE)
                    || entityType.equals(EntityType.HORSE)
                    || entityType.equals(EntityType.DONKEY)
                    || entityType.equals(EntityType.SKELETON_HORSE)) {
                AbstractHorse horse = (AbstractHorse) event.getRightClicked();
                if (horse.isTamed())
                    if (horse.getOwner() != null && horse.getOwner().getUniqueId().equals(uuid))
                        return;
            }
            user.getBase().sendMessage(ElementalsUtil.color("&cNu poti interactiona cu aceasta entitate in protectie."));
            event.setCancelled(true);
        } else if (entityType.equals(EntityType.MINECART_CHEST)
                || entityType.equals(EntityType.MINECART_FURNACE)
                || entityType.equals(EntityType.MINECART_HOPPER)
                || entityType.equals(EntityType.MINECART_TNT)
                || entityType.equals(EntityType.MINECART_MOB_SPAWNER)
                || entityType.equals(EntityType.MINECART_COMMAND)) {
            user.getBase().sendMessage(ElementalsUtil.color("&cNu poti interactiona cu aceast vehicul in protectie."));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void event(EntityInteractEvent event) {
        Entity entity = event.getEntity();
        EntityType entityType = entity.getType();
        Material blockType = event.getBlock().getType();
        if (entityType.equals(EntityType.ZOMBIE_HORSE)
                || entityType.equals(EntityType.MULE)
                || entityType.equals(EntityType.HORSE)
                || entityType.equals(EntityType.DONKEY)
                || entityType.equals(EntityType.SKELETON_HORSE)) {
            Field field = FieldUtil.getFieldByLocation(entity.getLocation());
            if (field.hasFun())
                return;
            if (!(Tag.WOODEN_PRESSURE_PLATES.isTagged(blockType)
                    || Tag.DOORS.isTagged(blockType)
                    || Tag.TRAPDOORS.isTagged(blockType)
                    || Tag.BUTTONS.isTagged(blockType)
                    || blockType.equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
                    || blockType.equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
                    || blockType.equals(Material.STONE_PRESSURE_PLATE)
                    || blockType.equals(Material.NOTE_BLOCK)
                    || blockType.equals(Material.LEVER)
                    || blockType.equals(Material.JUKEBOX)
                    || blockType.equals(Material.ACACIA_FENCE_GATE)
                    || blockType.equals(Material.BIRCH_FENCE_GATE)
                    || blockType.equals(Material.DARK_OAK_FENCE_GATE)
                    || blockType.equals(Material.JUNGLE_FENCE_GATE)
                    || blockType.equals(Material.OAK_FENCE_GATE)
                    || blockType.equals(Material.SPRUCE_FENCE_GATE)))
                return;
            AbstractHorse horse = (AbstractHorse) entity;
            if (!horse.isTamed() && horse.getOwner() == null)
                return;
            if (field.isMember(horse.getOwner().getUniqueId()) || field.isOwner(horse.getOwner().getUniqueId()))
                return;
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event0(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL)
            return;
        Material clickedBlockType = event.getClickedBlock().getType();
        if (!(clickedBlockType.equals(Material.CHEST)
                || clickedBlockType.equals(Material.FLOWER_POT)
                || clickedBlockType.equals(Material.TRAPPED_CHEST)
                || clickedBlockType.equals(Material.FURNACE)
                || clickedBlockType.equals(Material.JUKEBOX)
                || clickedBlockType.equals(Material.DROPPER)
                || clickedBlockType.equals(Material.DISPENSER)
                || clickedBlockType.equals(Material.NOTE_BLOCK)
                || clickedBlockType.equals(Material.LEVER)
                || clickedBlockType.equals(Material.STONE_BUTTON)
                || clickedBlockType.equals(Material.DAYLIGHT_DETECTOR)
                || clickedBlockType.equals(Material.HOPPER)
                || clickedBlockType.equals(Material.REPEATER)
                || clickedBlockType.equals(Material.COMPARATOR)
                || clickedBlockType.equals(Material.BEACON)
                || clickedBlockType.equals(Material.BREWING_STAND)
                || clickedBlockType.equals(Material.ACACIA_FENCE_GATE)
                || clickedBlockType.equals(Material.BIRCH_FENCE_GATE)
                || clickedBlockType.equals(Material.DARK_OAK_FENCE_GATE)
                || clickedBlockType.equals(Material.JUNGLE_FENCE_GATE)
                || clickedBlockType.equals(Material.OAK_FENCE_GATE)
                || clickedBlockType.equals(Material.SPRUCE_FENCE_GATE)
                || clickedBlockType.equals(Material.SHULKER_BOX)
                || clickedBlockType.equals(Material.BLACK_SHULKER_BOX)
                || clickedBlockType.equals(Material.BLUE_SHULKER_BOX)
                || clickedBlockType.equals(Material.BROWN_SHULKER_BOX)
                || clickedBlockType.equals(Material.CYAN_SHULKER_BOX)
                || clickedBlockType.equals(Material.GRAY_SHULKER_BOX)
                || clickedBlockType.equals(Material.GREEN_SHULKER_BOX)
                || clickedBlockType.equals(Material.LIGHT_BLUE_SHULKER_BOX)
                || clickedBlockType.equals(Material.LIGHT_GRAY_SHULKER_BOX)
                || clickedBlockType.equals(Material.LIME_SHULKER_BOX)
                || clickedBlockType.equals(Material.MAGENTA_SHULKER_BOX)
                || clickedBlockType.equals(Material.ORANGE_SHULKER_BOX)
                || clickedBlockType.equals(Material.PINK_SHULKER_BOX)
                || clickedBlockType.equals(Material.PURPLE_SHULKER_BOX)
                || clickedBlockType.equals(Material.RED_SHULKER_BOX)
                || clickedBlockType.equals(Material.WHITE_SHULKER_BOX)
                || clickedBlockType.equals(Material.YELLOW_SHULKER_BOX)
                || clickedBlockType.equals(Material.BLAST_FURNACE)
                || clickedBlockType.equals(Material.SMOKER)
                || clickedBlockType.equals(Material.BARREL)
                || clickedBlockType.equals(Material.CAMPFIRE)
                || clickedBlockType.equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
                || clickedBlockType.equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
                || clickedBlockType.equals(Material.STONE_PRESSURE_PLATE)
                || clickedBlockType.equals(Material.TURTLE_EGG)
                || Tag.WOODEN_PRESSURE_PLATES.isTagged(clickedBlockType)
                || Tag.DOORS.isTagged(clickedBlockType)
                || Tag.ANVIL.isTagged(clickedBlockType)
                || Tag.BEDS.isTagged(clickedBlockType)
                || Tag.TRAPDOORS.isTagged(clickedBlockType)
                || Tag.BUTTONS.isTagged(clickedBlockType)))
            return;
        //TODO more blocks
        Location loc = event.getClickedBlock().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.hasFun()) {
            if (Tag.WOODEN_PRESSURE_PLATES.isTagged(clickedBlockType)
                    || Tag.DOORS.isTagged(clickedBlockType)
                    || Tag.TRAPDOORS.isTagged(clickedBlockType)
                    || Tag.BUTTONS.isTagged(clickedBlockType)
                    || clickedBlockType.equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
                    || clickedBlockType.equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
                    || clickedBlockType.equals(Material.STONE_PRESSURE_PLATE)
                    || clickedBlockType.equals(Material.NOTE_BLOCK)
                    || clickedBlockType.equals(Material.LEVER)
                    || clickedBlockType.equals(Material.JUKEBOX)
                    || clickedBlockType.equals(Material.ACACIA_FENCE_GATE)
                    || clickedBlockType.equals(Material.BIRCH_FENCE_GATE)
                    || clickedBlockType.equals(Material.DARK_OAK_FENCE_GATE)
                    || clickedBlockType.equals(Material.JUNGLE_FENCE_GATE)
                    || clickedBlockType.equals(Material.OAK_FENCE_GATE)
                    || clickedBlockType.equals(Material.SPRUCE_FENCE_GATE))
                return;
        }
        UUID uuid = user.getBase().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        if (event.getAction() != Action.PHYSICAL)
            event.getPlayer().sendMessage(ElementalsUtil.color("&cNu poti interactiona cu acest bloc in protectie!"));
        event.setCancelled(true);
    }

    @EventHandler
    public void event1(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
        if (hand == null)
            return;
        Material handType = hand.getType();
        if (!(handType.equals(Material.MINECART)
                || handType.equals(Material.CHEST_MINECART)
                || handType.equals(Material.FURNACE_MINECART)
                || handType.equals(Material.HOPPER_MINECART)
                || handType.equals(Material.TNT_MINECART)
                || handType.equals(Material.BIRCH_BOAT)
                || handType.equals(Material.OAK_BOAT)
                || handType.equals(Material.ACACIA_BOAT)
                || handType.equals(Material.DARK_OAK_BOAT)
                || handType.equals(Material.JUNGLE_BOAT)
                || handType.equals(Material.SPRUCE_BOAT)))
            return;
        Location loc = event.getClickedBlock().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = user.getBase().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        user.getBase().sendMessage(ElementalsUtil.color("&cNu poti pune vehicule in protectie!"));
        event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
        if (hand == null)
            return;
        Material handType = hand.getType();
        if (!(hand.getItemMeta() instanceof SpawnEggMeta || handType.equals(Material.FLINT_AND_STEEL)
                || handType.equals(Material.ARMOR_STAND)))
            return;
        Location loc = event.getClickedBlock().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = user.getBase().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        if (event.getClickedBlock().getType().equals(Material.SPAWNER))
            event.getPlayer().sendMessage(ElementalsUtil.color("&cNu poti schimba acest spawner!"));
        else
            event.getPlayer().sendMessage(ElementalsUtil.color("&cNu poti folosi acest obiect aici!"));
        event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerMoveEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        if (event.getTo().getBlock().equals(event.getFrom().getBlock()))
            return;
        FieldUtil.updateUser(ElementalsX.getUser(event.getPlayer()), event.getTo());
    }

    @EventHandler
    public void event(PlayerTeleportEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        FieldUtil.updateUser(ElementalsX.getUser(event.getPlayer()), event.getTo());
    }

    @EventHandler
    public void event0(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.DIAMOND_BLOCK)) {
            if (FieldUtil.isFieldBlock(block))
                return;
        }
        Location loc = block.getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = user.getBase().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.getPlayer().sendMessage(ElementalsUtil.color("&bNu poti sparge blocuri in protectie!"));
        event.setCancelled(true);
    }

    @EventHandler
    public void event0(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.DIAMOND_BLOCK))
            return;
        Location loc = block.getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = user.getBase().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.getPlayer().sendMessage(ElementalsUtil.color("&bNu poti pune blocuri in protectie!"));
        event.setCancelled(true);
    }

    @EventHandler
    public void event0(EntityDamageByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getDamager()))
            return;
        Entity entity = event.getEntity();
        Entity damager;
        if (event.getDamager() instanceof Projectile) {
            Projectile proj = (Projectile) event.getDamager();
            if (proj.getShooter() == null)
                return;
            if (proj.getShooter() instanceof BlockProjectileSource)
                return;
            damager = (Entity) proj.getShooter();
        } else
            damager = event.getDamager();
        if (damager.getType() != EntityType.PLAYER)
            return;
        Location loc = entity.getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        EntityType entityType = entity.getType();
        if (!(entityType.equals(EntityType.PARROT)
                || entityType.equals(EntityType.LLAMA)
                || entityType.equals(EntityType.TRADER_LLAMA)
                || entityType.equals(EntityType.ZOMBIE_HORSE)
                || entityType.equals(EntityType.MULE)
                || entityType.equals(EntityType.HORSE)
                || entityType.equals(EntityType.DONKEY)
                || entityType.equals(EntityType.SKELETON_HORSE)
                || entityType.equals(EntityType.PIG)
                || entityType.equals(EntityType.COW)
                || entityType.equals(EntityType.OCELOT)
                || entityType.equals(EntityType.MUSHROOM_COW)
                || entityType.equals(EntityType.BAT)
                || entityType.equals(EntityType.BOAT)
                || entityType.equals(EntityType.CHICKEN)
                || entityType.equals(EntityType.WOLF)
                || entityType.equals(EntityType.VILLAGER)
                || entityType.equals(EntityType.IRON_GOLEM)
                || entityType.equals(EntityType.LEASH_HITCH)
                || entityType.equals(EntityType.RABBIT)
                || entityType.equals(EntityType.SHEEP)
                || entityType.equals(EntityType.SNOWMAN)
                || entityType.equals(EntityType.SQUID)
                || entityType.equals(EntityType.ARMOR_STAND)
                || entityType.equals(EntityType.ITEM_FRAME)))
            return;
        User user = ElementalsX.getUser((Player) damager);
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = user.getBase().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        user.getBase().sendMessage(ElementalsUtil.color("&cNu poti omora aceasta entitate in protectie."));
        event.setCancelled(true);
        if (event.getDamager().hasMetadata("flame_ench"))
            entity.setFireTicks(0);
    }

    @EventHandler
    public void event(VehicleDamageEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getVehicle()))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getAttacker()))
            return;
        Vehicle vehicle = event.getVehicle();
        Entity damager;
        if (event.getAttacker() instanceof Projectile) {
            Projectile proj = (Projectile) event.getAttacker();
            if (proj.getShooter() == null)
                return;
            if (proj.getShooter() instanceof BlockProjectileSource)
                return;
            damager = (Entity) proj.getShooter();
        } else
            damager = event.getAttacker();
        if (damager.getType() != EntityType.PLAYER)
            return;
        Location loc = vehicle.getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        EntityType entityType = vehicle.getType();
        if (!(entityType.equals(EntityType.MINECART)
                || entityType.equals(EntityType.MINECART_CHEST)
                || entityType.equals(EntityType.MINECART_FURNACE)
                || entityType.equals(EntityType.MINECART_HOPPER)
                || entityType.equals(EntityType.MINECART_TNT)
                || entityType.equals(EntityType.MINECART_MOB_SPAWNER)
                || entityType.equals(EntityType.MINECART_COMMAND)
                || entityType.equals(EntityType.BOAT)))
            return;
        User user = ElementalsX.getUser((Player) damager);
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = user.getBase().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        user.getBase().sendMessage(ElementalsUtil.color("&cNu poti distruge vehiculul in protectie."));
        event.setCancelled(true);
        if (event.getAttacker().hasMetadata("flame_ench"))
            vehicle.setFireTicks(0);
    }

    @EventHandler
    public void event(VehicleEnterEvent event) {
        Vehicle vehicle = event.getVehicle();
        User user = ElementalsX.getUser((Player) event.getEntered());
        Field field = FieldUtil.getFieldByLocation(vehicle.getLocation());
        if (field.hasFun())
            return;
        EntityType entityType = vehicle.getType();
        if (!(entityType.equals(EntityType.MINECART)
                || entityType.equals(EntityType.BOAT)))
            return;
        if (!(event.getEntered() instanceof Player))
            return;
        UUID uuid = user.getBase().getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        user.getBase().sendMessage(ElementalsUtil.color("&cNu poti intra in vehicul in protectie."));
        event.setCancelled(true);
    }

    @EventHandler
    public void event1(EntityDamageByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getDamager()))
            return;
        if (!(event.getDamager() instanceof Firework))
            return;
        Location loc = event.getEntity().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        if (!event.getDamager().hasMetadata("firework_ench"))
            return;
        event.setDamage(0);
    }
}
