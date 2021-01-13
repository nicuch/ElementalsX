package ro.nicuch.elementalsx.chair;

import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;

import java.util.Optional;

public class ChairListener implements Listener {

    @EventHandler
    public void event(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Player player = event.getPlayer();
        if (player.isSneaking() || player.isGliding() || player.isRiptiding())
            return;
        Optional<User> optionalUser = ElementalsX.getUser(player);
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        if (user.hasChairsDisabled())
            return;
        Block block = event.getClickedBlock();
        if (block == null)
            return;
        if (Tag.SLABS.isTagged(block.getType())) {
            ChairUtil.sitPlayerOnSlabs(player, block);
            event.setCancelled(true);
        } else if (Tag.STAIRS.isTagged(block.getType())) {
            ChairUtil.sitPlayerOnStairs(player, block);
            event.setCancelled(true);
        } else if (Tag.CARPETS.isTagged(block.getType())) {
            ChairUtil.sitPlayerOnCarpet(player, block);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void event(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            ChairUtil.unsitPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void event(PlayerQuitEvent event) {
        ChairUtil.unsitPlayer(event.getPlayer());
    }

    @EventHandler
    public void event(BlockBreakEvent event) {
        if (ChairUtil.occupiedChairs.containsValue(event.getBlock()) || ChairUtil.occupiedSits.containsValue(event.getBlock())) {
            ChairUtil.unsitPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void event(PlayerDeathEvent event) {
        ChairUtil.unsitPlayer(event.getEntity());
    }

    @EventHandler
    public void event(PlayerTeleportEvent event) {
        ChairUtil.unsitPlayer(event.getPlayer());
    }
}
