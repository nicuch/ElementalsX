package ro.nicuch.elementalsx.protection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

public class FieldUtil {
    private final static Map<FieldId, Field> loadedFields = new HashMap<>();

    public static void registerField(User user, Block block, FieldId id, Field2D field2D) {
        Field field = new Field(id, user.getBase().getUniqueId(), field2D, block);
        loadedFields.putIfAbsent(id, field);
    }

    public static void unregisterField(Block block) {
        FieldId id = FieldId.fromBlock(block);
        if (isFieldLoaded(id)) {
            Field field = getFieldById(id);
            field.delete();
            loadedFields.remove(id);
        }
    }

    public static boolean isFieldNerby(User user, Location location) {
        Location l1 = location.clone().add(-25, 0, -25);
        if (isFieldAtLocation(l1))
            if (!getFieldByLocation(l1).isOwner(user.getBase().getUniqueId()))
                return true;
        Location l2 = location.clone().add(25, 0, 25);
        if (isFieldAtLocation(l2))
            if (!getFieldByLocation(l2).isOwner(user.getBase().getUniqueId()))
                return true;
        Location l3 = location.clone().add(-25, 0, 25);
        if (isFieldAtLocation(l3))
            if (!getFieldByLocation(l3).isOwner(user.getBase().getUniqueId()))
                return true;
        Location l4 = location.clone().add(25, 0, -25);
        if (isFieldAtLocation(l4))
            return !getFieldByLocation(l4).isOwner(user.getBase().getUniqueId());
        return false;
    }

    public static Set<UUID> convertStringsToUUIDs(List<String> strings) {
        Set<UUID> list = new HashSet<>();
        strings.forEach((String arg) -> list.add(UUID.fromString(arg)));
        return list;
    }

    public static Set<String> convertUUIDsToStrings(Set<UUID> uuids) {
        Set<String> list = new HashSet<>();
        uuids.forEach((UUID uuid) -> list.add(uuid.toString()));
        return list;
    }

    public static boolean areThereEnoughProtections(Chunk chunk) {
        try {
            ResultSet rs = ElementalsX
                    .getBase().prepareStatement("SELECT COUNT(id) FROM protection WHERE chunkx='" + chunk.getX()
                            + "' AND chunkz='" + chunk.getZ() + "' AND world='" + chunk.getWorld().getName() + "';")
                    .executeQuery();
            if (rs.next())
                return rs.getInt(1) >= 4;
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void loadFieldsInChunk(Chunk chunk) {
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            String world = chunk.getWorld().getName();
            try {
                ResultSet rs = ElementalsX
                        .getBase().prepareStatement("SELECT maxx, maxz, minx, minz, x, y, z, owner FROM protection WHERE chunkx='" + chunk.getX()
                                + "' AND chunkz='" + chunk.getZ() + "' AND world='" + world + "';")
                        .executeQuery();
                while (rs.next()) {
                    // Get Strings and Ints to not catch SQLException in Async
                    int maxx = rs.getInt("maxx");
                    int maxz = rs.getInt("maxz");
                    int minx = rs.getInt("minx");
                    int minz = rs.getInt("minz");
                    int blockX = rs.getInt("x");
                    int blockY = rs.getInt("y");
                    int blockZ = rs.getInt("z");
                    UUID uuid = UUID.fromString(rs.getString("owner"));
                    Bukkit.getScheduler().runTask(ElementalsX.get(), () -> {
                        // Never access modifiers from BukkitAPI async
                        Field2D field2D = new Field2D(maxx, maxz, minx, minz);
                        FieldId id = FieldId.fromLocation(blockX, blockY, blockZ, world);
                        Field field = new Field(id, uuid, field2D, chunk, blockX, blockY, blockZ);
                        loadedFields.put(id, field);
                    });
                }
                if (rs != null)
                    rs.close();
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
                        .getBase().prepareStatement("SELECT x, y, z, world FROM protection WHERE chunkx='" + chunk.getX()
                                + "' AND chunkz='" + chunk.getZ() + "' AND world='" + chunk.getWorld().getName() + "';")
                        .executeQuery();
                while (rs.next()) {
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");
                    String world = rs.getString("world");
                    FieldId id = FieldId.fromLocation(x, y, z, world);
                    Bukkit.getScheduler().runTask(ElementalsX.get(), () -> {
                        if (loadedFields.containsKey(id)) loadedFields.remove(id).save();
                    });
                }
                if (rs != null)
                    rs.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public static boolean isFieldAtLocation(Location location) {
        for (Field field : loadedFields.values())
            if (field.getWorld().getName().equals(location.getWorld().getName())
                    && field.getField2D().isInLocation(location))
                return true;
        return false;
    }

    public static Field getFieldByLocation(Location location) {
        for (Field field : loadedFields.values())
            if (field.getWorld().getName().equals(location.getWorld().getName())
                    && field.getField2D().isInLocation(location))
                return field;
        throw new NullPointerException("Nici o protectie gasita la locatia data.");
    }

    public static Field getFieldById(FieldId id) {
        if (loadedFields.containsKey(id))
            return loadedFields.get(id);
        else
            throw new NullPointerException("Nici o protectie gasita dupa id-ul " + id.toString() + "!");
    }

    public static boolean isFieldLoaded(FieldId id) {
        return loadedFields.containsKey(id);
    }

    public static Collection<Field> getLoadedFields() {
        return loadedFields.values();
    }

    public static boolean isFieldBlock(Block block) {
        try {
            PreparedStatement ps = ElementalsX.getBase()
                    .prepareStatement("SELECT id FROM protection WHERE x='" + block.getX() + "' AND y='" + block.getY()
                            + "' AND z='" + block.getZ() + "' AND world='" + block.getWorld().getName() + "';");
            ResultSet rs = ps.executeQuery();
            boolean isIt = !rs.wasNull();
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
            return isIt;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    // DO NEVER PUT COLLIDABLE AGAIN
    public static void updateUser(User user, Location loc) {
        if (isFieldAtLocation(loc)) {
            if (!user.isInField()) {
                user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &b&oAi intrat in protectia lui &f&o"
                        + Bukkit.getOfflinePlayer(getFieldByLocation(loc).getOwner()).getName() + "&b&o!"));
            } else if (user.isInField() && !getFieldByLocation(loc).isOwner(user.getLastFieldOwner())) {
                user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &b&oAi iesit din protectia lui &f&o"
                        + Bukkit.getOfflinePlayer(user.getLastFieldOwner()).getName() + "&b&o!"));
                user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &b&oAi intrat in protectia lui &f&o"
                        + Bukkit.getOfflinePlayer(getFieldByLocation(loc).getOwner()).getName() + "&b&o!"));
            }
            user.toggleField(true);
            user.setLastFieldOwner(getFieldByLocation(loc).getOwner());
        } else if (!isFieldAtLocation(loc)) {
            if (user.isInField())
                user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &b&oAi iesit din protectia lui &f&o"
                        + Bukkit.getOfflinePlayer(user.getLastFieldOwner()).getName() + "&b&o!"));
            user.toggleField(false);
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
                    field = getFieldById(FieldId.fromBlock(block));
                else if (isFieldAtLocation(user.getBase().getLocation()))
                    field = getFieldByLocation(user.getBase().getLocation());
                else {
                    user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&oTrebuie sa te afli intr-o protectie!"));
                    return;
                }
                if (!(field.isOwner(user.getBase().getUniqueId()) || user.hasPermission("protection.override"))) {
                    user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &b&oNu esti detinatorul protectiei!"));
                    return;
                }
                if (allow) {
                    field.addMember(member.getUniqueId());
                    user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &a&oJucatorul &f&o" + name + " &a&oa fost adaugat in protectie."));
                } else {
                    field.removeMember(member.getUniqueId());
                    user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &a&oJucatorul &f&o" + name + " &a&oa fost sters din protectie."));
                }
            } else {
                //TODO add to all
                PreparedStatement ps = ElementalsX.getBase().prepareStatement(
                        "SELECT x, y, z, world FROM protection WHERE owner='" + user.getBase().getUniqueId().toString() + "';");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");
                    String world = rs.getString("world");
                    FieldId id = FieldId.fromLocation(x, y, z, world);
                    if (allow) {
                        if (isFieldLoaded(id))
                            getFieldById(id).addMember(member.getUniqueId());
                    } else {
                        if (isFieldLoaded(id))
                            getFieldById(id).removeMember(member.getUniqueId());
                    }
                }
                if (ps != null)
                    ps.close();
                if (rs != null)
                    rs.close();
                if (allow)
                    user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &a&oJucatorul &f&o" + name + " &a&oa fost adaugat in toate protectiile tale."));
                else
                    user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &a&oJucatorul &f&o" + name + " &a&oa fost sters din toate protectiile tale."));
            }
        } catch (Exception exception) {
            user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&l&oEroare. &a&l&oContacteaza un admin!"));
            exception.printStackTrace();
        }
    }

    public static void visualiseField(User user) {
        Field field;
        Block block = user.getBase().getTargetBlock(null, 3);
        if (block.getType().equals(Material.DIAMOND_BLOCK) && isFieldBlock(block))
            field = getFieldById(FieldId.fromBlock(block));
        else if (isFieldAtLocation(user.getBase().getLocation()))
            field = getFieldByLocation(user.getBase().getLocation());
        else {
            user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&oTrebuie sa te afli intr-o protectie!"));
            return;
        }
        if (!(field.isMember(user.getBase().getUniqueId()) || field.isOwner(user.getBase().getUniqueId())
                || user.hasPermission("protection.override"))) {
            user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&oTrebuie sa fii detinatorul sau membru al protectiei ca sa poti vizualiza!"));
            return;
        }
        field.getField2D().sendFieldVisualize(user.getBase(), field.getWorld());
    }

    public static void toggleFun(User user) {
        Field field;
        Block block = user.getBase().getTargetBlock(null, 3);
        if (block.getType().equals(Material.DIAMOND_BLOCK) && isFieldBlock(block))
            field = getFieldById(FieldId.fromBlock(block));
        else if (isFieldAtLocation(user.getBase().getLocation()))
            field = getFieldByLocation(user.getBase().getLocation());
        else {
            user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&oTrebuie sa te afli intr-o protectie!"));
            return;
        }
        if (!(field.isOwner(user.getBase().getUniqueId()) || field.isMember(user.getBase().getUniqueId())
                || user.hasPermission("protection.override"))) {
            user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &b&oNu poti seta modul &d&l&ofun &b&oin aceasta protectie!"));
            return;
        }
        field.toggleFun();
        user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &b&oAi " + (field.hasFun() ? "&a&o&lactivat" : "&c&o&ldezactivat") + " &b&omodul &d&l&ofun&b&o!"));
    }

    public static void infoField(User user) {
        Field field;
        Block block = user.getBase().getTargetBlock(null, 3);
        if (block.getType().equals(Material.DIAMOND_BLOCK) && isFieldBlock(block))
            field = getFieldById(FieldId.fromBlock(block));
        else if (isFieldAtLocation(user.getBase().getLocation()))
            field = getFieldByLocation(user.getBase().getLocation());
        else {
            user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&oTrebuie sa te afli intr-o protectie!"));
            return;
        }
        if (!(field.isOwner(user.getBase().getUniqueId()) || field.isMember(user.getBase().getUniqueId())
                || user.hasPermission("protection.override"))) {
            user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &b&oNu poti vedea informatii despre protectie!"));
            return;
        }
        user.getBase().sendMessage(ElementalsUtil.color("&a&lInformatii despre protectie:"));
        user.getBase().sendMessage("");
        user.getBase().sendMessage(ElementalsUtil.color("&eLocatie: &6" + block.getWorld().getName() + " &c/ &6" + block.getX()
                + "(x) &c/ &6" + block.getY() + "(y) &c/ &6" + block.getZ() + "(z)"));
        user.getBase().sendMessage(ElementalsUtil.color("&9Marime: &a51(x) &c/ &a256(y) &c/ &a51(x)"));
        user.getBase().sendMessage(ElementalsUtil.color("&4Locatie maxima: &a" + field.getWorld().getName() + " &c/ &a"
                + field.getField2D().getMaxX() + "(x) &c/ &a" + field.getField2D().getMaxZ() + "(z)"));
        user.getBase().sendMessage(ElementalsUtil.color("&cLocatie minima: &a" + field.getWorld().getName() + " &c/ &a"
                + field.getField2D().getMinX() + "(x) &c/ &a" + field.getField2D().getMinZ() + "(z)"));
        user.getBase().sendMessage(ElementalsUtil.color("&bDetinator: &6" + Bukkit.getOfflinePlayer(field.getOwner()).getName() + " &f-> &d"
                + field.getOwner().toString()));
        if (!field.getMembers().isEmpty())
            user.getBase().sendMessage(ElementalsUtil.color("&bMembrii:"));
        field.getMembers().forEach((UUID uuid) -> user.getBase()
                .sendMessage(ElementalsUtil.color("&e" + Bukkit.getOfflinePlayer(uuid).getName() + " &f-> &d" + uuid)));
        user.getBase().sendMessage(ElementalsUtil.color("&dFun mode: " + field.hasFun()));
        user.getBase().sendMessage("");
    }

    public static void locateField(User user) {
        Field field;
        Block block = user.getBase().getTargetBlock(null, 3);
        if (block.getType().equals(Material.DIAMOND_BLOCK) && isFieldBlock(block))
            field = getFieldById(FieldId.fromBlock(block));
        else if (isFieldAtLocation(user.getBase().getLocation()))
            field = getFieldByLocation(user.getBase().getLocation());
        else {
            user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&oTrebuie sa te afli intr-o protectie!"));
            return;
        }
        if (!(field.isOwner(user.getBase().getUniqueId()) || user.hasPermission("protection.override"))) {
            user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&oNu esti detinatorul protectiei!"));
            return;
        }
        field.getField2D().sendFieldLocate(user.getBase(), field.getWorld(), field.getBlockY());
    }

    public static void listFields(User user) {
        try {
            PreparedStatement ps = ElementalsX.getBase()
                    .prepareStatement("SELECT x, y, z, world FROM protection WHERE owner='"
                            + user.getBase().getUniqueId().toString() + "';");
            ResultSet rs = ps.executeQuery();
            if (rs.wasNull()) {
                user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&oNu ai nici-o protectie!"));
                return;
            }
            user.getBase().sendMessage(ElementalsUtil.color("&bProtectiile tale:"));
            while (rs.next())
                user.getBase().sendMessage(ElementalsUtil.color("&5&l> &b" + rs.getString("world") + "&c, &b" + rs.getString("x")
                        + "&c, &b" + rs.getString("y") + "&c, &b" + rs.getString("z")));
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&l&oEroare. &a&l&oContacteaza un admin!"));
            ex.printStackTrace();
        }
    }
}
