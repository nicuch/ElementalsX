package ro.nicuch.elementalsx.protection;

import org.bukkit.Chunk;
import ro.nicuch.tag.TagRegister;
import ro.nicuch.tag.register.ChunkRegister;
import ro.nicuch.tag.register.RegionRegister;
import ro.nicuch.tag.register.WorldRegister;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class FieldChunkUtil {
    private final static ConcurrentMap<ChunkData, Boolean> chunks = new ConcurrentHashMap<>();

    public static void setChunkToWait(Chunk chunk) {
        chunks.put(ChunkData.fromChunk(chunk), true);
    }

    public static void removeChunk(Chunk chunk) {
        chunks.remove(ChunkData.fromChunk(chunk));
    }

    public static boolean doChunkWait(Chunk chunk) {
        return chunks.containsKey(ChunkData.fromChunk(chunk));
    }

    public static boolean isChunkTagLoaded(Chunk chunk) {
        Optional<WorldRegister> worldRegister = TagRegister.getWorld(chunk.getWorld());
        if (worldRegister.isEmpty())
            return false;
        Optional<RegionRegister> regionRegister = worldRegister.get().getRegion(chunk);
        if (regionRegister.isEmpty())
            return false;
        Optional<ChunkRegister> chunkRegister = regionRegister.get().getChunk(chunk);
        return chunkRegister.isPresent();
    }
}
