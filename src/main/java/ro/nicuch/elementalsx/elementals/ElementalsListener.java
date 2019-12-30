package ro.nicuch.elementalsx.elementals;

import me.clip.placeholderapi.PlaceholderAPI;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;
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
    public void event(EntityDamageEvent event) {
        if (!event.getEntity().getWorld().getName().equals("spawn"))
            return;
        if (event.getEntityType() != EntityType.PLAYER)
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(CreatureSpawnEvent event) {
        if (!event.getLocation().getWorld().getName().equals("spawn"))
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
        if (!event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        if (event.getCause() != TeleportCause.CHORUS_FRUIT
                || event.getCause() != TeleportCause.ENDER_PEARL)
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
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
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
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
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
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
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void event0(AsyncPlayerChatEvent event) {
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        if (user == null) {
            event.setCancelled(true);
            return;
        }
        if (ElementalsUtil.hasChatDelay(user) && !user.hasPermission("elementals.chat.override")) {
            event.getPlayer().sendMessage(ElementalsUtil.color("&cTrebuie sa astepti o secunda pentru a putea vorbi!"));
            event.setCancelled(true);
            return;
        }
        if (ElementalsUtil.isChatStopped() && !user.hasPermission("elementals.chat.bypass")) {
            event.getPlayer().sendMessage(ElementalsUtil.color("&cNu se poate vorbi acum!"));
            event.setCancelled(true);
            return;
        }
        ElementalsUtil.delayChatPlayer(user);
        List<String> names = ElementalsUtil.getPlayersNames();
        List<String> recipeNames = new ArrayList<>();
        String msg = event.getMessage();
        for (String name : names) {
            if (msg.contains("@" + name))
                recipeNames.add(name);
        }
        String message = msg.toLowerCase();
        StringBuilder builder = new StringBuilder(msg);
        if (!(message.endsWith(".") || message.endsWith("?") || message.endsWith("!") || message.endsWith(")")
                || message.endsWith("]") || message.endsWith(":d") || message.endsWith("xd") || message.endsWith("*")
                || message.endsWith("-") || message.endsWith("_") || message.endsWith(",") || message.endsWith("'")
                || message.endsWith("/") || message.endsWith("|") || message.endsWith("(") || message.endsWith("[")
                || message.endsWith("@") || message.endsWith("#") || message.endsWith("$") || message.endsWith("%")
                || message.endsWith("^") || message.endsWith("{") || message.endsWith("}") || message.endsWith(";")
                || message.endsWith(":") || message.endsWith("<") || message.endsWith(">") || message.endsWith("\\")
                || message.endsWith("~") || message.endsWith("~") || message.endsWith("=") || message.endsWith("+")
                || message.endsWith(":3") || message.endsWith(":p") || message.endsWith(":o") || message.endsWith(":s")
                || message.endsWith("<3") || message.endsWith(":c") || message.endsWith("[item]")))
            builder.insert(msg.length(), ".");
        if (!(message.startsWith(".") || message.startsWith("?") || message.startsWith("!") || message.startsWith(")")
                || message.startsWith("]") || message.startsWith(":d") || message.startsWith("xd") || message.startsWith("*")
                || message.startsWith("-") || message.startsWith("_") || message.startsWith(",") || message.startsWith("'")
                || message.startsWith("/") || message.startsWith("|") || message.startsWith("(") || message.startsWith("[")
                || message.startsWith("@") || message.startsWith("#") || message.startsWith("$") || message.startsWith("%")
                || message.startsWith("^") || message.startsWith("{") || message.startsWith("}") || message.startsWith(";")
                || message.startsWith(":") || message.startsWith("<") || message.startsWith(">") || message.startsWith("\\")
                || message.startsWith("~") || message.startsWith("~") || message.startsWith("=") || message.startsWith("+")
                || message.startsWith(":3") || message.startsWith(":p") || message.startsWith(":o") || message.startsWith(":s")
                || message.startsWith("<3") || message.startsWith(":c") || message.startsWith("[item]")))
            builder.replace(0, 1, msg.substring(0, 1).toUpperCase());

        ElementalsX.getOnlineUsers().stream().filter(User::hasSounds).peek(u -> u.getBase().playSound(u.getBase().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f))
                .filter(u -> recipeNames.contains(u.getBase().getName()) && u.hasSounds()).forEach(u -> u.getBase().playSound(u.getBase().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f));
        String level = PlaceholderAPI.setPlaceholders(event.getPlayer(), "%math_{mcmmo_power_level}/15[precision:0]%");
        event.setFormat(event.getFormat().replace("{LEVEL}", level));
        event.setMessage(builder.toString());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void event3(BlockBreakEvent event) {
        if (event.getBlock().getWorld().getName().equals("spawn"))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
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
        if (!event.getBlock().getWorld().getName().equals("spawn"))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.override"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void event(BlockPlaceEvent event) {
        if (event.getBlock().getWorld().getName().equals("spawn"))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
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
        if (event.getBlock().getWorld().getName().equals("spawn"))
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

    @EventHandler(ignoreCancelled = true)
    public void event(BlockBurnEvent event) {
        if (!event.getBlock().getWorld().getName().equals("spawn"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(BlockIgniteEvent event) {
        if (!event.getBlock().getWorld().getName().equals("spawn"))
            return;
        if (event.getCause() == IgniteCause.FLINT_AND_STEEL) {
            Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
            if (!optionalUser.isPresent())
                return;
            User user = optionalUser.get();
            if (user.hasPermission("elementals.admin"))
                return;
        }
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(BlockSpreadEvent event) {
        if (!event.getBlock().getWorld().getName().equals("spawn"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(HangingBreakByEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (!event.getEntity().getWorld().getName().equals("spawn"))
            return;
        if (event.getRemover().getType() == EntityType.PLAYER) {
            Optional<User> optionalUser = ElementalsX.getUser(event.getRemover().getUniqueId());
            if (!optionalUser.isPresent())
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
        if (!event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
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
        if (!event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerBucketEmptyEvent event) {
        if (!event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PlayerBucketFillEvent event) {
        if (!event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void event(PlayerCommandPreprocessEvent event) {
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        if (event.getMessage().startsWith("/spawn")) {
            if (!event.getPlayer().isInsideVehicle())
                return;
            event.getPlayer().sendMessage(ElementalsUtil.color("&cNu poti folosi comanda cat timp esti intr-un vehicul!"));
            event.setCancelled(true);
        } else if (event.getMessage().startsWith("/sethome")) {
            if (event.getPlayer().getWorld().getName().equals("spawn")) {
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
        if (!event.getRightClicked().getWorld().getName().equals("spawn"))
            return;
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME)
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
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
        if (!event.getClickedBlock().getWorld().getName().equals("spawn"))
            return;
        if (event.getPlayer().getInventory().getItemInMainHand() == null)
            return;
        Material handType = event.getPlayer().getInventory().getItemInMainHand().getType();
        if (handType != Material.BAT_SPAWN_EGG
                || handType != Material.ARMOR_STAND)
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        if (user.hasPermission("elementals.admin"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerJoinEvent event) {
        ElementalsX.getOnlineUsers().stream().filter(User::hasSounds).forEach(u -> u.getBase().playSound(u.getBase().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1));
        ElementalsX.createUser(event.getPlayer());
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    @EventHandler
    public void event(PlayerQuitEvent event) {
        ElementalsX.removeUser(event.getPlayer());
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
        if (!event.getEntity().getWorld().getName().equals("spawn"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void event0(BlockBreakEvent event) {
        if (!event.getPlayer().getWorld().getName().equals("spawn"))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getPlayer().getUniqueId());
        if (!optionalUser.isPresent())
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
        if (!event.getEntity().getWorld().getName().equals("spawn"))
            return;
        if (event.getEntity().getType() != EntityType.ARMOR_STAND)
            return;
        if (event.getCause() != DamageCause.FIRE_TICK)
            return;
        event.setCancelled(true);
    }
}
