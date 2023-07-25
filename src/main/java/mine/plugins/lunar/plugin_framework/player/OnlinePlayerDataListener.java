package mine.plugins.lunar.plugin_framework.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class OnlinePlayerDataListener implements Listener {

    private final OnlinePlayerDataHandler<?> playerDataHandler;

    public OnlinePlayerDataListener(OnlinePlayerDataHandler<?> playerDataHandler) {
        this.playerDataHandler = playerDataHandler;

        for (var player : Bukkit.getServer().getOnlinePlayers())
            register(player);
    }

    public void disable() {
        for (var player : Bukkit.getServer().getOnlinePlayers())
            unregister(player);
    }

    private void register(Player player) {
        playerDataHandler.register(player);
    }

    private void unregister(Player player) {
        playerDataHandler.unregister(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void registerPlayer(PlayerJoinEvent e) {
        register(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void unregisterPlayer(PlayerQuitEvent e) {
        unregister(e.getPlayer());
    }
}
