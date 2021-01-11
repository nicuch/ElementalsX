package ro.nicuch.elementalsx.elementals;

import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.protection.Field;
import ro.nicuch.elementalsx.protection.FieldUtil;
import ro.nicuch.tag.TagRegister;
import ro.nicuch.tag.nbt.CompoundTag;
import ro.nicuch.tag.register.ChunkRegister;

import java.util.*;

public class ElementalsUtil {
    private static final List<String> autoMsg = Arrays.asList(
            "&8[&eInfo&8] &a&oUtilizarea hack-urilor este pedepsita cu &4&o&lBAN&a&o!",
            "&8[&eInfo&8] &b&oNu uita sa votezi in fiecare zi folosind comanda &f&o[&6&o/vote&f&o]&b&o! &a&oVei primi un premiu de fiecare data cand votezi. &6:)",
            "&8[&eInfo&8] &f&o[&6&o/sort&f&o] &b&oPoti sorta inventare! &a:D",
            "&8[&eInfo&8] &a&oDaca ai nevoie de bani, scrie &f&o[&6&o/jobs&f&o] &a&osi ia-ti un job!",
            "&8[&eInfo&8] &6&oProtectia se face folosind cubul de &b&oDiamant&6&o! &a&oNu uita sa o pui altfel casa ta va fi distrusa!",
            "&8[&eInfo&8] &a&oStaff-ul nu raspunde de obiectele pierdute!",
            "&8[&eInfo&8] &c&oDaca descoperiti un bug, va rugam sa-l raportati! &6&oVeti primi un bonus daca bug-ul nu a fost raportat deja!");

    public static boolean checkCollision(Block block, Entity entity) {
        BoundingBox blockBoundingBox = block.getBoundingBox();
        BoundingBox entityBoundingBox = entity.getBoundingBox();

        Location entityLocation = entity.getLocation();

        double blockMinX = block.getX();
        double blockMaxX = blockMinX + 1;

        double blockMinY = block.getY();
        double blockMaxY = blockMinY + 1;

        double blockMinZ = block.getZ();
        double blockMaxZ = blockMinZ + 1;

        double entityMinX = (entityLocation.getX() - (entityBoundingBox.getWidthX() / 2)) - 0.1;
        double entityMaxX = entityMinX + entityBoundingBox.getWidthX() + 0.2;

        double entityMinY = (entityLocation.getY() - (entityBoundingBox.getHeight() / 2)) - 0.1;
        double entityMaxY = entityMinY + entityBoundingBox.getHeight() + 0.2;

        double entityMinZ = (entityLocation.getZ() - (entityBoundingBox.getWidthZ() / 2)) - 0.1;
        double entityMaxZ = entityMinZ + entityBoundingBox.getWidthZ() + 0.2;

        return (blockMinX <= entityMaxX && blockMaxX >= entityMinX) &&
                (blockMinY <= entityMaxY && blockMaxY >= entityMinY) &&
                (blockMinZ <= entityMaxZ && blockMaxZ >= entityMinZ);
    }

    public static List<String> getAutoMessages() {
        return autoMsg;
    }

    @NotNull
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
            Field field = FieldUtil.getFieldByLocation(block.getLocation());
            if (!(field.isMember(user.getBase().getUniqueId())
                    || field.isOwner(user.getBase().getUniqueId())
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

    public static Firework randomFirework(Location loc) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fm = fw.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder()
                .withColor(Color.fromBGR(ElementalsUtil.nextInt(255), ElementalsUtil.nextInt(255),
                        ElementalsUtil.nextInt(255)), Color.fromBGR(ElementalsUtil.nextInt(255), ElementalsUtil.nextInt(255),
                        ElementalsUtil.nextInt(255)), Color.fromBGR(ElementalsUtil.nextInt(255), ElementalsUtil.nextInt(255),
                        ElementalsUtil.nextInt(255)), Color.fromBGR(ElementalsUtil.nextInt(255), ElementalsUtil.nextInt(255),
                        ElementalsUtil.nextInt(255)))
                .with(Type.values()[ElementalsUtil.nextInt(Type.values().length)]).flicker(ElementalsUtil.nextBoolean())
                .trail(ElementalsUtil.nextBoolean()).withColor(Color.fromBGR(ElementalsUtil.nextInt(255),
                        ElementalsUtil.nextInt(255), ElementalsUtil.nextInt(255)))
                .build());
        fm.setPower(1);
        fw.setFireworkMeta(fm);
        return fw;
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

    public static List<String> getPlayersNames() {
        List<String> args = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> args.add(player.getName()));
        return args;
    }

    public static int foundBlocks(Block block) {
        int i = 0;
        for (int x = (block.getX() - 3); x < (block.getX() + 3); x++) {
            for (int y = (block.getY() - 3); y < (block.getY() + 3); y++) {
                for (int z = (block.getZ() - 3); z < (block.getZ() + 3); z++) {
                    if (y < 0)
                        continue;
                    Block founded = block.getWorld().getBlockAt(x, y, z);
                    if (founded.isEmpty())
                        continue;
                    if (founded.getType() != block.getType())
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

    public static void setTag(Entity entity, String arg) {
        CompoundTag tag = TagRegister.getOrCreateEntity(entity);
        tag.putBoolean(arg, true);
    }

    public static boolean hasTag(Entity entity, String arg) {
        if (TagRegister.getStored(entity).isEmpty())
            return false;
        return TagRegister.getStored(entity).get().contains(arg);
    }

    public static void setTag(Block block, String arg) {
        CompoundTag tag = TagRegister.getOrCreateBlock(block);
        tag.putBoolean(arg, true);
    }

    public static boolean hasTag(Block block, String arg) {
        ChunkRegister chunkRegister = TagRegister.getOrLoadWorld(block.getWorld()).getOrLoadRegion(block.getChunk()).getOrLoadChunk(block.getChunk());
        if (!chunkRegister.isBlockStored(block))
            return false;
        return chunkRegister.getStoredBlockUnsafe(block).contains(arg);
    }

    public static void removeTag(Entity entity, String arg) {
        ChunkRegister chunkRegister = TagRegister.getOrLoadWorld(entity.getWorld()).getOrLoadRegion(entity.getLocation().getChunk()).getOrLoadChunk(entity.getLocation().getChunk());
        if (!chunkRegister.isEntityStored(entity))
            return;
        chunkRegister.getStoredEntityUnsafe(entity.getUniqueId()).remove(arg);
    }

    public static void removeTag(Block block, String arg) {
        ChunkRegister chunkRegister = TagRegister.getOrLoadWorld(block.getWorld()).getOrLoadRegion(block.getChunk()).getOrLoadChunk(block.getChunk());
        if (!chunkRegister.isBlockStored(block))
            return;
        chunkRegister.getStoredBlockUnsafe(block).remove(arg);
    }
}
