package ro.nicuch.elementalsx.protection;

import co.aikar.taskchain.TaskChain;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class FieldUtil {
    private final static Map<FieldId, Field> loadedFields = new HashMap<>();

    public static void registerField(User user, Block block, FieldId id, Field2D field2D) {
        Field field = new Field(id, user.getBase().getUniqueId(), field2D, block, new HashSet<>());
        loadedFields.putIfAbsent(id, field);
    }

    public static Set<UUID> getFieldMembers(FieldId fieldId) {
        try (Statement statement = ElementalsX.getDatabase().createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT uuid FROM protmembers WHERE id='" + fieldId.toString() + "';")) {
                if (!resultSet.wasNull()) {
                    Set<UUID> uuids = new HashSet<>();
                    while (resultSet.next()) {
                        uuids.add(UUID.fromString(resultSet.getString("uuid")));
                    }
                    return uuids;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new HashSet<>();
    }

    public static void unregisterField(Block block) {
        FieldId id = FieldId.fromBlock(block);
        if (isFieldLoaded(id)) {
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
        strings.forEach(arg -> list.add(UUID.fromString(arg)));
        return list;
    }

    public static Set<String> convertUUIDsToStrings(Set<UUID> uuids) {
        Set<String> list = new HashSet<>();
        uuids.forEach(uuid -> list.add(uuid.toString()));
        return list;
    }

    public static boolean areThereEnoughProtections(Chunk chunk) {
        try (Statement statement = ElementalsX.getDatabase().createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(id) FROM protection WHERE chunkx='" + chunk.getX()
                    + "' AND chunkz='" + chunk.getZ() + "' AND world='" + chunk.getWorld().getName() + "';")) {
                if (resultSet.wasNull()) {
                    return false;
                } else {
                    if (resultSet.next())
                        return resultSet.getInt(1) >= 4;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return true; //that means that we have enough protections
    }

    public static void loadFieldsInChunk(Chunk chunk) {
        String worldName = chunk.getWorld().getName();
        ElementalsX.newChain().async(() -> {
            try (Statement statement = ElementalsX.getDatabase().createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("SELECT maxx, maxz, minx, minz, x, y, z, owner FROM protection WHERE chunkx='" + chunk.getX()
                        + "' AND chunkz='" + chunk.getZ() + "' AND world='" + worldName + "';")) {
                    while (resultSet.next()) {
                        int maxx = resultSet.getInt("maxx");
                        int maxz = resultSet.getInt("maxz");
                        int minx = resultSet.getInt("minx");
                        int minz = resultSet.getInt("minz");
                        int blockX = resultSet.getInt("x");
                        int blockY = resultSet.getInt("y");
                        int blockZ = resultSet.getInt("z");
                        UUID uuid = UUID.fromString(resultSet.getString("owner"));
                        ElementalsX.newChain().sync(() -> {
                            Field2D field2D = new Field2D(maxx, maxz, minx, minz);
                            FieldId id = FieldId.fromCoords(blockX, blockY, blockZ, worldName);
                            Field field = new Field(id, uuid, field2D, chunk, blockX, blockY, blockZ, getFieldMembers(id));
                            loadedFields.put(id, field);
                        }).execute();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }).execute();
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
        String worldName = chunk.getWorld().getName();
        ElementalsX.newChain().async(() -> {
            try (Statement statement = ElementalsX.getDatabase().createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("SELECT x, y, z FROM protection WHERE chunkx='" + chunk.getX()
                        + "' AND chunkz='" + chunk.getZ() + "' AND world='" + worldName + "';")) {
                    while (resultSet.next()) {
                        int x = resultSet.getInt("x");
                        int y = resultSet.getInt("y");
                        int z = resultSet.getInt("z");
                        ElementalsX.newChain().sync(() -> {
                            FieldId id = FieldId.fromCoords(x, y, z, worldName);
                            loadedFields.remove(id);
                        }).execute();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }).execute();
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
        TaskChain<?> task = ElementalsX.newChain();
        task.async(() -> {
            try (Statement statement = ElementalsX.getDatabase().createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("SELECT id FROM protection WHERE x='" + block.getX() + "' AND y='" + block.getY()
                        + "' AND z='" + block.getZ() + "' AND world='" + block.getWorld().getName() + "';")) {
                    task.setTaskData("return", !resultSet.wasNull());
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }).execute();
        return task.getTaskData("return");
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

    public static void allowInField(User user, String name, boolean all, boolean allow) {
        try {
            Player member = Bukkit.getPlayer(name);
            if (member == null) {
                user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&oJucatorul nu este online!"));
                return;
            }
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
                    if (field.isMember(member.getUniqueId())) {
                        user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &a&oJucatorul &f&o" + name + " &a&oeste deja membru al protectiei."));
                        return;
                    }
                    field.addMember(member.getUniqueId());
                    user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &a&oJucatorul &f&o" + name + " &a&oa fost adaugat in protectie."));
                } else {
                    if (!field.isMember(member.getUniqueId())) {
                        user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &a&oJucatorul &f&o" + name + " &a&onu este membru al protectiei."));
                        return;
                    }
                    field.removeMember(member.getUniqueId());
                    user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &a&oJucatorul &f&o" + name + " &a&oa fost sters din protectie."));
                }
            } else {
                //TODO add to all
                if (allow) {
                    addMemberOnAllProtections(user, member.getUniqueId());
                    user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &a&oJucatorul &f&o" + name + " &a&oa fost adaugat in toate protectiile tale."));
                } else {
                    removeMemberOnAllProtections(user, member.getUniqueId());
                    user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &a&oJucatorul &f&o" + name + " &a&oa fost sters din toate protectiile tale."));
                }
            }
        } catch (Exception exception) {
            user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&l&oEroare. &a&l&oContacteaza un admin!"));
            exception.printStackTrace();
        }
    }

    private static void addMemberOnAllProtections(User user, UUID member) {
        ElementalsX.newChain().async(() -> {
            try (Statement statement = ElementalsX.getDatabase().createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("SELECT id FROM protection WHERE owner='" + user.getUUID().toString() + "';")) {
                    while (resultSet.next()) {
                        FieldId fieldId = FieldId.fromString(resultSet.getString("id"));
                        if (isFieldLoaded(fieldId)) {
                            Field field = getFieldById(fieldId);
                            if (!field.isMember(member))
                                field.addMember(member);
                            continue;
                        }
                        try (Statement statement1 = ElementalsX.getDatabase().createStatement()) {
                            statement1.executeQuery("INSERT INTO protmembers (id, uuid) VALUES ('" + fieldId.toString() + "', '" + member.toString() + "')" +
                                    " ON DUPLICATE KEY UPDATE id='" + fieldId.toString() + "', uuid='" + member.toString() + "';");
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }).execute();
    }

    private static void removeMemberOnAllProtections(User user, UUID member) {
        ElementalsX.newChain().async(() -> {
            try (Statement statement = ElementalsX.getDatabase().createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("SELECT id FROM protection WHERE owner='" + user.getUUID().toString() + "';")) {
                    while (resultSet.next()) {
                        FieldId fieldId = FieldId.fromString(resultSet.getString("id"));
                        if (isFieldLoaded(fieldId)) {
                            Field field = getFieldById(fieldId);
                            if (field.isMember(member))
                                field.removeMember(member);
                            continue;
                        }
                        try (Statement statement1 = ElementalsX.getDatabase().createStatement()) {
                            statement1.executeQuery("DELETE IGNORE FROM protmembers WHERE id='" + fieldId.toString() + "' AND uuid='" + member.toString() + "';");
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }).execute();
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
        ElementalsX.newChain().async(() -> {
            try (Statement statement = ElementalsX.getDatabase().createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("SELECT x, y, z, world FROM protection WHERE owner='"
                        + user.getBase().getUniqueId().toString() + "';")) {
                    if (resultSet.wasNull()) {
                        ElementalsX.newChain().sync(() -> user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&oNu ai nici-o protectie!"))).execute();
                    } else {
                        while (resultSet.next()) {
                            String worldName = resultSet.getString("world");
                            int x = resultSet.getInt("x");
                            int y = resultSet.getInt("y");
                            int z = resultSet.getInt("z");
                            ElementalsX.newChain().sync(() ->
                                    user.getBase().sendMessage(ElementalsUtil.color("&5&l> &b" + worldName + "&c, &b" + x
                                            + "&c, &b" + y + "&c, &b" + z))).execute();
                        }
                    }
                }
            } catch (SQLException ex) {
                user.getBase().sendMessage(ElementalsUtil.color("&f[&cProtectie&f] &c&l&oEroare. &a&l&oContacteaza un admin!"));
                ex.printStackTrace();
            }
        }).execute();
    }
}
