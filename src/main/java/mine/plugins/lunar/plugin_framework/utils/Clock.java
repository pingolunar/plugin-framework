package mine.plugins.lunar.plugin_framework.utils;

import java.time.Duration;
import java.time.Instant;

public class Clock {

    private final Duration cycleDuration;
    private Instant start;

    public Clock(Duration cycleDuration) {
        this.cycleDuration = cycleDuration;
        resetTimer();
    }

    public void resetTimer() {
        start = Instant.now();
    }

    public Duration getTimeElapsed() {
        return getTimeElapsed(start);
    }

    public boolean didDurationElapsed() {
        return didDurationElapsed(start, cycleDuration);
    }

    public void attemptRun(Runnable runnable) {
        if (!didDurationElapsed()) return;

        runnable.run();
        resetTimer();
    }


    public static Duration getTimeElapsed(Instant timestamp) {
        return Duration.between(timestamp, Instant.now());
    }

    public static boolean didDurationElapsed(Instant timestamp, Duration duration) {
        return getTimeElapsed(timestamp).compareTo(duration) > 0;
    }
}
