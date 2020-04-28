package ro.nicuch.elementalsx.protection;

import org.bukkit.Chunk;

import java.util.Objects;

public class ChunkData {
    private final int x;
    private final int z;
    private final String worldName;

    public ChunkData(final int x, final int z, final String worldName) {
        this.x = x;
        this.z = z;
        this.worldName = worldName;
    }

    public ChunkData(final Chunk chunk) {
        this(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
    }

    public final int getX() {
        return this.x;
    }

    public final int getZ() {
        return this.z;
    }

    public final String getWorld() {
        return this.worldName;
    }

    public static ChunkData fromChunk(final Chunk chunk) {
        return new ChunkData(chunk);
    }

    public static ChunkData fromCoords(final int x, final int z, final String worldName) {
        return new ChunkData(x, z, worldName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkData)) return false;
        ChunkData that = (ChunkData) o;
        return this.x == that.x &&
                this.z == that.z &&
                this.worldName.equals(that.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, worldName);
    }
}
