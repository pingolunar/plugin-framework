package mine.plugins.lunar.plugin_framework.structure;

import com.github.shynixn.structureblocklib.api.bukkit.StructureBlockLibApi;
import com.github.shynixn.structureblocklib.api.enumeration.StructureRestriction;
import com.github.shynixn.structureblocklib.api.enumeration.StructureRotation;
import lombok.NonNull;
import lombok.Setter;
import mine.plugins.lunar.plugin_framework.data.DataHandler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Vector3i;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

public class StructureHandler {

    private static final String fileExtension = ".nbt";
    private static final Path baseStructurePath = Paths.get("structures");

    private final JavaPlugin plugin;
    private final DataHandler dataHandler;

    @NonNull @Setter private String name = "default";
    @NonNull @Setter private Vector3i size = new Vector3i(16, 16, 16);
    @NonNull @Setter private StructureRotation rotation = StructureRotation.NONE;

    private Path structurePath;
    @NonNull @Setter private Location location;

    public StructureHandler(JavaPlugin plugin, World world) {
        this.plugin = plugin;
        this.dataHandler = new DataHandler(plugin);

        setStructurePath(Paths.get(""));
        this.location = new Location(world, 0, 0, 0);

        dataHandler.createPath(structurePath);
    }

    public void setStructurePath(@NonNull Path structurePath) {
        this.structurePath = baseStructurePath.resolve(structurePath);
    }

    public List<String> listStructuresNames() {
        return dataHandler.listFilesName(structurePath).map(dataHandler::removeExtension).toList();
    }

    private Path getStructureFile() {
        return plugin.getDataFolder().toPath()
                .resolve(structurePath).resolve(name+fileExtension);
    }

    /**
     * Saves the structure file only if it already exists.
     */
    public void save(@NonNull Consumer<Throwable> onException, @NonNull Runnable onResult) {

        if (!dataHandler.doesFileExist(structurePath.resolve(name+fileExtension))) {
            onException.accept(new IllegalArgumentException());
            return;
        }

        forceSave(onException, onResult);
    }

    /**
     * Saves the structure file even if it doesn't exist.
     */
    public void forceSave(@NonNull Consumer<Throwable> onException, @NonNull Runnable onResult) {

        dataHandler.createPath(structurePath);

        StructureBlockLibApi.INSTANCE
            .saveStructure(plugin)
            .at(location)
            .restriction(StructureRestriction.UNLIMITED)
            .sizeX(size.x)
            .sizeY(size.y)
            .sizeZ(size.z)
            .includeEntities(true)
            .saveToPath(getStructureFile())
            .onException(onException)
            .onResult(e -> onResult.run());
    }

    public void load(@NonNull Consumer<Throwable> onException, @NonNull Runnable onResult) {

        StructureBlockLibApi.INSTANCE
            .loadStructure(plugin)
            .at(location)
            .includeEntities(true)
            .rotation(rotation)
            .loadFromPath(getStructureFile())
            .onException(onException)
            .onResult(e -> onResult.run());
    }

}
