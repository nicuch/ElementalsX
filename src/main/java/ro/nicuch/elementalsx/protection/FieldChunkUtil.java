package ro.nicuch.elementalsx.protection;

import org.bukkit.Chunk;

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
}
