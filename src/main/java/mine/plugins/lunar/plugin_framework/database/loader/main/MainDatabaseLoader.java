package mine.plugins.lunar.plugin_framework.database.loader.main;

import lombok.NonNull;
import lombok.Setter;
import mine.plugins.lunar.plugin_framework.data.DataHandler;
import mine.plugins.lunar.plugin_framework.data.DataInfo;

public abstract class MainDatabaseLoader<T> {

    @Setter protected @NonNull DataHandler dataHandler;

    public abstract T loadData(DataInfo<T> dataInfo, T defaultData);
    public abstract void saveData(DataInfo<T> dataInfo, T data);
}
