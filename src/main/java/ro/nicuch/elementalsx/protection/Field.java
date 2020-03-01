package ro.nicuch.elementalsx.protection;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;

import ro.nicuch.elementalsx.ElementalsX;

public class Field {
    private final FieldId id;
    private final UUID owner;
    private final Field2D field2D;
    private final Set<UUID> members = new HashSet<>();
    private final int chunkX;
    private final int chunkZ;
    private final World world;
    private final YamlConfiguration config;
    private final File file;
    private boolean fun;
    private final int blockX;
    private final int blockY;
    private final int blockZ;

    public Field(FieldId id, UUID owner, Field2D field2D, Block block) {
        this(id, owner, field2D, block.getChunk(), block.getX(), block.getY(), block.getZ());
    }

    public Field(FieldId id, UUID owner, Field2D field2D, Chunk chunk, int x, int y, int z) {
        this.id = id;
        this.owner = owner;
        this.field2D = field2D;
        this.chunkX = chunk.getX();
        this.chunkZ = chunk.getZ();
        this.world = chunk.getWorld();
        this.file = new File(
                ElementalsX.get().getDataFolder() + File.separator + "regiuni" + File.separator + this.id.getOldId() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
        if (this.file.exists()) {
            this.members.addAll(FieldUtil.convertStringsToUUIDs(this.config.getStringList("members")));
            this.fun = this.config.getBoolean("fun", false);
        }
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
        this.save();
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
        this.save();
        return this;
    }

    public Field removeMember(UUID uuid) {
        if (!this.isMember(uuid))
            return this;
        this.members.remove(uuid);
        this.save();
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

    public Field setMembers(List<UUID> uuid) {
        this.members.clear();
        this.members.addAll(uuid);
        this.save();
        return this;
    }

    public void save() {
        try {
            this.config.set("members", FieldUtil.convertUUIDsToStrings(this.members));
            this.config.set("fun", this.fun);
            this.config.save(this.file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void delete() {
        this.file.delete();
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
