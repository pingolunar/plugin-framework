package mine.plugins.lunar.plugin_framework.config;

import lombok.Getter;
import mine.plugins.lunar.plugin_framework.cmds.BaseCmd;
import mine.plugins.lunar.plugin_framework.data.DataHandler;
import mine.plugins.lunar.plugin_framework.data.DataInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Serializable;
import java.util.List;

public class ConfigManager<T extends Serializable> {

    private final DataInfo<T> dataInfo;

    private final JavaPlugin plugin;
    @Getter private final T generalConfig;

    public ConfigManager(JavaPlugin plugin, DataInfo<T> dataInfo, T newConfig) {
        this.plugin = plugin;
        this.dataInfo = dataInfo;

        var dataHandler = new DataHandler(plugin);
        generalConfig = dataHandler.load(dataInfo, newConfig);
        new BaseCmd(new ConfigLinkArg(generalConfig, plugin.getName()+".config"), List.of());
    }

    public void save() {
        var dataHandler = new DataHandler(plugin);
        dataHandler.save(dataInfo, generalConfig);
    }
}
