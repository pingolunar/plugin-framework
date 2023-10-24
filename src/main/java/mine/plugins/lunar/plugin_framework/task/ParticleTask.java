package mine.plugins.lunar.plugin_framework.task;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class ParticleTask extends TaskHandler {

    private static final long taskUpdateTimer = 4;
    private static final double taskUpdateMultiplier = 1D / (20D / taskUpdateTimer);

    public ParticleTask(JavaPlugin plugin, double maxRenderDistance) {
        super(plugin, taskUpdateTimer);
        this.maxRenderDistance = maxRenderDistance;
    }

    private final double maxRenderDistance;
    @Getter protected final List<Location> locations = Collections.synchronizedList(new LinkedList<>());
    protected final Map<String, Vector> velocities = new ConcurrentHashMap<>();

    public void addLocations(List<Location> locations) {
        this.locations.addAll(locations);
    }

    public void clearLocations() {
        locations.clear();
    }

    public void setVelocity(String id, Vector dir) {
        velocities.put(id, dir.multiply(taskUpdateMultiplier));
    }

    public void removeVelocity(String id) {
        velocities.remove(id);
    }

    public void clearVelocities() {
        velocities.clear();
    }

    public void restart(Player player, @NonNull Particle.DustOptions dust) {
        super.restart(getStart(player, dust));
    }

    public void start(Player player, @NonNull Particle.DustOptions dust) {
        super.start(getStart(player, dust));
    }

    public void start(World world, @NonNull Particle.DustOptions dust) {
        super.start(getStart(world, dust));
    }

    public void start(World world, @NonNull Particle.DustOptions dust, @NonNull Callable<List<Location>> callable) {
        super.start(getStart(world, dust, callable));
    }

    private Callable<Boolean> getStart(World world, @NonNull Particle.DustOptions dust) {
        return getStart(world, dust, () -> locations);
    }

    private Callable<Boolean> getStart(World world, @NonNull Particle.DustOptions dust,
                                       @NonNull Callable<List<Location>> callable) {
        return () -> {
            for (var location : callable.call()) {
                world.spawnParticle(Particle.REDSTONE, location, 1, dust);

                for (var velocity : velocities.values())
                    location.add(velocity);
            }

            return true;
        };
    }

    private Callable<Boolean> getStart(Player player, @NonNull Particle.DustOptions dust) {
        return () -> {
            var playerLoc = player.getLocation();

            for (var location : locations) {
                if (Objects.equals(playerLoc.getWorld(), location.getWorld()) &&
                        playerLoc.distance(location) <= maxRenderDistance)
                    player.spawnParticle(Particle.REDSTONE, location, 1, dust);

                for (var velocity : velocities.values())
                    location.add(velocity);
            }

            return true;
        };
    }

}
