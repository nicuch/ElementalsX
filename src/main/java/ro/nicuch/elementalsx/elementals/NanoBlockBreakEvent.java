package ro.nicuch.elementalsx.elementals;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;

public class NanoBlockBreakEvent extends Event implements Cancellable {
    private final static HandlerList handlers = new HandlerList();
    private final Player player;
    private final Block block;
    private final List<ItemStack> items = Lists.newArrayList();
    private boolean cancel;

    public NanoBlockBreakEvent(Player player, Block block, List<ItemStack> list) {
        this.player = player;
        this.block = block;
        items.addAll(list);
    }

    public NanoBlockBreakEvent(Player player, Block block) {
        this.player = player;
        this.block = block;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Block getBlock() {
        return this.block;
    }

    public List<ItemStack> getDrops() {
        return this.items;
    }

    public void addDrop(ItemStack item) {
        this.items.add(item);
    }

    public void addDrops(List<ItemStack> items) {
        this.items.addAll(items);
    }

    public void clearDrops() {
        this.items.clear();
    }

    public User getUser() {
        return ElementalsX.getUser(this.player);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}
