package ro.nicuch.elementalsx.protection;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.event.block.BlockBreakEvent;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FieldUtil {
    private final static ConcurrentMap<FieldId, Field> loadedFields = new ConcurrentHashMap<>();

    public static void registerField(User user, Block block, FieldId id, Field2D field2D) {
        Field field = new Field(id, user.getBase().getUniqueId().toString(), field2D, block, new HashSet<>());
        loadedFields.put(id, field);
    }

    public static Set<String> getFieldMembers(FieldId fieldId) {
        String query = "SELECT uuid FROM protmembers WHERE protid=?;";
        try (PreparedStatement statement = ElementalsX.getDatabase().prepareStatement(query)) {
            statement.setString(1, fieldId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                Set<String> uuids = new HashSet<>();
                while (resultSet.next()) {
                    uuids.add(resultSet.getString("uuid"));
                }
                return uuids;
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
        removeMembersOnProtection(id);
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
        String query = "SELECT COUNT(id) FROM protection WHERE chunkx=? AND chunkz=? AND world=?;";
        try (PreparedStatement statement = ElementalsX.getDatabase().prepareStatement(query)) {
            statement.setInt(1, chunk.getX());
            statement.setInt(2, chunk.getZ());
            statement.setString(3, chunk.getWorld().getName());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next())
                    return resultSet.getInt(1) >= 4;
                else
                    return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return true; //that means that we have enough protections
    }

    public static void loadFieldsInChunk(Chunk chunk) {
        String worldName = chunk.getWorld().getName();
        FieldChunkUtil.setChunkToWait(chunk);
        FieldQueueRunnable.offer(() -> {
            String query = "SELECT x, y, z, owner FROM protection WHERE chunkx=? AND chunkz=? AND world=?;";
            try (PreparedStatement statement = ElementalsX.getDatabase().prepareStatement(query)) {
                statement.setInt(1, chunk.getX());
                statement.setInt(2, chunk.getZ());
                statement.setString(3, worldName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {

                        int blockX = resultSet.getInt("x");
                        int blockY = resultSet.getInt("y");
                        int blockZ = resultSet.getInt("z");

                        int maxx = blockX + 25;
                        int maxz = blockZ + 25;
                        int minx = blockX - 25;
                        int minz = blockZ - 25;

                        String uuid = resultSet.getString("owner");
                        Field2D field2D = new Field2D(maxx, maxz, minx, minz);
                        FieldId id = FieldId.fromCoords(blockX, blockY, blockZ, worldName);
                        Field field = new Field(id, uuid, field2D, chunk, blockX, blockY, blockZ, getFieldMembers(id));
                        loadedFields.put(id, field);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                FieldChunkUtil.removeChunk(chunk);
            }
        });
    }

    public static void unloadFieldsInChunk(Chunk chunk) {
        String worldName = chunk.getWorld().getName();
        FieldQueueRunnable.offer(() -> {
            String query = "SELECT x, y, z FROM protection WHERE chunkx=? AND chunkz=? AND world=?;";
            try (PreparedStatement statement = ElementalsX.getDatabase().prepareStatement(query)) {
                statement.setInt(1, chunk.getX());
                statement.setInt(2, chunk.getZ());
                statement.setString(3, worldName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int x = resultSet.getInt("x");
                        int y = resultSet.getInt("y");
                        int z = resultSet.getInt("z");
                        FieldId id = FieldId.fromCoords(x, y, z, worldName);
                        loadedFields.remove(id);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
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

    public static boolean isFieldAtLocation(Location location) {
        for (Field field : loadedFields.values())
            if (field.getWorld().equals(location.getWorld().getName())
                    && field.getField2D().isInLocation(location))
                return true;
        return false;
    }

    public static Field getFieldByLocation(Location location) {
        for (Field field : loadedFields.values())
            if (field.getWorld().equals(location.getWorld().getName())
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
        for (Field field : loadedFields.values()) {
            if (field.getBlock().getLocation().equals(block.getLocation()))
                return true;
        }
        return false;
    }

    // DO NEVER PUT COLLIDABLE AGAIN
    public static void updateUser(User user, Location loc) {
        if (isFieldAtLocation(loc)) {
            if (!user.isInField()) {
                user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &b&oAi intrat in protectia lui &f&o"
                        + Bukkit.getOfflinePlayer(UUID.fromString(getFieldByLocation(loc).getOwner())).getName() + "&b&o!"));
            } else if (user.isInField() && !getFieldByLocation(loc).isOwner(user.getLastFieldOwner())) {
                user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &b&oAi iesit din protectia lui &f&o"
                        + Bukkit.getOfflinePlayer(UUID.fromString(user.getLastFieldOwner())).getName() + "&b&o!"));
                user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &b&oAi intrat in protectia lui &f&o"
                        + Bukkit.getOfflinePlayer(UUID.fromString(getFieldByLocation(loc).getOwner())).getName() + "&b&o!"));
            }
            user.toggleField(true);
            user.setLastFieldOwner(getFieldByLocation(loc).getOwner());
        } else if (!isFieldAtLocation(loc)) {
            if (user.isInField())
                user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &b&oAi iesit din protectia lui &f&o"
                        + Bukkit.getOfflinePlayer(UUID.fromString(user.getLastFieldOwner())).getName() + "&b&o!"));
            user.toggleField(false);
        }
    }

    @SuppressWarnings("deprecation")
    public static void allowInField(User user, String name, boolean all, boolean allow) {
        try {
            OfflinePlayer member = Bukkit.getOfflinePlayer(name);
            if (allow && !member.isOnline()) {
                user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oJucatorul nu este online!"));
                return;
            }
            if (user.getBase().getName().equals(name)) {
                user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu poti face asta, lol!"));
                return;
            }
            if (!all) {
                Field field;
                Block block = user.getBase().getTargetBlock(null, 3);
                if (block.getType() == Material.DIAMOND_BLOCK && isFieldBlock(block))
                    field = getFieldById(FieldId.fromBlock(block));
                else if (isFieldAtLocation(user.getBase().getLocation()))
                    field = getFieldByLocation(user.getBase().getLocation());
                else {
                    user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oTrebuie sa te afli intr-o protectie!"));
                    return;
                }
                if (!(field.isOwner(user.getBase().getUniqueId()) || user.hasPermission("protection.override"))) {
                    user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &b&oNu esti detinatorul protectiei!"));
                    return;
                }
                if (allow) {
                    if (field.isMember(member.getUniqueId().toString())) {
                        user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &a&oJucatorul &f&o" + name + " &a&oeste deja membru al protectiei."));
                        return;
                    }
                    field.addMember(member.getUniqueId().toString());
                    user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &a&oJucatorul &f&o" + name + " &a&oa fost adaugat in protectie."));
                } else {
                    if (!field.isMember(member.getUniqueId())) {
                        user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &a&oJucatorul &f&o" + name + " &a&onu este membru al protectiei."));
                        return;
                    }
                    field.removeMember(member.getUniqueId().toString());
                    user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &a&oJucatorul &f&o" + name + " &a&oa fost sters din protectie."));
                }
            } else {
                //TODO add to all
                if (allow) {
                    addMemberOnAllProtections(user, member.getUniqueId());
                    user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &a&oJucatorul &f&o" + name + " &a&oa fost adaugat in toate protectiile tale."));
                } else {
                    removeMemberOnAllProtections(user, member.getUniqueId());
                    user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &a&oJucatorul &f&o" + name + " &a&oa fost sters din toate protectiile tale."));
                }
            }
        } catch (Exception exception) {
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&l&oEroare. &a&l&oContacteaza un admin!"));
            exception.printStackTrace();
        }
    }

    private static void addMemberOnAllProtections(User user, UUID member) {
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            String query = "SELECT id FROM protection WHERE owner=?;";
            try (PreparedStatement statement = ElementalsX.getDatabase().prepareStatement(query)) {
                statement.setString(1, user.getUUID().toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        FieldId fieldId = FieldId.fromString(resultSet.getString("id"));
                        if (isFieldLoaded(fieldId)) {
                            Field field = getFieldById(fieldId);
                            if (!field.isMember(member.toString()))
                                field.addMember(member.toString());
                        } else {
                            String query1 = "INSERT INTO protmembers (protid, uuid) VALUES (?, ?)" +
                                    " ON DUPLICATE KEY UPDATE protid=?, uuid=?;";
                            try (PreparedStatement statement1 = ElementalsX.getDatabase().prepareStatement(query1)) {
                                statement1.setString(1, fieldId.toString());
                                statement1.setString(2, member.toString());
                                statement1.setString(3, fieldId.toString());
                                statement1.setString(4, member.toString());
                                statement1.executeUpdate();
                            }
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private static void removeMembersOnProtection(FieldId fieldId) {
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            String query = "DELETE FROM protmembers WHERE protid=?;";
            try (PreparedStatement statement = ElementalsX.getDatabase().prepareStatement(query)) {
                statement.setString(1, fieldId.toString());
                statement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private static void removeMemberOnAllProtections(User user, UUID member) {
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            String query = "SELECT id FROM protection WHERE owner=?;";
            try (PreparedStatement statement = ElementalsX.getDatabase().prepareStatement(query)) {
                statement.setString(1, user.getUUID().toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        FieldId fieldId = FieldId.fromString(resultSet.getString("id"));
                        if (isFieldLoaded(fieldId)) {
                            Field field = getFieldById(fieldId);
                            if (field.isMember(member.toString()))
                                field.removeMember(member.toString());
                        } else {
                            String query1 = "DELETE FROM protmembers WHERE protid=? AND uuid=?;";
                            try (PreparedStatement statement1 = ElementalsX.getDatabase().prepareStatement(query1)) {
                                statement1.setString(1, fieldId.toString());
                                statement1.setString(2, member.toString());
                                statement1.executeUpdate();
                            }
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void visualiseField(User user) {
        Field field;
        Block block = user.getBase().getTargetBlock(null, 3);
        if (block.getType().equals(Material.DIAMOND_BLOCK) && isFieldBlock(block))
            field = getFieldById(FieldId.fromBlock(block));
        else if (isFieldAtLocation(user.getBase().getLocation()))
            field = getFieldByLocation(user.getBase().getLocation());
        else {
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oTrebuie sa te afli intr-o protectie!"));
            return;
        }
        if (!(field.isMember(user.getBase().getUniqueId()) || field.isOwner(user.getBase().getUniqueId())
                || user.hasPermission("protection.override"))) {
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oTrebuie sa fii detinatorul sau membru al protectiei ca sa poti vizualiza!"));
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
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oTrebuie sa te afli intr-o protectie!"));
            return;
        }
        if (!(field.isOwner(user.getBase().getUniqueId()) || field.isMember(user.getBase().getUniqueId())
                || user.hasPermission("protection.override"))) {
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &b&oNu poti seta modul &d&l&ofun &b&oin aceasta protectie!"));
            return;
        }
        field.toggleFun();
        user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &b&oAi " + (field.hasFun() ? "&a&o&lactivat" : "&c&o&ldezactivat") + " &b&omodul &d&l&ofun&b&o!"));
    }

    public static void infoField(User user) {
        Field field;
        Block block = user.getBase().getTargetBlock(Collections.singleton(Material.DIAMOND_BLOCK), 3);
        if (block != null && block.getType() == Material.DIAMOND_BLOCK && isFieldBlock(block))
            field = getFieldById(FieldId.fromBlock(block));
        else if (isFieldAtLocation(user.getBase().getLocation()))
            field = getFieldByLocation(user.getBase().getLocation());
        else {
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oTrebuie sa te afli intr-o protectie!"));
            return;
        }
        if (!(field.isOwner(user.getBase().getUniqueId()) || field.isMember(user.getBase().getUniqueId())
                || user.hasPermission("protection.override"))) {
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &b&oNu poti vedea informatii despre protectie!"));
            return;
        }
        user.getBase().sendMessage(ElementalsUtil.color("&a&lInformatii despre protectie:"));
        user.getBase().sendMessage("");
        user.getBase().sendMessage(ElementalsUtil.color("&eLocatie: &6" + field.getWorld() + " &c/ &6" + field.getBlock().getX()
                + "(x) &c/ &6" + field.getBlock().getY() + "(y) &c/ &6" + field.getBlock().getZ() + "(z)"));
        user.getBase().sendMessage(ElementalsUtil.color("&9Marime: &a51(x) &c/ &a256(y) &c/ &a51(x)"));
        user.getBase().sendMessage(ElementalsUtil.color("&4Locatie maxima: &a" + field.getWorld() + " &c/ &a"
                + field.getField2D().getMaxX() + "(x) &c/ &a" + field.getField2D().getMaxZ() + "(z)"));
        user.getBase().sendMessage(ElementalsUtil.color("&cLocatie minima: &a" + field.getWorld() + " &c/ &a"
                + field.getField2D().getMinX() + "(x) &c/ &a" + field.getField2D().getMinZ() + "(z)"));
        user.getBase().sendMessage(ElementalsUtil.color("&bDetinator: &6" + Bukkit.getOfflinePlayer(UUID.fromString(field.getOwner())).getName()));
        if (!field.getMembers().isEmpty())
            user.getBase().sendMessage(ElementalsUtil.color("&bMembrii:"));
        field.getMembers().forEach(uuid -> user.getBase()
                .sendMessage(ElementalsUtil.color("&e" + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName())));
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
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oTrebuie sa te afli intr-o protectie!"));
            return;
        }
        if (!(field.isOwner(user.getBase().getUniqueId()) || user.hasPermission("protection.override"))) {
            user.getBase().sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu esti detinatorul protectiei!"));
            return;
        }
        field.getField2D().sendFieldLocate(field.getBlock(), user.getBase(), field.getWorld(), field.getBlockY());
    }

    public static void listFields(UUID uuid, CommandSender sendTo) {
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            String query = "SELECT x, y, z, world FROM protection WHERE owner=?;";
            try (PreparedStatement statement = ElementalsX.getDatabase().prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    boolean no_prot = false;
                    while (resultSet.next()) {
                        no_prot = true;
                        String worldName = resultSet.getString("world");
                        int x = resultSet.getInt("x");
                        int y = resultSet.getInt("y");
                        int z = resultSet.getInt("z");
                        Bukkit.getScheduler().runTask(ElementalsX.get(), () ->
                                sendTo.sendMessage(ElementalsUtil.color("&5&l> &b" + worldName + "&c, &b" + x
                                        + "&c, &b" + y + "&c, &b" + z)));
                    }
                    if (!no_prot)
                        Bukkit.getScheduler().runTask(ElementalsX.get(), () -> sendTo.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&oNu ai nici-o protectie!")));
                }
            } catch (SQLException ex) {
                sendTo.sendMessage(ElementalsUtil.color("&8[&cProtectie&8] &c&l&oEroare. &a&l&oContacteaza un admin!"));
                ex.printStackTrace();
            }
        });
    }
}
