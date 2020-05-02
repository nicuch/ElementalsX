package ro.nicuch.elementalsx.elementals;

import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
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
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.protection.Field;
import ro.nicuch.elementalsx.protection.FieldUtil;
import ro.nicuch.lwjnbtl.CompoundTag;
import ro.nicuch.tag.TagRegister;

import java.util.*;

public class ElementalsListener implements Listener {
    private final static List<UUID> interactList = new ArrayList<>();

    @EventHandler
    public void event2(BlockIgniteEvent event) {
        Block block = event.getBlock();
        if (event.getCause() == IgniteCause.LAVA) {
            if (block.getType() != Material.FIRE)
                return;
            if (event.getIgnitingBlock() == null)
                return;
            Block from = event.getIgnitingBlock();
            Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(from);
            if (optionalCompoundTag.isEmpty())
                return;
            CompoundTag tag = optionalCompoundTag.get();
            if (!tag.containsString("lava-placed"))
                return;
            String uuid = tag.getString("lava-placed");
            CompoundTag toTag = TagRegister.getStored(block).orElseGet(() -> TagRegister.create(block));
            toTag.putString("fire-placed", uuid);
        }
    }

    @EventHandler
    public void event0(BlockSpreadEvent event) {
        Block from = event.getSource();
        Block to = event.getBlock();
        if (from.getType() != Material.FIRE)
            return;
        Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(from);
        if (optionalCompoundTag.isEmpty())
            return;
        CompoundTag tag = optionalCompoundTag.get();
        if (!tag.containsString("fire-placed"))
            return;
        String uuid = tag.getString("fire-placed");
        CompoundTag toTag = TagRegister.getStored(to).orElseGet(() -> TagRegister.create(to));
        toTag.putString("fire-placed", uuid);
    }

    @EventHandler
    public void event(BlockFormEvent event) {
        Block block = event.getBlock();
        if (event.getBlock().getType() != Material.LAVA)
            return;
        if (event.getNewState().getType() == Material.LAVA)
            return;
        Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(block);
        if (optionalCompoundTag.isEmpty())
            return;
        CompoundTag tag = optionalCompoundTag.get();
        if (!tag.containsString("lava-placed"))
            return;
        tag.remove("lava-placed");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void event0(PlayerBucketEmptyEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Block block = event.getBlock();
        Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(block);
        if (event.getBucket() == Material.LAVA_BUCKET) {
            CompoundTag tag = optionalCompoundTag.orElseGet(() -> TagRegister.create(block));
            tag.putString("lava-placed", uuid.toString());
        }
        if (block.getType() == Material.FIRE) {
            if (optionalCompoundTag.isEmpty())
                return;
            CompoundTag tag = optionalCompoundTag.get();
            if (!tag.containsString("fire-placed"))
                return;
            tag.remove("fire-placed");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;
        Block block = event.getClickedBlock();
        if (block == null)
            return;
        if (block.getType() != Material.FIRE)
            return;
        Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(block);
        if (optionalCompoundTag.isEmpty())
            return;
        CompoundTag tag = optionalCompoundTag.get();
        if (!tag.containsString("fire-placed"))
            return;
        tag.remove("fire-placed");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void event1(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(block);
        if (block.getType() == Material.LAVA) {
            if (event.getBlockPlaced().getType() != Material.LAVA)
                return;
            if (optionalCompoundTag.isEmpty())
                return;
            CompoundTag tag = optionalCompoundTag.get();
            if (!tag.containsString("lava-placed"))
                return;
            tag.remove("lava-placed");
        } else if (event.getBlockPlaced().getType() == Material.FIRE) {
            UUID uuid = event.getPlayer().getUniqueId();
            CompoundTag tag = optionalCompoundTag.orElseGet(() -> TagRegister.create(block));
            tag.putString("fire-placed", uuid.toString());
        } else if (block.getType() == Material.FIRE) {
            if (event.getBlockPlaced().getType() != Material.FIRE)
                return;
            if (optionalCompoundTag.isEmpty())
                return;
            CompoundTag tag = optionalCompoundTag.get();
            if (!tag.containsString("fire-placed"))
                return;
            tag.remove("fire-placed");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void event0(PlayerBucketFillEvent event) {
        Block block = event.getBlock();
        Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(block);
        if (optionalCompoundTag.isEmpty())
            return;
        CompoundTag tag = optionalCompoundTag.get();
        if (!tag.containsString("lava-placed"))
            return;
        tag.remove("lava-placed");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void event(FluidLevelChangeEvent event) {
        Block block = event.getBlock();
        if (event.getBlock().getType() != Material.LAVA)
            return;
        if (event.getNewData().getMaterial() == Material.LAVA)
            return;
        Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(block);
        if (optionalCompoundTag.isEmpty())
            return;
        CompoundTag tag = optionalCompoundTag.get();
        if (!tag.containsString("lava-placed"))
            return;
        tag.remove("lava-placed");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void event0(BlockFadeEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.LAVA) {
            if (event.getNewState().getType() == Material.LAVA)
                return;
            Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(block);
            if (optionalCompoundTag.isEmpty())
                return;
            CompoundTag tag = optionalCompoundTag.get();
            if (!tag.containsString("lava-placed"))
                return;
            tag.remove("lava-placed");
        } else if (block.getType() == Material.FIRE) {
            if (event.getNewState().getType() == Material.FIRE)
                return;
            Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(block);
            if (optionalCompoundTag.isEmpty())
                return;
            CompoundTag tag = optionalCompoundTag.get();
            if (!tag.containsString("fire-placed"))
                return;
            tag.remove("fire-placed");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void event0(BlockFromToEvent event) {
        Block from = event.getBlock();
        Block to = event.getToBlock();
        if (from.getType() == Material.LAVA) {
            Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(from);
            if (optionalCompoundTag.isEmpty())
                return;
            CompoundTag tag = optionalCompoundTag.get();
            if (!tag.containsString("lava-placed"))
                return;
            String uuid = tag.getString("lava-placed");
            CompoundTag toTag = TagRegister.getStored(to).orElseGet(() -> TagRegister.create(to));
            toTag.putString("lava-placed", uuid);
        }
        if (to.getType() == Material.AIR) {
            Optional<CompoundTag> optionalCompoundTag = TagRegister.getStored(to);
            if (optionalCompoundTag.isEmpty())
                return;
            CompoundTag tag = optionalCompoundTag.get();
            if (!tag.containsString("fire-placed"))
                return;
            tag.remove("fire-placed");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void event(BlockFadeEvent event) {
        if (!event.getBlock().getWorld().getName().equals("spawn"))
            return;
        Material blockMat = event.getBlock().getType();
        if (!(Tag.CORAL_BLOCKS.isTagged(blockMat) || Tag.CORAL_PLANTS.isTagged(blockMat) || Tag.CORALS.isTagged(blockMat) || Tag.WALL_CORALS.isTagged(blockMat)))
            return;
        event.setCancelled(true);
    }

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
        String worldName = event.getTo().getWorld().getName();
        User user = optionalUser.get();
        if (!user.hasPermission("elementals.gm.toggle")) {
            if (worldName.equals("spawn") || worldName.equals("dungeon"))
                user.getBase().setGameMode(GameMode.ADVENTURE);
            else
                user.getBase().setGameMode(GameMode.SURVIVAL);
        }
        if (!user.hasPermission("elementals.fly.toggle")) {
            if (worldName.equals("spawn") || worldName.equals("dungeon")) {
                event.getPlayer().setFlying(false);
                event.getPlayer().setAllowFlight(false);
            }
        }
    }

    @EventHandler
    public void event(PlayerToggleFlightEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.fly.toggle"))
            return;
        String worldName = event.getPlayer().getWorld().getName();
        if (!(worldName.equals("spawn") || worldName.equals("dungeon")))
            return;
        if (event.isFlying()) {
            event.getPlayer().setFlying(false);
            event.getPlayer().setAllowFlight(false);
        } else
            event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerTeleportEvent event) {
        if (event.getFrom().getBlock().equals(event.getTo().getBlock()))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (optionalUser.isEmpty())
            return;
        String worldName = event.getTo().getWorld().getName();
        User user = optionalUser.get();
        if (!user.hasPermission("elementals.gm.toggle")) {
            if (worldName.equals("spawn") || worldName.equals("dungeon"))
                user.getBase().setGameMode(GameMode.ADVENTURE);
            else
                user.getBase().setGameMode(GameMode.SURVIVAL);
        }
        if (!user.hasPermission("elementals.fly.toggle")) {
            if (worldName.equals("spawn") || worldName.equals("dungeon")) {
                event.getPlayer().setFlying(false);
                event.getPlayer().setAllowFlight(false);
            }
        }
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
        Set<Player> recipe = event.getRecipients();
        ElementalsX.getOnlineUsers().stream().filter(User::hasSounds).filter(user -> recipe.contains(user.getBase())).forEach(user -> user.getBase().playSound(user.getBase().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f));
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
    public void event3(FakeBlockBreakEvent event) {
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

    @EventHandler(priority = EventPriority.HIGHEST)
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
        int n = ElementalsUtil.foundBlocks(event.getBlock());
        if (n == 0)
            return;
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
        Location loc = event.getPlayer().getLocation();
        User user = optionalUser.get();
        if (event.getMessage().startsWith("/sethome") || event.getMessage().startsWith("/essentials:sethome")) {
            String worldname = event.getPlayer().getWorld().getName();
            if (worldname.equals("spawn") || worldname.equals("dungeon")) {
                if (user.hasPermission("elementals.command.sethome"))
                    return;
                event.getPlayer().sendMessage(ElementalsUtil.color("&cNu poti folosi comanda in spawn/dungeon!"));
                event.setCancelled(true);
            } else if (FieldUtil.isFieldAtLocation(loc)) {
                Field field = FieldUtil.getFieldByLocation(loc);
                if (!(field.isMember(user.getUUID()) || field.isOwner(user.getUUID()) || user.hasPermission("protection.override"))) {
                    event.getPlayer().sendMessage(ElementalsUtil.color("&cNu poti folosi comanda in protectia altcuiva!"));
                    event.setCancelled(true);
                }
            }
        } else if (event.getMessage().startsWith("/fly") || event.getMessage().startsWith("/essentials:fly")) {
            String worldname = event.getPlayer().getWorld().getName();
            if (worldname.equals("spawn") || worldname.equals("dungeon")) {
                if (user.hasPermission("elementals.command.fly"))
                    return;
                event.getPlayer().sendMessage(ElementalsUtil.color("&cNu poti folosi comanda in spawn/dungeon!"));
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
    }

    @EventHandler
    public void event(PlayerQuitEvent event) {
        ElementalsX.removeUser(event.getPlayer());
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
