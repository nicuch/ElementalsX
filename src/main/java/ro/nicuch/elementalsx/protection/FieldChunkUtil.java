package ro.nicuch.elementalsx.protection;

import io.netty.util.internal.ConcurrentSet;
import org.bukkit.Chunk;

import java.util.Set;

public class FieldChunkUtil {
    private final static Set<Chunk> chunks = new ConcurrentSet<>();

    public static void setChunkToWait(Chunk chunk) {
        chunks.add(chunk);
    }

    public static void removeChunk(Chunk chunk) {
        chunks.remove(chunk);
    }

    public static boolean doChunkWait(Chunk chunk) {
        return chunks.contains(chunk);
    }
}
