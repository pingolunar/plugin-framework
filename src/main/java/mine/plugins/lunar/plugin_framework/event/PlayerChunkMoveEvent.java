package mine.plugins.lunar.plugin_framework.event;

import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nullable;

public class PlayerChunkMoveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private boolean cancel = false;

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean state) {
        cancel = state;
    }

    public final PlayerMoveEvent playerMoveEvent;

    public PlayerChunkMoveEvent(PlayerMoveEvent playerMoveEvent) {
        this.playerMoveEvent = playerMoveEvent;
    }

    public @Nullable Chunk getToChunk() {
        var to = playerMoveEvent.getTo();
        if (to == null) return null;

        return to.getChunk();
    }

    public @NonNull Chunk getFromChunk() {
        var from = playerMoveEvent.getFrom();
        return from.getChunk();
    }

}
