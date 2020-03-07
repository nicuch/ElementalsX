package ro.nicuch.elementalsx.protection;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import ro.nicuch.elementalsx.ElementalsX;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Field {
    private final FieldId id;
    private final UUID owner;
    private final Field2D field2D;
    private final Set<UUID> members;
    private final int chunkX;
    private final int chunkZ;
    private final World world;
    private boolean fun;
    private final int blockX;
    private final int blockY;
    private final int blockZ;

    public Field(FieldId id, UUID owner, Field2D field2D, Block block, Set<UUID> members) {
        this(id, owner, field2D, block.getChunk(), block.getX(), block.getY(), block.getZ(), members);
    }

    public Field(FieldId id, UUID owner, Field2D field2D, Chunk chunk, int x, int y, int z, Set<UUID> members) {
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
        return this.world.getBlockAt(this.blockX, this.blockY, this.blockZ);
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

    public Field addMember(UUID uuid) {
        if (this.isMember(uuid))
            return this;
        this.members.add(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            try (Statement statement = ElementalsX.getDatabase().createStatement()) {
                statement.executeUpdate("INSERT INTO protmembers (protid, uuid) VALUES ('" + this.id.toString() + "', '" + uuid.toString() + "')" +
                        " ON DUPLICATE KEY UPDATE protid='" + this.id.toString() + "', uuid='" + uuid.toString() + "';");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        return this;
    }

    public Field removeMember(UUID uuid) {
        if (!this.isMember(uuid))
            return this;
        this.members.remove(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            try (Statement statement = ElementalsX.getDatabase().createStatement()) {
                statement.executeUpdate("DELETE IGNORE FROM protmembers WHERE protid='" + this.id.toString() + "' AND uuid='" + uuid.toString() + "';");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        return this;
    }

    public boolean isMember(UUID uuid) {
        return this.members.contains(uuid);
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public World getWorld() {
        return this.world;
    }

    public Set<UUID> getMembers() {
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
