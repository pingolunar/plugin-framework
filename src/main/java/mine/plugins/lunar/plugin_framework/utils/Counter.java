package mine.plugins.lunar.plugin_framework.utils;

import lombok.Getter;

public class Counter {
    @Getter private int count = 0;

    public void increment() {
        count++;
    }

    public void decrement() {
        count--;
    }
}
