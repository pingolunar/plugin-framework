package mine.plugins.lunar.plugin_framework.database;

import lombok.NonNull;
import mine.plugins.lunar.plugin_framework.data.DataHandler;
import mine.plugins.lunar.plugin_framework.data.DataInfo;
import mine.plugins.lunar.plugin_framework.database.loader.DatabaseLoader;
import mine.plugins.lunar.plugin_framework.database.loader.main.MainDatabaseLoader;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Stream;

public abstract class DatabaseHandler<T> {

    /**
     * @param dataType Shouldn't be anonymous.
     */
    @SafeVarargs
    public DatabaseHandler(JavaPlugin plugin, int dataBaseInitialSize, Class<T> dataType,
                           @NonNull MainDatabaseLoader<T> mainDatabaseLoader,
                           @NonNull DatabaseLoader<T>... databaseLoaders) {

        dataHandler = new DataHandler(plugin);

        this.dataType = dataType;
        this.mainDatabaseLoader = mainDatabaseLoader;
        this.databaseLoaders = databaseLoaders;

        dataPath = Path.of(dataType.getSimpleName());
        database = new ConcurrentHashMap<>(dataBaseInitialSize);

        mainDatabaseLoader.setDataHandler(dataHandler);
        for (var databaseLoader : databaseLoaders)
            databaseLoader.setDataHandler(dataHandler);
    }

    private final MainDatabaseLoader<T> mainDatabaseLoader;
    private final DatabaseLoader<T>[] databaseLoaders;

    private final Path dataPath;
    private final Class<T> dataType;

    protected final DataHandler dataHandler;
    private final Map<String, T> database;

    protected abstract T getDefaultData();

    public int getLoadedSize() {
        return database.size();
    }

    //region Executor
    private final static ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Do NOT use this recursively
     */
    public static @Nullable Future<?> execute(Runnable runnable) {
        try { return executor.submit(runnable); }
        catch (RejectedExecutionException e) { return null; }
    }
    //endregion

    private DataInfo<T> getDataInfo(String id) {
        return new DataInfo<>(dataPath, id, dataType);
    }

    protected T get(String dataID) {
        var storedData = database.get(dataID);
        if (storedData != null) return storedData;

        var dataInfo = getDataInfo(dataID);
        var data = mainDatabaseLoader.loadData(dataInfo, getDefaultData());

        for (var databaseLoader : databaseLoaders)
            databaseLoader.load(data, dataInfo);

        return data;
    }

    public Stream<T> get(Collection<String> dataIDs) {
        return dataIDs.stream().map(this::get);
    }

    public Stream<String> getFilesID() {
        return dataHandler.listFilesName(dataPath)
            .filter(fileName -> fileName.endsWith(DataHandler.serExtension))
            .map(dataHandler::removeExtension);
    }

    /**
     * @return All data saved as file
     * <p>
     * Does not include the cache
     */
    protected Stream<T> get() {
        return get(getFilesID().toList());
    }

    protected void saveData(String id, @Nullable T data) {
        if (data == null) return;
        var dataInfo = getDataInfo(id);

        for (var databaseLoader : databaseLoaders)
            databaseLoader.unload(data, dataInfo);
        mainDatabaseLoader.saveData(dataInfo, data);
    }

    protected @Nullable T unregister(@NonNull String dataID) {
        var removedData = database.remove(dataID);
        saveData(dataID, removedData);
        return removedData;
    }

    public void unregisterLoaded() {
        for (var dataSet : database.entrySet())
            saveData(dataSet.getKey(), dataSet.getValue());
    }

    public @Nullable T getLoaded(String dataID) {
        return database.get(dataID);
    }

    protected T register(@NonNull String dataID) {
        var data = get(dataID);
        if (!isLoaded(dataID)) database.put(dataID, data);
        return data;
    }

    public boolean isLoaded(String dataID) {
        return database.containsKey(dataID);
    }
}
