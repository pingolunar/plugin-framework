package mine.plugins.lunar.plugin_framework.database.loader;

import mine.plugins.lunar.plugin_framework.data.DataInfo;
import mine.plugins.lunar.plugin_framework.database.loader.type.TxtDatabase;

public class DatabaseTxtLoader<T> extends DatabaseLoader<T> {

    private final int fileLineSize;

    public DatabaseTxtLoader(int fileLineSize) {
        this.fileLineSize = fileLineSize;
    }

    @Override
    public void load(T data, DataInfo<T> dataInfo) {
        var txtData = (TxtDatabase) data;
        txtData.enable(dataHandler.loadTxt(dataInfo.path(), dataInfo.fileName(), fileLineSize));
    }

    @Override
    public void unload(T data, DataInfo<T> dataInfo) {
        var txtData = (TxtDatabase) data;
        dataHandler.saveTxt(dataInfo.path(), dataInfo.fileName(), txtData.disable());
    }
}
