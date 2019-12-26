package ro.nicuch.elementalsx.elementals;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.protection.FieldUtil;
import ro.nicuch.lwjnbtl.CompoundTag;
import ro.nicuch.tag.TagRegister;

import java.util.*;

public class ElementalsUtil {
    private static final Set<UUID> delayRandomTP = new HashSet<>();
    private static final Set<UUID> delayChat = new HashSet<>();
    private static boolean stopChat = false;
    private static String motd = "&6PikaCraft &b- &cMinecraft la un alt nivel!";
    private static final List<String> autoMsg = Arrays.asList(
            "&aUtilizarea hack-urilor este pedepsita cu &4&lBAN&a!",
            "&bNu uita sa votezi in fiecare zi folosind comanda &f[&6/vote&f]&b! &aVei primi un premiu de fiecare data cand votezi. &6:)",
            "&f[&6/sort&f] &bPoti sorta inventare! &a:D",
            "&aDaca ai nevoie de bani, scrie &f[&6/jobs&f] &asi ia-ti un job!",
            "&6Protectia se face folosind cubul de &bDiamant&6! &aNu uita sa o pui altfel casa ta va fi distrusa!",
            "&aStaff-ul nu raspunde de obiectele pierdute!",
            "&cDaca descoperiti un bug, va rugam sa-l raportati! &6Ve-ti primi un bonus daca bug-ul nu a fost raportat deja!",
            "&cPoti raporta &eJucatorii&c/&5Donatorii&c/&4Staff-ul &cserverului in cazul in care acestia &bincalca regulamentul&c!",
            "&6Asteptam sugestiile si ideile voastre!",
            "&ePikaaa-pii! Pikaaa-chu! &a:)");

    public static List<String> getAutoMessages() {
        return autoMsg;
    }

    public static String color(String arg) {
        return ChatColor.translateAlternateColorCodes('&', arg);
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
            if (item == null || item.getType() == Material.AIR)
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
            if (item == null || item.getType() == Material.AIR)
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

    public static void sortInventoryHolder(User user) {
        Block block = user.getBase().getTargetBlock(null, 3);
        if (!(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST
                || block.getType() == Material.BARREL || Tag.SHULKER_BOXES.isTagged(block.getType()))) {
            user.getBase().sendMessage(color("&cTrebuie sa te uiti la un block cu inventar!"));
            return;
        }
        InventoryHolder holder = (InventoryHolder) block.getState();
        if (FieldUtil.isFieldAtLocation(block.getLocation())) {
            if (!(FieldUtil.getFieldByLocation(block.getLocation()).isMember(user.getBase().getUniqueId())
                    || FieldUtil.getFieldByLocation(block.getLocation()).isOwner(user.getBase().getUniqueId())
                    || user.hasPermission("elementals.protection.override"))) {
                user.getBase().sendMessage(color("&cNu poti sorta acest chest!"));
                return;
            }
        }
        Inventory inv = holder.getInventory();
        List<ItemStack> sortedList = new ArrayList<>();
        for (ItemStack item : inv.getContents()) {
            if (item == null || item.getType() == Material.AIR)
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
        user.getBase().sendMessage(color("&aInventarul blocului a fost sortat!"));
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

    public static Optional<? extends Player> getPlayer(String name) {
        return Bukkit.getOnlinePlayers().stream().filter(Objects::nonNull).filter(p -> name.equals(p.getName())).findFirst();
    }

    public static Optional<OfflinePlayer> getOfflinePlayer(String name) {
        return Arrays.stream(Bukkit.getOfflinePlayers()).filter(Objects::nonNull).filter(p -> name.equals(p.getName())).findFirst();
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

    public static List<String> getPlayersNames() {
        List<String> args = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> args.add(player.getName()));
        return args;
    }

    public static boolean hasChatDelay(User user) {
        return delayChat.contains(user.getBase().getUniqueId());
    }

    public static boolean hasRandomTpDelay(User user) {
        return delayRandomTP.contains(user.getBase().getUniqueId());
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

    public static void setTag(Entity entity, String arg) {
        CompoundTag tag = TagRegister.isStored(entity) && TagRegister.getStored(entity).isPresent() ? TagRegister.getStored(entity).get() : TagRegister.create(entity);
        tag.putByte(arg, (byte) 1);
    }

    public static boolean hasTag(Entity entity, String arg) {
        if (!TagRegister.isStored(entity))
            return false;
        if (!TagRegister.getStored(entity).isPresent())
            return false;
        return TagRegister.getStored(entity).get().contains(arg);
    }

    public static void setTag(Block block, String arg) {
        CompoundTag tag = TagRegister.isStored(block) && TagRegister.getStored(block).isPresent() ? TagRegister.getStored(block).get() : TagRegister.create(block);
        tag.putByte(arg, (byte) 1);
    }

    public static boolean hasTag(Block block, String arg) {
        if (!TagRegister.isStored(block))
            return false;
        if (!TagRegister.getStored(block).isPresent())
            return false;
        return TagRegister.getStored(block).get().contains(arg);
    }

    public static void removeTag(Entity entity, String arg) {
        if (!TagRegister.isStored(entity))
            return;
        if (!TagRegister.getStored(entity).isPresent())
            return;
        TagRegister.getStored(entity).get().remove(arg);
    }

    public static void removeTag(Block block, String arg) {
        if (!TagRegister.isStored(block))
            return;
        if (!TagRegister.getStored(block).isPresent())
            return;
        TagRegister.getStored(block).get().remove(arg);
    }
}
