package ro.nicuch.elementalsx.protection;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldId {
    private final int x;
    private final int y;
    private final int z;
    private final String world;

    public FieldId(int x, int y, int z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    @Override
    public String toString() {
        return "<x" + this.x + ",y" + this.y + ",z" + this.z + ",world" + this.world + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof FieldId)) return false;
        FieldId that = (FieldId) o;
        return this.x == that.x &&
                this.y == that.y &&
                this.z == that.z &&
                world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, world, this.toString());
    }

    private final static Pattern pattern = Pattern.compile("<x([-]?[0-9]+),y([-]?[0-9]+),z([-]?[0-9]+),world([a-zA-Z0-9_-]+)>");

    public static FieldId fromLocation(Location location) {
        return fromCoords(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
    }

    public static FieldId fromCoords(int x, int y, int z, String world) {
        return new FieldId(x, y, z, world);
    }

    public static FieldId fromBlock(Block block) {
        return fromCoords(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
    }

    public static FieldId fromString(String str) {
        try {
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                int z = Integer.parseInt(matcher.group(3));
                String world = matcher.group(4);
                return new FieldId(x, y, z, world);
            } else
                throw new IllegalArgumentException("FieldId couldn't parse from string.");
        } catch (IllegalStateException | NumberFormatException e) {
            throw new IllegalArgumentException("FieldId couldn't parse from string.");
        }
    }
}
