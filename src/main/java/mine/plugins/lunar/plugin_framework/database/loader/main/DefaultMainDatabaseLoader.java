package mine.plugins.lunar.plugin_framework.database.loader.main;

import mine.plugins.lunar.plugin_framework.data.DataInfo;

public class DefaultMainDatabaseLoader<T> extends MainDatabaseLoader<T> {

    @Override
    public T loadData(DataInfo<T> dataInfo, T defaultData) {
        return defaultData;
    }

    @Override
    public void saveData(DataInfo<T> dataInfo, T data) {

    }
}
