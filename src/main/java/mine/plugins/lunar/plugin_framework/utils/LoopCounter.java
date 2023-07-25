package mine.plugins.lunar.plugin_framework.utils;

import lombok.Getter;

public class LoopCounter {

    private final int maxCount;
    @Getter private int count = 0;

    public LoopCounter(int maxCount) {
        this.maxCount = maxCount-1;
    }

    /**
     * @return True if the counter has reset.
     */
    public boolean increment() {
        if (count < maxCount) {
            count++;
            return false;
        }

        count = 0;
        return true;
    }

}
