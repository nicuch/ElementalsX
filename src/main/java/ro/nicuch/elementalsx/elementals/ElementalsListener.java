package ro.nicuch.elementalsx.elementals;

import java.text.SimpleDateFormat;
import java.util.*;

import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.*;
import org.bukkit.BanList.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.citizensnpcs.api.CitizensAPI;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil.NanoType;
import ro.nicuch.elementalsx.protection.Field;
import ro.nicuch.elementalsx.protection.FieldUtil;

public class ElementalsListener implements Listener {
    private final static List<UUID> interactList = new ArrayList<>();

    @EventHandler
    public void event(PlayerFishEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        if (!handItem.hasItemMeta())
            return;
        ItemMeta meta = handItem.getItemMeta();
        if (!meta.hasDisplayName())
            return;
        String displayName = meta.getDisplayName();
        if (!displayName.equals(ElementalsUtil.color("&bGrappling Hook")))
            return;
        if (!event.getState().equals(State.IN_GROUND))
            return;
        Player player = event.getPlayer();
        User user = ElementalsX.getUser(player);
        if (player.getWorld().getName().equals("spawn") && (!user.hasPermission("elementals.admin")))
            return;
        Location l1 = player.getLocation();
        Location l2 = event.getHook().getLocation();
        double g = -0.08D;
        double d = l2.distance(l1);
        double vX = (1.0D + 0.07D * d) * (l2.getX() - l1.getX()) / d;
        double vY = (1.0D + 0.03D * d) * (l2.getY() - l1.getY()) / d - 0.5D * g * d;
        double vZ = (1.0D + 0.07D * d) * (l2.getZ() - l1.getZ()) / d;
        Vector vec = new Vector(vX, vY, vZ);
        event.getPlayer().setVelocity(vec);
    }

    @EventHandler
    public void event(EntityShootBowEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.getType().equals(EntityType.PLAYER))
            return;
        ItemStack bow = event.getBow();
        if (!bow.hasItemMeta())
            return;
        ItemMeta meta = bow.getItemMeta();
        if (!meta.hasDisplayName())
            return;
        String displayName = meta.getDisplayName();
        if (!displayName.equals(ElementalsUtil.color("&bNaNo Arc")))
            return;
        Projectile proj = (Projectile) event.getProjectile();
        Vector vely = proj.getVelocity().clone();
        int fireTick = proj.getFireTicks();
        Bukkit.getScheduler().runTaskLater(ElementalsX.get(), () -> {
            Projectile proj1 = entity.launchProjectile(Arrow.class);
            proj1.setVelocity(vely);
            proj1.setFireTicks(fireTick);
            NanoBowShotEvent a = new NanoBowShotEvent(entity, bow, proj1);
            Bukkit.getPluginManager().callEvent(a);
            if (a.isCancelled())
                proj1.remove();
            if (!a.getProjectile().equals(proj1))
                proj1.remove();
            a.getProjectile().setMetadata("nano", new FixedMetadataValue(ElementalsX.get(), true));
        }, 20L);
        Bukkit.getScheduler().runTaskLater(ElementalsX.get(), () -> {
            Projectile proj2 = entity.launchProjectile(Arrow.class);
            proj2.setVelocity(vely);
            proj2.setFireTicks(fireTick);
            NanoBowShotEvent b = new NanoBowShotEvent(entity, bow, proj2);
            Bukkit.getPluginManager().callEvent(b);
            if (b.isCancelled())
                proj2.remove();
            if (!b.getProjectile().equals(proj2))
                proj2.remove();
            b.getProjectile().setMetadata("nano", new FixedMetadataValue(ElementalsX.get(), true));
        }, 40L);
    }

    @EventHandler
    public void event0(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!entity.getType().equals(EntityType.PLAYER))
            return;
        Location loc = entity.getLocation();
        if (!FieldUtil.isFieldAtLocation(loc))
            return;
        Field field = FieldUtil.getFieldByLocation(loc);
        UUID uuid = entity.getUniqueId();
        if (field.isMember(uuid) || field.isOwner(uuid))
            return;
        switch (event.getCause()) {
            case DROWNING:
                entity.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
                break;
            case FALLING_BLOCK:
            case FIRE:
            case FIRE_TICK:
            case MAGIC:
                event.setCancelled(true);
                break;
            case SUFFOCATION:
                entity.teleport(
                        entity.getWorld().getHighestBlockAt(entity.getLocation()).getRelative(BlockFace.UP).getLocation());
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void event0(PlayerInteractAtEntityEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item == null)
            return;
        if (!item.getType().equals(Material.NAME_TAG))
            return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;
        if (meta.getDisplayName() == null)
            return;
        if (!ElementalsUtil.isValidEntity(event.getRightClicked(), false))
            return;
        boolean corupted = ElementalsUtil.hasTag(event.getRightClicked(), "corupted");
        int entityCount = ElementalsUtil.getEntityCount(event.getRightClicked());
        if (entityCount > 1) {
            LivingEntity clone = (LivingEntity) event.getRightClicked().getWorld().spawnEntity(
                    event.getRightClicked().getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5),
                    event.getRightClicked().getType());
            if (event.getRightClicked().getType() == EntityType.SHEEP) {
                Sheep sheep = (Sheep) event.getRightClicked();
                ((Sheep) clone).setSheared(sheep.isSheared());
            }
            if (entityCount > 2) {
                int finalCount = entityCount - 1;
                ElementalsUtil.setEntityCount(clone, finalCount);
                clone.setCustomName(ElementalsUtil.color("&cStack&f: &a" + finalCount));
                clone.setCustomNameVisible(false);
            }
            ElementalsUtil.removeTag(event.getRightClicked(), "count");
            if (corupted && (clone instanceof Monster)) {
                ElementalsUtil.setTag(clone, "corupted");
                //ItemUtil.findTarget((Monster) clone);
            }
        }
    }

    @EventHandler
    public void event0(EntityExplodeEvent event) {
        if (!event.getEntity().getType().equals(EntityType.CREEPER))
            return;
        event.setYield(Math.round(event.getYield()) * ElementalsUtil.getEntityCount(event.getEntity()));
    }

    @EventHandler
    public void event(PlayerShearEntityEvent event) {
        if (!event.getEntity().getType().equals(EntityType.SHEEP))
            return;
        Sheep sheep = (Sheep) event.getEntity();
        Material mat;
        switch (sheep.getColor()) {
            case BLACK:
                mat = Material.BLACK_WOOL;
                break;
            case BLUE:
                mat = Material.BLUE_WOOL;
                break;
            case BROWN:
                mat = Material.BROWN_WOOL;
                break;
            case CYAN:
                mat = Material.CYAN_WOOL;
                break;
            case GRAY:
                mat = Material.GRAY_WOOL;
                break;
            case GREEN:
                mat = Material.GREEN_WOOL;
                break;
            case LIGHT_BLUE:
                mat = Material.LIGHT_BLUE_WOOL;
                break;
            case LIGHT_GRAY:
                mat = Material.LIGHT_GRAY_WOOL;
                break;
            case LIME:
                mat = Material.LIME_WOOL;
                break;
            case MAGENTA:
                mat = Material.MAGENTA_WOOL;
                break;
            case ORANGE:
                mat = Material.ORANGE_WOOL;
                break;
            case PINK:
                mat = Material.PINK_WOOL;
                break;
            case PURPLE:
                mat = Material.PURPLE_WOOL;
                break;
            case RED:
                mat = Material.RED_WOOL;
                break;
            case YELLOW:
                mat = Material.YELLOW_WOOL;
                break;
            case WHITE:
            default:
                mat = Material.WHITE_WOOL;
                break;
        }
        ItemStack theWool = new ItemStack(mat,
                ElementalsUtil.nextInt(2) + 1);
        int count = ElementalsUtil.getEntityCount(event.getEntity()) - 1;
        for (int n = 0; n < count; n++)
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), theWool);
    }

    @EventHandler
    public void event1(CreatureSpawnEvent event) {
        if (!event.getSpawnReason().equals(SpawnReason.SPAWNER))
            return;
        ElementalsUtil.setTag(event.getEntity(), "spawner");
    }

    @EventHandler
    public void event(CreatureSpawnEvent event) {
        if (!event.getLocation().getWorld().getName().equals("spawn"))
            return;
        if (!(event.getSpawnReason().equals(SpawnReason.TRAP)
                || event.getSpawnReason().equals(SpawnReason.REINFORCEMENTS)))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event2(EntityDeathEvent event) {
        if (!ElementalsUtil.isValidEntity(event.getEntity(), true))
            return;
        boolean corupted = ElementalsUtil.hasTag(event.getEntity(), "corupted");
        int entityCount = ElementalsUtil.getEntityCount(event.getEntity());
        if (entityCount > 1) {
            if (event.getEntity().getType() == EntityType.CREEPER) {
                return;
            }
            LivingEntity clone = (LivingEntity) event.getEntity().getWorld().spawnEntity(
                    event.getEntity().getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5),
                    event.getEntity().getType());
            if (event.getEntity().getType() == EntityType.SHEEP) {
                Sheep sheep = (Sheep) event.getEntity();
                ((Sheep) clone).setSheared(sheep.isSheared());
            }
            if (entityCount > 2) {
                int finalCount = entityCount - 1;
                ElementalsUtil.setEntityCount(clone, finalCount);
                clone.setCustomName(ElementalsUtil.color("&cStack&f: &a" + finalCount));
                clone.setCustomNameVisible(false);
            }
            if (corupted && (clone instanceof Monster)) {
                ElementalsUtil.setTag(clone, "corupted");
                //TODO ItemUtil.findTarget((Monster) clone);
            }
        }
    }

    @EventHandler
    public void event(PlayerResourcePackStatusEvent event) {
        User user = ElementalsX.getUser(event.getPlayer());
        switch (event.getStatus()) {
            case ACCEPTED:
            case SUCCESSFULLY_LOADED:
                user.toggleResourcePack(true);
                break;
            case DECLINED:
            case FAILED_DOWNLOAD:
                user.toggleResourcePack(false);
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void event1(PlayerTeleportEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        if (!event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        if (!(event.getCause().equals(TeleportCause.CHORUS_FRUIT)
                || event.getCause().equals(TeleportCause.ENDER_PEARL)))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event0(CreatureSpawnEvent event) {
        if (!event.getLocation().getWorld().getName().equals("world"))
            return;
        if (!(event.getEntity().getType().equals(EntityType.ZOMBIE)
                || event.getEntity().getType().equals(EntityType.SKELETON)))
            return;
        if (event.getSpawnReason().equals(SpawnReason.SPAWNER) || event.getSpawnReason().equals(SpawnReason.CUSTOM))
            return;
        if (event.getEntity().getPassengers() != null)
            return;
        if (!event.getEntity().getPassengers().isEmpty())
            return;
        if (ElementalsUtil.nextInt(50) != 1)
            return;
        EntityEquipment equip = event.getEntity().getEquipment();
        equip.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        equip.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        equip.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        equip.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        equip.setHelmetDropChance(0);
        equip.setChestplateDropChance(0);
        equip.setLeggingsDropChance(0);
        equip.setBootsDropChance(0);
        if (event.getEntity().getType().equals(EntityType.ZOMBIE)) {
            event.getEntity()
                    .addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, true, false), true);
            event.getEntity().getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
            event.getEntity().getEquipment().setItemInMainHandDropChance(0);
        }
        // TODO PlayerDisguise herobrine = new PlayerDisguise("Herobrine");
        // TODO DisguiseAPI.disguiseToAll(event.getEntity(), herobrine);
        ElementalsUtil.setTag(event.getEntity(), "herobrine");
    }

    @EventHandler
    public void event1(EntityDeathEvent event) {
        if (!ElementalsUtil.hasTag(event.getEntity(), "herobrine"))
            return;
        if (ElementalsUtil.nextInt(15) != 1)
            return;
        event.getEntity().getWorld().dropItem(event.getEntity().getLocation(),
                new ItemStack(Material.DIAMOND, ElementalsUtil.nextInt(2)));
    }

    @EventHandler
    public void event0(PlayerMoveEvent event) {
        if (event.getFrom().getBlock().equals(event.getTo().getBlock()))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        Location getToLoc = event.getTo().getBlock().getLocation().clone().add(0.5, 0.5, 0.5);
        if (!user.hasPermission("elementals.gm.toggle"))
            if (user.getBase().getWorld().getName().equals("spawn"))
                user.getBase().setGameMode(GameMode.ADVENTURE);
            else
                user.getBase().setGameMode(GameMode.SURVIVAL);
    }

    @EventHandler
    public void event(PlayerTeleportEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        Location getToLoc = event.getTo().getBlock().getLocation().clone().add(0.5, 0.5, 0.5);
        if (!user.hasPermission("elementals.gm.toggle"))
            if (user.getBase().getWorld().getName().equals("spawn"))
                user.getBase().setGameMode(GameMode.ADVENTURE);
            else
                user.getBase().setGameMode(GameMode.SURVIVAL);
    }

    @EventHandler
    public void event(ServerListPingEvent event) {
        event.setMotd(ElementalsUtil.color(ElementalsUtil.getMotd()));
    }

    @EventHandler
    public void event3(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        if (!event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        Material clickedBlockType = event.getClickedBlock().getType();
        if (!(clickedBlockType.equals(Material.DISPENSER) || clickedBlockType.equals(Material.TNT)
                || clickedBlockType.equals(Material.COMMAND_BLOCK) || clickedBlockType.equals(Material.CHAIN_COMMAND_BLOCK)
                || clickedBlockType.equals(Material.REPEATING_COMMAND_BLOCK)
                || clickedBlockType.equals(Material.DAYLIGHT_DETECTOR)
                || clickedBlockType.equals(Material.REPEATER)
                || clickedBlockType.equals(Material.COMPARATOR)
                || clickedBlockType.equals(Material.BEACON)
                || Tag.TRAPDOORS.isTagged(clickedBlockType)))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }


    @EventHandler
    public void event0(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (ElementalsUtil.hasChatDelay(user) && (!user.hasPermission("elementals.chat.override"))) {
            event.getPlayer().sendMessage(ElementalsUtil.color("&cTrebuie sa astepti o secunda pentru a putea vorbi!"));
            event.setCancelled(true);
            return;
        }
        if (ElementalsUtil.isChatStopped() && (!user.hasPermission("elementals.chat.bypass"))) {
            event.getPlayer().sendMessage(ElementalsUtil.color("&cNu se poate vorbi acum!"));
            event.setCancelled(true);
            return;
        }
        ElementalsUtil.delayChatPlayer(user);
        List<String> names = ElementalsUtil.getPlayersNames();
        List<String> recipeNames = new ArrayList<>();
        String message = event.getMessage();
        for (String name : names) {
            if (message.contains("@" + name))
                recipeNames.add(name);
        }
        StringBuilder builder = new StringBuilder(message);
        if (!(message.endsWith(".") || message.endsWith("?") || message.endsWith("!") || message.endsWith(")")
                || message.endsWith("]") || message.endsWith(":D") || message.endsWith("xD") || message.endsWith("*")
                || message.endsWith("-") || message.endsWith("_") || message.endsWith(",") || message.endsWith("'")
                || message.endsWith("/") || message.endsWith("|") || message.endsWith("(") || message.endsWith("[")
                || message.endsWith("@") || message.endsWith("#") || message.endsWith("$") || message.endsWith("%")
                || message.endsWith("^") || message.endsWith("{") || message.endsWith("}") || message.endsWith(";")
                || message.endsWith(":") || message.endsWith("<") || message.endsWith(">") || message.endsWith("\\")
                || message.endsWith("~") || message.endsWith("~") || message.endsWith("=") || message.endsWith("+")
                || message.endsWith(":P") || message.endsWith(":O") || message.endsWith(":S") || message.endsWith(":3")
                || message.endsWith(":p") || message.endsWith(":o") || message.endsWith(":s") || message.endsWith("<3")
                || message.endsWith(":c")))
            builder.insert(message.length(), ".");
        if (!(message.startsWith(".") || message.startsWith("?") || message.startsWith("!") || message.startsWith(")")
                || message.startsWith("]") || message.startsWith(":D") || message.startsWith("xD")
                || message.startsWith("*") || message.startsWith("-") || message.startsWith("_")
                || message.startsWith(",") || message.startsWith("'") || message.startsWith("/")
                || message.startsWith("|") || message.startsWith("(") || message.startsWith("[")
                || message.startsWith("@") || message.startsWith("#") || message.startsWith("$")
                || message.startsWith("%") || message.startsWith("^") || message.startsWith("{")
                || message.startsWith("}") || message.startsWith(";") || message.startsWith(":")
                || message.startsWith("<") || message.startsWith(">") || message.startsWith("\\")
                || message.startsWith("~") || message.startsWith("~") || message.startsWith("=")
                || message.startsWith("+") || message.startsWith("<3")))
            builder.replace(0, 1, message.substring(0, 1).toUpperCase());
        String display = user.getBase().getDisplayName();
        String group = ElementalsX.getPermission().getPrimaryGroup(user.getBase());
        switch (group) {
            case "Helper":
            case "Builder":
            case "Moderator":
                event.setFormat(ElementalsUtil.color(display + " &e➽ &b") + "%2$s");
                break;
            case "Iron":
            case "Gold":
            case "Diamond":
                event.setFormat(ElementalsUtil.color(display + " &e➽ &f") + "%2$s");
                break;
            case "Rainbow":
            case "Admin":
            case "Owner":
                event.setFormat(ElementalsUtil.color(display + " &e➽ &a") + "%2$s");
                break;
            default:
                event.setFormat(ElementalsUtil.color(display + " &e➽ &7") + "%2$s");
                break;
        }
        ElementalsX.getOnlineUsers().forEach((User user$) -> {
            if (recipeNames.contains(user$.getBase().getName()) && user$.hasSounds())
                user$.getBase().playSound(user$.getBase().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            else if (user$.hasSounds())
                user$.getBase().playSound(user$.getBase().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);

        });

        event.setMessage(builder.toString());
    }

    @EventHandler
    public void event3(BlockBreakEvent event) {
        if (event.getBlock().getWorld().getName().equals("spawn"))
            return;
        if (event.isCancelled())
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (FieldUtil.isFieldAtLocation(event.getBlock().getLocation())) {
            Field field = FieldUtil.getFieldByLocation(event.getBlock().getLocation());
            if (!(field.isMember(user.getBase().getUniqueId()) || field.isOwner(user.getBase().getUniqueId())
                    || user.hasPermission("protection.override")))
                return;
        }
        if (!(event.getBlock().getType().equals(Material.DIAMOND_ORE)
                || event.getBlock().getType().equals(Material.EMERALD_ORE)
                || event.getBlock().getType().equals(Material.GOLD_ORE)
                || event.getBlock().getType().equals(Material.IRON_ORE)
                || event.getBlock().getType().equals(Material.LAPIS_ORE)))
            return;
        ElementalsUtil.removeTag(event.getBlock(), "found");
    }

    @EventHandler
    public void event(BlockPlaceEvent event) {
        if (event.getBlock().getWorld().getName().equals("spawn"))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (FieldUtil.isFieldAtLocation(event.getBlock().getLocation())) {
            Field field = FieldUtil.getFieldByLocation(event.getBlock().getLocation());
            if (!(field.isMember(user.getBase().getUniqueId()) || field.isOwner(user.getBase().getUniqueId())
                    || user.hasPermission("protection.override")))
                return;
        }
        if (!(event.getBlock().getType().equals(Material.DIAMOND_ORE)
                || event.getBlock().getType().equals(Material.EMERALD_ORE)
                || event.getBlock().getType().equals(Material.GOLD_ORE)
                || event.getBlock().getType().equals(Material.IRON_ORE)
                || event.getBlock().getType().equals(Material.LAPIS_ORE)))
            return;
        ElementalsUtil.setTag(event.getBlock(), "found");
    }

    @EventHandler
    public void event(BlockDamageEvent event) {
        if (event.getBlock().getWorld().getName().equals("spawn"))
            return;
        if (event.isCancelled())
            return;
        if (!(event.getBlock().getType().equals(Material.DIAMOND_ORE)
                || event.getBlock().getType().equals(Material.EMERALD_ORE)
                || event.getBlock().getType().equals(Material.GOLD_ORE)
                || event.getBlock().getType().equals(Material.IRON_ORE)
                || event.getBlock().getType().equals(Material.LAPIS_ORE)))
            return;
        if (ElementalsUtil.hasTag(event.getBlock(), "found"))
            return;
        ElementalsUtil.setTag(event.getBlock(), "found");
        int n = ElementalsUtil.foundBlocks(event.getBlock());
        switch (event.getBlock().getType()) {
            case DIAMOND_ORE:
                if (n == 1) {
                    Bukkit.broadcastMessage(ElementalsUtil.color(event.getPlayer().getDisplayName() + " &ba descoperit o bucata de Diamant."));
                } else {
                    Bukkit.broadcastMessage(ElementalsUtil.color(
                            event.getPlayer().getDisplayName() + " &ba descoperit " + n + " bucati de Diamant."));
                }
                break;
            case GOLD_ORE:
                if (n == 1) {
                    Bukkit.broadcastMessage(ElementalsUtil.color(event.getPlayer().getDisplayName() + " &ea descoperit o bucata de Aur."));
                } else {
                    Bukkit.broadcastMessage(ElementalsUtil.color(
                            event.getPlayer().getDisplayName() + " &ea descoperit " + n + " bucati de Aur."));
                }
                break;
            case IRON_ORE:
                if (n == 1) {
                    Bukkit.broadcastMessage(ElementalsUtil.color(event.getPlayer().getDisplayName() + " &7a descoperit o bucata de Fier."));
                } else {
                    Bukkit.broadcastMessage(ElementalsUtil.color(
                            event.getPlayer().getDisplayName() + " &7a descoperit " + n + " bucati de Fier."));
                }
                break;
            case LAPIS_ORE:
                if (n == 1) {
                    Bukkit.broadcastMessage(ElementalsUtil.color(event.getPlayer().getDisplayName() + " &9a descoperit o bucata de Lapis."));
                } else {
                    Bukkit.broadcastMessage(ElementalsUtil.color(
                            event.getPlayer().getDisplayName() + " &9a descoperit " + n + " bucati de Lapis."));
                }
                break;
            case EMERALD_ORE:
                if (n == 1) {
                    Bukkit.broadcastMessage(ElementalsUtil.color(event.getPlayer().getDisplayName() + " &aa descoperit o bucata de Emerald."));
                } else {
                    Bukkit.broadcastMessage(ElementalsUtil.color(
                            event.getPlayer().getDisplayName() + " &aa descoperit " + n + " bucati de Emerald."));
                }
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void event(BlockBreakEvent event) {
        if (!event.getPlayer().getInventory().getItemInMainHand().hasItemMeta())
            return;
        if (!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasDisplayName())
            return;
        if (event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        int yaw = ElementalsUtil.yawCorrection((int) event.getPlayer().getLocation().getYaw());
        int pitch = (int) event.getPlayer().getLocation().getPitch();
        Material blockType = event.getBlock().getType();
        String displayName = event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName();
        if (displayName.equals(ElementalsUtil.color("&bNaNo Tarnacop"))) {
            if (blockType.equals(Material.STONE) || blockType.equals(Material.COBBLESTONE)
                    || blockType.equals(Material.GOLD_ORE) || blockType.equals(Material.IRON_ORE)
                    || blockType.equals(Material.COAL_ORE) || blockType.equals(Material.LAPIS_ORE)
                    || blockType.equals(Material.LAPIS_BLOCK) || blockType.equals(Material.SANDSTONE)
                    || blockType.equals(Material.GOLD_BLOCK) || blockType.equals(Material.IRON_BLOCK)
                    || blockType.equals(Material.OBSIDIAN) || blockType.equals(Material.DIAMOND_ORE)
                    || blockType.equals(Material.COBBLESTONE_STAIRS) || blockType.equals(Material.ICE)
                    || blockType.equals(Material.NETHERRACK) || blockType.equals(Material.GLOWSTONE)
                    || blockType.equals(Material.GLASS)
                    || blockType.equals(Material.BRICK_STAIRS)
                    || blockType.equals(Material.NETHER_BRICK)
                    || blockType.equals(Material.NETHER_BRICK_STAIRS)
                    || blockType.equals(Material.END_STONE) || blockType.equals(Material.SANDSTONE_STAIRS)
                    || blockType.equals(Material.EMERALD_ORE) || blockType.equals(Material.EMERALD_BLOCK)
                    || blockType.equals(Material.MOSSY_COBBLESTONE)
                    || blockType.equals(Material.QUARTZ_BLOCK)
                    || blockType.equals(Material.QUARTZ_STAIRS)
                    || blockType.equals(Material.PRISMARINE)
                    || blockType.equals(Material.SEA_LANTERN) || blockType.equals(Material.BRICK) ||
                    blockType.equals(Material.COAL_BLOCK)
                    || blockType.equals(Material.PACKED_ICE) || blockType.equals(Material.RED_SANDSTONE)
                    || blockType.equals(Material.RED_SANDSTONE_STAIRS) || blockType.equals(Material.FURNACE)
                    || blockType.equals(Material.DISPENSER)
                    || blockType.equals(Material.REDSTONE_BLOCK) || blockType.equals(Material.HOPPER)
                    || blockType.equals(Material.DROPPER)
                    || blockType.equals(Material.IRON_TRAPDOOR) || blockType.equals(Material.BEACON))
                ElementalsUtil.breakNano(user, pitch, yaw, event.getBlock(), NanoType.PICKAXE);
        } else if (displayName.equals(ElementalsUtil.color("&bNaNo Lopata"))) {
            if (blockType.equals(Material.GRASS) || blockType.equals(Material.DIRT) || blockType.equals(Material.SAND)
                    || blockType.equals(Material.GRAVEL) || blockType.equals(Material.SOUL_SAND)
                    || blockType.equals(Material.MYCELIUM) || blockType.equals(Material.CLAY)
                    || blockType.equals(Material.SNOW) || blockType.equals(Material.SNOW_BLOCK))
                ElementalsUtil.breakNano(user, pitch, yaw, event.getBlock(), NanoType.SPADE);
        } else if (displayName.equals(ElementalsUtil.color("&bNaNo Topor"))) {
            if (Tag.LOGS.isTagged(blockType)
                    || Tag.WOODEN_BUTTONS.isTagged(blockType)
                    || Tag.WOODEN_DOORS.isTagged(blockType)
                    || Tag.WOODEN_PRESSURE_PLATES.isTagged(blockType)
                    || Tag.WOODEN_BUTTONS.isTagged(blockType)
                    || Tag.WOODEN_SLABS.isTagged(blockType)
                    || Tag.WOODEN_STAIRS.isTagged(blockType)
                    || Tag.WOODEN_TRAPDOORS.isTagged(blockType)
                    || Tag.SIGNS.isTagged(blockType)
                    || Tag.STANDING_SIGNS.isTagged(blockType)
                    || Tag.WALL_SIGNS.isTagged(blockType)
                    || blockType == Material.CRAFTING_TABLE
                    || blockType == Material.NOTE_BLOCK)
                ElementalsUtil.breakNano(user, pitch, yaw, event.getBlock(), NanoType.AXE);
        }
    }

    @EventHandler
    public void event(BlockBurnEvent event) {
        if (!event.getBlock().getWorld().getName().equals("spawn"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(BlockIgniteEvent event) {
        if (!event.getBlock().getWorld().getName().equals("spawn"))
            return;
        if (event.getCause().equals(IgniteCause.FLINT_AND_STEEL)) {
            User user = ElementalsX.getUser(event.getPlayer());
            if (user.hasPermission("elementals.admin"))
                return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void event(BlockSpreadEvent event) {
        if (!event.getBlock().getWorld().getName().equals("spawn"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(EntityDamageEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (!event.getEntity().getType().equals(EntityType.PLAYER))
            return;
        User user = ElementalsX.getUser(event.getEntity().getUniqueId());
        if (ElementalsUtil.hasRandomTpCmdDelay(user)) {
            ElementalsUtil.cancelRandomTpCmd(user);
            ElementalsUtil.removeTandomTpCmdDelay(user);
        }
        if (!event.getEntity().getWorld().getName().equals("spawn"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(HangingBreakByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (!event.getEntity().getWorld().getName().equals("spawn"))
            return;
        if (event.getRemover().getType().equals(EntityType.PLAYER)) {
            User user = ElementalsX.getUser((Player) event.getRemover());
            if (!user.hasPermission("elementals.admin"))
                event.setCancelled(true);
        } else if (event.getRemover().getType().equals(EntityType.ARROW)
                || event.getRemover().getType().equals(EntityType.EGG)
                || event.getRemover().getType().equals(EntityType.ENDER_PEARL)
                || event.getRemover().getType().equals(EntityType.SNOWBALL)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void event(PlayerArmorStandManipulateEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked()))
            return;
        if (!event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(HangingPlaceEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (!event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(LeavesDecayEvent event) {
        if (event.getBlock().getWorld().getName().equals("spawn")) {
            event.setCancelled(true);
            return;
        }
        double random = ElementalsUtil.nextDouble(45);
        if (random <= 1)
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
                    new ItemStack(Material.GOLDEN_APPLE));
        else if (random > 1 && random <= 3)
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
                    new ItemStack(Material.APPLE));
    }

    @EventHandler
    public void event(PlayerBucketEmptyEvent event) {
        if (!event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerBucketFillEvent event) {
        if (!event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(PlayerCommandPreprocessEvent event) {
        User user = ElementalsX.getUser(event.getPlayer());
        if (event.getMessage().startsWith("/minecraft") || event.getMessage().startsWith("/?")
                || event.getMessage().startsWith("/bukkit") || event.getMessage().startsWith("/about")) {
            if (user.hasPermission("elementals.command.minecraft"))
                return;
            event.getPlayer().sendMessage(ElementalsUtil.color("&4Aici nu sunt bug-uri! :)"));
            event.setCancelled(true);
        } else if (event.getMessage().startsWith("/version") || event.getMessage().startsWith("/ver")
                || event.getMessage().startsWith("/icanhasbukkit")) {
            event.getPlayer().sendMessage(ElementalsUtil.color(
                    "&aServerul ruleaza pe Spigot versiunea 1.14.4-R0.1-SNAPSHOT!"));
            event.setCancelled(true);
        } else if (event.getMessage().startsWith("/spawn")) {
            if (!event.getPlayer().isInsideVehicle())
                return;
            event.getPlayer().sendMessage(ElementalsUtil.color("&cNu poti folosi comanda cat timp esti intr-un vehicul!"));
            event.setCancelled(true);
        } else if (event.getMessage().startsWith("/sethome")) {
            if (event.getPlayer().getWorld().getName().equals("spawn")) {
                if (user.hasPermission("elementals.command.minecraft"))
                    return;
                event.getPlayer().sendMessage(ElementalsUtil.color("&cNu poti folosi comanda aceasta aici!"));
                event.setCancelled(true);
            } else if (FieldUtil.isFieldAtLocation(event.getPlayer().getLocation()))
                if (!(FieldUtil.getFieldByLocation(event.getPlayer().getLocation())
                        .isMember(event.getPlayer().getUniqueId())
                        || FieldUtil.getFieldByLocation(event.getPlayer().getLocation()).isOwner(
                        event.getPlayer().getUniqueId())
                        || user.hasPermission("protection.override"))) {
                    event.getPlayer().sendMessage(ElementalsUtil.color("&cNu poti folosi comanda aceasta aici!"));
                    event.setCancelled(true);
                }
        }
    }

    /*
    @EventHandler
    public void event(PlayerDeathEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        User user = ElementalsX.getUser(event.getEntity());
        double random;
        if (!user.getLastDamageCause().equals(DamageCause.SUICIDE)) {
            random = ElementalsUtil.nextDouble(100);
            if (random <= 33.3) {
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                meta.setOwningPlayer(user.getBase());
                head.setItemMeta(meta);
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), head);
            }
        }
        event.setKeepInventory(true);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(event.getEntity().getFirstPlayed()));
        cal.add(Calendar.DATE, 5);
        if (new Date().before(cal.getTime())) {
            event.setKeepLevel(true);
            event.setDroppedExp(0);
            event.getEntity().sendMessage(ElementalsUtil.color("&eInventarul tau a fost salvat de &5Ender Dragon&e!"));
            event.getEntity().sendMessage(ElementalsUtil.color("&cNu poti pierde inventarul timp de 5 zile de la prima intrare."));
            return;
        }
        List<ItemStack> items = new ArrayList<>();
        for (int n = 0; n < event.getEntity().getInventory().getStorageContents().length; n++) {
            ItemStack item = event.getEntity().getInventory().getStorageContents()[n];
            if (item == null)
                continue;
            if (item.getType().equals(Material.AIR))
                continue;
            if (item.containsEnchantment(Enchantment.VANISHING_CURSE))
                continue;
            random = ElementalsUtil.nextDouble(100);
            if (random <= 66.6) {
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), item);
            } else {
                items.add(item);
            }
        }
        ItemStack helmet = event.getEntity().getInventory().getHelmet();
        if (helmet != null) {
            if (!helmet.getType().equals(Material.AIR)) {
                if (!helmet.containsEnchantment(Enchantment.VANISHING_CURSE)) {
                    random = ElementalsUtil.nextDouble(100);
                    if (random <= 66.6)
                        event.getEntity().getWorld().dropItem(event.getEntity().getLocation(),
                               helmet);
                    else
                        helmet = null;
                }
            }
        }
        ItemStack boots = event.getEntity().getInventory().getBoots();
        if (boots != null) {
            if (!boots.getType().equals(Material.AIR)) {
                if (!boots.containsEnchantment(Enchantment.VANISHING_CURSE)) {
                    random = ElementalsUtil.nextDouble(100);
                    if (random <= 66.6)
                        event.getEntity().getWorld().dropItem(event.getEntity().getLocation(),
                                boots);
                    else
                        boots = null;
                }
            }
        }
        ItemStack chestplate = event.getEntity().getInventory().getChestplate();
        if (chestplate != null) {
            if (!chestplate.getType().equals(Material.AIR)) {
                if (!chestplate.containsEnchantment(Enchantment.VANISHING_CURSE)) {
                    random = ElementalsUtil.nextDouble(100);
                    if (random <= 66.6)
                        event.getEntity().getWorld().dropItem(event.getEntity().getLocation(),
                                chestplate);
                    else
                        chestplate = null;
                }
            }
        }
        ItemStack leggings = event.getEntity().getInventory().getLeggings();
        if (leggings != null) {
            if (!leggings.getType().equals(Material.AIR)) {
                if (!leggings.containsEnchantment(Enchantment.VANISHING_CURSE)) {
                    random = ElementalsUtil.nextDouble(100);
                    if (random <= 66.6)
                        event.getEntity().getWorld().dropItem(event.getEntity().getLocation(),
                                leggings);
                    else
                        leggings = null;
                }
            }
        }
        ItemStack offhand = event.getEntity().getInventory().getItemInOffHand();
        if (offhand != null) {
            if (!offhand.getType().equals(Material.AIR)) {
                if (!offhand.containsEnchantment(Enchantment.VANISHING_CURSE)) {
                    random = ElementalsUtil.nextDouble(100);
                    if (random <= 66.6)
                        event.getEntity().getWorld().dropItem(event.getEntity().getLocation(),
                                offhand);
                    else
                        offhand = null;
                }
            }
        }
        event.getEntity().getInventory().clear();
        PlayerInventory entityInv = event.getEntity().getInventory();
        items.forEach((ItemStack item) -> entityInv.setItem(items.indexOf(item), item));
        entityInv.setHelmet(helmet);
        entityInv.setChestplate(chestplate);
        entityInv.setLeggings(leggings);
        entityInv.setBoots(boots);
        entityInv.setItemInOffHand(offhand);
        items.clear();
    }
     */

    @EventHandler
    public void event(PlayerInteractEntityEvent event) {
        if (!event.getRightClicked().getWorld().getName().equals("spawn"))
            return;
        if (!event.getRightClicked().getType().equals(EntityType.ITEM_FRAME))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event0(PlayerInteractEntityEvent event) {
        if (!event.getRightClicked().getType().equals(EntityType.PARROT))
            return;
        Parrot parrot = (Parrot) event.getRightClicked();
        if (parrot.getOwner() == null)
            return;
        if (parrot.getOwner().getUniqueId().equals(event.getPlayer().getUniqueId()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        if (!event.getClickedBlock().getWorld().getName().equals("spawn"))
            return;
        if (event.getPlayer().getInventory().getItemInMainHand() == null)
            return;
        if (!(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.BAT_SPAWN_EGG)
                || event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.ARMOR_STAND)))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerJoinEvent event) {
        ElementalsX.createUser(event.getPlayer());
        ElementalsX.getOnlineUsers().forEach((User user) -> {
            if (user.hasSounds())
                user.getBase().playSound(user.getBase().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(PlayerLoginEvent event) {
        if (event.getPlayer().getName().contains("&"))
            event.setResult(Result.KICK_OTHER);
        if (event.getResult().equals(Result.KICK_BANNED)) {
            BanEntry entry = Bukkit.getBanList(Type.NAME).getBanEntry(event.getPlayer().getName());
            event.setKickMessage(ElementalsUtil.color("\n" + "&a[&6PikaCraft&a]\n" + "&aNu te poti conecta!\n" + "&6Motiv: &c"
                    + entry.getReason() + " &e@ " + entry.getSource() + "\n"
                    + ((entry.getExpiration() == null) ? ""
                    : ("&9Expira pe: &e"
                    + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(entry.getExpiration()) + "\n"))
                    + "&ahttps://www.pikacraftmc.ro/"));
        }
        if (event.getResult().equals(Result.KICK_WHITELIST))
            event.setKickMessage(ElementalsUtil.color("\n" + "&a[&6PikaCraft&a]\n" + "&aNu te poti conecta!\n"
                    + "&aServerul este in mentenanta!\n &cReveniti mai tarziu!"));
        else if (event.getResult().equals(Result.KICK_FULL))
            event.setKickMessage(ElementalsUtil.color(
                    "\n" + "&a[&6PikaCraft&a]\n" + "&aNu te poti conecta!\n" + "&eServerul este plin."));
    }

    @EventHandler
    public void event(PlayerMoveEvent event) {
        if (event.getFrom().getBlock().equals(event.getTo().getBlock()))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        Player player = event.getPlayer();
        User user = ElementalsX.getUser(player);
        World spawn = Bukkit.getWorld("spawn");
        World end = Bukkit.getWorld("world_the_end");
        if (!event.getFrom().getWorld().getName().equals("spawn"))
            return;
        if (user.isInPvp()) {
            if (player.isFlying()) {
                player.setFlying(false);
                player.setAllowFlight(false);
                user.getBase().sendMessage(ElementalsUtil.color("&cNu poti zbura cat timp esti in pvp!"));
            }
        }
    }

    @EventHandler
    public void event(EntityExplodeEvent event) {
        if (!event.getEntity().getWorld().getName().equals("spawn"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerQuitEvent event) {
        User user = ElementalsX.getUser(event.getPlayer());
        if (user.isInPvp())
            user.getBase().setHealth(0);
        ElementalsX.removeUser(event.getPlayer());
        ElementalsX.getOnlineUsers().forEach((User $user) -> {
            if ($user.hasSounds())
                $user.getBase().playSound($user.getBase().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
        });
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void event(VotifierEvent event) {
        Optional<Player> player = (Optional<Player>) ElementalsUtil.getPlayer(event.getVote().getUsername());
        if (player.isPresent()) {
            player.get().getInventory().addItem(new ItemStack(Material.DIAMOND, 5));
            ItemStack key = new ItemStack(Material.PRISMARINE_SHARD);
            ItemMeta meta = key.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color("&aCrate Key"));
            key.setItemMeta(meta);
            player.get().getInventory().addItem(key);
            ElementalsX.getVault().depositPlayer(player.get(), 500);
            Bukkit.getPlayer(event.getVote().getUsername()).updateInventory();
            // TODO effect la warp vote
            Bukkit.broadcastMessage(ElementalsUtil.color(ChatColor.WHITE + "[" + ChatColor.GOLD + "/warp vote" + ChatColor.WHITE
                    + "] &aJucatorul &9" + event.getVote().getUsername()
                    + " &aa votat pentru server si a primit un premiu! Multumim! &e:)"));
        } else
            Bukkit.broadcastMessage(ElementalsUtil.color("&aJucatorul " + event.getVote().getUsername()
                    + " nu este online si nu poate primi premiul. &c:("));
    }

    @EventHandler
    public void event0(BlockBreakEvent event) {
        if (!event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        User user = ElementalsX.getUser(event.getPlayer());
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);

    }

    @EventHandler
    public void event0(EntityDamageByEntityEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER))
            return;
        if (!event.getDamager().getType().equals(EntityType.PLAYER))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getDamager()))
            return;
        if (event.isCancelled())
            return;
        if (event.getEntity().getWorld().getName().equals("spawn"))
            return;
        if (FieldUtil.isFieldAtLocation(event.getEntity().getLocation()))
            return;
        User damaged = ElementalsX.getUser((Player) event.getEntity());
        User damager = ElementalsX.getUser((Player) event.getDamager());
        if (!damaged.getBase().isOp()) {
            if (!damaged.isInPvp())
                damaged.getBase().sendMessage(ElementalsUtil.color(
                        "&6Esti in &cPvP&6! Nu te deconecta timp de 10 secunde altfel pierzi inventarul!"));
            damaged.setPvpTicks(11);
            damaged.togglePvp(true);
            damaged.getBase().setFlying(false);
            damaged.getBase().setAllowFlight(false);
        }
        if (!damager.getBase().isOp()) {
            if (!damager.isInPvp())
                damager.getBase().sendMessage(ElementalsUtil.color(
                        "&6Esti in &cPvP&6! Nu te deconecta timp de 10 secunde altfel pierzi inventarul!"));
            damager.setPvpTicks(11);
            damager.togglePvp(true);
            damager.getBase().setFlying(false);
            damager.getBase().setAllowFlight(false);
        }
    }

    @EventHandler
    public static void easterEgg(EntityDamageEvent event) {
        if (!event.getEntity().getType().equals(EntityType.CHICKEN))
            return;
        if (event.getEntity().getCustomName() == null)
            return;
        if (!event.getEntity().getCustomName().equals("Gina"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        if (event.getClickedInventory() == null)
            return;
        if (!event.getClickedInventory().getType().equals(InventoryType.ANVIL))
            return;
        if (!event.getSlotType().equals(SlotType.RESULT))
            return;
        if (event.getClickedInventory().getItem(0) == null)
            return;
        if (!event.getClickedInventory().getItem(0).hasItemMeta())
            return;
        if (!event.getClickedInventory().getItem(0).getItemMeta().hasDisplayName())
            return;
        String displayName = event.getClickedInventory().getItem(0).getItemMeta().getDisplayName();
        if (displayName.equals(ElementalsUtil.color("&bNaNo Tarnacop"))) {
            ItemStack is = event.getClickedInventory().getItem(2);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color("&bNaNo Tarnacop"));
            is.setItemMeta(meta);
            event.setCurrentItem(is);
        } else if (displayName.equals(ElementalsUtil.color("&bNaNo Sabie"))) {
            ItemStack is = event.getClickedInventory().getItem(2);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color("&bNaNo Sabie"));
            is.setItemMeta(meta);
            event.setCurrentItem(is);
        } else if (displayName.equals(ElementalsUtil.color("&bNaNo Lopata"))) {
            ItemStack is = event.getClickedInventory().getItem(2);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color("&bNaNo Lopata"));
            is.setItemMeta(meta);
            event.setCurrentItem(is);
        } else if (displayName.equals(ElementalsUtil.color("&bNaNo Topor"))) {
            ItemStack is = event.getClickedInventory().getItem(2);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color("&bNaNo Topor"));
            is.setItemMeta(meta);
            event.setCurrentItem(is);
        } else if (displayName.equals(ElementalsUtil.color("&bNaNo Casca"))) {
            ItemStack is = event.getClickedInventory().getItem(2);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color("&bNaNo Casca"));
            is.setItemMeta(meta);
            event.setCurrentItem(is);
        } else if (displayName.equals(ElementalsUtil.color("&bNaNo Armura"))) {
            ItemStack is = event.getClickedInventory().getItem(2);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color("&bNaNo Armura"));
            is.setItemMeta(meta);
            event.setCurrentItem(is);
        } else if (displayName.equals(ElementalsUtil.color("&bNaNo Pantaloni"))) {
            ItemStack is = event.getClickedInventory().getItem(2);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color("&bNaNo Pantaloni"));
            is.setItemMeta(meta);
            event.setCurrentItem(is);
        } else if (displayName.equals(ElementalsUtil.color("&bNaNo Pantofi"))) {
            ItemStack is = event.getClickedInventory().getItem(2);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color("&bNaNo Pantofi"));
            is.setItemMeta(meta);
            event.setCurrentItem(is);
        } else if (displayName.equals(ElementalsUtil.color("&bNaNo Arc"))) {
            ItemStack is = event.getClickedInventory().getItem(2);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color("&bNaNo Arc"));
            is.setItemMeta(meta);
            event.setCurrentItem(is);
        } else if (displayName.equals(ElementalsUtil.color("&4Lava King Sword"))) {
            ItemStack is = event.getClickedInventory().getItem(2);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color("&4Lava King Sword"));
            is.setItemMeta(meta);
            event.setCurrentItem(is);
        } else if (displayName.equals(ElementalsUtil.color("&1Ice King Sword"))) {
            ItemStack is = event.getClickedInventory().getItem(2);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color("&1Ice King Sword"));
            is.setItemMeta(meta);
            event.setCurrentItem(is);
        } else if (displayName.equals(ElementalsUtil.color("&5End King Sword"))) {
            ItemStack is = event.getClickedInventory().getItem(2);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color("&5End King Sword"));
            is.setItemMeta(meta);
            event.setCurrentItem(is);
        } else if (displayName.startsWith(ChatColor.COLOR_CHAR + "")) {
            ItemStack is = event.getClickedInventory().getItem(2);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ElementalsUtil.color(displayName));
            is.setItemMeta(meta);
            event.setCurrentItem(is);
        } else {
            ItemMeta meta = event.getClickedInventory().getItem(2).getItemMeta();
            ItemMeta meta2 = event.getCurrentItem().getItemMeta();
            if (meta.hasLore())
                meta2.setLore(meta.getLore());
            event.getCurrentItem().setItemMeta(meta2);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event1(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        Block block = event.getBlock();
        Player player = event.getPlayer();
        double random = ElementalsUtil.nextDouble(100);
        ItemStack offhandItem = player.getInventory().getItemInOffHand();
        if (offhandItem != null && offhandItem.getType().equals(Material.TOTEM_OF_UNDYING)) { //TODO figure this out
            if (random > 33.3)
                return;
        } else if (random > 20)
            return;
        if (!block.getType().equals(Material.SPAWNER))
            return;
        if (player.getGameMode().equals(GameMode.CREATIVE))
            return;
        UUID uuid = player.getUniqueId();
        Location loc = block.getLocation();
        if (FieldUtil.isFieldAtLocation(loc)) {
            Field field = FieldUtil.getFieldByLocation(loc);
            if (!(field.isOwner(uuid) || field.isMember(uuid) || player.isOp()))
                return;
        }
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem == null)
            return;
        if (!handItem.containsEnchantment(Enchantment.SILK_TOUCH))
            return;
        event.setExpToDrop(0);
        ItemStack spawner = new ItemStack(Material.SPAWNER);
        ItemMeta meta = spawner.getItemMeta();
        meta.setDisplayName(ElementalsUtil.color(ElementalsUtil.entityToName(((CreatureSpawner) block.getState()).getSpawnedType())));
        spawner.setItemMeta(meta);
        block.getWorld().dropItem(loc.add(0.5, 0.25, 0.5), spawner);
    }

    @EventHandler
    public void event4(BlockPlaceEvent event) {
        if (!event.getBlockPlaced().getType().equals(Material.SPAWNER))
            return;
        ItemStack handItem = event.getItemInHand();
        if (handItem == null)
            return;
        if (!handItem.hasItemMeta())
            return;
        ItemMeta meta = handItem.getItemMeta();
        if (!meta.hasDisplayName())
            return;
        ((CreatureSpawner) event.getBlock().getState())
                .setSpawnedType(ElementalsUtil.nameToEntity(meta.getDisplayName()));
    }

    @EventHandler
    public void event1(EntityDamageEvent event) {
        if (!event.getEntity().getType().equals(EntityType.ARMOR_STAND))
            return;
        if (!event.getCause().equals(DamageCause.FIRE_TICK))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event1(PlayerInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
            return;
        if (event.getPlayer().getInventory().getItemInMainHand() == null)
            return;
        if (!event.getPlayer().getInventory().getItemInMainHand().hasItemMeta())
            return;
        if (!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasDisplayName())
            return;
        if (!event.getPlayer().isSneaking())
            return;
        if (interactList.contains(event.getPlayer().getUniqueId()))
            return;
        interactList.add(event.getPlayer().getUniqueId());
        User user = ElementalsX.getUser(event.getPlayer());
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                .equals(ElementalsUtil.color("&bNaNo Tarnacop"))) {
            if (user.getNanoPickType().equals(ElementalsX.Nano.A)) {
                user.setNanoPickType(ElementalsX.Nano.B);
                user.getBase().sendMessage(ElementalsUtil.color("&bNaNo Tarnacop &f-> &61x3"));
            } else if (user.getNanoPickType().equals(ElementalsX.Nano.B)) {
                user.setNanoPickType(ElementalsX.Nano.C);
                user.getBase().sendMessage(ElementalsUtil.color("&bNaNo Tarnacop &f-> &63x1"));
            } else if (user.getNanoPickType().equals(ElementalsX.Nano.C)) {
                user.setNanoPickType(ElementalsX.Nano.D);
                user.getBase().sendMessage(ElementalsUtil.color("&bNaNo Tarnacop &f-> &63x3"));
            } else if (user.getNanoPickType().equals(ElementalsX.Nano.D)) {
                user.setNanoPickType(ElementalsX.Nano.A);
                user.getBase().sendMessage(ElementalsUtil.color("&bNaNo Tarnacop &f-> &61x1"));
            }
        } else if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                .equals(ElementalsUtil.color("&bNaNo Lopata"))) {
            if (user.getNanoSpadeType().equals(ElementalsX.Nano.A)) {
                user.setNanoSpadeType(ElementalsX.Nano.B);
                user.getBase().sendMessage(ElementalsUtil.color("&bNaNo Lopata &f-> &61x3"));
            } else if (user.getNanoSpadeType().equals(ElementalsX.Nano.B)) {
                user.setNanoSpadeType(ElementalsX.Nano.C);
                user.getBase().sendMessage(ElementalsUtil.color("&bNaNo Lopata &f-> &63x1"));
            } else if (user.getNanoSpadeType().equals(ElementalsX.Nano.C)) {
                user.setNanoSpadeType(ElementalsX.Nano.D);
                user.getBase().sendMessage(ElementalsUtil.color("&bNaNo Lopata &f-> &63x3"));
            } else if (user.getNanoSpadeType().equals(ElementalsX.Nano.D)) {
                user.setNanoSpadeType(ElementalsX.Nano.A);
                user.getBase().sendMessage(ElementalsUtil.color("&bNaNo Lopata &f-> &61x1"));
            }
        } else if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                .equals(ElementalsUtil.color("&bNaNo Topor"))) {
            if (user.getNanoAxeType().equals(ElementalsX.Nano.A)) {
                user.setNanoAxeType(ElementalsX.Nano.B);
                user.getBase().sendMessage(ElementalsUtil.color("&bNaNo Topor &f-> &61x3"));
            } else if (user.getNanoAxeType().equals(ElementalsX.Nano.B)) {
                user.setNanoAxeType(ElementalsX.Nano.C);
                user.getBase().sendMessage(ElementalsUtil.color("&bNaNo Topor &f-> &63x1"));
            } else if (user.getNanoAxeType().equals(ElementalsX.Nano.C)) {
                user.setNanoAxeType(ElementalsX.Nano.D);
                user.getBase().sendMessage(ElementalsUtil.color("&bNaNo Topor &f-> &63x3"));
            } else if (user.getNanoAxeType().equals(ElementalsX.Nano.D)) {
                user.setNanoAxeType(ElementalsX.Nano.A);
                user.getBase().sendMessage(ElementalsUtil.color("&bNaNo Topor &f-> &61x1"));
            }
        }
        Bukkit.getScheduler().runTaskLater(ElementalsX.get(), () -> interactList.remove(event.getPlayer().getUniqueId()),
                5L);
    }

    @EventHandler
    public void event2(BlockBreakEvent event) {
        if (!event.getBlock().getWorld().getName().equals("world"))
            return;
        if (!Tag.LEAVES.isTagged(event.getBlock().getType()))
            return;
        if (FieldUtil.isFieldAtLocation(event.getBlock().getLocation())) {
            Field field = FieldUtil.getFieldByLocation(event.getBlock().getLocation());
            if (!(field.isOwner(event.getPlayer().getUniqueId()) || field.isMember(event.getPlayer().getUniqueId())
                    || event.getPlayer().isOp()))
                return;
        }
        double random = ElementalsUtil.nextDouble(45);
        if (random <= 1)
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
                    new ItemStack(Material.GOLDEN_APPLE));
        else if (random > 1 && random <= 3)
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
                    new ItemStack(Material.APPLE));
    }
}
