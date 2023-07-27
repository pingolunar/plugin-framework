package mine.plugins.lunar.plugin_framework.event;

import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nullable;

public class PlayerBlockMoveEvent extends Event implements Cancellable {

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

    public PlayerBlockMoveEvent(PlayerMoveEvent playerMoveEvent) {
        this.playerMoveEvent = playerMoveEvent;
    }

    public @Nullable Block getToBlock() {
        var to = playerMoveEvent.getTo();
        if (to == null) return null;

        return to.getBlock();
    }

    public @NonNull Block getFromBlock() {
        var from = playerMoveEvent.getFrom();
        return from.getBlock();
    }

}
