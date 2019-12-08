package ro.nicuch.elementalsx.enchants;

import java.util.UUID;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dropper;
import org.bukkit.block.data.Directional;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.citizensnpcs.api.CitizensAPI;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.elementals.NanoBowShotEvent;
import ro.nicuch.elementalsx.enchants.EnchantUtil.CCurseEnchType;
import ro.nicuch.elementalsx.enchants.EnchantUtil.CEnchantType;
import ro.nicuch.elementalsx.protection.Field;
import ro.nicuch.elementalsx.protection.FieldUtil;

public class EnchantListener implements Listener {

    @EventHandler
    public void event(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        EnchantUtil.useEnchant(event, event.getEntity(), event.getDamager(), event.getCause());
    }

    @EventHandler
    public void event(EntityDamageEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (!(event.getEntity() instanceof LivingEntity))
            return;
        ItemStack vh = ((LivingEntity) event.getEntity()).getEquipment().getHelmet();
        if (vh == null)
            return;
        if (vh.getType().equals(Material.AIR))
            return;
        if (!(event.getCause().equals(DamageCause.WITHER) || event.getCause().equals(DamageCause.POISON)))
            return;
        if (EnchantUtil.hasEnchant(vh, CEnchantType.ANTIVENOM) && EnchantUtil.chanceEnchant(CEnchantType.ANTIVENOM))
            event.setDamage(0);
    }

    @EventHandler
    public void event(EntityDeathEvent event) {
        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
            return;
        EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
        EnchantUtil.useEnchant(event, ev.getEntity(), ev.getDamager(), ev.getCause());
    }

    @EventHandler
    public void event(EntityShootBowEvent event) {
        ItemStack bow = event.getBow();
        if (bow == null)
            return;
        if (!event.getProjectile().getType().equals(EntityType.ARROW))
            return;
        if (EnchantUtil.hasEnchant(bow, CEnchantType.FIREWORK) && EnchantUtil.chanceEnchant(CEnchantType.FIREWORK))
            event.getProjectile().setMetadata("firework1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.BLAZE) && EnchantUtil.chanceEnchant(CEnchantType.BLAZE)) {
            Fireball ball = event.getEntity().launchProjectile(Fireball.class);
            ball.setVelocity(event.getProjectile().getVelocity());
            event.setProjectile(ball);
        }
        if (EnchantUtil.hasEnchant(bow, CEnchantType.SHARPSHOOTER1)
                && EnchantUtil.chanceEnchant(CEnchantType.SHARPSHOOTER1))
            event.getProjectile().setVelocity(event.getProjectile().getVelocity().normalize().multiply(1.5));
        else if (EnchantUtil.hasEnchant(bow, CEnchantType.SHARPSHOOTER2)
                && EnchantUtil.chanceEnchant(CEnchantType.SHARPSHOOTER2))
            event.getProjectile().setVelocity(event.getProjectile().getVelocity().normalize().multiply(1.75));
        else if (EnchantUtil.hasEnchant(bow, CEnchantType.SHARPSHOOTER3)
                && EnchantUtil.chanceEnchant(CEnchantType.SHARPSHOOTER3))
            event.getProjectile().setVelocity(event.getProjectile().getVelocity().normalize().multiply(2));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.MOLOTOV) && EnchantUtil.chanceEnchant(CEnchantType.MOLOTOV))
            event.getProjectile().setMetadata("molotov", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.FROZEN) && EnchantUtil.chanceEnchant(CEnchantType.FROZEN))
            event.getProjectile().setMetadata("frozen1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.WISDOM1) && EnchantUtil.chanceEnchant(CEnchantType.WISDOM1))
            event.getProjectile().setMetadata("wishdom1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.WISDOM2) && EnchantUtil.chanceEnchant(CEnchantType.WISDOM2))
            event.getProjectile().setMetadata("wishdom2", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.HUNTER1) && EnchantUtil.chanceEnchant(CEnchantType.HUNTER1))
            event.getProjectile().setMetadata("hunter1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.HUNTER2) && EnchantUtil.chanceEnchant(CEnchantType.HUNTER2))
            event.getProjectile().setMetadata("hunter2", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.HUNTER3) && EnchantUtil.chanceEnchant(CEnchantType.HUNTER3))
            event.getProjectile().setMetadata("hunter3", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.NECROMANCER1)
                && EnchantUtil.chanceEnchant(CEnchantType.NECROMANCER1))
            event.getProjectile().setMetadata("necromancer1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.NECROMANCER2)
                && EnchantUtil.chanceEnchant(CEnchantType.NECROMANCER2))
            event.getProjectile().setMetadata("necromancer2", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.NECROMANCER3)
                && EnchantUtil.chanceEnchant(CEnchantType.NECROMANCER3))
            event.getProjectile().setMetadata("necromancer3", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.PESTICIDE1) && EnchantUtil.chanceEnchant(CEnchantType.PESTICIDE1))
            event.getProjectile().setMetadata("pesticide1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.PESTICIDE2) && EnchantUtil.chanceEnchant(CEnchantType.PESTICIDE2))
            event.getProjectile().setMetadata("pesticide2", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.PESTICIDE3) && EnchantUtil.chanceEnchant(CEnchantType.PESTICIDE3))
            event.getProjectile().setMetadata("pesticide3", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.STALKER1) && EnchantUtil.chanceEnchant(CEnchantType.STALKER1))
            event.getProjectile().setMetadata("stalker1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.STALKER2) && EnchantUtil.chanceEnchant(CEnchantType.STALKER2))
            event.getProjectile().setMetadata("stalker2", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.STALKER3) && EnchantUtil.chanceEnchant(CEnchantType.STALKER3))
            event.getProjectile().setMetadata("stalker3", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.HOMING) && EnchantUtil.chanceEnchant(CEnchantType.HOMING)) {
            // SethBling homing arrows.
            LivingEntity shooter = event.getEntity();
            double minAngle = 6.283185307179586D;
            Entity minEntity = null;
            for (Entity entity : shooter.getNearbyEntities(64.0D, 64.0D, 64.0D)) {
                if (shooter.hasLineOfSight(entity) && (entity instanceof LivingEntity) && (!entity.isDead())) {
                    Vector toTarget = entity.getLocation().toVector().clone()
                            .subtract(shooter.getLocation().toVector());
                    double angle = event.getProjectile().getVelocity().angle(toTarget);
                    if (angle < minAngle) {
                        minAngle = angle;
                        minEntity = entity;
                    }
                }
            }
            if (minEntity != null) {
                Projectile proj = (Projectile) event.getProjectile();
                Entity target = minEntity;

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Location projLoc = proj.getLocation();
                        Location targetLoc = target.getLocation();
                        double speed = proj.getVelocity().length();
                        if ((proj.isOnGround()) || (proj.isDead()) || (target.isDead())) {
                            this.cancel();
                            return;
                        }
                        Vector toTarget = targetLoc.clone().add(new Vector(0.0D, 0.5D, 0.0D)).subtract(projLoc)
                                .toVector();
                        Vector dirVelocity = proj.getVelocity().clone().normalize();
                        Vector dirToTarget = toTarget.clone().normalize();
                        double angle = dirVelocity.angle(dirToTarget);

                        double newSpeed = 0.9D * speed + 0.13999999999999999D;
                        Vector newVelocity;
                        if (angle < 0.12D) {
                            newVelocity = dirVelocity.clone().multiply(newSpeed);
                        } else {
                            Vector newDir = dirVelocity.clone().multiply((angle - 0.12D) / angle)
                                    .add(dirToTarget.clone().multiply(0.12D / angle));
                            newDir.normalize();
                            newVelocity = newDir.clone().multiply(newSpeed);
                        }
                        proj.setVelocity(newVelocity.add(new Vector(0.0D, 0.03D, 0.0D)));
                        proj.getWorld().playEffect(projLoc, Effect.SMOKE, 0);
                    }
                }.runTaskTimer(ElementalsX.get(), 1L, 1L);
            }
        }
        if (bow.containsEnchantment(Enchantment.ARROW_FIRE))
            event.getProjectile().setMetadata("flame_ench", new FixedMetadataValue(ElementalsX.get(), true));
    }

    @EventHandler
    public void event(NanoBowShotEvent event) {
        ItemStack bow = event.getBow();
        if (bow == null)
            return;
        if (!event.getProjectile().getType().equals(EntityType.ARROW))
            return;
        if (EnchantUtil.hasEnchant(bow, CEnchantType.FIREWORK) && EnchantUtil.chanceEnchant(CEnchantType.FIREWORK))
            event.getProjectile().setMetadata("firework1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.BLAZE) && EnchantUtil.chanceEnchant(CEnchantType.BLAZE)) {
            Fireball ball = event.getEntity().launchProjectile(Fireball.class);
            ball.setVelocity(event.getProjectile().getVelocity());
            event.setProjectile(ball);
        }
        if (EnchantUtil.hasEnchant(bow, CEnchantType.SHARPSHOOTER1)
                && EnchantUtil.chanceEnchant(CEnchantType.SHARPSHOOTER1))
            event.getProjectile().setVelocity(event.getProjectile().getVelocity().normalize().multiply(1.5));
        else if (EnchantUtil.hasEnchant(bow, CEnchantType.SHARPSHOOTER2)
                && EnchantUtil.chanceEnchant(CEnchantType.SHARPSHOOTER2))
            event.getProjectile().setVelocity(event.getProjectile().getVelocity().normalize().multiply(1.75));
        else if (EnchantUtil.hasEnchant(bow, CEnchantType.SHARPSHOOTER3)
                && EnchantUtil.chanceEnchant(CEnchantType.SHARPSHOOTER3))
            event.getProjectile().setVelocity(event.getProjectile().getVelocity().normalize().multiply(2));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.MOLOTOV) && EnchantUtil.chanceEnchant(CEnchantType.MOLOTOV))
            event.getProjectile().setMetadata("molotov", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.FROZEN) && EnchantUtil.chanceEnchant(CEnchantType.FROZEN))
            event.getProjectile().setMetadata("frozen1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.WISDOM1) && EnchantUtil.chanceEnchant(CEnchantType.WISDOM1))
            event.getProjectile().setMetadata("wishdom1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.WISDOM2) && EnchantUtil.chanceEnchant(CEnchantType.WISDOM2))
            event.getProjectile().setMetadata("wishdom2", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.HUNTER1) && EnchantUtil.chanceEnchant(CEnchantType.HUNTER1))
            event.getProjectile().setMetadata("hunter1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.HUNTER2) && EnchantUtil.chanceEnchant(CEnchantType.HUNTER2))
            event.getProjectile().setMetadata("hunter2", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.HUNTER3) && EnchantUtil.chanceEnchant(CEnchantType.HUNTER3))
            event.getProjectile().setMetadata("hunter3", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.NECROMANCER1)
                && EnchantUtil.chanceEnchant(CEnchantType.NECROMANCER1))
            event.getProjectile().setMetadata("necromancer1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.NECROMANCER2)
                && EnchantUtil.chanceEnchant(CEnchantType.NECROMANCER2))
            event.getProjectile().setMetadata("necromancer2", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.NECROMANCER3)
                && EnchantUtil.chanceEnchant(CEnchantType.NECROMANCER3))
            event.getProjectile().setMetadata("necromancer3", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.PESTICIDE1) && EnchantUtil.chanceEnchant(CEnchantType.PESTICIDE1))
            event.getProjectile().setMetadata("pesticide1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.PESTICIDE2) && EnchantUtil.chanceEnchant(CEnchantType.PESTICIDE2))
            event.getProjectile().setMetadata("pesticide2", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.PESTICIDE3) && EnchantUtil.chanceEnchant(CEnchantType.PESTICIDE3))
            event.getProjectile().setMetadata("pesticide3", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.STALKER1) && EnchantUtil.chanceEnchant(CEnchantType.STALKER1))
            event.getProjectile().setMetadata("stalker1", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.STALKER2) && EnchantUtil.chanceEnchant(CEnchantType.STALKER2))
            event.getProjectile().setMetadata("stalker2", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.STALKER3) && EnchantUtil.chanceEnchant(CEnchantType.STALKER3))
            event.getProjectile().setMetadata("stalker3", new FixedMetadataValue(ElementalsX.get(), true));
        if (EnchantUtil.hasEnchant(bow, CEnchantType.HOMING) && EnchantUtil.chanceEnchant(CEnchantType.HOMING)) {
            // SethBling homing arrows.
            LivingEntity shooter = event.getEntity();
            double minAngle = 6.283185307179586D;
            Entity minEntity = null;
            for (Entity entity : shooter.getNearbyEntities(64.0D, 64.0D, 64.0D)) {
                if (shooter.hasLineOfSight(entity) && (entity instanceof LivingEntity) && (!entity.isDead())) {
                    Vector toTarget = entity.getLocation().toVector().clone()
                            .subtract(shooter.getLocation().toVector());
                    double angle = event.getProjectile().getVelocity().angle(toTarget);
                    if (angle < minAngle) {
                        minAngle = angle;
                        minEntity = entity;
                    }
                }
            }
            if (minEntity != null) {
                Projectile proj = event.getProjectile();
                Entity target = minEntity;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Location projLoc = proj.getLocation();
                        Location targetLoc = target.getLocation();
                        double speed = proj.getVelocity().length();
                        if ((proj.isOnGround()) || (proj.isDead()) || (target.isDead())) {
                            this.cancel();
                            return;
                        }
                        Vector toTarget = targetLoc.clone().add(new Vector(0.0D, 0.5D, 0.0D)).subtract(projLoc)
                                .toVector();
                        Vector dirVelocity = proj.getVelocity().clone().normalize();
                        Vector dirToTarget = toTarget.clone().normalize();
                        double angle = dirVelocity.angle(dirToTarget);

                        double newSpeed = 0.9D * speed + 0.13999999999999999D;
                        Vector newVelocity;
                        if (angle < 0.12D) {
                            newVelocity = dirVelocity.clone().multiply(newSpeed);
                        } else {
                            Vector newDir = dirVelocity.clone().multiply((angle - 0.12D) / angle)
                                    .add(dirToTarget.clone().multiply(0.12D / angle));
                            newDir.normalize();
                            newVelocity = newDir.clone().multiply(newSpeed);
                        }
                        proj.setVelocity(newVelocity.add(new Vector(0.0D, 0.03D, 0.0D)));
                        proj.getWorld().playEffect(projLoc, Effect.SMOKE, 0);
                    }
                }.runTaskTimer(ElementalsX.get(), 1L, 1L);
            }
        }
        if (bow.containsEnchantment(Enchantment.ARROW_FIRE))
            event.getProjectile().setMetadata("flame_ench", new FixedMetadataValue(ElementalsX.get(), true));
    }

    @EventHandler
    public void event(ProjectileHitEvent event) {
        if (!event.getEntity().getType().equals(EntityType.ARROW))
            return;
        if (event.getEntity().hasMetadata("firework1")) {
            Firework fw = ElementalsUtil.randomFirework(event.getEntity().getLocation());
            fw.setMetadata("firework_ench", new FixedMetadataValue(ElementalsX.get(), true));
        }
        if (event.getEntity().hasMetadata("molotov")) {
            Block bEnt = event.getEntity().getLocation().getBlock();
            if (!bEnt.getWorld().getName().equals("spawn")) {
                Block n_b = bEnt.getRelative(BlockFace.NORTH);
                Block s_b = bEnt.getRelative(BlockFace.SOUTH);
                Block w_b = bEnt.getRelative(BlockFace.WEST);
                Block e_b = bEnt.getRelative(BlockFace.EAST);
                Block d_b = bEnt.getRelative(BlockFace.DOWN);
                Block u_b = bEnt.getRelative(BlockFace.UP);
                if (bEnt.isEmpty() && !FieldUtil.isFieldAtLocation(bEnt.getLocation()))
                    bEnt.setType(Material.FIRE, true);
                if (n_b.isEmpty() && !FieldUtil.isFieldAtLocation(n_b.getLocation()))
                    n_b.setType(Material.FIRE, true);
                if (s_b.isEmpty() && !FieldUtil.isFieldAtLocation(s_b.getLocation()))
                    s_b.setType(Material.FIRE, true);
                if (w_b.isEmpty() && !FieldUtil.isFieldAtLocation(w_b.getLocation()))
                    w_b.setType(Material.FIRE, true);
                if (e_b.isEmpty() && !FieldUtil.isFieldAtLocation(e_b.getLocation()))
                    e_b.setType(Material.FIRE, true);
                if (d_b.isEmpty() && !FieldUtil.isFieldAtLocation(d_b.getLocation()))
                    d_b.setType(Material.FIRE, true);
                if (u_b.isEmpty() && !FieldUtil.isFieldAtLocation(u_b.getLocation()))
                    u_b.setType(Material.FIRE, true);
            }
        }
        if (event.getEntity().hasMetadata("stalker1"))
            event.getEntity().getNearbyEntities(5, 5, 5).forEach((Entity rEnt) -> {
                if (rEnt instanceof LivingEntity)
                    if (EnchantUtil.checkPotion((LivingEntity) rEnt, PotionEffectType.GLOWING, 20 * 6))
                        ((LivingEntity) rEnt).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 6, 1),
                                true);
            });
        if (event.getEntity().hasMetadata("stalker2"))
            event.getEntity().getNearbyEntities(10, 10, 10).forEach((Entity rEnt) -> {
                if (rEnt instanceof LivingEntity)
                    if (EnchantUtil.checkPotion((LivingEntity) rEnt, PotionEffectType.GLOWING, 20 * 8))
                        ((LivingEntity) rEnt).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 8, 1),
                                true);
            });
        if (event.getEntity().hasMetadata("stalker3"))
            event.getEntity().getNearbyEntities(15, 15, 15).forEach((Entity rEnt) -> {
                if (rEnt instanceof LivingEntity)
                    if (EnchantUtil.checkPotion((LivingEntity) rEnt, PotionEffectType.GLOWING, 20 * 10))
                        ((LivingEntity) rEnt).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 10, 1),
                                true);
            });
        if (event.getEntity().hasMetadata("nano"))
            event.getEntity().remove();
    }

    @EventHandler
    public void event(EntityExplodeEvent event) {
        if (!event.getEntity().getType().equals(EntityType.FIREBALL))
            return;
        if (!event.getEntity().getWorld().getName().equals("spawn"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(InventoryCloseEvent event) {
        InventoryView view = event.getView();
        Inventory inv = view.getTopInventory();
        if (inv == null)
            return;
        if (!view.getTitle().equals(ElementalsUtil.color("&5Enchanter")))
            return;
        ItemStack i_slot2 = inv.getItem(2);
        ItemStack i_slot6 = inv.getItem(6);
        World world = event.getPlayer().getWorld();
        Location loc = event.getPlayer().getLocation();
        if (i_slot2 != null) {
            Material m_slot2 = i_slot2.getType();
            if (!(m_slot2.equals(Material.AIR) || m_slot2.equals(Material.GLASS_PANE)))
                world.dropItem(loc, i_slot2.clone());
        }
        if (i_slot6 != null) {
            Material m_slot6 = i_slot6.getType();
            if (!(m_slot6.equals(Material.AIR) || m_slot6.equals(Material.GLASS_PANE)))
                world.dropItem(loc, i_slot6.clone());
        }
    }

    @EventHandler
    public void event0(InventoryClickEvent event) {
        InventoryView view = event.getView();
        ClickType click = event.getClick();
        if (!(click.equals(ClickType.DOUBLE_CLICK) || click.equals(ClickType.SHIFT_LEFT)
                || click.equals(ClickType.SHIFT_RIGHT)))
            return;
        if (view == null)
            return;
        if (view.getTitle().equals(ElementalsUtil.color("&5Enchanter"))
                || view.getTitle().equals(ElementalsUtil.color("&9&ki&r &5Ender Case &9&ki"))
                || view.getTitle().equals(ElementalsUtil.color("&9&kii&r &5Ender Case &9&kii"))
                || view.getTitle().equals(ElementalsUtil.color("&9&kii&r &6Blaze Case &9&kii"))
                || view.getTitle().equals(ElementalsUtil.color("&9&kii&r &2Crate Chest &9&kii"))) {
            event.setResult(Result.DENY);
        }
    }

    @EventHandler
    public void event(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory inv = event.getClickedInventory();
        if (inv == null)
            return;
        if (view == null)
            return;
        if (inv.getHolder() == null)
            return;
        if (!(inv.getHolder() instanceof Dropper))
            return;
        if (!inv.getType().equals(InventoryType.CHEST))
            return;
        if (view.getTitle().equals(ElementalsUtil.color("&5Enchanter"))) {
            switch (event.getRawSlot()) {
                case 2:
                    break;
                case 6:
                    break;
                case 4:
                    event.setResult(Result.DENY);
                    if (inv.getItem(2) == null)
                        break;
                    if (inv.getItem(4) == null)
                        break;
                    if (inv.getItem(2).getType().equals(Material.AIR))
                        break;
                    if (inv.getItem(4).getType().equals(Material.AIR))
                        break;
                    if (!event.getWhoClicked().getGameMode().equals(GameMode.CREATIVE))
                        if (((Player) event.getWhoClicked()).getLevel() < 10)
                            break;
                    ItemStack enchanted = new ItemStack(Material.AIR);
                    ItemStack secondary = new ItemStack(Material.AIR);
                    ItemStack enchantItem = inv.getItem(2);
                    ItemStack enchantBook = inv.getItem(6);
                    switch (enchantItem.getType()) {
                        // TODO Sword
                        case DIAMOND_SWORD:
                        case GOLDEN_SWORD:
                        case IRON_SWORD:
                        case STONE_SWORD:
                        case WOODEN_SWORD:
                            if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WISDOM1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.WISDOM1,
                                                CEnchantType.WISDOM2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.WISDOM1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WISDOM2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.WISDOM1,
                                                CEnchantType.WISDOM2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.WISDOM2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.STRIKE1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.STRIKE1,
                                                CEnchantType.STRIKE2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.STRIKE1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.STRIKE2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.STRIKE1,
                                                CEnchantType.STRIKE2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.STRIKE2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ICEASPECT1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ICEASPECT1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ICEASPECT2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.ICEASPECT1,
                                                CEnchantType.ICEASPECT2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ICEASPECT2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.ICEASPECT1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ICEASPECT2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ICEASPECT1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ICEASPECT2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.ICEASPECT1,
                                                CEnchantType.ICEASPECT2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ICEASPECT2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.ICEASPECT2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.POISON1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.POISON1,
                                                CEnchantType.POISON2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.POISON1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.POISON2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.POISON1,
                                                CEnchantType.POISON2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.POISON2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WITHER1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.WITHER1,
                                                CEnchantType.WITHER2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.WITHER1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WITHER2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.WITHER1,
                                                CEnchantType.WITHER2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.WITHER2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HARDENED1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.HARDENED1,
                                                CEnchantType.HARDENED2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.HARDENED1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HARDENED2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.HARDENED1,
                                                CEnchantType.HARDENED2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.HARDENED2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.BLIND)) {
                                if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.BLIND))
                                    enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.BLIND);
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.LIFESTEAL)) {
                                if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.LIFESTEAL))
                                    enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.LIFESTEAL);
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.GOOEY)) {
                                if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.GOOEY))
                                    enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.GOOEY);
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.STRIKE1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.STRIKE1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.STRIKE1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.STRIKE2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.STRIKE2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.STRIKE2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLIND)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.BLIND)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.BLIND);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLIND);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.LIFESTEAL)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.LIFESTEAL)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.LIFESTEAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.LIFESTEAL);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.GOOEY)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.GOOEY)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.GOOEY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.GOOEY);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.ICEASPECT1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ICEASPECT1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.ICEASPECT1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.ICEASPECT1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.ICEASPECT2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ICEASPECT2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.ICEASPECT2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.ICEASPECT2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.POISON1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.POISON1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.POISON1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.POISON2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.POISON2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.POISON2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.WITHER1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.WITHER1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.WITHER1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.WITHER2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.WITHER2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.WITHER2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.HARDENED1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.HARDENED1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.HARDENED1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.HARDENED2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.HARDENED2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.HARDENED2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.WISDOM1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.WISDOM1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.WISDOM1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.WISDOM2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.WISDOM2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.WISDOM2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SHARPNESS1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ALL, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SHARPNESS1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SHARPNESS2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ALL, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SHARPNESS2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SHARPNESS3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ALL, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SHARPNESS3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SHARPNESS4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ALL, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SHARPNESS4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SHARPNESS5)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ALL, 5)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SHARPNESS5);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SMITE1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_UNDEAD, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_UNDEAD);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SMITE1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SMITE2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_UNDEAD, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_UNDEAD);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SMITE2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SMITE3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_UNDEAD, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_UNDEAD);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SMITE3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SMITE4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_UNDEAD, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_UNDEAD);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SMITE4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SMITE5)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_UNDEAD, 5)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_UNDEAD);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SMITE5);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BANEOFARTHROPODS1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ARTHROPODS, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ARTHROPODS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BANEOFARTHROPODS1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BANEOFARTHROPODS2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ARTHROPODS, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ARTHROPODS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BANEOFARTHROPODS2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BANEOFARTHROPODS3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ARTHROPODS, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ARTHROPODS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BANEOFARTHROPODS3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BANEOFARTHROPODS4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ARTHROPODS, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ARTHROPODS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BANEOFARTHROPODS4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BANEOFARTHROPODS5)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ARTHROPODS, 5)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ARTHROPODS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BANEOFARTHROPODS5);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.KNOCKBACK1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.KNOCKBACK, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.KNOCKBACK);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.KNOCKBACK1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.KNOCKBACK2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.KNOCKBACK, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.KNOCKBACK);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.KNOCKBACK2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREASPECT1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.FIRE_ASPECT, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.FIRE_ASPECT);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREASPECT1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREASPECT2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.FIRE_ASPECT, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.FIRE_ASPECT);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREASPECT2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.LOOTING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LOOT_BONUS_MOBS, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LOOT_BONUS_MOBS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.LOOTING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.LOOTING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LOOT_BONUS_MOBS, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LOOT_BONUS_MOBS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.LOOTING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.LOOTING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LOOT_BONUS_MOBS, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LOOT_BONUS_MOBS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.LOOTING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SWEEPINGEDGE1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.SWEEPING_EDGE, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.SWEEPING_EDGE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SWEEPINGEDGE1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SWEEPINGEDGE2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.SWEEPING_EDGE, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.SWEEPING_EDGE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SWEEPINGEDGE2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SWEEPINGEDGE3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.SWEEPING_EDGE, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.SWEEPING_EDGE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SWEEPINGEDGE3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MENDING)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.MENDING, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.MENDING);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MENDING);
                                }
                            }
                            break;
                        // TODO Helmet
                        case CHAINMAIL_HELMET:
                        case DIAMOND_HELMET:
                        case GOLDEN_HELMET:
                        case IRON_HELMET:
                        case LEATHER_HELMET:
                            if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HARDENED1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.HARDENED1,
                                                CEnchantType.HARDENED2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.HARDENED1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HARDENED2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.HARDENED1,
                                                CEnchantType.HARDENED2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.HARDENED2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ANTIVENOM)) {
                                if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ANTIVENOM))
                                    enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.ANTIVENOM);
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.GLOWING)) {
                                if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.GLOWING))
                                    enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.GLOWING);
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.GLOWING)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.GLOWING)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.GLOWING);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.GLOWING);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.HARDENED1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.HARDENED1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.HARDENED1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.HARDENED2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.HARDENED2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.HARDENED2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.ANTIVENOM)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ANTIVENOM)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.ANTIVENOM);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.ANTIVENOM);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.RESPIRATION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.OXYGEN, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.OXYGEN);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.RESPIRATION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.RESPIRATION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.OXYGEN, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.OXYGEN);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.RESPIRATION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.RESPIRATION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.OXYGEN, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.OXYGEN);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.RESPIRATION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.AQUAAFFINITY)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.WATER_WORKER, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.WATER_WORKER);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.AQUAAFFINITY);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MENDING)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.MENDING, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.MENDING);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MENDING);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.THORNS1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.THORNS, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.THORNS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.THORNS1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.THORNS2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.THORNS, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.THORNS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.THORNS2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.THORNS3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.THORNS, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.THORNS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.THORNS3);
                                }
                            }
                            break;
                        // TODO Chestplate
                        case CHAINMAIL_CHESTPLATE:
                        case DIAMOND_CHESTPLATE:
                        case GOLDEN_CHESTPLATE:
                        case IRON_CHESTPLATE:
                        case LEATHER_CHESTPLATE:
                            if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.REGENERATION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.REGENERATION1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.REGENERATION2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.REGENERATION1,
                                                CEnchantType.REGENERATION2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.REGENERATION2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.REGENERATION1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.REGENERATION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.REGENERATION1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.REGENERATION2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.REGENERATION1,
                                                CEnchantType.REGENERATION2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.REGENERATION2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.REGENERATION2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.POISON1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.POISON1,
                                                CEnchantType.POISON2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.POISON1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.POISON2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.REGENERATION2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.POISON1,
                                                CEnchantType.POISON2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.POISON2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WITHER1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.WITHER1,
                                                CEnchantType.WITHER2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.WITHER1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WITHER2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.REGENERATION2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.WITHER1,
                                                CEnchantType.WITHER2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.WITHER2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.BLIND)) {
                                if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.BLIND))
                                    enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.BLIND);
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.REGENERATION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.REGENERATION1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.REGENERATION1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.REGENERATION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.REGENERATION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.REGENERATION2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.REGENERATION2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.REGENERATION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLIND)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.BLIND)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.BLIND);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLIND);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.POISON1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.POISON1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.POISON1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.POISON2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.POISON2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.POISON2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.POISON2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.WITHER1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.WITHER1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.WITHER1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.WITHER2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WITHER2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.WITHER2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.WITHER2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MOLTEN1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.MOLTEN1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.MOLTEN1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MOLTEN1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MOLTEN2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.MOLTEN2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.MOLTEN2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MOLTEN2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MENDING)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.MENDING, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.MENDING);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MENDING);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.THORNS1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.THORNS, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.THORNS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.THORNS1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.THORNS2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.THORNS, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.THORNS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.THORNS2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.THORNS3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.THORNS, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.THORNS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.THORNS3);
                                }
                            }
                            break;
                        // TODO Leggings
                        case CHAINMAIL_LEGGINGS:
                        case DIAMOND_LEGGINGS:
                        case GOLDEN_LEGGINGS:
                        case IRON_LEGGINGS:
                        case LEATHER_LEGGINGS:
                            if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.MOLTEN1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.MOLTEN1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.MOLTEN2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.MOLTEN1,
                                                CEnchantType.MOLTEN2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.MOLTEN2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.MOLTEN1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.MOLTEN2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.MOLTEN1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.MOLTEN2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.MOLTEN1,
                                                CEnchantType.MOLTEN2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.MOLTEN2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.MOLTEN2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ROCKETS1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ROCKETS1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ROCKETS2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.ROCKETS1,
                                                CEnchantType.ROCKETS2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ROCKETS2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.ROCKETS1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ROCKETS2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ROCKETS1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ROCKETS2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.ROCKETS1,
                                                CEnchantType.ROCKETS2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ROCKETS2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.ROCKETS2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.OBSIDIANSHIELD)) {
                                if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.OBSIDIANSHIELD))
                                    enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.OBSIDIANSHIELD);
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.OBSIDIANSHIELD)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.OBSIDIANSHIELD)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.OBSIDIANSHIELD);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.OBSIDIANSHIELD);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.ROCKETS1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ROCKETS1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.ROCKETS1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.ROCKETS1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.ROCKETS2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ROCKETS2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.ROCKETS2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.ROCKETS2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MENDING)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.MENDING, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.MENDING);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MENDING);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.THORNS1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.THORNS, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.THORNS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.THORNS1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.THORNS2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.THORNS, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.THORNS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.THORNS2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.THORNS3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.THORNS, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.THORNS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.THORNS3);
                                }
                            }
                            break;
                        // TODO Boots
                        case CHAINMAIL_BOOTS:
                        case DIAMOND_BOOTS:
                        case GOLDEN_BOOTS:
                        case IRON_BOOTS:
                        case LEATHER_BOOTS:
                            if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WHEELS1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WHEELS1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WHEELS2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.WHEELS1,
                                                CEnchantType.WHEELS2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WHEELS2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.WHEELS1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WHEELS2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WHEELS1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WHEELS2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.WHEELS1,
                                                CEnchantType.WHEELS2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WHEELS2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.WHEELS2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.WHEELS1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WHEELS1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.WHEELS1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.WHEELS1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.WHEELS2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WHEELS2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.WHEELS2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.WHEELS2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FEATHERFALLING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FALL, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FEATHERFALLING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FEATHERFALLING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FALL, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FEATHERFALLING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FEATHERFALLING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FALL, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FEATHERFALLING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_ENVIRONMENTAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREPROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FIRE, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREPROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLASTPROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_EXPLOSIONS, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_EXPLOSIONS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLASTPROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PROJPROTECTION4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_PROJECTILE, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_PROJECTILE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PROJPROTECTION4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MENDING)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.MENDING, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.MENDING);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MENDING);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FEATHERFALLING4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.PROTECTION_FALL, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.PROTECTION_FALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FEATHERFALLING4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.THORNS1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.THORNS, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.THORNS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.THORNS1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.THORNS2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.THORNS, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.THORNS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.THORNS2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.THORNS3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.THORNS, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.THORNS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.THORNS3);
                                }
                            }
                            break;
                        // TODO Pickaxe
                        case DIAMOND_PICKAXE:
                        case GOLDEN_PICKAXE:
                        case IRON_PICKAXE:
                        case STONE_PICKAXE:
                        case WOODEN_PICKAXE:
                            // TODO Spade
                        case DIAMOND_SHOVEL:
                        case GOLDEN_SHOVEL:
                        case IRON_SHOVEL:
                        case STONE_SHOVEL:
                        case WOODEN_SHOVEL:
                            if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ENERGIZING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.ENERGIZING1,
                                                CEnchantType.ENERGIZING2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.ENERGIZING1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ENERGIZING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.ENERGIZING1,
                                                CEnchantType.ENERGIZING2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.ENERGIZING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.ENERGIZING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.ENERGIZING1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.ENERGIZING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.ENERGIZING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.ENERGIZING2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.ENERGIZING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY5)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 5)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY5);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SILKTOUCH)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.SILK_TOUCH, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.SILK_TOUCH);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SILKTOUCH);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FORTUNE1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LOOT_BONUS_BLOCKS, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LOOT_BONUS_BLOCKS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FORTUNE1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FORTUNE2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LOOT_BONUS_BLOCKS, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LOOT_BONUS_BLOCKS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FORTUNE2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FORTUNE3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LOOT_BONUS_BLOCKS, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LOOT_BONUS_BLOCKS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FORTUNE3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MENDING)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.MENDING, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.MENDING);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MENDING);
                                }
                            }
                            break;
                        // TODO Axe
                        case DIAMOND_AXE:
                        case GOLDEN_AXE:
                        case IRON_AXE:
                        case STONE_AXE:
                        case WOODEN_AXE:
                            if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ENERGIZING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.ENERGIZING1,
                                                CEnchantType.ENERGIZING2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.ENERGIZING1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ENERGIZING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.ENERGIZING1,
                                                CEnchantType.ENERGIZING2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.ENERGIZING2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.STRIKE1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.STRIKE1,
                                                CEnchantType.STRIKE2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.STRIKE1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.STRIKE2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.STRIKE1,
                                                CEnchantType.STRIKE2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.STRIKE2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HARDENED1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.HARDENED1,
                                                CEnchantType.HARDENED2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.HARDENED1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HARDENED2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.HARDENED1,
                                                CEnchantType.HARDENED2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.HARDENED2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.LIFESTEAL)) {
                                if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.LIFESTEAL))
                                    enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.LIFESTEAL);
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.ENERGIZING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.ENERGIZING1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.ENERGIZING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.STRIKE1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.STRIKE1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.STRIKE1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.STRIKE2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STRIKE2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.STRIKE2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.STRIKE2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.ENERGIZING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.ENERGIZING2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.ENERGIZING2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.ENERGIZING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.LIFESTEAL)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.LIFESTEAL)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.LIFESTEAL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.LIFESTEAL);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.GOOEY)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.GOOEY)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.GOOEY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.GOOEY);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.HARDENED1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.HARDENED1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.HARDENED1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.HARDENED2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HARDENED2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.HARDENED2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.HARDENED2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY5)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 5)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY5);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SILKTOUCH)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.SILK_TOUCH, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.SILK_TOUCH);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SILKTOUCH);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FORTUNE1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LOOT_BONUS_BLOCKS, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LOOT_BONUS_BLOCKS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FORTUNE1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FORTUNE2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LOOT_BONUS_BLOCKS, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LOOT_BONUS_BLOCKS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FORTUNE2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FORTUNE3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LOOT_BONUS_BLOCKS, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LOOT_BONUS_BLOCKS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FORTUNE3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MENDING)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.MENDING, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.MENDING);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MENDING);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SHARPNESS1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ALL, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SHARPNESS1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SHARPNESS2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ALL, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SHARPNESS2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SHARPNESS3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ALL, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SHARPNESS3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SHARPNESS4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ALL, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SHARPNESS4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SHARPNESS5)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ALL, 5)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ALL);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SHARPNESS5);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SMITE1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_UNDEAD, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_UNDEAD);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SMITE1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SMITE2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_UNDEAD, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_UNDEAD);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SMITE2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SMITE3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_UNDEAD, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_UNDEAD);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SMITE3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SMITE4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_UNDEAD, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_UNDEAD);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SMITE4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SMITE5)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_UNDEAD, 5)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_UNDEAD);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SMITE5);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BANEOFARTHROPODS1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ARTHROPODS, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ARTHROPODS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BANEOFARTHROPODS1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BANEOFARTHROPODS2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ARTHROPODS, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ARTHROPODS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BANEOFARTHROPODS2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BANEOFARTHROPODS3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ARTHROPODS, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ARTHROPODS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BANEOFARTHROPODS3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BANEOFARTHROPODS4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ARTHROPODS, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ARTHROPODS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BANEOFARTHROPODS4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BANEOFARTHROPODS5)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DAMAGE_ARTHROPODS, 5)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DAMAGE_ARTHROPODS);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BANEOFARTHROPODS5);
                                }
                            }
                            break;
                        // TODO Bow
                        case BOW:
                            if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.SHARPSHOOTER1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.SHARPSHOOTER1,
                                                    CEnchantType.SHARPSHOOTER2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.SHARPSHOOTER1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.SHARPSHOOTER2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.SHARPSHOOTER1,
                                                    CEnchantType.SHARPSHOOTER2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER2)) {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.SHARPSHOOTER2);
                                    } else {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.SHARPSHOOTER2,
                                                    CEnchantType.SHARPSHOOTER3);
                                    }
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.SHARPSHOOTER3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.SHARPSHOOTER1,
                                                    CEnchantType.SHARPSHOOTER3);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER2)) {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.SHARPSHOOTER3);
                                    } else {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.SHARPSHOOTER2,
                                                    CEnchantType.SHARPSHOOTER3);
                                    }
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.NECROMANCER1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.NECROMANCER1,
                                                    CEnchantType.NECROMANCER2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.NECROMANCER1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.NECROMANCER2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.NECROMANCER1,
                                                    CEnchantType.NECROMANCER2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER2)) {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.NECROMANCER2);
                                    } else {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.NECROMANCER2,
                                                    CEnchantType.NECROMANCER3);
                                    }
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.NECROMANCER3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.NECROMANCER1,
                                                    CEnchantType.NECROMANCER3);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER2)) {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.NECROMANCER3);
                                    } else {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.NECROMANCER2,
                                                    CEnchantType.NECROMANCER3);
                                    }
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.PESTICIDE1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.PESTICIDE1,
                                                    CEnchantType.PESTICIDE2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE2)) {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.PESTICIDE1);
                                    }
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.PESTICIDE2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.PESTICIDE1,
                                                    CEnchantType.PESTICIDE2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE2)) {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.PESTICIDE2);
                                    } else {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.PESTICIDE2,
                                                    CEnchantType.PESTICIDE3);
                                    }
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.PESTICIDE3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.PESTICIDE1,
                                                    CEnchantType.PESTICIDE3);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE2)) {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.PESTICIDE3);
                                    } else {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.PESTICIDE2,
                                                    CEnchantType.PESTICIDE3);
                                    }
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HUNTER1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.HUNTER1,
                                                    CEnchantType.HUNTER2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER2)) {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.HUNTER1);
                                    }
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HUNTER2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.HUNTER1,
                                                    CEnchantType.HUNTER2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER2)) {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.HUNTER2);
                                    } else {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.HUNTER2,
                                                    CEnchantType.HUNTER3);
                                    }
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HUNTER3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.HUNTER1,
                                                    CEnchantType.HUNTER3);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER2)) {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.HUNTER3);
                                    } else {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.HUNTER2,
                                                    CEnchantType.HUNTER3);
                                    }
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.STALKER1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.STALKER1,
                                                    CEnchantType.STALKER2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER2)) {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.STALKER1);
                                    }
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.STALKER2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.STALKER1,
                                                    CEnchantType.STALKER2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER2)) {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.STALKER2);
                                    } else {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.STALKER2,
                                                    CEnchantType.STALKER3);
                                    }
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.STALKER3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER2))
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.STALKER1,
                                                    CEnchantType.STALKER3);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER2)) {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER3))
                                            enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.STALKER3);
                                    } else {
                                        if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER3))
                                            enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.STALKER2,
                                                    CEnchantType.STALKER3);
                                    }
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WISDOM1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.WISDOM1,
                                                CEnchantType.WISDOM2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.WISDOM1);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WISDOM2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM1)) {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM2))
                                        enchanted = EnchantUtil.changeEnchantTo(enchantItem, CEnchantType.WISDOM1,
                                                CEnchantType.WISDOM2);
                                } else {
                                    if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM2))
                                        enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.WISDOM2);
                                }
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.FROZEN)) {
                                if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.FROZEN))
                                    enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.FROZEN);
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.BLAZE)) {
                                if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.BLAZE))
                                    enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.BLAZE);
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.FIREWORK)) {
                                if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.FIREWORK))
                                    enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.FIREWORK);
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.MOLOTOV)) {
                                if (!EnchantUtil.hasEnchant(enchantItem, CEnchantType.MOLOTOV))
                                    enchanted = EnchantUtil.enchantItem(enchantItem, CEnchantType.MOLOTOV);
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FIREWORK)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.FIREWORK)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.FIREWORK);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FIREWORK);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.BLAZE)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.BLAZE)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.BLAZE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.BLAZE);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FROZEN)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.FROZEN)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.FROZEN);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FROZEN);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MOLOTOV)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.MOLOTOV)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.MOLOTOV);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MOLOTOV);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SHARPSHOOTER1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.SHARPSHOOTER1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SHARPSHOOTER1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SHARPSHOOTER2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.SHARPSHOOTER2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SHARPSHOOTER2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.SHARPSHOOTER3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.SHARPSHOOTER3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.SHARPSHOOTER3);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.SHARPSHOOTER3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.WISDOM1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.WISDOM1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.WISDOM1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.WISDOM2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.WISDOM2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.WISDOM2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.WISDOM2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.HUNTER1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.HUNTER1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.HUNTER1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.HUNTER2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.HUNTER2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.HUNTER2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.HUNTER3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.HUNTER3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.HUNTER3);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.HUNTER3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.NECROMANCER1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.NECROMANCER1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.NECROMANCER1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.NECROMANCER2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.NECROMANCER2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.NECROMANCER2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.NECROMANCER3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.NECROMANCER3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.NECROMANCER3);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.NECROMANCER3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PESTICIDE1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.PESTICIDE1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PESTICIDE1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PESTICIDE2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.PESTICIDE2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PESTICIDE2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PESTICIDE3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.PESTICIDE3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.PESTICIDE3);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PESTICIDE3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.STALKER1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.STALKER1);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.STALKER1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.STALKER2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.STALKER2);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.STALKER2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.STALKER3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, CEnchantType.STALKER3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, CEnchantType.STALKER3);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.STALKER3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MENDING)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.MENDING, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.MENDING);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MENDING);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.POWER1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.ARROW_DAMAGE, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.ARROW_DAMAGE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.POWER1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.POWER2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.ARROW_DAMAGE, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.ARROW_DAMAGE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.POWER2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.POWER3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.ARROW_DAMAGE, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.ARROW_DAMAGE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.POWER3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.POWER4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.ARROW_DAMAGE, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.ARROW_DAMAGE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.POWER4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.POWER5)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.ARROW_DAMAGE, 5)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.ARROW_DAMAGE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.POWER5);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PUNCH1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.ARROW_KNOCKBACK, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.ARROW_KNOCKBACK);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PUNCH1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.PUNCH2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.ARROW_KNOCKBACK, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.ARROW_KNOCKBACK);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.PUNCH2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.FLAME)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.ARROW_FIRE, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.ARROW_FIRE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.FLAME);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.INFINITY)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.ARROW_INFINITE, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.ARROW_INFINITE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.INFINITY);
                                }
                            }
                            break;
                        // TODO Fishing Rod
                        case FISHING_ROD:
                            if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.LUCKOFTHESEA1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LUCK, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LUCK);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.LUCKOFTHESEA1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.LUCKOFTHESEA2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LUCK, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LUCK);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.LUCKOFTHESEA2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.LUCKOFTHESEA3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LUCK, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LUCK);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.LUCKOFTHESEA3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.LURE1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LURE, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LURE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.LURE1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.LURE2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LURE, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LURE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.LURE2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.LURE3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.LURE, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.LURE);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.LURE3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MENDING)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.MENDING, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.MENDING);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MENDING);
                                }
                            }
                            break;
                        // TODO Shears
                        case SHEARS:
                            if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY4)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 4)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY4);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.EFFICIENCY5)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DIG_SPEED, 5)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DIG_SPEED);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.EFFICIENCY5);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MENDING)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.MENDING, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.MENDING);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MENDING);
                                }
                            }
                            // TODO Hoe
                        case DIAMOND_HOE:
                        case GOLDEN_HOE:
                        case IRON_HOE:
                        case STONE_HOE:
                        case WOODEN_HOE:
                            if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MENDING)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.MENDING, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.MENDING);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MENDING);
                                }
                            }
                            break;
                        // TODO Flint and steal
                        case FLINT_AND_STEEL:
                            // TODO Shield
                        case SHIELD:
                            // TODO Elytra
                        case ELYTRA:
                            if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING1)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING1);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING2)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 2)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING2);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.UNBREAKING3)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.DURABILITY, 3)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.DURABILITY);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.UNBREAKING3);
                                }
                            } else if (EnchantUtil.isCurseEnchBook(enchantBook, CCurseEnchType.MENDING)) {
                                if (EnchantUtil.hasEnchant(enchantItem, Enchantment.MENDING, 1)) {
                                    enchanted = EnchantUtil.unEnchantItem(enchantItem, Enchantment.MENDING);
                                    secondary = EnchantUtil.getEnchantedBook(CCurseEnchType.MENDING);
                                }
                            }
                            break;
                        // TODO Enchanted books
                        case ENCHANTED_BOOK:
                            if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ENERGIZING1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ENERGIZING1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.ENERGIZING2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.STRIKE1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.STRIKE1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.STRIKE2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.REGENERATION1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.REGENERATION1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.REGENERATION2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ICEASPECT1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ICEASPECT1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.ICEASPECT2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.POISON1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.POISON1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.POISON2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WITHER1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WITHER1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.WITHER2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.MOLTEN1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.MOLTEN1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.MOLTEN2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HARDENED1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HARDENED1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.HARDENED2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WHEELS1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WHEELS1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.WHEELS2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ROCKETS1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.ROCKETS1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.ROCKETS2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WISDOM1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WISDOM1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.WISDOM2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WEIGHTED1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.WEIGHTED1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.WEIGHTED2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.SHARPSHOOTER1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.SHARPSHOOTER1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.SHARPSHOOTER2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.SHARPSHOOTER2)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.SHARPSHOOTER2))
                                    enchanted = EnchantUtil.getBook(CEnchantType.SHARPSHOOTER3).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HUNTER1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HUNTER1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.HUNTER2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HUNTER2)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.HUNTER2))
                                    enchanted = EnchantUtil.getBook(CEnchantType.HUNTER3).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.NECROMANCER1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.NECROMANCER1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.NECROMANCER2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.NECROMANCER2)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.NECROMANCER2))
                                    enchanted = EnchantUtil.getBook(CEnchantType.NECROMANCER3).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.PESTICIDE1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.PESTICIDE1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.PESTICIDE2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.PESTICIDE2)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.PESTICIDE2))
                                    enchanted = EnchantUtil.getBook(CEnchantType.PESTICIDE3).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.STALKER1)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.STALKER1))
                                    enchanted = EnchantUtil.getBook(CEnchantType.STALKER2).clone();
                            } else if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.STALKER2)) {
                                if (EnchantUtil.isEnchantedBook(enchantBook, CEnchantType.STALKER2))
                                    enchanted = EnchantUtil.getBook(CEnchantType.STALKER3).clone();
                            }
                            break;
                        default:
                            break;
                    }
                    if (!enchanted.getType().equals(Material.AIR)) {
                        if (!event.getWhoClicked().getGameMode().equals(GameMode.CREATIVE))
                            ((Player) event.getWhoClicked()).setLevel(((Player) event.getWhoClicked()).getLevel() - 10);
                        inv.setItem(2, new ItemStack(Material.AIR));
                        inv.setItem(6, new ItemStack(Material.AIR));
                        event.getWhoClicked().getInventory().addItem(enchanted);
                        if (!secondary.getType().equals(Material.AIR))
                            event.getWhoClicked().getInventory().addItem(secondary);
                        ((Player) event.getWhoClicked()).updateInventory();
                        ElementalsUtil.randomFirework(
                                ((Dropper) event.getClickedInventory().getHolder()).getLocation().add(0.5, 1, 0.5));
                    }
                    break;
                default:
                    event.setResult(Result.DENY);
            }
        }
    }

    @EventHandler
    public void event(BlockDamageEvent event) {
        ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
        if (hand.getType().equals(Material.AIR))
            return;
        Block block = event.getBlock();
        if (block.getWorld().getName().equals("spawn"))
            return;
        Material blockType = block.getType();
        if (blockType.equals(Material.BEDROCK) || blockType.equals(Material.COMMAND_BLOCK)
                || blockType.equals(Material.NETHER_PORTAL) || blockType.equals(Material.END_PORTAL)
                || blockType.equals(Material.END_PORTAL_FRAME))
            return;
        if ((EnchantUtil.hasEnchant(hand, CEnchantType.ENERGIZING1)
                && EnchantUtil.chanceEnchant(CEnchantType.ENERGIZING1))
                || (EnchantUtil.hasEnchant(hand, CEnchantType.ENERGIZING2)
                && EnchantUtil.chanceEnchant(CEnchantType.ENERGIZING2)))
            switch (hand.getType()) {
                case DIAMOND_PICKAXE:
                case GOLDEN_PICKAXE:
                case IRON_PICKAXE:
                case STONE_PICKAXE:
                case WOODEN_PICKAXE:
                    //TODO
                    if (blockType.equals(Material.STONE) || blockType.equals(Material.COBBLESTONE)
                            || blockType.equals(Material.GOLD_ORE) || blockType.equals(Material.IRON_ORE)
                            || blockType.equals(Material.COAL_ORE) || blockType.equals(Material.LAPIS_ORE)
                            || blockType.equals(Material.LAPIS_BLOCK) || blockType.equals(Material.SANDSTONE)
                            || blockType.equals(Material.GOLD_BLOCK) || blockType.equals(Material.IRON_BLOCK)
                            || blockType.equals(Material.STONE_SLAB)
                            || blockType.equals(Material.BRICK) || blockType.equals(Material.MOSSY_COBBLESTONE)
                            || blockType.equals(Material.OBSIDIAN) || blockType.equals(Material.DIAMOND_ORE)
                            || blockType.equals(Material.COBBLESTONE_STAIRS) || blockType.equals(Material.ICE)
                            || blockType.equals(Material.NETHERRACK) || blockType.equals(Material.GLOWSTONE)
                            || blockType.equals(Material.GLASS) || blockType.equals(Material.WHITE_STAINED_GLASS)
                            || blockType.equals(Material.GLASS_PANE) || blockType.equals(Material.WHITE_STAINED_GLASS_PANE)
                            || blockType.equals(Material.BRICK_STAIRS) || blockType.equals(Material.NETHER_BRICK)
                            || blockType.equals(Material.NETHER_BRICK_STAIRS) || blockType.equals(Material.NETHER_BRICK_FENCE)
                            || blockType.equals(Material.END_STONE) || blockType.equals(Material.SANDSTONE_STAIRS)
                            || blockType.equals(Material.EMERALD_ORE) || blockType.equals(Material.EMERALD_BLOCK)
                            || blockType.equals(Material.COBBLESTONE_WALL)
                            || blockType.equals(Material.NETHER_QUARTZ_ORE) || blockType.equals(Material.QUARTZ_BLOCK)
                            || blockType.equals(Material.QUARTZ_STAIRS) || blockType.equals(Material.TERRACOTTA)
                            || blockType.equals(Material.PRISMARINE)
                            || blockType.equals(Material.SEA_LANTERN)
                            || blockType.equals(Material.COAL_BLOCK)
                            || blockType.equals(Material.PACKED_ICE) || blockType.equals(Material.RED_SANDSTONE)
                            || blockType.equals(Material.RED_SANDSTONE_STAIRS) || blockType.equals(Material.FURNACE)
                            || blockType.equals(Material.DISPENSER)
                            || blockType.equals(Material.MOVING_PISTON) || blockType.equals(Material.STICKY_PISTON)
                            || blockType.equals(Material.REDSTONE_LAMP)
                            || blockType.equals(Material.REDSTONE_BLOCK) || blockType.equals(Material.HOPPER)
                            || blockType.equals(Material.DROPPER) || blockType.equals(Material.STONE_PRESSURE_PLATE)
                            || blockType.equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE) || blockType.equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
                            || blockType.equals(Material.IRON_TRAPDOOR) || blockType.equals(Material.BEACON)
                            || blockType.equals(Material.REDSTONE_ORE))
                        event.setInstaBreak(true);
                    break;
                case DIAMOND_AXE:
                case GOLDEN_AXE:
                case IRON_AXE:
                case STONE_AXE:
                case WOODEN_AXE:
                    if (Tag.LOGS.isTagged(blockType)
                            || Tag.WOODEN_BUTTONS.isTagged(blockType)
                            || Tag.WOODEN_DOORS.isTagged(blockType)
                            || Tag.WOODEN_PRESSURE_PLATES.isTagged(blockType)
                            || Tag.WOODEN_BUTTONS.isTagged(blockType)
                            || Tag.WOODEN_SLABS.isTagged(blockType)
                            || Tag.WOODEN_STAIRS.isTagged(blockType)
                            || Tag.WOODEN_TRAPDOORS.isTagged(blockType)
                            || Tag.SIGNS.isTagged(blockType)
                            || Tag.WALL_SIGNS.isTagged(blockType)
                            || Tag.STANDING_SIGNS.isTagged(blockType)
                            || blockType == Material.CRAFTING_TABLE
                            || blockType == Material.NOTE_BLOCK)
                        event.setInstaBreak(true);
                    break;
                case DIAMOND_SHOVEL:
                case GOLDEN_SHOVEL:
                case IRON_SHOVEL:
                case STONE_SHOVEL:
                case WOODEN_SHOVEL:
                    if (blockType.equals(Material.GRASS) || blockType.equals(Material.DIRT)
                            || blockType.equals(Material.SAND) || blockType.equals(Material.GRAVEL)
                            || blockType.equals(Material.SOUL_SAND) || blockType.equals(Material.MYCELIUM)
                            || blockType.equals(Material.CLAY) || blockType.equals(Material.SNOW)
                            || blockType.equals(Material.SNOW_BLOCK))
                        event.setInstaBreak(true);
                    break;
                default:
                    break;
            }

    }

    @EventHandler
    public void event(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getWorld().getName().equals("spawn"))
            return;
        if (!block.getType().equals(Material.DROPPER))
            return;
        //TODO let's see
        String customName = ((Dropper) event.getBlockPlaced().getBlockData()).getCustomName();
        if (customName == null)
            return;
        if (!customName.equals(ElementalsUtil.color("&5Enchanter")))
            return;
        Directional dropper = (Directional) block.getBlockData();
        dropper.setFacing(BlockFace.UP);
        block.getState().update(true);
    }

    @EventHandler
    public void event(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        if (block.getWorld().getName().equals("spawn"))
            return;
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;
        if (!block.getType().equals(Material.DROPPER))
            return;
        String customName = ((Dropper) event.getBlock().getBlockData()).getCustomName();
        if (customName == null)
            return;
        if (!customName.equals(ElementalsUtil.color("&5Enchanter")))
            return;
        if (FieldUtil.isFieldAtLocation(loc)) {
            User user = ElementalsX.getUser(event.getPlayer());
            Field field = FieldUtil.getFieldByLocation(loc);
            UUID uuid = user.getBase().getUniqueId();
        }
    }

    @EventHandler
    public void event(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        if (!event.getClickedBlock().getType().equals(Material.DROPPER))
            return;
        String customName = ((Dropper) event.getClickedBlock().getBlockData()).getCustomName();
        if (customName == null)
            return;
        if (!customName.equals(ElementalsUtil.color("&5Enchanter")))
            return;
        event.setUseInteractedBlock(Result.DENY);
        EnchantUtil.openEnchanter(event.getPlayer(), (Dropper) event.getClickedBlock().getState());
    }

    @EventHandler
    public void event0(PlayerInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
            return;
        Location loc = event.getPlayer().getTargetBlock(null, 30).getLocation();
        if (FieldUtil.isFieldAtLocation(loc))
            return;
        if (loc.getWorld().getName().equals("spawn"))
            return;
        if (EnchantUtil.hasEnchant(event.getPlayer().getInventory().getItemInMainHand(), CEnchantType.STRIKE1)
                && EnchantUtil.chanceEnchant(CEnchantType.STRIKE1))
            event.getPlayer().getWorld().strikeLightning(loc);
        else if (EnchantUtil.hasEnchant(event.getPlayer().getInventory().getItemInMainHand(), CEnchantType.STRIKE2)
                && EnchantUtil.chanceEnchant(CEnchantType.STRIKE2))
            event.getPlayer().getWorld().strikeLightning(loc);
    }
}
