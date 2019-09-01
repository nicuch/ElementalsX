package ro.nicuch.elementalsx.elementals;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class NanoBowShotEvent extends Event implements Cancellable {
    private final static HandlerList handlers = new HandlerList();
    private final LivingEntity entity;
    private final ItemStack bow;
    private Projectile proj;
    private boolean cancel;

    public NanoBowShotEvent(LivingEntity entity, ItemStack bow, Projectile proj) {
        this.entity = entity;
        this.bow = bow;
        this.proj = proj;
    }

    public void setProjectile(Projectile proj) {
        this.proj = proj;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public ItemStack getBow() {
        return this.bow;
    }

    public Projectile getProjectile() {
        return this.proj;
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
