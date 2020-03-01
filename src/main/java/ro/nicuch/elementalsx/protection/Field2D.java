package ro.nicuch.elementalsx.protection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import ro.nicuch.elementalsx.ElementalsX;

public class Field2D {
    private final int maxX, minX, maxZ, minZ;

    public Field2D(final int maxX, final int maxZ, final int minX, final int minZ) {
        this.maxX = maxX;
        this.maxZ = maxZ;
        this.minX = minX;
        this.minZ = minZ;
    }

    public final int getMaxX() {
        return this.maxX;
    }

    public final int getMaxZ() {
        return this.maxZ;
    }

    public final int getMinX() {
        return this.minX;
    }

    public final int getMinZ() {
        return this.minZ;
    }

    public boolean isInLocation(Location location) {
        return this.maxX >= location.getBlockX()
                && this.maxZ >= location.getBlockZ()
                && this.minX <= location.getBlockX()
                && this.minZ <= location.getBlockZ();
    }

    public void sendFieldLocate(Player player, World world, int yLoc) {
        BlockData glass = Material.GLASS.createBlockData();
        for (int x = this.minX; x <= this.maxX; x++)
            player.sendBlockChange(world.getBlockAt(x, yLoc, (this.maxZ - 25)).getLocation(), glass);
        for (int z = this.minZ; z <= this.maxZ; z++)
            player.sendBlockChange(world.getBlockAt((this.maxX - 25), yLoc, z).getLocation(), glass);
        for (int y = 0; y < 256; y++)
            player.sendBlockChange(world.getBlockAt((this.maxX - 25), y, (this.maxZ - 25)).getLocation(), glass);
        Bukkit.getScheduler().runTaskLater(ElementalsX.get(), () -> {
            if (player != null && player.isOnline()) {
                Block block;
                for (int x = this.minX; x <= this.maxX; x++) {
                    block = world.getBlockAt(x, yLoc, (this.maxZ - 25));
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                }
                for (int z = this.minZ; z <= this.maxZ; z++) {
                    block = world.getBlockAt((this.maxX - 25), yLoc, z);
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                }
                for (int y = 0; y < 256; y++) {
                    block = world.getBlockAt((this.maxX - 25), y, (this.maxZ - 25));
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                }
            }
        }, 20 * 20);
    }

    public void sendFieldVisualize(Player player, World world) {
        int midX = this.maxX - ((this.maxX - this.minX) / 2);
        int midZ = this.maxZ - ((this.maxZ - this.minZ) / 2);
        BlockData glass = Material.GLASS.createBlockData();
        for (int y = 0; y < 256; y++) {
            player.sendBlockChange(world.getBlockAt(this.maxX, y, this.maxZ).getLocation(), glass);
            player.sendBlockChange(world.getBlockAt(this.minX, y, this.minZ).getLocation(), glass);
            player.sendBlockChange(world.getBlockAt(this.minX, y, this.maxZ).getLocation(), glass);
            player.sendBlockChange(world.getBlockAt(this.maxX, y, this.minZ).getLocation(), glass);
            player.sendBlockChange(world.getBlockAt(this.maxX, y, midZ).getLocation(), glass);
            player.sendBlockChange(world.getBlockAt(this.minX, y, midZ).getLocation(), glass);
            player.sendBlockChange(world.getBlockAt(midX, y, this.maxZ).getLocation(), glass);
            player.sendBlockChange(world.getBlockAt(midX, y, this.minZ).getLocation(), glass);
        }
        for (int y = 0; y < 256; y += 16) {
            for (int x = this.minX; x < this.maxX; x++) {
                player.sendBlockChange(world.getBlockAt(x, y, this.maxZ).getLocation(), glass);
                player.sendBlockChange(world.getBlockAt(x, y, this.minZ).getLocation(), glass);
            }
            for (int z = this.minZ; z < this.maxZ; z++) {
                player.sendBlockChange(world.getBlockAt(this.maxX, y, z).getLocation(), glass);
                player.sendBlockChange(world.getBlockAt(this.minX, y, z).getLocation(), glass);
            }
        }
        Bukkit.getScheduler().runTaskLater(ElementalsX.get(), () -> {
            if (player != null && player.isOnline()) {
                Block block;
                for (int y = 0; y < 256; y++) {
                    block = world.getBlockAt(this.maxX, y, this.maxZ);
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    block = world.getBlockAt(this.minX, y, this.minZ);
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    block = world.getBlockAt(this.minX, y, this.maxZ);
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    block = world.getBlockAt(this.maxX, y, this.minZ);
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    block = world.getBlockAt(this.maxX, y, midZ);
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    block = world.getBlockAt(this.minX, y, midZ);
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    block = world.getBlockAt(midX, y, this.maxZ);
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    block = world.getBlockAt(midX, y, this.minZ);
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                }
                for (int y = 0; y < 256; y += 16) {
                    for (int x = this.minX; x < this.maxX; x++) {
                        block = world.getBlockAt(x, y, this.maxZ);
                        player.sendBlockChange(block.getLocation(), block.getBlockData());
                        block = world.getBlockAt(x, y, this.minZ);
                        player.sendBlockChange(block.getLocation(), block.getBlockData());
                    }
                    for (int z = this.minZ; z < maxZ; z++) {
                        block = world.getBlockAt(this.maxX, y, z);
                        player.sendBlockChange(block.getLocation(), block.getBlockData());
                        block = world.getBlockAt(this.minX, y, z);
                        player.sendBlockChange(block.getLocation(), block.getBlockData());
                    }
                }
            }
        }, 20 * 20);
    }
}
