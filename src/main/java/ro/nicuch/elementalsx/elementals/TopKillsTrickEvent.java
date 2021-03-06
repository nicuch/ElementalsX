package ro.nicuch.elementalsx.elementals;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class TopKillsTrickEvent extends PlayerEvent {
    private final static HandlerList handlers = new HandlerList();

    public TopKillsTrickEvent(Player player) {
        super(player);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}