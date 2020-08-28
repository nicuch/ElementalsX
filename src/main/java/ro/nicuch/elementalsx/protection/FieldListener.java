package ro.nicuch.elementalsx.protection;

import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.projectiles.BlockProjectileSource;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.tag.TagRegister;
import ro.nicuch.tag.nbt.CompoundTag;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FieldListener implements Listener {

    @EventHandler
    public void event(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getEntity();
        Entity target = event.getTarget();
        if (target == null)
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(entity))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(target))
            return;
        if (target.getType() != EntityType.PLAYER)
            return;
        Location loc = entity.getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        EntityType entityType = entity.getType();
        if (!(entityType == EntityType.BEE
                || entityType == EntityType.PARROT
                || entityType == EntityType.LLAMA
                || entityType == EntityType.TRADER_LLAMA
                || entityType == EntityType.ZOMBIE_HORSE
                || entityType == EntityType.MULE
                || entityType == EntityType.HORSE
                || entityType == EntityType.DONKEY
                || entityType == EntityType.SKELETON_HORSE
                || entityType == EntityType.PIG
                || entityType == EntityType.COW
                || entityType == EntityType.OCELOT
                || entityType == EntityType.MUSHROOM_COW
                || entityType == EntityType.BAT
                || entityType == EntityType.BOAT
                || entityType == EntityType.CHICKEN
                || entityType == EntityType.WOLF
                || entityType == EntityType.VILLAGER
                || entityType == EntityType.IRON_GOLEM
                || entityType == EntityType.LEASH_HITCH
                || entityType == EntityType.RABBIT
                || entityType == EntityType.SHEEP
                || entityType == EntityType.SNOWMAN
                || entityType == EntityType.SQUID
                || entityType == EntityType.ARMOR_STAND
                || entityType == EntityType.ITEM_FRAME
                || entityType == EntityType.POLAR_BEAR))
            return;
        UUID uuid = target.getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(SpongeAbsorbEvent event) {
        if (!FieldUtil.isFieldAtLocation(event.getBlock().getLocation()))
            event.getBlocks().removeIf(b -> FieldUtil.isFieldAtLocation(b.getLocation()));
    }

    @EventHandler
    public void event(PlayerMoveEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        if (event.getTo().getBlock().equals(event.getFrom().getBlock()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        FieldUtil.updateUser(user, event.getTo());
    }

    //Prevents job payments for players without access in field
    @EventHandler(ignoreCancelled = true)
    public void jobPrevent(JobsPrePaymentEvent event) {
        Block block = event.getBlock();
        Entity entity = event.getEntity();
        if (block != null) {
            UUID uuid = event.getPlayer().getUniqueId();
            Optional<User> optionalUser = ElementalsX.getUser(uuid);
            if (optionalUser.isEmpty())
                return;
            User user = optionalUser.get();
            if (FieldChunkUtil.doChunkWait(block.getChunk())) {
                event.setCancelled(true);
                return;
            }
            if (!FieldUtil.isFieldAtLocation(block.getLocation()))
                return;
            Field field = FieldUtil.getFieldByLocation(block.getLocation());
            if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
                return;
            event.setCancelled(true);
        }
        if (entity != null) {
            if (block != null) {
                UUID uuid = event.getPlayer().getUniqueId();
                Optional<User> optionalUser = ElementalsX.getUser(uuid);
                if (optionalUser.isEmpty())
                    return;
                User user = optionalUser.get();
                if (FieldChunkUtil.doChunkWait(entity.getLocation().getChunk())) {
                    event.setCancelled(true);
                    return;
                }
                if (!FieldUtil.isFieldAtLocation(entity.getLocation()))
                    return;
                Field field = FieldUtil.getFieldByLocation(entity.getLocation());
                if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
                    return;
                event.setCancelled(true);
            }
        }
    }

    //Prevent pushing of protections
    @EventHandler(ignoreCancelled = true)
    public void pushProtection(BlockPistonExtendEvent event) {
        if (FieldChunkUtil.doChunkWait(event.getBlock().getChunk())) {
            event.setCancelled(true);
            return;
        }
        for (Block block : event.getBlocks()) {
            if (block.getType() != Material.DIAMOND_BLOCK)
                continue;
            if (FieldUtil.isFieldBlock(block)) {
                event.setCancelled(true);
                return; //break; breaks the loop
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void pushProtection(BlockPistonRetractEvent event) {
        if (FieldChunkUtil.doChunkWait(event.getBlock().getChunk())) {
            event.setCancelled(true);
            return;
        }
        for (Block block : event.getBlocks()) {
            if (block.getType() != Material.DIAMOND_BLOCK)
                continue;
            if (FieldUtil.isFieldBlock(block)) {
                event.setCancelled(true);
                return; //break; breaks the loop
            }
        }
    }

    //Prevents entities in a field from beeing cought with a fishing rod
    @EventHandler(ignoreCancelled = true)
    public void fishingProtection(PlayerFishEvent event) {
        if (event.getState() != State.CAUGHT_ENTITY)
            return;
        if (!FieldUtil.isFieldAtLocation(event.getCaught().getLocation()))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(event.getCaught().getLocation());
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void protectionPlace(BlockPlaceEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        if (FieldChunkUtil.doChunkWait(chunk)) {
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlock();
        if (block.getType() != Material.DIAMOND_BLOCK)
            return;
        String world_name = block.getWorld().getName();
        if (!(world_name.equals("world") || world_name.equals("world_nether") || world_name.equals("world_the_end")))
            return;
        if (world_name.equals("world_the_end") && block.getX() <= 125 && block.getX() >= -125 && block.getZ() <= 125 && block.getZ() >= -125)
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (user.isIgnoringPlacingFields())
            return;
        if (!user.hasPermission("elementals.protection.override"))
            if (FieldUtil.isFieldNerby(user, block.getLocation())) {
                event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oProtectia ta se intersecteaza cu protectia altui jucator!"));
                event.setCancelled(true);
                return;
            }
        if (!user.hasPermission("elementals.protection.override"))
            for (Entity entity : event.getPlayer().getNearbyEntities(25, 64, 25))
                if (entity.getType() == EntityType.PLAYER) {
                    Optional<User> entityuser = ElementalsX.getUser(entity.getUniqueId()); //using uuids is better
                    if (entityuser.isEmpty())
                        continue; //Continue
                    if (!entityuser.get().hasPermission("elementals.protection.override")) {
                        event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti pune protectia cat timp sunt jucatori in zona!"));
                        event.setCancelled(true);
                        return; //One player found, no need to loop more
                    }
                }
        if (FieldUtil.areThereEnoughProtections(block.getChunk()) && !user.hasPermission("elementals.protection.override")) {
            event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oLimita de 4 protectii intr-un chunk a fost atinsa! Nu mai poti pune alte protectii."));
            event.setCancelled(true);
            return;
        }
        FieldId id = FieldId.fromBlock(block);

        int maxx = block.getX() + 25;
        int maxz = block.getZ() + 25;
        int minx = block.getX() - 25;
        int minz = block.getZ() - 25;

        Field2D field2D = new Field2D(maxx, maxz, minx, minz);

        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            String query = "INSERT INTO protection (id, x, y, z, world, owner, maxx, maxz, minx, minz, chunkx, chunkz) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            try (PreparedStatement statement = ElementalsX.getDatabase().prepareStatement(query)) {
                statement.setString(1, id.toString());
                statement.setInt(2, block.getX());
                statement.setInt(3, block.getY());
                statement.setInt(4, block.getZ());
                statement.setString(5, block.getWorld().getName());
                statement.setString(6, uuid.toString());
                statement.setInt(7, maxx);
                statement.setInt(8, maxz);
                statement.setInt(9, minx);
                statement.setInt(10, minz);
                statement.setInt(11, chunk.getX());
                statement.setInt(12, chunk.getZ());
                statement.executeUpdate();
                Bukkit.getScheduler().runTask(ElementalsX.get(), () -> {
                    FieldUtil.registerField(user, block, id, field2D);
                    event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &a&oProtectia ta a fost creata!"));
                });
            } catch (SQLException ex) {
                event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&l&oEroare. &a&l&oContacteaza un admin!"));
                event.setCancelled(true);
                ex.printStackTrace();
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void protectionBreak(BlockBreakEvent event) {
        if (FieldChunkUtil.doChunkWait(event.getBlock().getChunk())) {
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlock();
        if (block.getType() != Material.DIAMOND_BLOCK)
            return;
        String world_name = block.getWorld().getName();
        if (!(world_name.equals("world") || world_name.equals("world_nether") || world_name.equals("world_the_end")))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (!FieldUtil.isFieldBlock(event.getBlock()))
            return;
        if (!(FieldUtil.getFieldById(FieldId.fromBlock(event.getBlock()))
                .isOwner(uuid) || user.hasPermission("elementals.protection.override"))) {
            event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu esti detinatorul protectiei pentru a o distruge!"));
            event.setCancelled(true);
            return;
        }
        if (!user.hasPermission("elementals.protection.override"))
            for (Entity entity : event.getPlayer().getNearbyEntities(25, 64, 25))
                if (entity.getType() == EntityType.PLAYER) {
                    Optional<User> entityuser = ElementalsX.getUser(entity.getUniqueId()); //using uuids is better
                    if (entityuser.isEmpty())
                        continue; //Continue
                    if (!entityuser.get().hasPermission("elementals.protection.override")) {
                        event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti distruge protectia cat timp sunt jucatori in zona!"));
                        event.setCancelled(true);
                        return; //One player found, no need to loop more
                    }
                }
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            String query = "DELETE FROM protection WHERE x=? AND y=? AND z=? AND world=?;";
            try (PreparedStatement statement = ElementalsX.getDatabase().prepareStatement(query)) {
                statement.setInt(1, block.getX());
                statement.setInt(2, block.getY());
                statement.setInt(3, block.getZ());
                statement.setString(4, world_name);
                statement.executeUpdate();
                Bukkit.getScheduler().runTask(ElementalsX.get(), () -> {
                    FieldUtil.unregisterField(block);
                    event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &a&oProtectia ta a fost distrusa!"));
                });
            } catch (SQLException ex) {
                event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&l&oEroare. &a&l&oContacteaza un admin!"));
                event.setCancelled(true);
                ex.printStackTrace();
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void event(BlockBurnEvent event) {
        if (FieldChunkUtil.doChunkWait(event.getBlock().getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(event.getBlock().getLocation()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(ChunkLoadEvent event) {
        String worldName = event.getWorld().getName();
        if (worldName.equals("spawn") || worldName.equals("dungeon"))
            return;
        FieldUtil.loadFieldsInChunk(event.getChunk());
    }

    @EventHandler
    public void event(ChunkUnloadEvent event) {
        String worldName = event.getWorld().getName();
        if (worldName.equals("spawn") || worldName.equals("dungeon"))
            return;
        FieldUtil.unloadFieldsInChunk(event.getChunk());
    }

    @EventHandler(ignoreCancelled = true)
    public void event(BlockFromToEvent event) {
        Location from = event.getBlock().getLocation();
        Location to = event.getToBlock().getLocation();
        if (!FieldUtil.isFieldAtLocation(from)
                && FieldUtil.isFieldAtLocation(to)) {
            if (!FieldUtil.getFieldByLocation(to).hasFun())
                event.setCancelled(true);
        } else if (FieldUtil.isFieldAtLocation(from)
                && FieldUtil.isFieldAtLocation(to))
            if (!FieldUtil.getFieldByLocation(from)
                    .isOwner(FieldUtil.getFieldByLocation(to).getOwner()))
                event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(BlockSpreadEvent event) {
        if (event.getSource().getType() != Material.FIRE)
            return;
        if (!FieldUtil.isFieldAtLocation(event.getBlock().getLocation()))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event0(EntityChangeBlockEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (event.getEntity().getType() != EntityType.WITHER)
            return;
        if (!FieldUtil.isFieldAtLocation(event.getBlock().getLocation()))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void event2(PlayerInteractEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        if (event.getAction() != Action.PHYSICAL)
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Block block = event.getClickedBlock();
        if (block == null)
            return;
        if (block.getType() != Material.FARMLAND)
            return;
        Location loc = block.getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event2(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (!(event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
            return;
        if (damager.getType() == EntityType.PRIMED_TNT) {
            if (FieldChunkUtil.doChunkWait(entity.getLocation().getChunk())) {
                event.setCancelled(true);
                return;
            }
            if (!FieldUtil.isFieldAtLocation(entity.getLocation()))
                return;
            Field field = FieldUtil.getFieldByLocation(entity.getLocation());
            if (field.isMember(entity.getUniqueId()) || field.isOwner(entity.getUniqueId()))
                return;
            Optional<CompoundTag> optionalTNTCompoundTag = TagRegister.getStored(damager);
            if (optionalTNTCompoundTag.isEmpty())
                return;
            CompoundTag tntCompoundTag = optionalTNTCompoundTag.get();
            if (!(tntCompoundTag.containsString("tnt-igniter-type") && tntCompoundTag.containsString("tnt-igniter-uuid")))
                return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void event1(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (!(entity.getType() == EntityType.ARROW || entity.getType() == EntityType.SPECTRAL_ARROW || entity.getType() == EntityType.FIREBALL))
            return;
        Block block = event.getBlock();
        if (!FieldUtil.isFieldAtLocation(block.getLocation()))
            return;
        Optional<CompoundTag> optionalProjectileCompoundTag = TagRegister.getStored(entity);
        if (optionalProjectileCompoundTag.isEmpty())
            return;
        CompoundTag projectileCompoundTag = optionalProjectileCompoundTag.get();
        if (!(projectileCompoundTag.containsString("projectile-shooter-type") && projectileCompoundTag.containsString("projectile-shooter-uuid")))
            return;
        String uuid = projectileCompoundTag.getString("projectile-shooter-uuid");
        String entityType = projectileCompoundTag.getString("projectile-shooter-type");
        if (!entityType.equals(EntityType.PLAYER.toString()))
            return;
        Field field = FieldUtil.getFieldByLocation(block.getLocation());
        if (field.isMember(uuid) || field.isOwner(uuid))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.PLAYER)
            return;
        if (!(event.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
                event.getCause() == EntityDamageEvent.DamageCause.LAVA))
            return;
        if (FieldChunkUtil.doChunkWait(entity.getLocation().getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(entity.getLocation()))
            return;
        Field field = FieldUtil.getFieldByLocation(entity.getLocation());
        if (field.isMember(entity.getUniqueId()) || field.isOwner(entity.getUniqueId()))
            return;
        List<BlockFace> foundLava = new ArrayList<>();
        Block center = event.getEntity().getLocation().getBlock();
        Block north = center.getRelative(BlockFace.NORTH);
        Block south = center.getRelative(BlockFace.SOUTH);
        Block west = center.getRelative(BlockFace.WEST);
        Block east = center.getRelative(BlockFace.EAST);
        Block north_west = center.getRelative(BlockFace.NORTH_WEST);
        Block north_east = center.getRelative(BlockFace.NORTH_EAST);
        Block south_west = center.getRelative(BlockFace.SOUTH_WEST);
        Block south_east = center.getRelative(BlockFace.SOUTH_EAST);
        Block up = center.getRelative(BlockFace.UP);
        Block down = center.getRelative(BlockFace.DOWN);
        if (center.getType() == Material.LAVA || center.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(center, entity))
                foundLava.add(BlockFace.SELF);
        }
        if (north.getType() == Material.LAVA || north.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(north, entity))
                foundLava.add(BlockFace.NORTH);
        }
        if (south.getType() == Material.LAVA || south.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(south, entity))
                foundLava.add(BlockFace.SOUTH);
        }
        if (west.getType() == Material.LAVA || west.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(west, entity))
                foundLava.add(BlockFace.WEST);
        }
        if (east.getType() == Material.LAVA || east.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(east, entity))
                foundLava.add(BlockFace.EAST);
        }
        if (north_west.getType() == Material.LAVA || north_west.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(north_west, entity))
                foundLava.add(BlockFace.NORTH_WEST);
        }
        if (north_east.getType() == Material.LAVA || north_east.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(north_east, entity))
                foundLava.add(BlockFace.NORTH_EAST);
        }
        if (south_west.getType() == Material.LAVA || south_west.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(south_west, entity))
                foundLava.add(BlockFace.SOUTH_WEST);
        }
        if (south_east.getType() == Material.LAVA || south_east.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(south_east, entity))
                foundLava.add(BlockFace.SOUTH_EAST);
        }
        if (up.getType() == Material.LAVA || up.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(up, entity))
                foundLava.add(BlockFace.UP);
        }
        if (down.getType() == Material.LAVA || down.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(down, entity))
                foundLava.add(BlockFace.DOWN);
        }
        for (BlockFace face : foundLava) {
            Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(center.getRelative(face));
            if (optionalCompoundTag.isEmpty())
                continue;
            CompoundTag tag = optionalCompoundTag.get();
            String uuid = null;
            if (tag.containsString("lava-placed"))
                uuid = tag.getString("lava-placed");
            else if (tag.containsString("fire-placed"))
                uuid = tag.getString("fire-placed");
            if (uuid == null)
                continue;
            if (entity.getUniqueId().toString().equals(uuid))
                continue;
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void event0(PlayerMoveEvent event) {
        Player entity = event.getPlayer();
        if (entity.getFireTicks() <= 0)
            return;
        if (FieldChunkUtil.doChunkWait(entity.getLocation().getChunk()))
            return;
        if (!FieldUtil.isFieldAtLocation(entity.getLocation()))
            return;
        Field field = FieldUtil.getFieldByLocation(entity.getLocation());
        if (field.isMember(entity.getUniqueId()) || field.isOwner(entity.getUniqueId()))
            return;
        List<BlockFace> foundLava = new ArrayList<>();
        Block center = entity.getLocation().getBlock();
        Block north = center.getRelative(BlockFace.NORTH);
        Block south = center.getRelative(BlockFace.SOUTH);
        Block west = center.getRelative(BlockFace.WEST);
        Block east = center.getRelative(BlockFace.EAST);
        Block north_west = center.getRelative(BlockFace.NORTH_WEST);
        Block north_east = center.getRelative(BlockFace.NORTH_EAST);
        Block south_west = center.getRelative(BlockFace.SOUTH_WEST);
        Block south_east = center.getRelative(BlockFace.SOUTH_EAST);
        if (center.getType() == Material.LAVA || center.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(center, entity))
                foundLava.add(BlockFace.SELF);
        }
        if (north.getType() == Material.LAVA || north.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(north, entity))
                foundLava.add(BlockFace.NORTH);
        }
        if (south.getType() == Material.LAVA || south.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(south, entity))
                foundLava.add(BlockFace.SOUTH);
        }
        if (west.getType() == Material.LAVA || west.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(west, entity))
                foundLava.add(BlockFace.WEST);
        }
        if (east.getType() == Material.LAVA || east.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(east, entity))
                foundLava.add(BlockFace.EAST);
        }
        if (north_west.getType() == Material.LAVA || north_west.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(north_west, entity))
                foundLava.add(BlockFace.NORTH_WEST);
        }
        if (north_east.getType() == Material.LAVA || north_east.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(north_east, entity))
                foundLava.add(BlockFace.NORTH_EAST);
        }
        if (south_west.getType() == Material.LAVA || south_west.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(south_west, entity))
                foundLava.add(BlockFace.SOUTH_WEST);
        }
        if (south_east.getType() == Material.LAVA || south_east.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(south_east, entity))
                foundLava.add(BlockFace.SOUTH_EAST);
        }
        for (BlockFace face : foundLava) {
            Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(center.getRelative(face));
            if (optionalCompoundTag.isEmpty())
                continue;
            CompoundTag tag = optionalCompoundTag.get();
            String uuid = null;
            if (tag.containsString("lava-placed"))
                uuid = tag.getString("lava-placed");
            else if (tag.containsString("fire-placed"))
                uuid = tag.getString("fire-placed");
            if (uuid == null)
                continue;
            if (entity.getUniqueId().toString().equals(uuid))
                continue;
            entity.setFireTicks(0);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void event1(PlayerTeleportEvent event) {
        Player entity = event.getPlayer();
        if (entity.getFireTicks() <= 0)
            return;
        if (FieldChunkUtil.doChunkWait(entity.getLocation().getChunk()))
            return;
        if (!FieldUtil.isFieldAtLocation(entity.getLocation()))
            return;
        Field field = FieldUtil.getFieldByLocation(entity.getLocation());
        if (field.isMember(entity.getUniqueId()) || field.isOwner(entity.getUniqueId()))
            return;
        List<BlockFace> foundLava = new ArrayList<>();
        Block center = entity.getLocation().getBlock();
        Block north = center.getRelative(BlockFace.NORTH);
        Block south = center.getRelative(BlockFace.SOUTH);
        Block west = center.getRelative(BlockFace.WEST);
        Block east = center.getRelative(BlockFace.EAST);
        Block north_west = center.getRelative(BlockFace.NORTH_WEST);
        Block north_east = center.getRelative(BlockFace.NORTH_EAST);
        Block south_west = center.getRelative(BlockFace.SOUTH_WEST);
        Block south_east = center.getRelative(BlockFace.SOUTH_EAST);
        if (center.getType() == Material.LAVA || center.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(center, entity))
                foundLava.add(BlockFace.SELF);
        }
        if (north.getType() == Material.LAVA || north.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(north, entity))
                foundLava.add(BlockFace.NORTH);
        }
        if (south.getType() == Material.LAVA || south.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(south, entity))
                foundLava.add(BlockFace.SOUTH);
        }
        if (west.getType() == Material.LAVA || west.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(west, entity))
                foundLava.add(BlockFace.WEST);
        }
        if (east.getType() == Material.LAVA || east.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(east, entity))
                foundLava.add(BlockFace.EAST);
        }
        if (north_west.getType() == Material.LAVA || north_west.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(north_west, entity))
                foundLava.add(BlockFace.NORTH_WEST);
        }
        if (north_east.getType() == Material.LAVA || north_east.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(north_east, entity))
                foundLava.add(BlockFace.NORTH_EAST);
        }
        if (south_west.getType() == Material.LAVA || south_west.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(south_west, entity))
                foundLava.add(BlockFace.SOUTH_WEST);
        }
        if (south_east.getType() == Material.LAVA || south_east.getType() == Material.FIRE) {
            if (ElementalsUtil.checkCollision(south_east, entity))
                foundLava.add(BlockFace.SOUTH_EAST);
        }
        for (BlockFace face : foundLava) {
            Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(center.getRelative(face));
            if (optionalCompoundTag.isEmpty())
                continue;
            CompoundTag tag = optionalCompoundTag.get();
            String uuid = null;
            if (tag.containsString("lava-placed"))
                uuid = tag.getString("lava-placed");
            else if (tag.containsString("fire-placed"))
                uuid = tag.getString("fire-placed");
            if (uuid == null)
                continue;
            if (entity.getUniqueId().toString().equals(uuid))
                continue;
            entity.setFireTicks(0);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void event(EntityDamageByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Entity entity = event.getEntity();
        if (FieldChunkUtil.doChunkWait(entity.getLocation().getChunk())) {
            event.setCancelled(true);
            return;
        }
        Entity damager;
        if (event.getDamager() instanceof Projectile) {
            Projectile proj = (Projectile) event.getDamager();
            if (proj.getShooter() == null)
                return;
            if (proj.getShooter() instanceof BlockProjectileSource) {
                //TODO proj block event
                event.setCancelled(true);
                return;
            }
            damager = (Entity) proj.getShooter();
        } else
            damager = event.getDamager();
        if (entity.getType() != EntityType.PLAYER)
            return;
        if (damager.getType() == EntityType.PLAYER) {
            if (!FieldUtil.isFieldAtLocation(entity.getLocation()))
                return;
            Optional<User> optionalUser = ElementalsX.getUser(damager.getUniqueId());
            if (optionalUser.isEmpty())
                return;
            User user = optionalUser.get();
            if (user.hasPermission("elementals.protection.override"))
                return;
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti face PvP cu un jucator aflat in protectie!"));
            event.setCancelled(true);
            if (event.getDamager().hasMetadata("flame_ench"))
                entity.setFireTicks(0);
        } else if (damager.getType() == EntityType.BEE) {
            if (!FieldUtil.isFieldAtLocation(entity.getLocation()))
                return;
            Optional<User> optionalUser = ElementalsX.getUser(entity.getUniqueId());
            if (optionalUser.isEmpty())
                return;
            User user = optionalUser.get();
            Field field = FieldUtil.getFieldByLocation(entity.getLocation());
            if (field.isMember(user.getUUID()) || field.isOwner(user.getUUID()))
                return;
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void event(EntityExplodeEvent event) {
        if (FieldChunkUtil.doChunkWait(event.getLocation().getChunk())) {
            event.blockList().clear();
            return;
        }
        String worldName = event.getLocation().getWorld().getName();
        if (worldName.equals("spawn") || worldName.equals("dungeon"))
            return;
        event.blockList().removeIf(block -> {
            if (!FieldUtil.isFieldAtLocation(block.getLocation()))
                return false;
            if (block.getType() != Material.TNT)
                return true;
            Optional<CompoundTag> optionalTNTCompoundTag = TagRegister.getStored(event.getEntity());
            if (optionalTNTCompoundTag.isEmpty())
                return false;
            CompoundTag tntCompoundTag = optionalTNTCompoundTag.get();
            if (!(tntCompoundTag.containsString("tnt-igniter-type") && tntCompoundTag.containsString("tnt-igniter-uuid")))
                return false;
            String type = tntCompoundTag.getString("tnt-igniter-type");
            if (!type.equals(EntityType.PLAYER.toString()))
                return false;
            String uuid = tntCompoundTag.getString("tnt-igniter-uuid");
            Field field = FieldUtil.getFieldByLocation(block.getLocation());
            Optional<User> optionalUser = ElementalsX.getUser(UUID.fromString(uuid));
            User user = null;
            if (optionalUser.isPresent())
                user = optionalUser.get();
            return (!(field.isMember(uuid) || field.isOwner(uuid) || (user != null && user.hasPermission("elementals.protection.override"))));
        });
    }

    @EventHandler
    public void event(BlockExplodeEvent event) {
        if (FieldChunkUtil.doChunkWait(event.getBlock().getChunk())) {
            event.blockList().clear();
            return;
        }
        String worldName = event.getBlock().getWorld().getName();
        if (worldName.equals("spawn") || worldName.equals("dungeon"))
            return;
        event.blockList().removeIf(block -> {
            if (!FieldUtil.isFieldAtLocation(block.getLocation()))
                return false;
            if (block.getType() != Material.TNT)
                return true;
            Optional<CompoundTag> optionalTNTCompoundTag = TagRegister.getStored(event.getBlock());
            if (optionalTNTCompoundTag.isEmpty())
                return false;
            CompoundTag tntCompoundTag = optionalTNTCompoundTag.get();
            if (!(tntCompoundTag.containsString("tnt-igniter-type") && tntCompoundTag.containsString("tnt-igniter-uuid")))
                return false;
            String type = tntCompoundTag.getString("tnt-igniter-type");
            if (!type.equals(EntityType.PLAYER.toString()))
                return false;
            String uuid = tntCompoundTag.getString("tnt-igniter-uuid");
            Field field = FieldUtil.getFieldByLocation(block.getLocation());
            Optional<User> optionalUser = ElementalsX.getUser(UUID.fromString(uuid));
            User user = null;
            if (optionalUser.isPresent())
                user = optionalUser.get();
            return (!(field.isMember(uuid) || field.isOwner(uuid) || (user != null && user.hasPermission("elementals.protection.override"))));
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void event(HangingBreakByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Location loc = event.getEntity().getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        if (event.getRemover() == null)
            return;
        String uuid;
        if (event.getRemover().getType() == EntityType.PLAYER) {
            uuid = event.getRemover().getUniqueId().toString();
        } else if (event.getRemover().getType() == EntityType.PRIMED_TNT) {
            Optional<CompoundTag> optionalRemovedCompoundTag = TagRegister.getStored(event.getRemover());
            if (optionalRemovedCompoundTag.isEmpty())
                return;
            CompoundTag removedCompoundTag = optionalRemovedCompoundTag.get();
            if (!(removedCompoundTag.containsString("tnt-igniter-type") && removedCompoundTag.containsString("tnt-igniter-uuid")))
                return;
            String type = removedCompoundTag.getString("tnt-igniter-type");
            if (!type.equals(EntityType.PLAYER.toString()))
                return;
            uuid = removedCompoundTag.getString("tnt-igniter-uuid");
        } else {
            Optional<CompoundTag> optionalRemovedCompoundTag = TagRegister.getStored(event.getRemover());
            if (optionalRemovedCompoundTag.isEmpty())
                return;
            CompoundTag removedCompoundTag = optionalRemovedCompoundTag.get();
            if (!(removedCompoundTag.containsString("projectile-shooter-type") && removedCompoundTag.containsString("projectile-shooter-uuid")))
                return;
            String type = removedCompoundTag.getString("projectile-shooter-type");
            if (!type.equals(EntityType.PLAYER.toString()))
                return;
            uuid = removedCompoundTag.getString("projectile-shooter-uuid");
        }
        Optional<User> optionalUser = ElementalsX.getUser(UUID.fromString(uuid));
        User user = null;
        if (optionalUser.isPresent())
            user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || (user != null && user.hasPermission("elementals.protection.override")))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(HangingPlaceEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Location loc = event.getEntity().getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerBucketEmptyEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        Location loc = event.getBlockClicked().getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti folosi galeata aici!"));
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerBucketFillEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        Location loc = event.getBlockClicked().getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti folosi galeata aici!"));
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerArmorStandManipulateEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked()))
            return;
        Location loc = event.getRightClicked().getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti interactiona cu aceast armor stand aflat in protectie."));
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerInteractEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked()))
            return;
        Location loc = event.getRightClicked().getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        EntityType entityType = event.getRightClicked().getType();
        if (entityType == EntityType.PARROT
                || entityType == EntityType.LLAMA
                || entityType == EntityType.TRADER_LLAMA
                || entityType == EntityType.ZOMBIE_HORSE
                || entityType == EntityType.MULE
                || entityType == EntityType.HORSE
                || entityType == EntityType.DONKEY
                || entityType == EntityType.SKELETON_HORSE
                || entityType == EntityType.PIG
                || entityType == EntityType.COW
                || entityType == EntityType.OCELOT
                || entityType == EntityType.MUSHROOM_COW
                || entityType == EntityType.BAT
                || entityType == EntityType.BOAT
                || entityType == EntityType.CHICKEN
                || entityType == EntityType.WOLF
                || entityType == EntityType.VILLAGER
                || entityType == EntityType.IRON_GOLEM
                || entityType == EntityType.LEASH_HITCH
                || entityType == EntityType.RABBIT
                || entityType == EntityType.SHEEP
                || entityType == EntityType.SNOWMAN
                || entityType == EntityType.SQUID
                || entityType == EntityType.ARMOR_STAND
                || entityType == EntityType.ITEM_FRAME) {
            if (entityType == EntityType.ZOMBIE_HORSE
                    || entityType == EntityType.MULE
                    || entityType == EntityType.HORSE
                    || entityType == EntityType.DONKEY
                    || entityType == EntityType.SKELETON_HORSE) {
                AbstractHorse horse = (AbstractHorse) event.getRightClicked();
                if (horse.isTamed())
                    if (horse.getOwner() != null && horse.getOwner().getUniqueId().equals(uuid))
                        return;
            }
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti interactiona cu aceasta entitate aflata in protectie."));
            event.setCancelled(true);
        } else if (entityType == EntityType.MINECART_CHEST
                || entityType == EntityType.MINECART_FURNACE
                || entityType == EntityType.MINECART_HOPPER
                || entityType == EntityType.MINECART_TNT
                || entityType == EntityType.MINECART_MOB_SPAWNER
                || entityType == EntityType.MINECART_COMMAND) {
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti interactiona cu aceast vehicul aflat in protectie."));
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void event(EntityInteractEvent event) {
        Entity entity = event.getEntity();
        EntityType entityType = entity.getType();
        Material blockType = event.getBlock().getType();
        if (entityType == EntityType.ZOMBIE_HORSE
                || entityType == EntityType.MULE
                || entityType == EntityType.HORSE
                || entityType == EntityType.DONKEY
                || entityType == EntityType.SKELETON_HORSE) {
            if (FieldChunkUtil.doChunkWait(event.getBlock().getChunk())) {
                event.setCancelled(true);
                return;
            }
            Location loc = event.getBlock().getLocation();
            if (!FieldUtil.isFieldAtLocation(loc))
                return;
            Field field = FieldUtil.getFieldByLocation(loc);
            if (field.hasFun())
                return;
            if (!Tag.WOODEN_PRESSURE_PLATES.isTagged(blockType)
                    || !Tag.DOORS.isTagged(blockType)
                    || !Tag.TRAPDOORS.isTagged(blockType)
                    || !Tag.BUTTONS.isTagged(blockType)
                    || blockType != Material.HEAVY_WEIGHTED_PRESSURE_PLATE
                    || blockType != Material.LIGHT_WEIGHTED_PRESSURE_PLATE
                    || blockType != Material.STONE_PRESSURE_PLATE
                    || blockType != Material.NOTE_BLOCK
                    || blockType != Material.LEVER
                    || blockType != Material.JUKEBOX
                    || blockType != Material.ACACIA_FENCE_GATE
                    || blockType != Material.BIRCH_FENCE_GATE
                    || blockType != Material.DARK_OAK_FENCE_GATE
                    || blockType != Material.JUNGLE_FENCE_GATE
                    || blockType != Material.OAK_FENCE_GATE
                    || blockType != Material.SPRUCE_FENCE_GATE)
                return;
            AbstractHorse horse = (AbstractHorse) entity;
            if (!horse.isTamed() && horse.getOwner() == null)
                return;
            if (field.isMember(horse.getOwner().getUniqueId()) || field.isOwner(horse.getOwner().getUniqueId()))
                return;
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void event0(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL)
            return;
        Material clickedBlockType = event.getClickedBlock().getType();
        if (!(clickedBlockType == Material.CHEST
                || clickedBlockType == Material.BEE_NEST
                || clickedBlockType == Material.BEEHIVE
                || clickedBlockType == Material.FLOWER_POT
                || clickedBlockType == Material.TRAPPED_CHEST
                || clickedBlockType == Material.FURNACE
                || clickedBlockType == Material.JUKEBOX
                || clickedBlockType == Material.DROPPER
                || clickedBlockType == Material.DISPENSER
                || clickedBlockType == Material.NOTE_BLOCK
                || clickedBlockType == Material.LEVER
                || clickedBlockType == Material.STONE_BUTTON
                || clickedBlockType == Material.DAYLIGHT_DETECTOR
                || clickedBlockType == Material.HOPPER
                || clickedBlockType == Material.REPEATER
                || clickedBlockType == Material.COMPARATOR
                || clickedBlockType == Material.BEACON
                || clickedBlockType == Material.BREWING_STAND
                || clickedBlockType == Material.ACACIA_FENCE_GATE
                || clickedBlockType == Material.BIRCH_FENCE_GATE
                || clickedBlockType == Material.DARK_OAK_FENCE_GATE
                || clickedBlockType == Material.JUNGLE_FENCE_GATE
                || clickedBlockType == Material.OAK_FENCE_GATE
                || clickedBlockType == Material.SPRUCE_FENCE_GATE
                || clickedBlockType == Material.SHULKER_BOX
                || clickedBlockType == Material.BLACK_SHULKER_BOX
                || clickedBlockType == Material.BLUE_SHULKER_BOX
                || clickedBlockType == Material.BROWN_SHULKER_BOX
                || clickedBlockType == Material.CYAN_SHULKER_BOX
                || clickedBlockType == Material.GRAY_SHULKER_BOX
                || clickedBlockType == Material.GREEN_SHULKER_BOX
                || clickedBlockType == Material.LIGHT_BLUE_SHULKER_BOX
                || clickedBlockType == Material.LIGHT_GRAY_SHULKER_BOX
                || clickedBlockType == Material.LIME_SHULKER_BOX
                || clickedBlockType == Material.MAGENTA_SHULKER_BOX
                || clickedBlockType == Material.ORANGE_SHULKER_BOX
                || clickedBlockType == Material.PINK_SHULKER_BOX
                || clickedBlockType == Material.PURPLE_SHULKER_BOX
                || clickedBlockType == Material.RED_SHULKER_BOX
                || clickedBlockType == Material.WHITE_SHULKER_BOX
                || clickedBlockType == Material.YELLOW_SHULKER_BOX
                || clickedBlockType == Material.BLAST_FURNACE
                || clickedBlockType == Material.SMOKER
                || clickedBlockType == Material.BARREL
                || clickedBlockType == Material.CAMPFIRE
                || clickedBlockType == Material.HEAVY_WEIGHTED_PRESSURE_PLATE
                || clickedBlockType == Material.LIGHT_WEIGHTED_PRESSURE_PLATE
                || clickedBlockType == Material.STONE_PRESSURE_PLATE
                || clickedBlockType == Material.TURTLE_EGG
                || Tag.WOODEN_PRESSURE_PLATES.isTagged(clickedBlockType)
                || Tag.DOORS.isTagged(clickedBlockType)
                || Tag.ANVIL.isTagged(clickedBlockType)
                || Tag.BEDS.isTagged(clickedBlockType)
                || Tag.TRAPDOORS.isTagged(clickedBlockType)
                || Tag.BUTTONS.isTagged(clickedBlockType)))
            return;
        //TODO more blocks
        Location loc = event.getClickedBlock().getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.hasFun()) {
            if (Tag.WOODEN_PRESSURE_PLATES.isTagged(clickedBlockType)
                    || Tag.DOORS.isTagged(clickedBlockType)
                    || Tag.TRAPDOORS.isTagged(clickedBlockType)
                    || Tag.BUTTONS.isTagged(clickedBlockType)
                    || clickedBlockType == Material.HEAVY_WEIGHTED_PRESSURE_PLATE
                    || clickedBlockType == Material.LIGHT_WEIGHTED_PRESSURE_PLATE
                    || clickedBlockType == Material.STONE_PRESSURE_PLATE
                    || clickedBlockType == Material.NOTE_BLOCK
                    || clickedBlockType == Material.LEVER
                    || clickedBlockType == Material.JUKEBOX
                    || clickedBlockType == Material.ACACIA_FENCE_GATE
                    || clickedBlockType == Material.BIRCH_FENCE_GATE
                    || clickedBlockType == Material.DARK_OAK_FENCE_GATE
                    || clickedBlockType == Material.JUNGLE_FENCE_GATE
                    || clickedBlockType == Material.OAK_FENCE_GATE
                    || clickedBlockType == Material.SPRUCE_FENCE_GATE)
                return;
        }
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        if (event.getAction() != Action.PHYSICAL)
            event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti interactiona cu acest bloc aflat in protectie!"));
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event1(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        ItemStack item = event.getItem();
        if (item == null)
            return;
        Material itemType = item.getType();
        if (!(itemType == Material.MINECART
                || itemType == Material.CHEST_MINECART
                || itemType == Material.FURNACE_MINECART
                || itemType == Material.HOPPER_MINECART
                || itemType == Material.TNT_MINECART
                || itemType == Material.BIRCH_BOAT
                || itemType == Material.OAK_BOAT
                || itemType == Material.ACACIA_BOAT
                || itemType == Material.DARK_OAK_BOAT
                || itemType == Material.JUNGLE_BOAT
                || itemType == Material.SPRUCE_BOAT))
            return;
        Location loc = event.getClickedBlock().getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti pune vehicule in protectie!"));
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        ItemStack item = event.getItem();
        if (item == null)
            return;
        Material itemType = item.getType();
        Location loc = event.getClickedBlock().getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (!(item.getItemMeta() instanceof SpawnEggMeta || itemType == Material.FLINT_AND_STEEL
                || itemType == Material.ARMOR_STAND) || (!field.hasFun() && itemType == Material.END_CRYSTAL))
            return;
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        if (event.getClickedBlock().getType() == Material.SPAWNER)
            event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti schimba acest spawner aflat in protectie!"));
        else
            event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti folosi acest obiect in aceasta protectie!"));
        event.setCancelled(true);
    }

    @EventHandler
    public void event0(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)
            return;
        if (!FieldUtil.isFieldAtLocation(event.getFrom()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(event.getFrom());
        if (field.hasFun())
            return;
        if (field.isOwner(user.getUUID()) || field.isMember(user.getUUID()) || user.hasPermission("elementals.protection.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerTeleportEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        FieldUtil.updateUser(user, event.getTo());
    }

    @EventHandler(ignoreCancelled = true)
    public void event0(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (block.getType() == Material.DIAMOND_BLOCK) {
            if (FieldUtil.isFieldBlock(block))
                return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti sparge blocuri in aceasta protectie!"));
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event0(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.DIAMOND_BLOCK)
            return;
        Location loc = block.getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti pune blocuri in aceasta protectie!"));
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event0(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();
        if (CitizensAPI.getNPCRegistry().isNPC(victim))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(damager))
            return;
        EntityType entityType = victim.getType();
        if (!(entityType == EntityType.BEE
                || entityType == EntityType.PARROT
                || entityType == EntityType.LLAMA
                || entityType == EntityType.TRADER_LLAMA
                || entityType == EntityType.ZOMBIE_HORSE
                || entityType == EntityType.MULE
                || entityType == EntityType.HORSE
                || entityType == EntityType.DONKEY
                || entityType == EntityType.SKELETON_HORSE
                || entityType == EntityType.PIG
                || entityType == EntityType.COW
                || entityType == EntityType.OCELOT
                || entityType == EntityType.MUSHROOM_COW
                || entityType == EntityType.BAT
                || entityType == EntityType.BOAT
                || entityType == EntityType.CHICKEN
                || entityType == EntityType.WOLF
                || entityType == EntityType.VILLAGER
                || entityType == EntityType.IRON_GOLEM
                || entityType == EntityType.LEASH_HITCH
                || entityType == EntityType.RABBIT
                || entityType == EntityType.SHEEP
                || entityType == EntityType.SNOWMAN
                || entityType == EntityType.SQUID
                || entityType == EntityType.ARMOR_STAND
                || entityType == EntityType.ITEM_FRAME
                || entityType == EntityType.POLAR_BEAR))
            return;
        Location loc = event.getEntity().getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        if (damager.hasMetadata("CS_Label")) {
            event.setCancelled(true);
            return;
        }
        String uuid;
        if (damager.getType() == EntityType.PLAYER) {
            uuid = damager.getUniqueId().toString();
        } else if (damager.getType() == EntityType.PRIMED_TNT) {
            Optional<CompoundTag> optionalRemovedCompoundTag = TagRegister.getStored(damager);
            if (optionalRemovedCompoundTag.isEmpty())
                return;
            CompoundTag removedCompoundTag = optionalRemovedCompoundTag.get();
            if (!(removedCompoundTag.containsString("tnt-igniter-type") && removedCompoundTag.containsString("tnt-igniter-uuid")))
                return;
            String type = removedCompoundTag.getString("tnt-igniter-type");
            if (!type.equals(EntityType.PLAYER.toString()))
                return;
            uuid = removedCompoundTag.getString("tnt-igniter-uuid");
        } else {
            Optional<CompoundTag> optionalRemovedCompoundTag = TagRegister.getStored(damager);
            if (optionalRemovedCompoundTag.isEmpty())
                return;
            CompoundTag removedCompoundTag = optionalRemovedCompoundTag.get();
            if (!(removedCompoundTag.containsString("projectile-shooter-type") && removedCompoundTag.containsString("projectile-shooter-uuid")))
                return;
            String type = removedCompoundTag.getString("projectile-shooter-type");
            if (!type.equals(EntityType.PLAYER.toString()))
                return;
            uuid = removedCompoundTag.getString("projectile-shooter-uuid");
        }
        Optional<User> optionalUser = ElementalsX.getUser(UUID.fromString(uuid));
        User user = null;
        if (optionalUser.isPresent())
            user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || (user != null && user.hasPermission("elementals.protection.override")))
            return;
        if (damager instanceof Projectile)
            damager.remove(); //Don't spam the players, lol
        if (user != null && damager.getType() != EntityType.PRIMED_TNT)
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti lovi aceasta entitate aflata in protectie."));
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
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
            damager = (Entity) proj.getShooter();
        } else
            damager = event.getAttacker();
        if (damager == null)
            return;
        if (damager.getType() != EntityType.PLAYER)
            return;
        Location loc = vehicle.getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        EntityType entityType = vehicle.getType();
        if (!(entityType == EntityType.MINECART
                || entityType == EntityType.MINECART_CHEST
                || entityType == EntityType.MINECART_FURNACE
                || entityType == EntityType.MINECART_HOPPER
                || entityType == EntityType.MINECART_TNT
                || entityType == EntityType.MINECART_MOB_SPAWNER
                || entityType == EntityType.MINECART_COMMAND
                || entityType == EntityType.BOAT))
            return;
        UUID uuid = damager.getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti distruge acest vehiculul aflat in protectie."));
        event.setCancelled(true);
        if (event.getAttacker().hasMetadata("flame_ench"))
            vehicle.setFireTicks(0);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(VehicleEnterEvent event) {
        Vehicle vehicle = event.getVehicle();
        UUID uuid = event.getEntered().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        Location loc = vehicle.getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.hasFun())
            return;
        EntityType entityType = vehicle.getType();
        if (!(entityType == EntityType.MINECART
                || entityType == EntityType.BOAT))
            return;
        if (!(event.getEntered() instanceof Player))
            return;
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti intra in acest vehicul aflat in protectie."));
        event.setCancelled(true);
    }

    @EventHandler
    public void event1(EntityDamageByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getDamager()))
            return;
        if (event.getDamager().getType() != EntityType.FIREWORK)
            return;
        Location loc = event.getEntity().getLocation();
        if (FieldChunkUtil.doChunkWait(loc.getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        if (!event.getDamager().hasMetadata("firework_ench"))
            return;
        event.setDamage(0);
    }
}
