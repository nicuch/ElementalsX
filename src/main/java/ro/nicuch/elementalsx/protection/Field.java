package ro.nicuch.elementalsx.protection;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import ro.nicuch.elementalsx.ElementalsX;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Field {
    private final FieldId id;
    private final UUID owner;
    private final Field2D field2D;
    private final Set<String> members;
    private final int chunkX;
    private final int chunkZ;
    private final String world;
    private boolean fun;
    private final int blockX;
    private final int blockY;
    private final int blockZ;

    public Field(FieldId id, UUID owner, Field2D field2D, Block block, Set<String> members) {
        this(id, owner, field2D, block.getChunk(), block.getX(), block.getY(), block.getZ(), members);
    }

    public Field(FieldId id, UUID owner, Field2D field2D, Chunk chunk, int x, int y, int z, Set<String> members) {
        this.id = id;
        this.owner = owner;
        this.field2D = field2D;
        this.chunkX = chunk.getX();
        this.chunkZ = chunk.getZ();
        this.world = chunk.getWorld().getName();
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
        this.members = members;
    }

    public Field(FieldId id, UUID owner, Field2D field2D, ChunkData chunk, int x, int y, int z, Set<String> members) {
        this.id = id;
        this.owner = owner;
        this.field2D = field2D;
        this.chunkX = chunk.getX();
        this.chunkZ = chunk.getZ();
        this.world = chunk.getWorld();
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
        this.members = members;
    }

    public Field(FieldId id, UUID owner, Field2D field2D, int chunkx, int chunkz, String world, int x, int y, int z, Set<String> members) {
        this.id = id;
        this.owner = owner;
        this.field2D = field2D;
        this.chunkX = chunkx;
        this.chunkZ = chunkz;
        this.world = world;
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
        this.members = members;
    }

    public int getBlockX() {
        return this.blockX;
    }

    public int getBlockY() {
        return this.blockY;
    }

    public int getBlockZ() {
        return this.blockZ;
    }

    public Block getBlock() {
        return Bukkit.getWorld(this.world).getBlockAt(this.blockX, this.blockY, this.blockZ);
    }

    public FieldId getId() {
        return this.id;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public boolean isOwner(UUID uuid) {
        return this.owner.equals(uuid);
    }

    public Field2D getField2D() {
        return this.field2D;
    }

    public Field addMember(String uuid) {
        if (this.isMember(uuid))
            return this;
        this.members.add(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            String query = "INSERT INTO protmembers (protid, uuid) VALUES (?, ?) ON DUPLICATE KEY UPDATE protid=?, uuid=?;";
            try (PreparedStatement statement = ElementalsX.getDatabase().prepareStatement(query)) {
                statement.setString(1, this.id.toString());
                statement.setString(2, uuid);
                statement.setString(3, this.id.toString());
                statement.setString(4, uuid);
                statement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        return this;
    }

    public Field removeMember(String uuid) {
        if (!this.isMember(uuid))
            return this;
        this.members.remove(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            String query = "DELETE IGNORE FROM protmembers WHERE protid=? AND uuid=?;";
            try (PreparedStatement statement = ElementalsX.getDatabase().prepareStatement(query)) {
                statement.setString(1, this.id.toString());
                statement.setString(2, uuid);
                statement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        return this;
    }

    public boolean isMember(String uuid) {
        return this.members.contains(uuid);
    }

    public boolean isMember(UUID uuid) {
        return this.members.contains(uuid.toString());
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public String getWorld() {
        return this.world;
    }

    public Set<String> getMembers() {
        return this.members;
    }

    public boolean hasFun() {
        return this.fun;
    }

    public void toggleFun() {
        this.fun = !this.fun;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Field)) return false;
        Field field = (Field) o;
        return this.getId().equals(field.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
