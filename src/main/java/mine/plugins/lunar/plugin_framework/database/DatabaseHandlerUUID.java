package mine.plugins.lunar.plugin_framework.database;

import lombok.NonNull;
import mine.plugins.lunar.plugin_framework.database.loader.DatabaseLoader;
import mine.plugins.lunar.plugin_framework.database.loader.main.MainDatabaseLoader;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class DatabaseHandlerUUID<T extends Serializable> extends DatabaseHandler<T> {

    /**
     * @see DatabaseHandler
     */
    @SafeVarargs
    public DatabaseHandlerUUID(JavaPlugin plugin, int dataBaseInitialSize, Class<T> dataType,
                               MainDatabaseLoader<T> mainDatabaseLoader, DatabaseLoader<T>... databaseLoaders) {

        super(plugin, dataBaseInitialSize, dataType, mainDatabaseLoader, databaseLoaders);
    }

    protected @Nullable T getLoaded(UUID dataID) {
        return getLoaded(dataID.toString());
    }

    protected void saveData(UUID dataID, T data) {
        super.saveData(dataID.toString(), data);
    }

    public @Nullable T unregister(@NonNull UUID dataID) {
        return super.unregister(dataID.toString());
    }

    public T register(@NonNull UUID dataID) {
        return super.register(dataID.toString());
    }

    public T get(UUID dataID) {
        return super.get(dataID.toString());
    }

    public Stream<T> get() {
        return super.get();
    }

    public Stream<T> getList(Collection<UUID> dataIDs) {
        return super.get(dataIDs.stream().map(UUID::toString).toList());
    }
}
