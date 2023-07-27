package mine.plugins.lunar.plugin_framework.event.listener;

import mine.plugins.lunar.plugin_framework.event.PlayerBlockMoveEvent;
import mine.plugins.lunar.plugin_framework.event.PlayerChunkMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Implements PlayerChunkMoveEvent & PlayerBlockMoveEvent
 */
public class PlayerMovementListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    private void branchEvent(PlayerMoveEvent e) {

        var to = e.getTo();
        if (to == null) return;

        var from = e.getFrom();

        var toChunk = to.getChunk();
        var fromChunk = from.getChunk();

        if (fromChunk.getX() != toChunk.getX() || fromChunk.getZ() != toChunk.getZ()) {
            var playerChunkMoveEvent = new PlayerChunkMoveEvent(e);

            Bukkit.getPluginManager().callEvent(playerChunkMoveEvent);
            e.setCancelled(playerChunkMoveEvent.isCancelled());
            return;
        }


        if (from.getBlockX() == to.getBlockX() &&
                from.getBlockY() == to.getBlockY() &&
                from.getBlockZ() == to.getBlockZ())
            return;

        var playerBlockMoveEvent = new PlayerBlockMoveEvent(e);

        Bukkit.getPluginManager().callEvent(playerBlockMoveEvent);
        e.setCancelled(playerBlockMoveEvent.isCancelled());
    }
}
