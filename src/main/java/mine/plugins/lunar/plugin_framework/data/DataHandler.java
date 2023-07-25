package mine.plugins.lunar.plugin_framework.data;

import lombok.Cleanup;
import lombok.NonNull;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static mine.plugins.lunar.plugin_framework.data.Debugger.isFileDebugActive;

public record DataHandler(JavaPlugin plugin) {

	public static final String serExtension = ".ser", ymlExtension = ".yml", txtExtension = ".txt";

	private Path getPluginDataFolder() {
		return plugin.getDataFolder().toPath();
	}

	public void saveTxt(Path path, String name, String... info) {
		var objPath = path.resolve(name + txtExtension);

		try {
			createFile(objPath);
			@Cleanup var myWriter = new FileWriter(getPluginDataFolder().resolve(objPath).toString());
			for (var infoLine : info) myWriter.write(infoLine+"\n");

			if (isFileDebugActive) plugin.getLogger().log(Level.INFO, "Saved: " + objPath);
		} catch (IOException e) {
			plugin.getLogger().log(Level.WARNING, "Failed to save: " + objPath);
		}
	}

	public @NonNull String[] loadTxt(Path path, String name, int lineAmount) {
		var objPath = path.resolve(name + txtExtension);

		try {
			var file = getFile(objPath);
			@Cleanup var scanner = new Scanner(file);

			var lines = new String[lineAmount];
			Arrays.fill(lines, "");

			var intIterator = IntStream.range(0, lines.length).iterator();
			while (intIterator.hasNext() && scanner.hasNextLine())
				lines[intIterator.next()] = scanner.nextLine();

			if (isFileDebugActive) plugin.getLogger().log(Level.INFO, "Loaded: " + objPath);
			return lines;

		} catch (FileNotFoundException e) {
			if (isFileDebugActive) plugin.getLogger().log(Level.WARNING, "Failed to load: " + objPath);
			return new String[0];
		}
	}

	public void saveConfig(Path path, String name, YamlConfiguration config) {
		var objPath = path.resolve(name + ymlExtension);

		try {
			var file = createFile(objPath);
			config.save(file);

			if (isFileDebugActive) plugin.getLogger().log(Level.INFO, "Saved: " + objPath);

		} catch (IOException | SecurityException e) {
			plugin.getLogger().log(Level.WARNING, "Failed to save: " + objPath);
		}
	}

	public YamlConfiguration loadConfig(Path path, String name) {
		var objPath = path.resolve(name + ymlExtension);
		var config = new YamlConfiguration();

		try {
			var file = getFile(objPath);
			config.load(file);

			if (isFileDebugActive) plugin.getLogger().log(Level.INFO, "Loaded: " + objPath);

		} catch (InvalidConfigurationException e) {
			plugin.getLogger().log(Level.WARNING, "Invalid config detected when loading: " + objPath);

		} catch (IOException | SecurityException e) {
			if (isFileDebugActive) plugin.getLogger().log(Level.WARNING, "Failed to load: " + objPath);
		}

		return config;
	}

	/**
	 * @param obj Must be serializable
	 */
	public <T> void save(DataInfo<T> dataInfo, @NonNull Object obj) {
		var objPath = dataInfo.path().resolve(dataInfo.fileName() + serExtension);

		try {
			createFile(objPath);

			@Cleanup FileOutputStream fileOut = new FileOutputStream(getPluginDataFolder().resolve(objPath).toString());
			@Cleanup ObjectOutputStream out = new ObjectOutputStream(fileOut);

			out.writeObject(obj);
			if (isFileDebugActive) plugin.getLogger().log(Level.INFO, "Saved: " + objPath);

		} catch (IllegalStateException e) {
			plugin.getLogger().log(Level.WARNING, "Illegal data state: " + objPath);

		} catch (IOException | SecurityException e) {
			plugin.getLogger().log(Level.WARNING, "Failed to save: " + objPath);

		} catch (ClassCastException e) {
			plugin.getLogger().log(Level.WARNING, "Failed to convert data: " + objPath);
		}
	}

	/**
	 * @return A serialized object
	 */
	public <T> T load(DataInfo<T> dataInfo, T newInstance) {
		var objPath = dataInfo.path().resolve(dataInfo.fileName() + serExtension);

		try {
			@Cleanup FileInputStream fileIn = new FileInputStream(getPluginDataFolder().resolve(objPath).toString());
			@Cleanup ObjectInputStream in = new ObjectInputStream(fileIn);

			if (isFileDebugActive) plugin.getLogger().log(Level.INFO, "Loaded: " + objPath);
			return dataInfo.type().cast(in.readObject());

		} catch (IOException | ClassNotFoundException e) {
			if (isFileDebugActive) plugin.getLogger().log(Level.WARNING, "Failed to load: " + objPath);
			return newInstance;

		} catch (ClassCastException e) {
			plugin.getLogger().log(Level.WARNING, "Failed to convert data: " + objPath);
			return newInstance;
		}
	}

	public Stream<String> listFilesName(Path path) {
		var dir = createPath(path);
		if (dir == null) return Stream.empty();

		try {
			var filesNames = dir.list();
			if (filesNames == null) {
				plugin.getLogger().log(Level.WARNING, "Failed to list files names at: " + path);
				return Stream.empty();
			}

			return Arrays.stream(filesNames).filter(fileName -> fileName.contains("."));

		} catch (SecurityException e) {
			plugin.getLogger().log(Level.WARNING, "Failed to read files names at: " + path);
			return Stream.empty();
		}
	}

	public String removeExtension(String fileName) {
		var fileNameSplit = fileName.split("\\.");
		if (fileNameSplit.length == 0) return "";
		return fileNameSplit[0];
	}

	@Nullable
	public File createPath(Path path) {
		try {
			var dir = new File(getPluginDataFolder().resolve(path).toString());
			if (dir.exists())
				return dir;

			var isPathCreated = dir.mkdirs();
			if (isPathCreated) {
				if (isFileDebugActive) plugin.getLogger().log(Level.INFO, "Path created: " + path);
				return dir;
			}

			return null;

		} catch (SecurityException e) {
			plugin.getLogger().log(Level.WARNING, "Failed to create path: " + path);
			return null;
		}
	}

	public File getFile(Path path) {
		return new File(getPluginDataFolder().resolve(path).toString());
	}

	public boolean doesFileExist(Path path) {
		try {
			return getFile(path).exists();

		} catch (SecurityException e) {
			plugin.getLogger().log(Level.WARNING, "Failed to verify path: " + path);
			return false;
		}
	}

	public void renameFile(Path path, String newName) {
		try {
			var source = Paths.get(getPluginDataFolder().resolve(path).toString());
			Files.move(source, source.resolveSibling(newName), StandardCopyOption.REPLACE_EXISTING);
			if (isFileDebugActive) plugin.getLogger().log(Level.INFO, "File '" + path + "' renamed to '" + newName + "'");

		} catch (IOException | SecurityException e) {
			plugin.getLogger().log(Level.WARNING, "Failed to rename file: " + path);
		}
	}

	private File createFile(Path path) throws IOException, SecurityException {
		var file = new File(getPluginDataFolder().resolve(path).toString());
		if (file.exists())
			return file;

		createPath(path.getParent());
		if (!file.createNewFile()) {
			plugin.getLogger().log(Level.WARNING, "Failed to create file at: " + path);
			return file;
		}

		if (isFileDebugActive) plugin.getLogger().log(Level.INFO, "File created: " + path);
		return file;
	}

}
