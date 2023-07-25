package mine.plugins.lunar.plugin_framework.data;

import java.nio.file.Path;

public record DataInfo<T>(Path path, String fileName, Class<T> type) {

}
