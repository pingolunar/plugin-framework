package mine.plugins.lunar.plugin_framework.database.loader;

import lombok.NonNull;
import lombok.Setter;
import mine.plugins.lunar.plugin_framework.data.DataHandler;
import mine.plugins.lunar.plugin_framework.data.DataInfo;

public abstract class DatabaseLoader<T> {

    @Setter protected @NonNull DataHandler dataHandler;

    public abstract void load(T data, DataInfo<T> dataInfo);
    public abstract void unload(T data, DataInfo<T> dataInfo);
}
