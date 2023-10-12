package mine.plugins.lunar.plugin_framework.utils;

import lombok.Getter;

@Getter
public class Counter {

    private int count = 0;
    private final int maxCount;

    public Counter() {
        maxCount = 0;
    }

    public Counter(int maxCount) {
        this.maxCount = maxCount;
    }

    public void increment() {
        count++;
    }

    public void decrement() {
        count--;
    }

    /**
     * Increments if the counter is less than maxCount
     * @return True if the counter has reset.
     */
    public boolean clampedIncrement() {
        if (count < maxCount) {
            ++count;
            return false;
        }

        count = 0;
        return true;
    }
}
