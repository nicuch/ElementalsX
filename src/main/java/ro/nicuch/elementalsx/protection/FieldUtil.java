package ro.nicuch.elementalsx.protection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.block.BlockBreakEvent;

import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

public class FieldUtil {
    private final static Map<String, Field> loadedFields = new HashMap<>();

    public static void registerField(User user, Block block, String id, Block maxLoc, Block minLoc) {
        Field field = new Field(id, user.getBase().getUniqueId(), maxLoc, minLoc, block.getChunk(), block.getWorld());
        loadedFields.putIfAbsent(id, field);
    }

    public static void unregisterField(Block block) {
        String id = getFieldIdByBlock(block);
        if (isFieldLoaded(id)) {
            Field field = getFieldById(id);
            field.delete();
            loadedFields.remove(id);
        }
    }

    public static boolean isFieldNerby(User user, Location loc) {
        Location l1 = loc.clone().add(-25, 0, -25);
        if (isFieldAtLocation(l1))
            if (!getFieldByLocation(l1).isOwner(user.getBase().getUniqueId()))
                return true;
        Location l2 = loc.clone().add(25, 0, 25);
        if (isFieldAtLocation(l2))
            if (!getFieldByLocation(l2).isOwner(user.getBase().getUniqueId()))
                return true;
        Location l3 = loc.clone().add(-25, 0, 25);
        if (isFieldAtLocation(l3))
            if (!getFieldByLocation(l3).isOwner(user.getBase().getUniqueId()))
                return true;
        Location l4 = loc.clone().add(25, 0, -25);
        if (isFieldAtLocation(l4))
            return !getFieldByLocation(l4).isOwner(user.getBase().getUniqueId());
        return false;
    }

    public static List<UUID> convertStringsToUUIDs(List<String> strings) {
        List<UUID> list = new ArrayList<>();
        strings.forEach((String arg) -> list.add(UUID.fromString(arg)));
        return list;
    }

    public static List<String> convertUUIDsToStrings(List<UUID> uuids) {
        List<String> list = new ArrayList<>();
        uuids.forEach((UUID uuid) -> list.add(uuid.toString()));
        return list;
    }

    public static boolean areThereEnoughProtections(Chunk chunk) {
        try {
            ResultSet rs = ElementalsX
                    .getBase().prepareStatement("SELECT COUNT(*) FROM protection WHERE chunkx='" + chunk.getX()
                            + "' AND chunkz='" + chunk.getZ() + "' AND world='" + chunk.getWorld().getName() + "';")
                    .executeQuery();
            if (rs.next())
                return rs.getInt(1) >= 4;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void loadFieldsInChunk(Chunk chunk) {
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            try {
                ResultSet rs = ElementalsX
                        .getBase().prepareStatement("SELECT * FROM protection WHERE chunkx='" + chunk.getX()
                                + "' AND chunkz='" + chunk.getZ() + "' AND world='" + chunk.getWorld().getName() + "';")
                        .executeQuery();
                while (rs.next()) {
                    // Get Strings and Ints to not catch SQLException in Async
                    String worldName = rs.getString("world");
                    int y = rs.getInt("y");
                    int maxx = rs.getInt("maxx");
                    int maxz = rs.getInt("maxz");
                    int minx = rs.getInt("minx");
                    int minz = rs.getInt("minz");
                    String id = rs.getString("id");
                    UUID uuid = UUID.fromString(rs.getString("owner"));
                    Bukkit.getScheduler().runTask(ElementalsX.get(), () -> {
                        // Never access BukkitAPI async
                        World world = Bukkit.getWorld(worldName);
                        Block maxLoc = world.getBlockAt(maxx, y, maxz);
                        Block minLoc = world.getBlockAt(minx, y, minz);
                        Field field = new Field(id, uuid, maxLoc, minLoc, chunk, world);
                        loadedFields.putIfAbsent(id, field);
                    });
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public static void takeProtection(User user) {
        Block block = user.getBase().getTargetBlock(null, 3);
        if (!block.getType().equals(Material.DIAMOND_BLOCK)) {
            user.getBase().sendMessage(ElementalsUtil.color("&cTrebuie sa te uiti la blocul de diamant (&arespectiv protectia&c)!"));
            return;
        }
        BlockBreakEvent event = new BlockBreakEvent(block, user.getBase());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        block.breakNaturally();
    }

    public static void unloadFieldsInChunk(Chunk chunk) {
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            try {
                ResultSet rs = ElementalsX
                        .getBase().prepareStatement("SELECT id FROM protection WHERE chunkx='" + chunk.getX()
                                + "' AND chunkz='" + chunk.getZ() + "' AND world='" + chunk.getWorld().getName() + "';")
                        .executeQuery();
                while (rs.next()) {
                    String id = rs.getString("id");
                    Bukkit.getScheduler().runTask(ElementalsX.get(), () -> {
                        Field field = loadedFields.get(id);
                        field.save();
                        loadedFields.remove(id);
                    });
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public static boolean isFieldAtLocation(Location location) {
        for (Field field : loadedFields.values())
            if (field.getMaximLocation().getX() >= location.getBlockX()
                    && field.getMaximLocation().getZ() >= location.getBlockZ()
                    && field.getMinimLocation().getX() <= location.getBlockX()
                    && field.getMinimLocation().getZ() <= location.getBlockZ()
                    && field.getWorld().getName().equals(location.getWorld().getName()))
                return true;
        return false;
    }

    public static Field getFieldByLocation(Location location) {
        for (Field field : loadedFields.values())
            if (field.getMaximLocation().getX() >= location.getBlockX()
                    && field.getMaximLocation().getZ() >= location.getBlockZ()
                    && field.getMinimLocation().getX() <= location.getBlockX()
                    && field.getMinimLocation().getZ() <= location.getBlockZ()
                    && field.getWorld().getName().equals(location.getWorld().getName()))
                return field;
        throw new NullPointerException("Nici o protectie gasita la locatia data.");
    }

    public static Field getFieldById(String id) {
        if (loadedFields.containsKey(id))
            return loadedFields.get(id);
        else
            throw new NullPointerException("Nici o protectie gasita dupa id-ul dat.");
    }

    public static boolean isFieldLoaded(String id) {
        return loadedFields.containsKey(id);
    }

    public static Collection<Field> getLoadedFields() {
        return loadedFields.values();
    }

    public static boolean isFieldBlock(Block block) {
        try {
            ResultSet rs = ElementalsX.getBase()
                    .prepareStatement("SELECT id FROM protection WHERE x='" + block.getX() + "' AND y='" + block.getY()
                            + "' AND z='" + block.getZ() + "' AND world='" + block.getWorld().getName() + "';")
                    .executeQuery();
            return rs.next();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public static String getFieldIdByBlock(Block block) {
        return "x" + block.getX() + "y" + block.getY() + "z"
                + block.getZ() + "world" + block.getWorld().getName();
    }

    public static void updateUser(User user, Location loc) {
        if (isFieldAtLocation(loc)) {
            if (!user.isInField()) {
                user.getBase().sendMessage(ElementalsUtil.color("&bAi intrat in protectia lui "
                        + Bukkit.getOfflinePlayer(getFieldByLocation(loc).getOwner()).getName() + "!"));
            } else if (user.isInField() && !getFieldByLocation(loc).isOwner(user.getLastFieldOwner())) {
                user.getBase().sendMessage(ElementalsUtil.color("&bAi iesit din protectia lui "
                        + Bukkit.getOfflinePlayer(user.getLastFieldOwner()).getName() + "!"));
                user.getBase().sendMessage(ElementalsUtil.color("&bAi intrat in protectia lui "
                        + Bukkit.getOfflinePlayer(getFieldByLocation(loc).getOwner()).getName() + "!"));
            }
            user.getBase().setCollidable(false);
            user.toggleField(true);
            user.setLastFieldOwner(getFieldByLocation(loc).getOwner());
        } else if (!isFieldAtLocation(loc)) {
            if (user.isInField())
                user.getBase().sendMessage(ElementalsUtil.color("&bAi iesit din protectia lui "
                        + Bukkit.getOfflinePlayer(user.getLastFieldOwner()).getName() + "!"));
            user.toggleField(false);
            user.getBase().setCollidable(true);
        }
    }

    @SuppressWarnings("deprecation")
    public static void allowInField(User user, String name, boolean all, boolean allow) {
        try {
            OfflinePlayer member = Bukkit.getOfflinePlayer(name);
            if (!all) {
                Field field;
                Block block = user.getBase().getTargetBlock(null, 3);
                if (block.getType().equals(Material.DIAMOND_BLOCK) && isFieldBlock(block))
                    field = getFieldById(getFieldIdByBlock(block));
                else
                    field = getFieldByLocation(user.getBase().getLocation());
                if (!(field.isOwner(user.getBase().getUniqueId()) || user.hasPermission("protection.override"))) {
                    user.getBase().sendMessage(ElementalsUtil.color("&bNu esti detinatorul protectiei!"));
                    return;
                }
                if (allow) {
                    field.addMember(member.getUniqueId());
                    user.getBase().sendMessage(ElementalsUtil.color("&aJucatorul &b" + name + " &aa fost adaugat."));
                } else {
                    field.removeMember(member.getUniqueId());
                    user.getBase().sendMessage(ElementalsUtil.color("&aJucatorul &b" + name + " &aa fost sters."));
                }
            } else {
                ResultSet rs = ElementalsX.getBase().prepareStatement(
                        "SELECT id FROM protection WHERE owner='" + user.getBase().getUniqueId().toString() + "';")
                        .executeQuery();
                while (rs.next()) {
                    if (allow) {
                        if (isFieldLoaded(rs.getString("id")))
                            getFieldById(rs.getString("id")).addMember(member.getUniqueId());
                    } else {
                        if (isFieldLoaded(rs.getString("id")))
                            getFieldById(rs.getString("id")).removeMember(member.getUniqueId());
                    }
                }
                if (allow)
                    user.getBase().sendMessage(ElementalsUtil.color("&aJucatorul &b" + name + " &aa fost adaugat in toate protectiile."));
                else
                    user.getBase().sendMessage(ElementalsUtil.color("&aJucatorul &c" + name + " &aa fost sters din toate protectiile."));
            }
        } catch (Exception exception) {
            user.getBase().sendMessage(ElementalsUtil.color("&cEroare. &aContacteaza un admin!"));
            exception.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public static void visualiseField(User user) {
        if (!isFieldAtLocation(user.getBase().getLocation())) {
            user.getBase().sendMessage(ElementalsUtil.color("&cTrebuie sa fii in protectia ta ca sa poti vizualiza!"));
            return;
        }
        Field field = getFieldByLocation(user.getBase().getLocation());
        if (!(field.isMember(user.getBase().getUniqueId()) || field.isOwner(user.getBase().getUniqueId())
                || user.hasPermission("protection.override"))) {
            user.getBase().sendMessage(ElementalsUtil.color("&cTrebuie sa fii membru al protectiei ca sa poti vizualiza!"));
            return;
        }
        int maxx = field.getMaximLocation().getX();
        int maxz = field.getMaximLocation().getZ();
        int minx = field.getMinimLocation().getX();
        int minz = field.getMinimLocation().getZ();
        int midx = maxx - ((maxx - minx) / 2);
        int midz = maxz - ((maxz - minz) / 2);
        BlockData glass = Material.GLASS.createBlockData();
        for (int y = 0; y < 256; y++) {
            Location a = new Location(field.getWorld(), maxx, y, maxz);
            user.getBase().sendBlockChange(a, glass);
            Location b = new Location(field.getWorld(), minx, y, minz);
            user.getBase().sendBlockChange(b, glass);
            Location c = new Location(field.getWorld(), minx, y, maxz);
            user.getBase().sendBlockChange(c, glass);
            Location d = new Location(field.getWorld(), maxx, y, minz);
            user.getBase().sendBlockChange(d, glass);
            Location e = new Location(field.getWorld(), maxx, y, midz);
            user.getBase().sendBlockChange(e, glass);
            Location f = new Location(field.getWorld(), minx, y, midz);
            user.getBase().sendBlockChange(f, glass);
            Location g = new Location(field.getWorld(), midx, y, maxz);
            user.getBase().sendBlockChange(g, glass);
            Location h = new Location(field.getWorld(), midx, y, minz);
            user.getBase().sendBlockChange(h, glass);
        }
        for (int y = 0; y < 256; y += 16) {
            for (int x = minx; x < maxx; x++) {
                Location loc0 = new Location(field.getWorld(), x, y, maxz);
                user.getBase().sendBlockChange(loc0, glass);
                user.getBase().sendBlockChange(loc0, glass);
                Location loc1 = new Location(field.getWorld(), x, y, minz);
                user.getBase().sendBlockChange(loc1, glass);
            }
            for (int z = minz; z < maxz; z++) {
                Location loc0 = new Location(field.getWorld(), maxx, y, z);
                user.getBase().sendBlockChange(loc0, Material.GLASS, (byte) 0);
                Location loc1 = new Location(field.getWorld(), minx, y, z);
                user.getBase().sendBlockChange(loc1, Material.GLASS, (byte) 0);
            }
        }
        Bukkit.getScheduler().runTaskLater(ElementalsX.get(), () -> {
            for (int y = 0; y < 256; y++) {
                Location a = new Location(field.getWorld(), maxx, y, maxz);
                user.getBase().sendBlockChange(a, a.getBlock().getBlockData());
                Location b = new Location(field.getWorld(), minx, y, minz);
                user.getBase().sendBlockChange(b, b.getBlock().getBlockData());
                Location c = new Location(field.getWorld(), minx, y, maxz);
                user.getBase().sendBlockChange(c, c.getBlock().getBlockData());
                Location d = new Location(field.getWorld(), maxx, y, minz);
                user.getBase().sendBlockChange(d, d.getBlock().getBlockData());
                Location e = new Location(field.getWorld(), maxx, y, midz);
                user.getBase().sendBlockChange(e, e.getBlock().getBlockData());
                Location f = new Location(field.getWorld(), minx, y, midz);
                user.getBase().sendBlockChange(f, f.getBlock().getBlockData());
                Location g = new Location(field.getWorld(), midx, y, maxz);
                user.getBase().sendBlockChange(g, g.getBlock().getBlockData());
                Location h = new Location(field.getWorld(), midx, y, minz);
                user.getBase().sendBlockChange(h, h.getBlock().getBlockData());
            }
            for (int y = 0; y < 256; y += 16) {
                for (int x = minx; x < maxx; x++) {
                    Location loc0 = new Location(field.getWorld(), x, y, maxz);
                    user.getBase().sendBlockChange(loc0, loc0.getBlock().getBlockData());
                    Location loc1 = new Location(field.getWorld(), x, y, minz);
                    user.getBase().sendBlockChange(loc1, loc1.getBlock().getBlockData());
                }
                for (int z = minz; z < maxz; z++) {
                    Location loc0 = new Location(field.getWorld(), maxx, y, z);
                    user.getBase().sendBlockChange(loc0, loc0.getBlock().getBlockData());
                    Location loc1 = new Location(field.getWorld(), minx, y, z);
                    user.getBase().sendBlockChange(loc1, loc1.getBlock().getBlockData());
                }
            }
        }, 20 * 20);
    }

    public static void toggleFun(User user) {
        Field field;
        Block block = user.getBase().getTargetBlock(null, 3);
        if (block.getType().equals(Material.DIAMOND_BLOCK) && isFieldBlock(block))
            field = getFieldById(getFieldIdByBlock(block));
        else
            field = getFieldByLocation(user.getBase().getLocation());
        if (!(field.isOwner(user.getBase().getUniqueId()) || field.isMember(user.getBase().getUniqueId())
                || user.hasPermission("protection.override"))) {
            user.getBase().sendMessage(ElementalsUtil.color("&bNu poti seta modul &dfun &bin aceasta protectie!"));
            return;
        }
        field.toggleFun();
        user.getBase().sendMessage(ElementalsUtil.color("&bAi " + (field.hasFun() ? "&aactivat" : "&cdezactivat") + " &bmodul &dfun&b!"));
    }

    public static void infoField(User user) {
        Field field;
        Block block = user.getBase().getTargetBlock(null, 3);
        if (block.getType().equals(Material.DIAMOND_BLOCK) && isFieldBlock(block))
            field = getFieldById(getFieldIdByBlock(block));
        else
            field = getFieldByLocation(user.getBase().getLocation());
        if (!(field.isOwner(user.getBase().getUniqueId()) || field.isMember(user.getBase().getUniqueId())
                || user.hasPermission("protection.override"))) {
            user.getBase().sendMessage(ElementalsUtil.color("&bNu poti vedea informatii despre protectie!"));
            return;
        }
        user.getBase().sendMessage(ElementalsUtil.color("&a&lInformatii despre protectie:"));
        user.getBase().sendMessage("");
        user.getBase().sendMessage(ElementalsUtil.color("&eLocatie: &6" + block.getWorld().getName() + " &c/ &6" + block.getX()
                + "(x) &c/ &6" + block.getY() + "(y) &c/ &6" + block.getZ() + "(z)"));
        user.getBase().sendMessage(ElementalsUtil.color("&9Marime: &a51(x) &c/ &a256(y) &c/ &a51(x)"));
        user.getBase().sendMessage(ElementalsUtil.color("&4Locatie maxima: &a" + field.getWorld().getName() + " &c/ &a"
                + field.getMaximLocation().getX() + "(x) &c/ &a" + field.getMaximLocation().getZ() + "(z)"));
        user.getBase().sendMessage(ElementalsUtil.color("&cLocatie minima: &a" + field.getWorld().getName() + " &c/ &a"
                + field.getMinimLocation().getX() + "(x) &c/ &a" + field.getMinimLocation().getZ() + "(z)"));
        user.getBase().sendMessage(ElementalsUtil.color("&bDetinator: &6" + Bukkit.getOfflinePlayer(field.getOwner()).getName() + " &f-> &d"
                + field.getOwner().toString()));
        if (!field.getMembers().isEmpty())
            user.getBase().sendMessage(ElementalsUtil.color("&bMembrii:"));
        field.getMembers().forEach((UUID uuid) -> user.getBase()
                .sendMessage(ElementalsUtil.color("&e" + Bukkit.getOfflinePlayer(uuid).getName() + " &f-> &d" + uuid)));
        user.getBase().sendMessage(ElementalsUtil.color("&dFun mode: " + field.hasFun()));
        user.getBase().sendMessage("");
    }

    @SuppressWarnings("deprecation")
    public static void locFields(User user) {
        try {
            if (isFieldAtLocation(user.getBase().getLocation())) {
                Field field = getFieldByLocation(user.getBase().getLocation());
                if (field.isOwner(user.getBase().getUniqueId()) || user.hasPermission("protection.override")) {
                    for (int x = field.getMinimLocation().getX(); x < field.getMaximLocation().getX(); x++) {
                        Location loc = new Location(field.getWorld(), x, field.getMaximLocation().getY(),
                                field.getMaximLocation().getZ() - 25);
                        user.getBase().sendBlockChange(loc, Material.GLASS, (byte) 0);
                    }
                    for (int z = field.getMinimLocation().getZ(); z < field.getMaximLocation().getZ(); z++) {
                        Location loc = new Location(field.getWorld(), field.getMaximLocation().getX() - 25,
                                field.getMaximLocation().getY(), z);
                        user.getBase().sendBlockChange(loc, Material.GLASS, (byte) 0);
                    }
                    for (int y = 0; y < 256; y++) {
                        Location loc = new Location(field.getWorld(), field.getMaximLocation().getX() - 25, y,
                                field.getMaximLocation().getZ() - 25);
                        user.getBase().sendBlockChange(loc, Material.GLASS, (byte) 0);
                    }
                    Bukkit.getScheduler().runTaskLater(ElementalsX.get(), () -> {
                        for (int x = field.getMinimLocation().getX(); x < field.getMaximLocation().getX(); x++) {
                            Location loc = new Location(field.getWorld(), x, field.getMaximLocation().getY(),
                                    field.getMaximLocation().getZ() - 25);
                            user.getBase().sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
                        }
                        for (int z = field.getMinimLocation().getZ(); z < field.getMaximLocation().getZ(); z++) {
                            Location loc = new Location(field.getWorld(), field.getMaximLocation().getX() - 25,
                                    field.getMaximLocation().getY(), z);
                            user.getBase().sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
                        }
                        for (int y = 0; y < 256; y++) {
                            Location loc = new Location(field.getWorld(), field.getMaximLocation().getX() - 25, y,
                                    field.getMaximLocation().getZ() - 25);
                            user.getBase().sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
                        }
                    }, 20 * 20);
                } else {
                    ResultSet rs = ElementalsX.getBase()
                            .prepareStatement("SELECT x, y, z, world FROM protection WHERE owner='"
                                    + user.getBase().getUniqueId().toString() + "';")
                            .executeQuery();
                    user.getBase().sendMessage(ElementalsUtil.color("&bProtectiile tale:"));
                    while (rs.next())
                        user.getBase().sendMessage(ElementalsUtil.color("&5&l> &b" + rs.getString("world") + "&c, &b" + rs.getString("x")
                                + "&c, &b" + rs.getString("y") + "&c, &b" + rs.getString("z")));
                }
            } else {
                ResultSet rs = ElementalsX.getBase()
                        .prepareStatement("SELECT x, y, z, world FROM protection WHERE owner='"
                                + user.getBase().getUniqueId().toString() + "';")
                        .executeQuery();
                user.getBase().sendMessage(ElementalsUtil.color("&bProtectiile tale:"));
                while (rs.next())
                    user.getBase().sendMessage(ElementalsUtil.color("&5&l> &b" + rs.getString("world") + "&c, &b" + rs.getString("x")
                            + "&c, &b" + rs.getString("y") + "&c, &b" + rs.getString("z")));
            }
        } catch (Exception exception) {
            user.getBase().sendMessage(ElementalsUtil.color("&cEroare. &aContacteaza un admin!"));
            exception.printStackTrace();
        }
    }
}
