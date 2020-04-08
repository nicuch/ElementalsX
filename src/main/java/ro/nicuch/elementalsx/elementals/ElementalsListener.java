package ro.nicuch.elementalsx.elementals;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.protection.Field;
import ro.nicuch.elementalsx.protection.FieldUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ElementalsListener implements Listener {
    private final static List<UUID> interactList = new ArrayList<>();

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!item.hasItemMeta())
            return;
        ItemMeta itemMeta = item.getItemMeta();
        if (!itemMeta.hasDisplayName())
            return;
        String itemName = itemMeta.getDisplayName();
        Material itemType = item.getType();
        Player player = event.getPlayer();
        if (itemType == Material.CARROT) {
            if (itemName.equals(ElementalsUtil.color("&b&lRARE &fCarrot"))) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 45, 0));
            } else if (itemName.equals(ElementalsUtil.color("&c&lEPIC &fCarrot"))) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 90, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 150, 0));
            } else if (itemName.equals(ElementalsUtil.color("&6&lLEGENDARY &fCarrot"))) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 180, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 300, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 60, 0));
            }
        } else if (itemType == Material.POTATO) {
            if (itemName.equals(ElementalsUtil.color("&b&lRARE &fPotato"))) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 60, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 90, 0));
            } else if (itemName.equals(ElementalsUtil.color("&c&lEPIC &fPotato"))) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 120, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 180, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 60, 1));
            } else if (itemName.equals(ElementalsUtil.color("&6&lLEGENDARY &fPotato"))) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 300, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 300, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 120, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 180, 0));
            }
        } else if (itemType == Material.BREAD) {
            if (itemName.equals(ElementalsUtil.color("&b&lRARE &fBread"))) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 120, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 120, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 1));
            } else if (itemName.equals(ElementalsUtil.color("&c&lEPIC &fBread"))) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 240, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 240, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 240, 3));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 240, 0));
            } else if (itemName.equals(ElementalsUtil.color("&6&lLEGENDARY &fBread"))) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 360, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 360, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 360, 7));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 360, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 20 * 120, 2));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void event(WeaponDamageEntityEvent event) {
        if (!event.getVictim().getWorld().getName().equals("spawn"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(EntityDamageEvent event) {
        if (!event.getEntity().getWorld().getName().equals("spawn"))
            return;
        if (event.getEntityType() != EntityType.PLAYER)
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(CreatureSpawnEvent event) {
        String worldName = event.getLocation().getWorld().getName();
        if (!(worldName.equals("spawn")
                || worldName.equals("dungeon")))
            return;
        if (event.getSpawnReason() != SpawnReason.TRAP
                || event.getSpawnReason() != SpawnReason.REINFORCEMENTS)
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event1(PlayerTeleportEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        String worldName = event.getTo().getWorld().getName();
        if (!(worldName.equals("spawn") || worldName.equals("dungeon")))
            return;
        if (event.getCause() != TeleportCause.CHORUS_FRUIT
                || event.getCause() != TeleportCause.ENDER_PEARL)
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event0(PlayerMoveEvent event) {
        if (event.getFrom().getBlock().equals(event.getTo().getBlock()))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (!user.hasPermission("elementals.gm.toggle")) {
            String worldName = event.getTo().getWorld().getName();
            if (worldName.equals("spawn") || worldName.equals("dungeon"))
                user.getBase().setGameMode(GameMode.ADVENTURE);
            else
                user.getBase().setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler
    public void event(PlayerTeleportEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (!user.hasPermission("elementals.gm.toggle")) {
            String worldName = event.getTo().getWorld().getName();
            if (worldName.equals("spawn") || worldName.equals("dungeon"))
                user.getBase().setGameMode(GameMode.ADVENTURE);
            else
                user.getBase().setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler
    public void event(ServerListPingEvent event) {
        event.setMotd(ElementalsUtil.color(ElementalsUtil.getMotd()));
    }

    @EventHandler
    public void event3(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        String worldName = event.getPlayer().getWorld().getName();
        if (!(worldName.equals("spawn") || worldName.equals("dungeon")))
            return;
        Material clickedBlockType = event.getClickedBlock().getType();
        if (clickedBlockType != Material.FLOWER_POT
                || clickedBlockType != Material.JUKEBOX
                || clickedBlockType != Material.NOTE_BLOCK
                || clickedBlockType != Material.REPEATER
                || clickedBlockType != Material.COMPARATOR
                || clickedBlockType != Material.BEACON
                || clickedBlockType != Material.CAMPFIRE
                || clickedBlockType != Material.TURTLE_EGG
                || !Tag.BEDS.isTagged(clickedBlockType))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void event0(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String level = PlaceholderAPI.setPlaceholders(player, "%math_{mcmmo_power_level}/15[precision:0]%");
        String dragon = PlaceholderAPI.setPlaceholders(player, "%dragonslayer_prefix%");
        ElementalsX.getOnlineUsers().stream().filter(User::hasSounds).forEach(user -> user.getBase().playSound(user.getBase().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f));
        event.setFormat(event.getFormat().replace("{LEVEL}", level).replace("{DRAGON}", dragon));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void event3(BlockBreakEvent event) {
        String worldname = event.getBlock().getWorld().getName();
        if (worldname.equals("spawn") || worldname.equals("dungeon"))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (FieldUtil.isFieldAtLocation(event.getBlock().getLocation())) {
            Field field = FieldUtil.getFieldByLocation(event.getBlock().getLocation());
            if (!(field.isMember(user.getBase().getUniqueId()) || field.isOwner(user.getBase().getUniqueId())
                    || user.hasPermission("protection.override")))
                return;
        }
        if (!(event.getBlock().getType() == Material.DIAMOND_ORE
                || event.getBlock().getType() == Material.EMERALD_ORE
                || event.getBlock().getType() == Material.GOLD_ORE
                || event.getBlock().getType() == Material.IRON_ORE
                || event.getBlock().getType() == Material.LAPIS_ORE))
            return;
        ElementalsUtil.removeTag(event.getBlock(), "found");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void event0(BlockPlaceEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        if (!(worldName.equals("spawn") || worldName.equals("dungeon")))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void event(BlockPlaceEvent event) {
        String worldname = event.getBlock().getWorld().getName();
        if (worldname.equals("spawn") || worldname.equals("dungeon"))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (FieldUtil.isFieldAtLocation(event.getBlock().getLocation())) {
            Field field = FieldUtil.getFieldByLocation(event.getBlock().getLocation());
            if (!(field.isMember(user.getBase().getUniqueId()) || field.isOwner(user.getBase().getUniqueId())
                    || user.hasPermission("protection.override")))
                return;
        }
        if (!(event.getBlock().getType() == Material.DIAMOND_ORE
                || event.getBlock().getType() == Material.EMERALD_ORE
                || event.getBlock().getType() == Material.GOLD_ORE
                || event.getBlock().getType() == Material.IRON_ORE
                || event.getBlock().getType() == Material.LAPIS_ORE))
            return;
        ElementalsUtil.setTag(event.getBlock(), "found");
    }

    @EventHandler(ignoreCancelled = true)
    public void event(BlockDamageEvent event) {
        String worldname = event.getBlock().getWorld().getName();
        if (worldname.equals("spawn") || worldname.equals("dungeon"))
            return;
        if (!(event.getBlock().getType() == Material.DIAMOND_ORE
                || event.getBlock().getType() == Material.EMERALD_ORE
                || event.getBlock().getType() == Material.GOLD_ORE
                || event.getBlock().getType() == Material.IRON_ORE
                || event.getBlock().getType() == Material.LAPIS_ORE))
            return;
        if (ElementalsUtil.hasTag(event.getBlock(), "found"))
            return;
        ElementalsUtil.setTag(event.getBlock(), "found");
        int n = ElementalsUtil.foundBlocks(event.getBlock());
        switch (event.getBlock().getType()) {
            case DIAMOND_ORE:
                if (n == 1) {
                    Bukkit.broadcastMessage(ElementalsUtil.color("&8[&7Info&8] &f&o" + event.getPlayer().getName() + " &b&oa descoperit o bucata de Diamant."));
                } else {
                    Bukkit.broadcastMessage(ElementalsUtil.color("&8[&7Info&8] &f&o" +
                            event.getPlayer().getName() + " &b&oa descoperit " + n + " bucati de Diamant."));
                }
                break;
            case GOLD_ORE:
                if (n == 1) {
                    Bukkit.broadcastMessage(ElementalsUtil.color("&8[&7Info&8] &f&o" + event.getPlayer().getName() + " &e&oa descoperit o bucata de Aur."));
                } else {
                    Bukkit.broadcastMessage(ElementalsUtil.color("&8[&7Info&8] &f&o" +
                            event.getPlayer().getName() + " &e&oa descoperit " + n + " bucati de Aur."));
                }
                break;
            case IRON_ORE:
                if (n == 1) {
                    Bukkit.broadcastMessage(ElementalsUtil.color("&8[&7Info&8] &f&o" + event.getPlayer().getName() + " &7&oa descoperit o bucata de Fier."));
                } else {
                    Bukkit.broadcastMessage(ElementalsUtil.color("&8[&7Info&8] &f&o" +
                            event.getPlayer().getName() + " &7&oa descoperit " + n + " bucati de Fier."));
                }
                break;
            case LAPIS_ORE:
                if (n == 1) {
                    Bukkit.broadcastMessage(ElementalsUtil.color("&8[&7Info&8] &f&o" + event.getPlayer().getName() + " &9&oa descoperit o bucata de Lapis."));
                } else {
                    Bukkit.broadcastMessage(ElementalsUtil.color("&8[&7Info&8] &f&o" +
                            event.getPlayer().getName() + " &9&oa descoperit " + n + " bucati de Lapis."));
                }
                break;
            case EMERALD_ORE:
                if (n == 1) {
                    Bukkit.broadcastMessage(ElementalsUtil.color("&8[&7Info&8] &f&o" + event.getPlayer().getName() + " &a&oa descoperit o bucata de Emerald."));
                } else {
                    Bukkit.broadcastMessage(ElementalsUtil.color("&8[&7Info&8] &f&o" +
                            event.getPlayer().getName() + " &a&oa descoperit " + n + " bucati de Emerald."));
                }
                break;
            default:
                break;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void event(BlockBurnEvent event) {
        String worldname = event.getBlock().getWorld().getName();
        if (!(worldname.equals("spawn") || worldname.equals("dungeon")))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(BlockIgniteEvent event) {
        String worldname = event.getBlock().getWorld().getName();
        if (!(worldname.equals("spawn") || worldname.equals("dungeon")))
            return;
        if (event.getCause() == IgniteCause.FLINT_AND_STEEL) {
            Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
            if (optionalUser.isEmpty())
                return;
            User user = optionalUser.get();
            if (user.hasPermission("elementals.admin"))
                return;
        }
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(BlockSpreadEvent event) {
        String worldname = event.getBlock().getWorld().getName();
        if (!(worldname.equals("spawn") || worldname.equals("dungeon")))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(HangingBreakByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        String worldname = event.getEntity().getWorld().getName();
        if (!(worldname.equals("spawn") || worldname.equals("dungeon")))
            return;
        if (event.getRemover().getType() == EntityType.PLAYER) {
            Optional<User> optionalUser = ElementalsX.getUser(event.getRemover().getUniqueId());
            if (optionalUser.isEmpty())
                return;
            User user = optionalUser.get();
            if (!user.hasPermission("elementals.admin"))
                event.setCancelled(true);
        } else if (event.getRemover().getType() == EntityType.ARROW
                || event.getRemover().getType() == EntityType.EGG
                || event.getRemover().getType() == EntityType.ENDER_PEARL
                || event.getRemover().getType() == EntityType.SNOWBALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerArmorStandManipulateEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked()))
            return;
        String worldname = event.getPlayer().getWorld().getName();
        if (!(worldname.equals("spawn") || worldname.equals("dungeon")))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(HangingPlaceEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        String worldname = event.getEntity().getWorld().getName();
        if (!(worldname.equals("spawn") || worldname.equals("dungeon")))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerBucketEmptyEvent event) {
        String worldname = event.getPlayer().getWorld().getName();
        if (!(worldname.equals("spawn") || worldname.equals("dungeon")))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerBucketFillEvent event) {
        String worldname = event.getPlayer().getWorld().getName();
        if (!(worldname.equals("spawn") || worldname.equals("dungeon")))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void event(PlayerCommandPreprocessEvent event) {
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (event.getMessage().startsWith("/spawn")) {
            if (!event.getPlayer().isInsideVehicle())
                return;
            event.getPlayer().sendMessage(ElementalsUtil.color("&cNu poti folosi comanda cat timp esti intr-un vehicul!"));
            event.setCancelled(true);
        } else if (event.getMessage().startsWith("/sethome")) {
            String worldname = event.getPlayer().getWorld().getName();
            if (worldname.equals("spawn") || worldname.equals("dungeon")) {
                if (user.hasPermission("elementals.command.minecraft"))
                    return;
                event.getPlayer().sendMessage(ElementalsUtil.color("&cNu poti folosi comanda in spawn!"));
                event.setCancelled(true);
            } else if (FieldUtil.isFieldAtLocation(event.getPlayer().getLocation()))
                if (!(FieldUtil.getFieldByLocation(event.getPlayer().getLocation())
                        .isMember(event.getPlayer().getUniqueId())
                        || FieldUtil.getFieldByLocation(event.getPlayer().getLocation()).isOwner(
                        event.getPlayer().getUniqueId())
                        || user.hasPermission("protection.override"))) {
                    event.getPlayer().sendMessage(ElementalsUtil.color("&cNu poti folosi comanda in protectia altcuiva!"));
                    event.setCancelled(true);
                }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerInteractEntityEvent event) {
        String worldname = event.getRightClicked().getWorld().getName();
        if (!(worldname.equals("spawn") || worldname.equals("dungeon")))
            return;
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME)
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event0(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.PARROT)
            return;
        Parrot parrot = (Parrot) event.getRightClicked();
        if (parrot.getOwner() == null)
            return;
        if (parrot.getOwner().getUniqueId().equals(event.getPlayer().getUniqueId()))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        String worldname = event.getClickedBlock().getWorld().getName();
        if (!(worldname.equals("spawn") || worldname.equals("dungeon")))
            return;
        if (event.getPlayer().getInventory().getItemInMainHand() == null)
            return;
        Material handType = event.getPlayer().getInventory().getItemInMainHand().getType();
        if (handType != Material.BAT_SPAWN_EGG
                || handType != Material.ARMOR_STAND)
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerJoinEvent event) {
        ElementalsX.createUser(event.getPlayer());
        if (!event.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY))
            ElementalsX.getOnlineUsers().stream().filter(User::hasSounds).forEach(u -> u.getBase().playSound(u.getBase().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1));
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    @EventHandler
    public void event(PlayerQuitEvent event) {
        ElementalsX.removeUser(event.getPlayer());
        if (!event.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY))
            ElementalsX.getOnlineUsers().stream().filter(User::hasSounds).forEach(u ->
                    u.getBase().playSound(u.getBase().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(PlayerLoginEvent event) {
        if (event.getResult().equals(Result.KICK_FULL))
            event.setKickMessage(ElementalsUtil.color(
                    "\n" + "&a[&6PikaCraft&a]\n" + "&aNu te poti conecta!\n" + "&eServerul este plin."));
    }

    @EventHandler(ignoreCancelled = true)
    public void event(EntityExplodeEvent event) {
        String worldname = event.getEntity().getWorld().getName();
        if (!(worldname.equals("spawn") || worldname.equals("dungeon")))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event0(BlockBreakEvent event) {
        String worldname = event.getPlayer().getWorld().getName();
        if (!(worldname.equals("spawn") || worldname.equals("dungeon")))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);

    }

    @EventHandler(ignoreCancelled = true)
    public static void easterEgg(EntityDamageEvent event) {
        if (event.getEntity().getType() != EntityType.CHICKEN)
            return;
        if (event.getEntity().getCustomName() == null)
            return;
        if (!event.getEntity().getCustomName().equals("Gina"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event1(EntityDamageEvent event) {
        String worldname = event.getEntity().getWorld().getName();
        if (!(worldname.equals("spawn") || worldname.equals("dungeon")))
            return;
        if (event.getEntity().getType() != EntityType.ARMOR_STAND)
            return;
        if (event.getCause() != DamageCause.FIRE_TICK)
            return;
        event.setCancelled(true);
    }
}
