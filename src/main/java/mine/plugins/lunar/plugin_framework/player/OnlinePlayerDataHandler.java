package mine.plugins.lunar.plugin_framework.player;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public abstract class OnlinePlayerDataHandler<T extends OnlinePlayerData> {

    protected final JavaPlugin plugin;

    private final Map<UUID, T> playersData = new ConcurrentHashMap<>();

    protected abstract T getDefaultPlayerData(Player player);

    public T register(Player player) {
        var defaultPlayerData = getDefaultPlayerData(player);
        playersData.put(player.getUniqueId(), defaultPlayerData);
        return defaultPlayerData;
    }

    void unregister(Player player) {
        playersData.remove(player.getUniqueId());
    }

    public T get(Player player) {
        var playerData = get(player.getUniqueId());
        return playerData == null ? register(player) : playerData;
    }

    public @Nullable T get(UUID id) {
        return playersData.get(id);
    }

}
