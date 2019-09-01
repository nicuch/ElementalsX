package ro.nicuch.elementalsx.elementals;

import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.commands.RandomTpCommand;
import ro.nicuch.elementalsx.protection.FieldUtil;
import ro.nicuch.lwjnbtl.CompoundTag;
import ro.nicuch.lwjnbtl.TagType;
import ro.nicuch.tag.TagRegister;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ElementalsUtil {
    private static final Set<UUID> delayRandomTP = new HashSet<>();
    private static final Map<UUID, Integer> delayRandomTPCmd = new HashMap<>();
    private static boolean create;
    private static final Set<UUID> delayChat = new HashSet<>();
    private static boolean stopChat = false;
    private static String motd = "&6PikaCraft &b- &cMinecraft la un alt nivel!";
    private static final List<String> autoMsg = Arrays.asList(
            "&aUtilizarea hack-urilor este pedepsita cu &4&lBAN&a!",
            "&bNu uita sa votezi in fiecare zi folosind comanda &f[&6/vote&f]&b! &aVei primi un premiu de fiecare data cand votezi. &6:)",
            "&f[&6/sort&f] &bPoti sorta inventare! &a:D",
            "&aDaca ai nevoie de bani, scrie &f[&6/jobs&f] &asi i-ati un job!",
            "&6Protectia se face folosind cubul de &bDiamant&6! &aNu uita sa o pui altfel casa ta va fi distrusa!",
            "&aStaff-ul nu raspunde de obiectele pierdute!",
            "&cDaca descoperiti un bug, va rugam sa-l raportati! &6Ve-ti primi un bonus daca bug-ul nu a fost raportat deja!",
            "&cPoti raporta &eJucatorii&c/&5Donatorii&c/&4Staff-ul &cserverului in cazul in care acestia &bincalca regulamentul&c!",
            "&6Asteptam sugestiile si ideile voastre!",
            "&ePikaaa-pii! Pikaaa-chu! &a:)");

    public static List<String> getAutoMessages() {
        return autoMsg;
    }

    public enum NanoType {
        PICKAXE, SPADE, AXE
    }

    public static EntityType nameToEntity(String name) {
        switch (name) {
            case "&aSpawner <Zombie>":
                return EntityType.ZOMBIE;
            case "&aSpawner <Schelete>":
                return EntityType.SKELETON;
            case "&aSpawner <Blaze>":
                return EntityType.BLAZE;
            case "&aSpawner <Porc Zombie>":
                return EntityType.PIG_ZOMBIE;
            case "&aSpawner <Vrajitoare>":
                return EntityType.WITCH;
            case "&aSpawner <Iepure>":
                return EntityType.RABBIT;
            case "&aSpawner <Vaca>":
                return EntityType.COW;
            case "&aSpawner <Oaie>":
                return EntityType.SHEEP;
            case "&aSpawner <Urs Polar>":
                return EntityType.POLAR_BEAR;
            case "&aSpawner <Schelete Negru>":
                return EntityType.WITHER_SKELETON;
            case "&aSpawner <Golem de Fier>":
                return EntityType.IRON_GOLEM;
            case "&aSpawner <Porc>":
                return EntityType.PIG;
            case "&aSpawner <Paianjen>":
                return EntityType.SPIDER;
            case "&aSpawner <Paianjen Mic>":
                return EntityType.CAVE_SPIDER;
            case "&aSpawner <Enderman>":
                return EntityType.ENDERMAN;
            default:
                return EntityType.UNKNOWN;
        }
    }

    public static String entityToName(EntityType type) {
        switch (type) {
            case ZOMBIE:
                return "&aSpawner <Zombie>";
            case SKELETON:
                return "&aSpawner <Schelete>";
            case BLAZE:
                return "&aSpawner <Blaze>";
            case PIG_ZOMBIE:
                return "&aSpawner <Porc Zombie>";
            case WITCH:
                return "&aSpawner <Vrajitoare>";
            case RABBIT:
                return "&aSpawner <Iepure>";
            case COW:
                return "&aSpawner <Vaca>";
            case SHEEP:
                return "&aSpawner <Oaie>";
            case POLAR_BEAR:
                return "&aSpawner <Urs Polar>";
            case WITHER_SKELETON:
                return "&aSpawner <Schelete Negru>";
            case IRON_GOLEM:
                return "&aSpawner <Golem de Fier>";
            case PIG:
                return "&aSpawner <Porc>";
            case SPIDER:
                return "&aSpawner <Paianjen>";
            case CAVE_SPIDER:
                return "&aSpawner <Paianjen Mic>";
            case ENDERMAN:
                return "&aSpawner <Enderman>";
            default:
                return "&aSpawner <Necunoscut>";
        }
    }

    public static String color(String arg) {
        return ChatColor.translateAlternateColorCodes('&', arg);
    }

    public static void sortShulkerBox(User user) {
        Block block = user.getBase().getTargetBlock(null, 3);
        Material blockType = block.getType();
        if (!(blockType.equals(Material.BLACK_SHULKER_BOX) || blockType.equals(Material.BLUE_SHULKER_BOX)
                || blockType.equals(Material.BROWN_SHULKER_BOX) || blockType.equals(Material.CYAN_SHULKER_BOX)
                || blockType.equals(Material.GRAY_SHULKER_BOX) || blockType.equals(Material.RED_SHULKER_BOX)
                || blockType.equals(Material.GREEN_SHULKER_BOX) || blockType.equals(Material.YELLOW_SHULKER_BOX)
                || blockType.equals(Material.MAGENTA_SHULKER_BOX) || blockType.equals(Material.LIGHT_BLUE_SHULKER_BOX)
                || blockType.equals(Material.LIME_SHULKER_BOX) || blockType.equals(Material.ORANGE_SHULKER_BOX)
                || blockType.equals(Material.PINK_SHULKER_BOX) || blockType.equals(Material.LIGHT_GRAY_SHULKER_BOX)
                || blockType.equals(Material.WHITE_SHULKER_BOX) || blockType.equals(Material.PURPLE_SHULKER_BOX))) {
            user.getBase().sendMessage(color("&cTrebuie sa te uiti la un shulker box!"));
            return;
        }
        ShulkerBox shulker = (ShulkerBox) block.getState();
        if (FieldUtil.isFieldAtLocation(shulker.getLocation())) {
            if (!(FieldUtil.getFieldByLocation(shulker.getLocation()).isMember(user.getBase().getUniqueId())
                    || FieldUtil.getFieldByLocation(shulker.getLocation()).isOwner(user.getBase().getUniqueId())
                    || user.hasPermission("elementals.protection.override"))) {
                user.getBase().sendMessage(color("&cNu poti sorta acest shulker box!"));
                return;
            }
        }
        Inventory inv = shulker.getInventory();
        List<ItemStack> sortedList = new ArrayList<>();
        for (ItemStack item : inv.getContents()) {
            if (item == null || item.getType().equals(Material.AIR))
                continue;
            sortedList.add(item.clone());
        }
        inv.clear();
        sortedList.sort((o1, o2) -> {
            if (!o1.hasItemMeta() && o2.hasItemMeta())
                return -1;
            else if (!o2.hasItemMeta() && o1.hasItemMeta())
                return 1;
            else if (!(o1.hasItemMeta() && o2.hasItemMeta()))
                return 0;
            if (!o1.getItemMeta().hasDisplayName() && o2.getItemMeta().hasDisplayName())
                return -1;
            else if (!o2.getItemMeta().hasDisplayName() && o1.getItemMeta().hasDisplayName())
                return 1;
            else if (!(o1.getItemMeta().hasDisplayName() && o2.getItemMeta().hasDisplayName()))
                return 0;
            if (o1.getItemMeta().getDisplayName() == null && o2.getItemMeta().getDisplayName() != null)
                return -1;
            else if (o2.getItemMeta().getDisplayName() == null && o1.getItemMeta().getDisplayName() != null)
                return 1;
            else if (!(o1.getItemMeta().getDisplayName() == null && o2.getItemMeta().getDisplayName() == null))
                return 0;
            return o1.getItemMeta().getDisplayName().compareTo(o2.getItemMeta().getDisplayName());
        });
        sortedList.sort(Comparator.comparing(ItemStack::getType));
        sortedList.forEach(inv::addItem);
        user.getBase().sendMessage(color("&aShulker Box-ul a fost sortat!"));
    }

    public static void sortInventory(User user) {
        List<ItemStack> sortedList = new ArrayList<>();
        ItemStack s0 = user.getBase().getInventory().getItem(0) == null ? new ItemStack(Material.AIR)
                : user.getBase().getInventory().getItem(0).clone();
        ItemStack s1 = user.getBase().getInventory().getItem(1) == null ? new ItemStack(Material.AIR)
                : user.getBase().getInventory().getItem(1).clone();
        ItemStack s2 = user.getBase().getInventory().getItem(2) == null ? new ItemStack(Material.AIR)
                : user.getBase().getInventory().getItem(2).clone();
        ItemStack s3 = user.getBase().getInventory().getItem(3) == null ? new ItemStack(Material.AIR)
                : user.getBase().getInventory().getItem(3).clone();
        ItemStack s4 = user.getBase().getInventory().getItem(4) == null ? new ItemStack(Material.AIR)
                : user.getBase().getInventory().getItem(4).clone();
        ItemStack s5 = user.getBase().getInventory().getItem(5) == null ? new ItemStack(Material.AIR)
                : user.getBase().getInventory().getItem(5).clone();
        ItemStack s6 = user.getBase().getInventory().getItem(6) == null ? new ItemStack(Material.AIR)
                : user.getBase().getInventory().getItem(6).clone();
        ItemStack s7 = user.getBase().getInventory().getItem(7) == null ? new ItemStack(Material.AIR)
                : user.getBase().getInventory().getItem(7).clone();
        ItemStack s8 = user.getBase().getInventory().getItem(8) == null ? new ItemStack(Material.AIR)
                : user.getBase().getInventory().getItem(8).clone();
        for (int i = 9; i < 36; i++) {
            ItemStack item = user.getBase().getInventory().getItem(i);
            if (item == null || item.getType().equals(Material.AIR))
                continue;
            sortedList.add(item.clone());
            user.getBase().getInventory().setItem(i, new ItemStack(Material.AIR));
        }
        user.getBase().getInventory().setItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        user.getBase().getInventory().setItem(1, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        user.getBase().getInventory().setItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        user.getBase().getInventory().setItem(3, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        user.getBase().getInventory().setItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        user.getBase().getInventory().setItem(5, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        user.getBase().getInventory().setItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        user.getBase().getInventory().setItem(7, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        user.getBase().getInventory().setItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        sortedList.sort((o1, o2) -> {
            if (!o1.hasItemMeta() && o2.hasItemMeta())
                return -1;
            else if (!o2.hasItemMeta() && o1.hasItemMeta())
                return 1;
            else if (!(o1.hasItemMeta() && o2.hasItemMeta()))
                return 0;
            if (!o1.getItemMeta().hasDisplayName() && o2.getItemMeta().hasDisplayName())
                return -1;
            else if (!o2.getItemMeta().hasDisplayName() && o1.getItemMeta().hasDisplayName())
                return 1;
            else if (!(o1.getItemMeta().hasDisplayName() && o2.getItemMeta().hasDisplayName()))
                return 0;
            if (o1.getItemMeta().getDisplayName() == null && o2.getItemMeta().getDisplayName() != null)
                return -1;
            else if (o2.getItemMeta().getDisplayName() == null && o1.getItemMeta().getDisplayName() != null)
                return 1;
            else if (!(o1.getItemMeta().getDisplayName() == null && o2.getItemMeta().getDisplayName() == null))
                return 0;
            return o1.getItemMeta().getDisplayName().compareTo(o2.getItemMeta().getDisplayName());
        });
        sortedList.sort(Comparator.comparing(ItemStack::getType));
        sortedList.forEach(i -> user.getBase().getInventory().addItem(i));
        user.getBase().getInventory().setItem(0, s0);
        user.getBase().getInventory().setItem(1, s1);
        user.getBase().getInventory().setItem(2, s2);
        user.getBase().getInventory().setItem(3, s3);
        user.getBase().getInventory().setItem(4, s4);
        user.getBase().getInventory().setItem(5, s5);
        user.getBase().getInventory().setItem(6, s6);
        user.getBase().getInventory().setItem(7, s7);
        user.getBase().getInventory().setItem(8, s8);
        user.getBase().sendMessage(color("&aInventarul a fost sortat!"));
    }

    public static void sortEnderChest(User user) {
        List<ItemStack> sortedList = new ArrayList<>();
        for (ItemStack item : user.getBase().getEnderChest()) {
            if (item == null || item.getType().equals(Material.AIR))
                continue;
            sortedList.add(item.clone());
        }
        user.getBase().getEnderChest().clear();
        sortedList.sort((o1, o2) -> {
            if (!o1.hasItemMeta() && o2.hasItemMeta())
                return -1;
            else if (!o2.hasItemMeta() && o1.hasItemMeta())
                return 1;
            else if (!(o1.hasItemMeta() && o2.hasItemMeta()))
                return 0;
            if (!o1.getItemMeta().hasDisplayName() && o2.getItemMeta().hasDisplayName())
                return -1;
            else if (!o2.getItemMeta().hasDisplayName() && o1.getItemMeta().hasDisplayName())
                return 1;
            else if (!(o1.getItemMeta().hasDisplayName() && o2.getItemMeta().hasDisplayName()))
                return 0;
            if (o1.getItemMeta().getDisplayName() == null && o2.getItemMeta().getDisplayName() != null)
                return -1;
            else if (o2.getItemMeta().getDisplayName() == null && o1.getItemMeta().getDisplayName() != null)
                return 1;
            else if (!(o1.getItemMeta().getDisplayName() == null && o2.getItemMeta().getDisplayName() == null))
                return 0;
            return o1.getItemMeta().getDisplayName().compareTo(o2.getItemMeta().getDisplayName());
        });
        sortedList.sort(Comparator.comparing(ItemStack::getType));
        sortedList.forEach(i -> user.getBase().getEnderChest().addItem(i));
        user.getBase().sendMessage(color("&aEnder Chest-ul a fost sortat!"));
    }

    public static void sortChest(User user) {
        Block block = user.getBase().getTargetBlock(null, 3);
        if (!(block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST))) {
            user.getBase().sendMessage(color("&cTrebuie sa te uiti la un chest!"));
            return;
        }
        Chest chest = (Chest) block.getState();
        if (FieldUtil.isFieldAtLocation(chest.getLocation())) {
            if (!(FieldUtil.getFieldByLocation(chest.getLocation()).isMember(user.getBase().getUniqueId())
                    || FieldUtil.getFieldByLocation(chest.getLocation()).isOwner(user.getBase().getUniqueId())
                    || user.hasPermission("elementals.protection.override"))) {
                user.getBase().sendMessage(color("&cNu poti sorta acest chest!"));
                return;
            }
        }
        Inventory inv = chest.getInventory();
        List<ItemStack> sortedList = new ArrayList<>();
        for (ItemStack item : inv.getContents()) {
            if (item == null || item.getType().equals(Material.AIR))
                continue;
            sortedList.add(item.clone());
        }
        inv.clear();
        sortedList.sort((o1, o2) -> {
            if (!o1.hasItemMeta() && o2.hasItemMeta())
                return -1;
            else if (!o2.hasItemMeta() && o1.hasItemMeta())
                return 1;
            else if (!(o1.hasItemMeta() && o2.hasItemMeta()))
                return 0;
            if (!o1.getItemMeta().hasDisplayName() && o2.getItemMeta().hasDisplayName())
                return -1;
            else if (!o2.getItemMeta().hasDisplayName() && o1.getItemMeta().hasDisplayName())
                return 1;
            else if (!(o1.getItemMeta().hasDisplayName() && o2.getItemMeta().hasDisplayName()))
                return 0;
            if (o1.getItemMeta().getDisplayName() == null && o2.getItemMeta().getDisplayName() != null)
                return -1;
            else if (o2.getItemMeta().getDisplayName() == null && o1.getItemMeta().getDisplayName() != null)
                return 1;
            else if (!(o1.getItemMeta().getDisplayName() == null && o2.getItemMeta().getDisplayName() == null))
                return 0;
            return o1.getItemMeta().getDisplayName().compareTo(o2.getItemMeta().getDisplayName());
        });
        sortedList.sort(Comparator.comparing(ItemStack::getType));
        sortedList.forEach(inv::addItem);
        user.getBase().sendMessage(color("&aChest-ul a fost sortat!"));
    }

    /*

    >>> Now we have world.rayTrace()

    public static List<Location> getPlayerDirectionLine(Player player, double distance, double addition, boolean bdet) {
        List<Location> list = new ArrayList<>();
        double pitch = Math.toRadians(player.getLocation().getPitch() + 90);
        double yaw = Math.toRadians(player.getLocation().getYaw() + 90);
        double dx = Math.sin(pitch) * Math.cos(yaw);
        double dz = Math.sin(pitch) * Math.sin(yaw);
        double dy = Math.cos(pitch);
        double dxa = dx * addition;
        double dya = dy * addition;
        double dza = dz * addition;
        double xM = dxa;
        double yM = dya;
        double zM = dza;
        Location loc = player.getEyeLocation().clone();
        while (player.getEyeLocation().distance(loc) <= distance) {
            loc = loc.add(xM, yM, zM);
            list.add(loc.clone());
            if (loc.getBlock().getType().isSolid() && bdet)
                break;
            xM += dxa;
            yM += dya;
            zM += dza;
        }
        return list;
    }
    */

    public static Entity getTargetEntity(Player player, double distance) {
        RayTraceResult rtr = player.getWorld().rayTraceEntities(player.getLocation(), player.getLocation().getDirection(), distance);
        if (rtr == null)
            return null;
        return rtr.getHitEntity();
    }


    public static String getCurrentDate() {
        return new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date());
    }

    public static void tickMotd() {
        int random = nextInt(2);
        switch (random) {
            case 0:
                motd = "&6PikaCraft &b- &aMinecraft la un alt nivel!\n&bUpdate: &aNEW SERVER!";
                break;
            case 1:
                motd = "&6PikaCraft &b- &5Minecraft la un alt nivel!\n&bUpdate: &aNEW SERVER!";
                break;
            default:
                motd = "&6PikaCraft &b- &cMinecraft la un alt nivel!\n&bUpdate: &aNEW SERVER!";
                break;
        }
    }

    public static String getMotd() {
        return motd;
    }

    public static Vector getRandomVector() {
        return Vector.getRandom().multiply(nextDouble(2));
    }

    public static Firework randomFirework(Location loc) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fm = fw.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder()
                .withColor(Color.fromBGR(ElementalsUtil.nextInt(255), ElementalsUtil.nextInt(255),
                        ElementalsUtil.nextInt(255)))
                .with(Type.values()[ElementalsUtil.nextInt(Type.values().length)]).flicker(ElementalsUtil.nextBoolean())
                .trail(ElementalsUtil.nextBoolean()).withColor(Color.fromBGR(ElementalsUtil.nextInt(255),
                        ElementalsUtil.nextInt(255), ElementalsUtil.nextInt(255)))
                .build());
        fm.setPower(nextInt(2));
        fw.setFireworkMeta(fm);
        return fw;
    }

    public static boolean isInt(String arg) {
        try {
            Integer.parseInt(arg);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static int nextInt(int max) {
        return (int) nextDouble(max);
    }

    public static double nextDouble(double max) {
        return Math.random() * max;
    }

    public static boolean nextBoolean() {
        return (nextDouble(1) >= 0.5);
    }

    public static String arrayToString(String[] args, String spliter) {
        return String.join(spliter, args);
    }

    // TODO nano
    public static void breakNano(User user, int pitch, int yaw, Block block, NanoType type) {
        final boolean b = (yaw > 315 && yaw <= 360 || yaw >= 0 && yaw <= 45) || (yaw > 135 && yaw <= 225);
        switch (type) {
            case PICKAXE:
                if (user.getNanoPickType().equals(ElementalsX.Nano.B)) {
                    if (pitch < -65 || pitch > 65) {
                        Block b1 = block.getRelative(BlockFace.NORTH);
                        if (b1.getType().equals(block.getType()))
                            breakNaturaly(b1, user.getBase());
                        Block b2 = block.getRelative(BlockFace.SOUTH);
                        if (b2.getType().equals(block.getType()))
                            breakNaturaly(b2, user.getBase());
                    } else {
                        if (b) {
                            Block b1 = block.getRelative(BlockFace.EAST);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            Block b2 = block.getRelative(BlockFace.WEST);
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                        } else if ((yaw > 45 && yaw <= 145) || (yaw > 225 && yaw <= 315)) {
                            Block b1 = block.getRelative(BlockFace.SOUTH);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            Block b2 = block.getRelative(BlockFace.NORTH);
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                        }
                    }
                } else if (user.getNanoPickType().equals(ElementalsX.Nano.C)) {
                    if (pitch < -65 || pitch > 65) {
                        Block b1 = block.getRelative(BlockFace.EAST);
                        if (b1.getType().equals(block.getType()))
                            breakNaturaly(b1, user.getBase());
                        Block b2 = block.getRelative(BlockFace.WEST);
                        if (b2.getType().equals(block.getType()))
                            breakNaturaly(b2, user.getBase());
                    } else {
                        if (b) {
                            Block b1 = block.getRelative(BlockFace.UP);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            Block b2 = block.getRelative(BlockFace.DOWN);
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                        } else if ((yaw > 45 && yaw <= 145) || (yaw > 225 && yaw <= 315)) {
                            Block b1 = block.getRelative(BlockFace.UP);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            Block b2 = block.getRelative(BlockFace.DOWN);
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                        }
                    }
                } else if (user.getNanoPickType().equals(ElementalsX.Nano.D)) {
                    if (pitch < -65 || pitch > 65) {
                        Block b1 = block.getRelative(BlockFace.EAST);
                        Block b2 = block.getRelative(BlockFace.WEST);
                        Block b3 = block.getRelative(BlockFace.NORTH);
                        Block b4 = block.getRelative(BlockFace.SOUTH);
                        Block b5 = block.getRelative(BlockFace.NORTH_EAST);
                        Block b6 = block.getRelative(BlockFace.NORTH_WEST);
                        Block b7 = block.getRelative(BlockFace.SOUTH_EAST);
                        Block b8 = block.getRelative(BlockFace.SOUTH_WEST);
                        if (b1.getType().equals(block.getType()))
                            breakNaturaly(b1, user.getBase());
                        if (b2.getType().equals(block.getType()))
                            breakNaturaly(b2, user.getBase());
                        if (b3.getType().equals(block.getType()))
                            breakNaturaly(b3, user.getBase());
                        if (b4.getType().equals(block.getType()))
                            breakNaturaly(b4, user.getBase());
                        if (b5.getType().equals(block.getType()))
                            breakNaturaly(b5, user.getBase());
                        if (b6.getType().equals(block.getType()))
                            breakNaturaly(b6, user.getBase());
                        if (b7.getType().equals(block.getType()))
                            breakNaturaly(b7, user.getBase());
                        if (b8.getType().equals(block.getType()))
                            breakNaturaly(b8, user.getBase());
                    } else {
                        if (b) {
                            Block b1 = block.getRelative(BlockFace.UP);
                            Block b2 = block.getRelative(BlockFace.DOWN);
                            Block b3 = block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST);
                            Block b4 = block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST);
                            Block b5 = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST);
                            Block b6 = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST);
                            Block b7 = block.getRelative(BlockFace.EAST);
                            Block b8 = block.getRelative(BlockFace.WEST);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                            if (b3.getType().equals(block.getType()))
                                breakNaturaly(b3, user.getBase());
                            if (b4.getType().equals(block.getType()))
                                breakNaturaly(b4, user.getBase());
                            if (b5.getType().equals(block.getType()))
                                breakNaturaly(b5, user.getBase());
                            if (b6.getType().equals(block.getType()))
                                breakNaturaly(b6, user.getBase());
                            if (b7.getType().equals(block.getType()))
                                breakNaturaly(b7, user.getBase());
                            if (b8.getType().equals(block.getType()))
                                breakNaturaly(b8, user.getBase());
                        } else if ((yaw > 45 && yaw <= 145) || (yaw > 225 && yaw <= 315)) {
                            Block b1 = block.getRelative(BlockFace.UP);
                            Block b2 = block.getRelative(BlockFace.DOWN);
                            Block b3 = block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH);
                            Block b4 = block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH);
                            Block b5 = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH);
                            Block b6 = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH);
                            Block b7 = block.getRelative(BlockFace.SOUTH);
                            Block b8 = block.getRelative(BlockFace.NORTH);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                            if (b3.getType().equals(block.getType()))
                                breakNaturaly(b3, user.getBase());
                            if (b4.getType().equals(block.getType()))
                                breakNaturaly(b4, user.getBase());
                            if (b5.getType().equals(block.getType()))
                                breakNaturaly(b5, user.getBase());
                            if (b6.getType().equals(block.getType()))
                                breakNaturaly(b6, user.getBase());
                            if (b7.getType().equals(block.getType()))
                                breakNaturaly(b7, user.getBase());
                            if (b8.getType().equals(block.getType()))
                                breakNaturaly(b8, user.getBase());
                        }
                    }
                }
                break;
            case AXE:
                if (user.getNanoAxeType().equals(ElementalsX.Nano.B)) {
                    if (pitch < -65 || pitch > 65) {
                        Block b1 = block.getRelative(BlockFace.NORTH);
                        if (b1.getType().equals(block.getType()))
                            breakNaturaly(b1, user.getBase());
                        Block b2 = block.getRelative(BlockFace.SOUTH);
                        if (b2.getType().equals(block.getType()))
                            breakNaturaly(b2, user.getBase());
                    } else {
                        if (b) {
                            Block b1 = block.getRelative(BlockFace.EAST);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            Block b2 = block.getRelative(BlockFace.WEST);
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                        } else if ((yaw > 45 && yaw <= 145) || (yaw > 225 && yaw <= 315)) {
                            Block b1 = block.getRelative(BlockFace.SOUTH);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            Block b2 = block.getRelative(BlockFace.NORTH);
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                        }
                    }
                } else if (user.getNanoAxeType().equals(ElementalsX.Nano.C)) {
                    if (pitch < -65 || pitch > 65) {
                        Block b1 = block.getRelative(BlockFace.EAST);
                        if (b1.getType().equals(block.getType()))
                            breakNaturaly(b1, user.getBase());
                        Block b2 = block.getRelative(BlockFace.WEST);
                        if (b2.getType().equals(block.getType()))
                            breakNaturaly(b2, user.getBase());
                    } else {
                        if (b) {
                            Block b1 = block.getRelative(BlockFace.UP);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            Block b2 = block.getRelative(BlockFace.DOWN);
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                        } else if ((yaw > 45 && yaw <= 145) || (yaw > 225 && yaw <= 315)) {
                            Block b1 = block.getRelative(BlockFace.UP);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            Block b2 = block.getRelative(BlockFace.DOWN);
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                        }
                    }
                } else if (user.getNanoAxeType().equals(ElementalsX.Nano.D)) {
                    if (pitch < -65 || pitch > 65) {
                        Block b1 = block.getRelative(BlockFace.EAST);
                        Block b2 = block.getRelative(BlockFace.WEST);
                        Block b3 = block.getRelative(BlockFace.NORTH);
                        Block b4 = block.getRelative(BlockFace.SOUTH);
                        Block b5 = block.getRelative(BlockFace.NORTH_EAST);
                        Block b6 = block.getRelative(BlockFace.NORTH_WEST);
                        Block b7 = block.getRelative(BlockFace.SOUTH_EAST);
                        Block b8 = block.getRelative(BlockFace.SOUTH_WEST);
                        if (b1.getType().equals(block.getType()))
                            breakNaturaly(b1, user.getBase());
                        if (b2.getType().equals(block.getType()))
                            breakNaturaly(b2, user.getBase());
                        if (b3.getType().equals(block.getType()))
                            breakNaturaly(b3, user.getBase());
                        if (b4.getType().equals(block.getType()))
                            breakNaturaly(b4, user.getBase());
                        if (b5.getType().equals(block.getType()))
                            breakNaturaly(b5, user.getBase());
                        if (b6.getType().equals(block.getType()))
                            breakNaturaly(b6, user.getBase());
                        if (b7.getType().equals(block.getType()))
                            breakNaturaly(b7, user.getBase());
                        if (b8.getType().equals(block.getType()))
                            breakNaturaly(b8, user.getBase());
                    } else {
                        if (b) {
                            Block b1 = block.getRelative(BlockFace.UP);
                            Block b2 = block.getRelative(BlockFace.DOWN);
                            Block b3 = block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST);
                            Block b4 = block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST);
                            Block b5 = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST);
                            Block b6 = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST);
                            Block b7 = block.getRelative(BlockFace.EAST);
                            Block b8 = block.getRelative(BlockFace.WEST);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                            if (b3.getType().equals(block.getType()))
                                breakNaturaly(b3, user.getBase());
                            if (b4.getType().equals(block.getType()))
                                breakNaturaly(b4, user.getBase());
                            if (b5.getType().equals(block.getType()))
                                breakNaturaly(b5, user.getBase());
                            if (b6.getType().equals(block.getType()))
                                breakNaturaly(b6, user.getBase());
                            if (b7.getType().equals(block.getType()))
                                breakNaturaly(b7, user.getBase());
                            if (b8.getType().equals(block.getType()))
                                breakNaturaly(b8, user.getBase());
                        } else if ((yaw > 45 && yaw <= 145) || (yaw > 225 && yaw <= 315)) {
                            Block b1 = block.getRelative(BlockFace.UP);
                            Block b2 = block.getRelative(BlockFace.DOWN);
                            Block b3 = block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH);
                            Block b4 = block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH);
                            Block b5 = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH);
                            Block b6 = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH);
                            Block b7 = block.getRelative(BlockFace.SOUTH);
                            Block b8 = block.getRelative(BlockFace.NORTH);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                            if (b3.getType().equals(block.getType()))
                                breakNaturaly(b3, user.getBase());
                            if (b4.getType().equals(block.getType()))
                                breakNaturaly(b4, user.getBase());
                            if (b5.getType().equals(block.getType()))
                                breakNaturaly(b5, user.getBase());
                            if (b6.getType().equals(block.getType()))
                                breakNaturaly(b6, user.getBase());
                            if (b7.getType().equals(block.getType()))
                                breakNaturaly(b7, user.getBase());
                            if (b8.getType().equals(block.getType()))
                                breakNaturaly(b8, user.getBase());
                        }
                    }
                }
                break;
            case SPADE:
                if (user.getNanoSpadeType().equals(ElementalsX.Nano.B)) {
                    if (pitch < -65 || pitch > 65) {
                        Block b1 = block.getRelative(BlockFace.NORTH);
                        if (b1.getType().equals(block.getType()))
                            breakNaturaly(b1, user.getBase());
                        Block b2 = block.getRelative(BlockFace.SOUTH);
                        if (b2.getType().equals(block.getType()))
                            breakNaturaly(b2, user.getBase());
                    } else {
                        if (b) {
                            Block b1 = block.getRelative(BlockFace.EAST);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            Block b2 = block.getRelative(BlockFace.WEST);
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                        } else if ((yaw > 45 && yaw <= 145) || (yaw > 225 && yaw <= 315)) {
                            Block b1 = block.getRelative(BlockFace.SOUTH);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            Block b2 = block.getRelative(BlockFace.NORTH);
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                        }
                    }
                } else if (user.getNanoSpadeType().equals(ElementalsX.Nano.C)) {
                    if (pitch < -65 || pitch > 65) {
                        Block b1 = block.getRelative(BlockFace.EAST);
                        if (b1.getType().equals(block.getType()))
                            breakNaturaly(b1, user.getBase());
                        Block b2 = block.getRelative(BlockFace.WEST);
                        if (b2.getType().equals(block.getType()))
                            breakNaturaly(b2, user.getBase());
                    } else {
                        if (b) {
                            Block b1 = block.getRelative(BlockFace.UP);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            Block b2 = block.getRelative(BlockFace.DOWN);
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                        } else if ((yaw > 45 && yaw <= 145) || (yaw > 225 && yaw <= 315)) {
                            Block b1 = block.getRelative(BlockFace.UP);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            Block b2 = block.getRelative(BlockFace.DOWN);
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                        }
                    }
                } else if (user.getNanoSpadeType().equals(ElementalsX.Nano.D)) {
                    if (pitch < -65 || pitch > 65) {
                        Block b1 = block.getRelative(BlockFace.EAST);
                        Block b2 = block.getRelative(BlockFace.WEST);
                        Block b3 = block.getRelative(BlockFace.NORTH);
                        Block b4 = block.getRelative(BlockFace.SOUTH);
                        Block b5 = block.getRelative(BlockFace.NORTH_EAST);
                        Block b6 = block.getRelative(BlockFace.NORTH_WEST);
                        Block b7 = block.getRelative(BlockFace.SOUTH_EAST);
                        Block b8 = block.getRelative(BlockFace.SOUTH_WEST);
                        if (b1.getType().equals(block.getType()))
                            breakNaturaly(b1, user.getBase());
                        if (b2.getType().equals(block.getType()))
                            breakNaturaly(b2, user.getBase());
                        if (b3.getType().equals(block.getType()))
                            breakNaturaly(b3, user.getBase());
                        if (b4.getType().equals(block.getType()))
                            breakNaturaly(b4, user.getBase());
                        if (b5.getType().equals(block.getType()))
                            breakNaturaly(b5, user.getBase());
                        if (b6.getType().equals(block.getType()))
                            breakNaturaly(b6, user.getBase());
                        if (b7.getType().equals(block.getType()))
                            breakNaturaly(b7, user.getBase());
                        if (b8.getType().equals(block.getType()))
                            breakNaturaly(b8, user.getBase());
                    } else {
                        if (b) {
                            Block b1 = block.getRelative(BlockFace.UP);
                            Block b2 = block.getRelative(BlockFace.DOWN);
                            Block b3 = block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST);
                            Block b4 = block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST);
                            Block b5 = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST);
                            Block b6 = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST);
                            Block b7 = block.getRelative(BlockFace.EAST);
                            Block b8 = block.getRelative(BlockFace.WEST);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                            if (b3.getType().equals(block.getType()))
                                breakNaturaly(b3, user.getBase());
                            if (b4.getType().equals(block.getType()))
                                breakNaturaly(b4, user.getBase());
                            if (b5.getType().equals(block.getType()))
                                breakNaturaly(b5, user.getBase());
                            if (b6.getType().equals(block.getType()))
                                breakNaturaly(b6, user.getBase());
                            if (b7.getType().equals(block.getType()))
                                breakNaturaly(b7, user.getBase());
                            if (b8.getType().equals(block.getType()))
                                breakNaturaly(b8, user.getBase());
                        } else if ((yaw > 45 && yaw <= 145) || (yaw > 225 && yaw <= 315)) {
                            Block b1 = block.getRelative(BlockFace.UP);
                            Block b2 = block.getRelative(BlockFace.DOWN);
                            Block b3 = block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH);
                            Block b4 = block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH);
                            Block b5 = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH);
                            Block b6 = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH);
                            Block b7 = block.getRelative(BlockFace.SOUTH);
                            Block b8 = block.getRelative(BlockFace.NORTH);
                            if (b1.getType().equals(block.getType()))
                                breakNaturaly(b1, user.getBase());
                            if (b2.getType().equals(block.getType()))
                                breakNaturaly(b2, user.getBase());
                            if (b3.getType().equals(block.getType()))
                                breakNaturaly(b3, user.getBase());
                            if (b4.getType().equals(block.getType()))
                                breakNaturaly(b4, user.getBase());
                            if (b5.getType().equals(block.getType()))
                                breakNaturaly(b5, user.getBase());
                            if (b6.getType().equals(block.getType()))
                                breakNaturaly(b6, user.getBase());
                            if (b7.getType().equals(block.getType()))
                                breakNaturaly(b7, user.getBase());
                            if (b8.getType().equals(block.getType()))
                                breakNaturaly(b8, user.getBase());
                        }
                    }
                }
                break;
            default:
                break;
        }
    }


    @SuppressWarnings("deprecation")
    public static void breakNaturaly(Block block, Player player) {
        NanoBlockBreakEvent event = new NanoBlockBreakEvent(player, block);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        Material mat = block.getState().getType();
        MaterialData data = block.getState().getData();
        if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            block.setType(Material.AIR);
            ItemStack item = new ItemStack(mat);
            item.setData(data);
            block.getWorld().dropItem(block.getLocation(), item);
            block.getWorld().spawnParticle(Particle.BLOCK_DUST, block.getLocation().clone().add(.5, .5, .5), 3,
                    block.getState().getData());
        } else
            block.breakNaturally(player.getInventory().getItemInMainHand());
    }

    public static void broadcastRawMessage(String raw) {
        Bukkit.getOnlinePlayers().forEach((Player player) -> player.sendRawMessage(raw));
        Bukkit.getConsoleSender().sendMessage(raw);
    }

    public static void changePoints(CommandSender sender, String name, String amount, boolean give) {
        int a;
        try {
            a = Integer.parseInt(amount);
        } catch (NumberFormatException exception) {
            sender.sendMessage(color("&cSuma trebuie sa fie un numar!"));
            return;
        }
        try {
            OfflinePlayer off = Bukkit.getOfflinePlayer(name);
            if (off.isOnline()) {
                User user = ElementalsX.getUser(off.getPlayer());
                if (give) {
                    user.addPoints(a);
                    sender.sendMessage(color("&3Ai adaugat &a" + a + " &3points in contul lui &c" + name + "&3!"));
                } else {
                    user.removePoints(a);
                    sender.sendMessage(color("&3Ai scos &a" + a + " &3points din contul lui &c" + name + "&3!"));
                }
            } else {
                ResultSet rs = ElementalsX.getBase()
                        .prepareStatement(
                                "SELECT points FROM pikapoints WHERE uuid='" + off.getUniqueId().toString() + "';")
                        .executeQuery();
                if (rs.next()) {
                    int points = rs.getInt("points");
                    if (give) {
                        points += a;
                        sender.sendMessage(color("&3Ai adaugat &a" + a + " &3points in contul lui &c" + name + "&3!"));
                    } else {
                        points -= a;
                        sender.sendMessage(color("&3Ai scos &a" + a + " &3points din contul lui &c" + name + "&3!"));
                    }
                    ElementalsX.getBase().prepareStatement("UPDATE pikapoints SET points='" + points + "' WHERE uuid='"
                            + off.getUniqueId().toString() + "';").executeUpdate();
                } else {
                    sender.sendMessage(color("&cJucatorul nu a fost gasit!"));
                }
            }
        } catch (SQLException ex) {
            sender.sendMessage(color("&cEroare la baza de date."));
        } catch (NullPointerException ex) {
            sender.sendMessage(color("&cJucatorul nu a fost gasit!"));
        }
    }

    public static void delayChatPlayer(User user) {
        UUID uuid = user.getBase().getUniqueId();
        delayChat.add(uuid);
        Bukkit.getScheduler().runTaskLater(ElementalsX.get(), () -> delayChat.remove(uuid), 20L);
    }

    public static void delayRandomTPPlayer(User user) {
        UUID uuid = user.getBase().getUniqueId();
        delayRandomTP.add(uuid);
        Bukkit.getScheduler().runTaskLater(ElementalsX.get(), () -> delayRandomTP.remove(uuid), 30 * 60 * 20L);
    }

    public static synchronized void delayRandomTPCmdPlayer(User user, int taskID) {
        delayRandomTPCmd.putIfAbsent(user.getBase().getUniqueId(), taskID);
    }

    public static synchronized boolean hasRandomTpCmdDelay(User user) {
        return delayRandomTPCmd.containsKey(user.getBase().getUniqueId());
    }

    public static synchronized void cancelRandomTpCmd(User user) {
        Bukkit.getScheduler().cancelTask(delayRandomTPCmd.get(user.getBase().getUniqueId()));
        if (RandomTpCommand.hasTeleportReq(user))
            RandomTpCommand.removeTeleportReq(user);
        user.getBase().sendMessage(color("&cTeleportarea a fost anulata..."));
    }

    public static synchronized void removeTandomTpCmdDelay(User user) {
        delayRandomTPCmd.remove(user.getBase().getUniqueId());
    }

    public static List<String> getPlayersNames() {
        List<String> args = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach((Player player) -> args.add(player.getName()));
        return args;
    }

    public static boolean hasChatDelay(User user) {
        return delayChat.contains(user.getBase().getUniqueId());
    }

    public static boolean hasRandomTpDelay(User user) {
        return delayRandomTP.contains(user.getBase().getUniqueId());
    }

    public static boolean isCreateInUse() {
        return create;
    }

    @SuppressWarnings("deprecation")
    public static void setPoints(CommandSender sender, String name, String amount, boolean set) {
        int a;
        try {
            a = Integer.parseInt(amount);
        } catch (NumberFormatException exception) {
            sender.sendMessage(color("&cSuma trebuie sa fie un numar!"));
            return;
        }
        try {
            OfflinePlayer off = Bukkit.getOfflinePlayer(name);
            if (off.isOnline()) {
                User user = ElementalsX.getUser(off.getPlayer());
                if (set) {
                    user.setPoints(a);
                    sender.sendMessage(color("&3Ai setat &a" + a + " &3points in contul lui &c" + name + "&3!"));
                } else {
                    user.setPoints(0);
                    sender.sendMessage(color("&3Ai resetat la 0 contul lui &c" + name + "&3!"));
                }
            } else {
                try {
                    ElementalsX.getBase().prepareStatement("UPDATE pikapoints SET points='" + (set ? a : 0)
                            + "' WHERE uuid='" + off.getUniqueId().toString() + "';").executeUpdate();
                    if (set)
                        sender.sendMessage(color("&3Ai setat &a" + a + " &3points in contul lui &c" + name + "&3!"));
                    else
                        sender.sendMessage(color("&3Ai resetat la 0 contul lui &c" + name + "&3!"));
                } catch (SQLException e) {
                    sender.sendMessage(color("&cJucatorul nu a fost gasit!"));
                }
            }
        } catch (NullPointerException ex) {
            sender.sendMessage(color("&cJucatorul nu a fost gasit!"));
        }
    }

    public static Location teleportLoc(Player player) {
        Location loc;
        World world = Bukkit.getWorld("world");
        int x, y, z;
        do {
            x = -10000 + nextInt(20000);
            z = -10000 + nextInt(20000);
            world.loadChunk(x, z);
            if (world.getHighestBlockAt(x, z).getType().isSolid())
                y = world.getHighestBlockYAt(x, z) + 1;
            else
                y = world.getHighestBlockYAt(x, z);
            loc = new Location(world, x, y, z);
        } while (world.getBiome(x, z) == Biome.OCEAN || world.getBiome(x, z) == Biome.DEEP_OCEAN
                || world.getBiome(x, z) == Biome.NETHER || world.getBiome(x, z) == Biome.THE_VOID
                || world.getBiome(x, z) == Biome.THE_END
                || world.getBiome(x, z) == Biome.RIVER || FieldUtil.isFieldAtLocation(loc));
        loc.getBlock().setType(Material.AIR);
        loc.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
        player.sendMessage(color("&bAi fost teleportat la x:" + x + " y:" + y + " z:" + z + "!"));
        return loc;
    }

    public static void toggleCreate(boolean b) {
        create = b;
    }

    public static int yawCorrection(int n) {
        if (n < 0)
            n += 360;
        return n;
    }

    public static double yawCorrectionN(float n) {
        double yaw = n;
        if (yaw < 0)
            yaw += 360;
        return yaw;
    }

    public static int foundBlocks(Block block) {
        int i = 1;
        for (int x = (block.getX() - 2); x < (block.getX() + 2); x++) {
            for (int y = (block.getY() - 2); y < (block.getY() + 2); y++) {
                for (int z = (block.getZ() - 2); z < (block.getZ() + 2); z++) {
                    if (y < 0)
                        continue;
                    Block founded = block.getWorld().getBlockAt(x, y, z);
                    if (founded.isEmpty())
                        continue;
                    if (!founded.getType().equals(block.getType()))
                        continue;
                    if (hasTag(founded, "found"))
                        continue;
                    setTag(founded, "found");
                    i++;
                }
            }
        }
        return i;
    }

    public static void toggleChat(boolean b) {
        stopChat = b;
    }

    public static boolean isChatStopped() {
        return stopChat;
    }

    // TODO we have a lag-hole here
    public static void procesWorld(World world) {
        for (Entity entity : world.getEntities()) {
            if (isValidEntity(entity, false)) {
                int count = getEntityCount(entity);
                for (Entity other : entity.getNearbyEntities(15, 15, 15))
                    if (isValidEntity(other, false))
                        if (match(entity, other)) {
                            if (other.getCustomName() != null)
                                if (other.getCustomName().startsWith(color("&cStack")))
                                    continue;
                            if (count <= getMaximEntityCount(entity.getType())) {
                                other.remove();
                                int finalCount = count + 1;
                                setEntityCount(entity, finalCount);
                                entity.setCustomName(color("&cStack&f: &a" + finalCount));
                                entity.setCustomNameVisible(false);
                            }
                        }
            }
        }
    }

    private static boolean match(Entity entity, Entity other) {
        return entity.getType() == other.getType();
    }

    public static boolean isValidEntity(Entity entity, boolean isDead) {
        if (!isDead) {
            if (!entity.isValid())
                return false;
            if (!(entity instanceof LivingEntity))
                return false;
        }
        if (CitizensAPI.getNPCRegistry().isNPC(entity))
            return false;
        if (entity.isInsideVehicle())
            return false;
        switch (entity.getType()) {
            case BAT:
            case CHICKEN:
            case COW:
            case ENDERMITE:
            case GHAST:
            case GIANT:
            case MUSHROOM_COW:
            case PIG:
            case POLAR_BEAR:
            case RABBIT:
            case SHEEP:
            case SILVERFISH:
            case WITCH:
            case BLAZE:
            case CAVE_SPIDER:
            case CREEPER:
            case ENDERMAN:
            case GUARDIAN:
            case SPIDER:
            case SQUID:
            case PIG_ZOMBIE:
            case SKELETON:
            case ZOMBIE:
            case WITHER_SKELETON:
            case HUSK:
            case STRAY:
            case ZOMBIE_VILLAGER:
            case VINDICATOR:
                return true;
            default:
                return false;
        }
    }

    public static int getMaximEntityCount(EntityType type) {
        switch (type) {
            case BAT:
            case CHICKEN:
            case COW:
            case ENDERMITE:
            case GHAST:
            case GIANT:
            case MUSHROOM_COW:
            case PIG:
            case POLAR_BEAR:
            case RABBIT:
            case SHEEP:
            case SILVERFISH:
            case WITCH:
            case WITHER_SKELETON:
            case ZOMBIE_VILLAGER:
            case VINDICATOR:
                return 5;
            case BLAZE:
            case CAVE_SPIDER:
            case CREEPER:
            case ENDERMAN:
            case GUARDIAN:
            case SPIDER:
            case SQUID:
                return 10;
            case PIG_ZOMBIE:
            case SKELETON:
            case ZOMBIE:
            case HUSK:
            case STRAY:
                return 15;
            default:
                return 1;
        }
    }

    public static int getEntityCount(Entity entity) {
        if (!TagRegister.isStored(entity))
            return 1;
        CompoundTag tag = TagRegister.getStored(entity);
        if (!tag.contains("count", TagType.INT))
            return 1;
        return tag.getInt("count");
    }

    public static void setEntityCount(Entity entity, int count) {
        CompoundTag tag = TagRegister.isStored(entity) ? TagRegister.getStored(entity) : TagRegister.create(entity);
        tag.putInt("count", count);
    }

    public static void setTag(Entity entity, String arg) {
        CompoundTag tag = TagRegister.isStored(entity) ? TagRegister.getStored(entity) : TagRegister.create(entity);
        tag.putByte(arg, (byte) 1);
    }

    public static boolean hasTag(Entity entity, String arg) {
        if (!TagRegister.isStored(entity))
            return false;
        return TagRegister.getStored(entity).contains(arg);
    }

    public static void setTag(Block block, String arg) {
        CompoundTag tag = TagRegister.isStored(block) ? TagRegister.getStored(block) : TagRegister.create(block);
        tag.putByte(arg, (byte) 1);
    }

    public static boolean hasTag(Block block, String arg) {
        if (!TagRegister.isStored(block))
            return false;
        return TagRegister.getStored(block).contains(arg);
    }

    public static void removeTag(Entity entity, String arg) {
        if (!TagRegister.isStored(entity))
            return;
        TagRegister.getStored(entity).remove(arg);
    }

    public static void removeTag(Block block, String arg) {
        if (!TagRegister.isStored(block))
            return;
        TagRegister.getStored(block).remove(arg);
    }

    public static void addDir(File dirObj, ZipOutputStream out) throws IOException {
        File[] files = dirObj.listFiles();
        byte[] tmpBuf = new byte[1024];
        if (files == null)
            return;
        for (File file : files) {
            if (file.isDirectory()) {
                addDir(file, out);
                continue;
            }
            FileInputStream in = new FileInputStream(file.getAbsolutePath());
            out.putNextEntry(new ZipEntry(file.getAbsolutePath()));
            int len;
            while ((len = in.read(tmpBuf)) > 0)
                out.write(tmpBuf, 0, len);
            out.closeEntry();
            in.close();
        }
    }

    public static void zipDir(String zipFileName, String... dir) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        for (String abc : dir)
            addDir(new File(abc), out);
        out.close();
    }
}
