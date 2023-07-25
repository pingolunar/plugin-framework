package mine.plugins.lunar.plugin_framework.database.loader;

import mine.plugins.lunar.plugin_framework.data.DataInfo;
import mine.plugins.lunar.plugin_framework.database.loader.type.DefaultDatabase;

public class DefaultDatabaseLoader<T> extends DatabaseLoader<T> {

    @Override
    public void load(T data, DataInfo<T> dataInfo) {
        ((DefaultDatabase)data).enable();
    }

    @Override
    public void unload(T data, DataInfo<T> dataInfo) {
        ((DefaultDatabase)data).disable();
    }
}
