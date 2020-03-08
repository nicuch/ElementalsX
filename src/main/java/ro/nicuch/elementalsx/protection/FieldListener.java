package ro.nicuch.elementalsx.protection;

import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class FieldListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void event1(WeaponDamageEntityEvent event) {
        if (!FieldUtil.isFieldAtLocation(event.getVictim().getLocation()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getDamager().getUniqueId());
        if (optionalUser.isPresent()) {
            if (!optionalUser.get().hasPermission("elementals.protection.override"))
                return;
            if (event.getVictim().getType() == EntityType.PLAYER)
                event.getDamager().sendMessage("&8[&cProtectie&8] &c&oNu poti ataca un jucator aflat in protectie!");
            else
                event.getDamager().sendMessage("&8[&cProtectie&8] &c&oNu poti ataca o entitate aflat in protectie!");
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerMoveEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        if (event.getTo().getBlock().equals(event.getFrom().getBlock()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        FieldUtil.updateUser(user, event.getTo());
    }

    //Prevents job payments for players without acces in field
    @EventHandler(ignoreCancelled = true)
    public void jobPrevent(JobsPrePaymentEvent event) {
        Block block = event.getBlock();
        Entity entity = event.getEntity();
        if (block != null) {
            UUID uuid = event.getPlayer().getUniqueId();
            Optional<User> optionalUser = ElementalsX.getUser(uuid);
            if (!optionalUser.isPresent())
                return;
            User user = optionalUser.get();
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
                if (!optionalUser.isPresent())
                    return;
                User user = optionalUser.get();
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
        for (Block block : event.getBlocks()) {
            if (!block.getType().equals(Material.DIAMOND_BLOCK))
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
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(event.getCaught().getLocation());
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void protectionPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.DIAMOND_BLOCK)
            return;
        String world_name = block.getWorld().getName();
        if (!(world_name.equals("world") || world_name.equals("world_nether") || world_name.equals("world_the_end")))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
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
                    if (!entityuser.isPresent())
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
        Location loc = block.getLocation();
        Block locMax = loc.clone().add(25, 0, 25).getBlock();
        Block locMin = loc.clone().add(-25, 0, -25).getBlock();
        Field2D field2D = new Field2D(locMax.getX(), locMax.getZ(), locMin.getX(), locMin.getZ());
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            String query = "INSERT INTO protection (id, x, y, z, world, owner, maxx, maxz, minx, minz, chunkx, chunkz) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            try (PreparedStatement statement = ElementalsX.getDatabase().prepareStatement(query)) {
                statement.setString(1, id.toString());
                statement.setInt(2, block.getX());
                statement.setInt(3, block.getY());
                statement.setInt(4, block.getZ());
                statement.setString(5, block.getWorld().getName());
                statement.setString(6, uuid.toString());
                statement.setInt(7, field2D.getMaxX());
                statement.setInt(8, field2D.getMaxZ());
                statement.setInt(9, field2D.getMinX());
                statement.setInt(10, field2D.getMinZ());
                statement.setInt(11, block.getChunk().getX());
                statement.setInt(12, block.getChunk().getZ());
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
        Block block = event.getBlock();
        if (block.getType() != Material.DIAMOND_BLOCK)
            return;
        String world_name = block.getWorld().getName();
        if (!(world_name.equals("world") || world_name.equals("world_nether") || world_name.equals("world_the_end")))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
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
                    if (!entityuser.isPresent())
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
        if (!FieldUtil.isFieldAtLocation(event.getBlock().getLocation())
                && FieldUtil.isFieldAtLocation(event.getToBlock().getLocation()))
            event.setCancelled(true);
        else if (FieldUtil.isFieldAtLocation(event.getBlock().getLocation())
                && FieldUtil.isFieldAtLocation(event.getToBlock().getLocation()))
            if (!FieldUtil.getFieldByLocation(event.getBlock().getLocation())
                    .isOwner(FieldUtil.getFieldByLocation(event.getToBlock().getLocation()).getOwner()))
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

    @EventHandler(ignoreCancelled = true)
    public void event(EntityChangeBlockEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (event.getEntity().getType() != EntityType.PLAYER)
            return;
        UUID uuid = event.getEntity().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        if (event.getBlock().getType() != Material.FARMLAND)
            return;
        Location loc = event.getBlock().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(FakeEntityDamageByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Entity entity = event.getEntity();
        Entity damager;
        if (event.getDamager() instanceof Projectile) {
            Projectile proj = (Projectile) event.getDamager();
            if (proj.getShooter() == null)
                return;
            if (proj.getShooter() instanceof BlockProjectileSource) {
                if (entity.getType() == EntityType.PLAYER)
                    event.setCancelled(true);
                return;
            }
            damager = (Entity) proj.getShooter();
        } else
            damager = event.getDamager();
        if (entity.getType() != EntityType.PLAYER)
            return;
        if (damager.getType() != EntityType.PLAYER)
            return;
        if (!FieldUtil.isFieldAtLocation(entity.getLocation()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(damager.getUniqueId());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.protection.override"))
            return;
        user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti face PvP cu un jucator aflat in protectie!"));
        event.setCancelled(true);
        if (event.getDamager().hasMetadata("flame_ench"))
            entity.setFireTicks(0);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(EntityDamageByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Entity entity = event.getEntity();
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
        if (damager.getType() != EntityType.PLAYER)
            return;
        if (!FieldUtil.isFieldAtLocation(entity.getLocation()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(damager.getUniqueId());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.protection.override"))
            return;
        user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti face PvP cu un jucator aflat in protectie!"));
        event.setCancelled(true);
        if (event.getDamager().hasMetadata("flame_ench"))
            entity.setFireTicks(0);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(EntityExplodeEvent event) {
        String worldName = event.getLocation().getWorld().getName();
        if (worldName.equals("spawn") || worldName.equals("dungeon"))
            return;
        if (!FieldUtil.isFieldAtLocation(event.getLocation()))
            return;
        event.blockList().removeIf(block -> block.getType() != Material.TNT && FieldUtil.isFieldAtLocation(block.getLocation()));
    }

    @EventHandler
    public void event(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> block.getType() != Material.TNT && FieldUtil.isFieldAtLocation(block.getLocation()));
    }

    @EventHandler(ignoreCancelled = true)
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
            remover = (Entity) proj.getShooter();
        } else
            remover = event.getRemover();
        if (remover.getType() != EntityType.PLAYER)
            return;
        UUID uuid = remover.getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (!(field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override")))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerArmorStandManipulateEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        if (CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked()))
            return;
        Location loc = event.getRightClicked().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(HangingPlaceEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Location loc = event.getEntity().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
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
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
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
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti folosi galeata aici!"));
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerInteractEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked()))
            return;
        Location loc = event.getRightClicked().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
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
            Field field = FieldUtil.getFieldByLocation(entity.getLocation());
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
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
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
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
        if (hand == null)
            return;
        Material handType = hand.getType();
        if (!(handType == Material.MINECART
                || handType == Material.CHEST_MINECART
                || handType == Material.FURNACE_MINECART
                || handType == Material.HOPPER_MINECART
                || handType == Material.TNT_MINECART
                || handType == Material.BIRCH_BOAT
                || handType == Material.OAK_BOAT
                || handType == Material.ACACIA_BOAT
                || handType == Material.DARK_OAK_BOAT
                || handType == Material.JUNGLE_BOAT
                || handType == Material.SPRUCE_BOAT))
            return;
        Location loc = event.getClickedBlock().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
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
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
        if (hand == null)
            return;
        Material handType = hand.getType();
        Location loc = event.getClickedBlock().getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (!(hand.getItemMeta() instanceof SpawnEggMeta || handType == Material.FLINT_AND_STEEL
                || handType == Material.ARMOR_STAND) || (!field.hasFun() && handType == Material.END_CRYSTAL))
            return;
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        if (event.getClickedBlock().getType().equals(Material.SPAWNER))
            event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti schimba acest spawner aflat in protectie!"));
        else
            event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti folosi acest obiect in aceasta protectie!"));
        event.setCancelled(true);
    }


    @EventHandler
    public void event(PlayerTeleportEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        FieldUtil.updateUser(user, event.getTo());
    }

    @EventHandler(ignoreCancelled = true)
    public void event0(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.DIAMOND_BLOCK)) {
            if (FieldUtil.isFieldBlock(block))
                return;
        }
        Location loc = block.getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
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
        if (block.getType().equals(Material.DIAMOND_BLOCK))
            return;
        Location loc = block.getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        event.getPlayer().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti pune blocuri in aceasta protectie!"));
        event.setCancelled(true);
    }

    @EventHandler
    public void event0(FakeEntityDamageByEntityEvent event) {
        {
            if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
                return;
            Entity entity = event.getEntity();
            Entity damager;
            if (event.getDamager() instanceof Projectile) {
                Projectile proj = (Projectile) event.getDamager();
                if (proj.getShooter() == null)
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
            if (entityType != EntityType.PARROT
                    || entityType != EntityType.LLAMA
                    || entityType != EntityType.TRADER_LLAMA
                    || entityType != EntityType.ZOMBIE_HORSE
                    || entityType != EntityType.MULE
                    || entityType != EntityType.HORSE
                    || entityType != EntityType.DONKEY
                    || entityType != EntityType.SKELETON_HORSE
                    || entityType != EntityType.PIG
                    || entityType != EntityType.COW
                    || entityType != EntityType.OCELOT
                    || entityType != EntityType.MUSHROOM_COW
                    || entityType != EntityType.BAT
                    || entityType != EntityType.BOAT
                    || entityType != EntityType.CHICKEN
                    || entityType != EntityType.WOLF
                    || entityType != EntityType.VILLAGER
                    || entityType != EntityType.IRON_GOLEM
                    || entityType != EntityType.LEASH_HITCH
                    || entityType != EntityType.RABBIT
                    || entityType != EntityType.SHEEP
                    || entityType != EntityType.SNOWMAN
                    || entityType != EntityType.SQUID
                    || entityType != EntityType.ARMOR_STAND
                    || entityType != EntityType.ITEM_FRAME)
                return;
            UUID uuid = damager.getUniqueId();
            Optional<User> optionalUser = ElementalsX.getUser(uuid);
            if (!optionalUser.isPresent())
                return;
            User user = optionalUser.get();
            Field field = FieldUtil.getFieldByLocation(loc);
            if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
                return;
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti lovi aceasta entitate aflata in protectie."));
            event.setCancelled(true);
            if (event.getDamager().hasMetadata("flame_ench"))
                entity.setFireTicks(0);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void event0(EntityDamageByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Entity entity = event.getEntity();
        Entity damager;
        if (event.getDamager() instanceof Projectile) {
            Projectile proj = (Projectile) event.getDamager();
            if (proj.getShooter() == null)
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
        if (entityType != EntityType.PARROT
                || entityType != EntityType.LLAMA
                || entityType != EntityType.TRADER_LLAMA
                || entityType != EntityType.ZOMBIE_HORSE
                || entityType != EntityType.MULE
                || entityType != EntityType.HORSE
                || entityType != EntityType.DONKEY
                || entityType != EntityType.SKELETON_HORSE
                || entityType != EntityType.PIG
                || entityType != EntityType.COW
                || entityType != EntityType.OCELOT
                || entityType != EntityType.MUSHROOM_COW
                || entityType != EntityType.BAT
                || entityType != EntityType.BOAT
                || entityType != EntityType.CHICKEN
                || entityType != EntityType.WOLF
                || entityType != EntityType.VILLAGER
                || entityType != EntityType.IRON_GOLEM
                || entityType != EntityType.LEASH_HITCH
                || entityType != EntityType.RABBIT
                || entityType != EntityType.SHEEP
                || entityType != EntityType.SNOWMAN
                || entityType != EntityType.SQUID
                || entityType != EntityType.ARMOR_STAND
                || entityType != EntityType.ITEM_FRAME)
            return;
        UUID uuid = damager.getUniqueId();
        Optional<User> optionalUser = ElementalsX.getUser(uuid);
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        Field field = FieldUtil.getFieldByLocation(loc);
        if (field.isMember(uuid) || field.isOwner(uuid) || user.hasPermission("elementals.protection.override"))
            return;
        user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti lovi aceasta entitate aflata in protectie."));
        event.setCancelled(true);
        if (event.getDamager().hasMetadata("flame_ench"))
            entity.setFireTicks(0);
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
        if (!optionalUser.isPresent())
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
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        Location loc = vehicle.getLocation();
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
        if (!FieldUtil.isFieldAtLocation(event.getEntity().getLocation()))
            return;
        if (!event.getDamager().hasMetadata("firework_ench"))
            return;
        event.setDamage(0);
    }

    @EventHandler
    public void event1(FakeEntityDamageByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getDamager()))
            return;
        if (event.getDamager().getType() != EntityType.FIREWORK)
            return;
        if (!FieldUtil.isFieldAtLocation(event.getEntity().getLocation()))
            return;
        if (!event.getDamager().hasMetadata("firework_ench"))
            return;
        event.setDamage(0);
    }
}
