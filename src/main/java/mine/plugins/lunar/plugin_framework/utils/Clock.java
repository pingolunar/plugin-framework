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
        return Duration.between(start, Instant.now());
    }

    public void attemptRun(Runnable runnable) {
        if (getTimeElapsed().compareTo(cycleDuration) < 0) return;

        runnable.run();
        resetTimer();
    }

}
