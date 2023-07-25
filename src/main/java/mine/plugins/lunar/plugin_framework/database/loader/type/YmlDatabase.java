package mine.plugins.lunar.plugin_framework.database.loader.type;

import org.bukkit.configuration.file.YamlConfiguration;

public interface YmlDatabase {

    void enable(YamlConfiguration config);
    YamlConfiguration disable();

}
