package mine.plugins.lunar.plugin_framework.task;

import lombok.NonNull;
import mine.plugins.lunar.plugin_framework.data.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class TaskHandler {

    protected final JavaPlugin plugin;
    public TaskHandler(JavaPlugin plugin, long period) {
        this.plugin = plugin;
        this.period = period;
    }

    protected final long period;

    private BukkitTask task;
    private long tickTimeStamp = System.nanoTime();

    public void restart(@NonNull Callable<Boolean> runnable) {
        restart(0, runnable);
    }

    public void restart(long delay, @NonNull Callable<Boolean> runnable) {
        start(delay, runnable);
    }

    /**
     * Return false to stop the task
     */
    public void start(@NonNull Callable<Boolean> runnable) {
        start(Math.max(period - (long) (((System.nanoTime() - tickTimeStamp) /
                (double) TimeUnit.SECONDS.toNanos(1)) * 20), 0),
            runnable);
    }

    public void start(long delay, @NonNull Callable<Boolean> runnable) {
        if (!plugin.isEnabled())
            return;

        stop();

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            tickTimeStamp = System.nanoTime();

            try {
                if (!runnable.call()) stop();
            } catch (Exception e) {
                if (Debugger.isDebugActive)
                    plugin.getLogger().log(Level.WARNING, "Task handle failed to execute");
                stop();
            }

        }, delay, period);
    }

    public void stop() {
        if (task == null || !plugin.isEnabled()) return;
        task.cancel();
    }

    public boolean isStopped() {
        return task == null || task.isCancelled();
    }
}
