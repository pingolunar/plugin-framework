package mine.plugins.lunar.plugin_framework.task;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.List;

public class LinearParticleTask extends ParticleTask {

    public LinearParticleTask(JavaPlugin plugin, double maxRenderDistance, int moveCount) {
        this(plugin, maxRenderDistance, moveCount, true);
    }

    public LinearParticleTask(JavaPlugin plugin, double maxRenderDistance, int moveCount, boolean isShapeClosed) {
        super(plugin, maxRenderDistance);

        this.spacing = 1D / Math.max(1, moveCount);
        dirTask = new TaskHandler(plugin, period);

        count = moveCount;
        this.isShapeClosed = isShapeClosed;
    }

    private final double spacing;
    private final int count;
    private final boolean isShapeClosed;

    private final TaskHandler dirTask;
    private final List<Vector> dirs = new LinkedList<>();

    int t = 0;

    public void clearLocations() {
        dirs.clear();
        super.clearLocations();
    }

    private void addDir(Location loc, Location nextLoc) {
        dirs.add(nextLoc.clone().subtract(loc).toVector().multiply(spacing));
    }

    public void addLocations(List<Location> locations) {
        var locationIterator = locations.iterator();

        if (!locationIterator.hasNext()) return;
        var previousLocation = locationIterator.next();

        while (locationIterator.hasNext()) {
            var currentLocation = locationIterator.next();

            addDir(previousLocation, currentLocation);
            previousLocation = currentLocation;
        }

        if (isShapeClosed)
            addDir(previousLocation, locations.get(0));
        else
            addDir(locations.get(locations.size()-2), locations.get(locations.size()-1));

        super.addLocations(locations);
    }

    public void restart(Player player, @NonNull Particle.DustOptions dust) {
        super.restart(player, dust);
        run();
    }

    public void start(Player player, @NonNull Particle.DustOptions dust) {
        super.start(player, dust);
        run();
    }

    private void run() {
        t = 0;
        dirTask.start(() -> {
            int i = 0;

            if (t++ < count) {
                for (var location : locations)
                    location.add(dirs.get(i++));
            }
            else {
                for (var location : locations)
                    location.subtract(dirs.get(i++).clone().multiply(count));
                t = 0;
            }

            return true;
        });
    }

    public void stop() {
        dirTask.stop();
        super.stop();
    }
}
