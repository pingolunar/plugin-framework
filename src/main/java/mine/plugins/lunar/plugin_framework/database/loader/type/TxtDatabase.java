package mine.plugins.lunar.plugin_framework.database.loader.type;

import lombok.NonNull;

public interface TxtDatabase {
    void enable(@NonNull String[] lines);
    String[] disable();
}
