package mine.plugins.lunar.plugin_framework.database.loader.main;

import mine.plugins.lunar.plugin_framework.data.DataInfo;

public class MainDatabaseSerLoader<T> extends MainDatabaseLoader<T> {

    @Override
    public T loadData(DataInfo<T> dataInfo, T defaultData) {
        return dataHandler.load(dataInfo, defaultData);
    }

    @Override
    public void saveData(DataInfo<T> dataInfo, T data) {
        dataHandler.save(dataInfo, data);
    }
}
