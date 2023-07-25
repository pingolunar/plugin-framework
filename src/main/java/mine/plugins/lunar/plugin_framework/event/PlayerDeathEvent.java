package mine.plugins.lunar.plugin_framework.event;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nullable;

public class PlayerDeathEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Getter private final EntityDamageEvent baseEvent;
    @Getter @Nullable private final Player aggressor;
    @Getter private final Player victim;

    public PlayerDeathEvent(EntityDamageEvent baseEvent, @Nullable Player aggressor, Player victim) {
        this.baseEvent = baseEvent;
        this.aggressor = aggressor;
        this.victim = victim;

        victim.getWorld().spawnParticle(Particle.SMOKE_NORMAL, victim.getLocation(), 6);

        if (aggressor != null)
            aggressor.playSound(aggressor.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
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
}
