package mine.plugins.lunar.plugin_framework.database.loader;

import mine.plugins.lunar.plugin_framework.data.DataInfo;
import mine.plugins.lunar.plugin_framework.database.loader.type.YmlDatabase;

public class DatabaseYmlLoader<T> extends DatabaseLoader<T> {

    @Override
    public void load(T data, DataInfo<T> dataInfo) {
        ((YmlDatabase)data).enable(dataHandler.loadConfig(dataInfo.path(), dataInfo.fileName()));
    }

    @Override
    public void unload(T data, DataInfo<T> dataInfo) {
        dataHandler.saveConfig(dataInfo.path(), dataInfo.fileName(), ((YmlDatabase)data).disable());
    }
}
