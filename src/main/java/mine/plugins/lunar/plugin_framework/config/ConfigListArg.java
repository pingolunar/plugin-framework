package mine.plugins.lunar.plugin_framework.config;

import mine.plugins.lunar.plugin_framework.cmds.args.Arg;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

public class ConfigListArg extends Arg {

    private final Object configObj;
    private final Map<String, Field> fields;

    public ConfigListArg(Object configObj, Map<String, Field> fields, String permission) {
        super("list", new String[] {}, permission);

        this.configObj = configObj;
        this.fields = fields;
    }

    @Override
    protected void execute(CommandSender sender, String base, LinkedList<String> args) {

        var msg = new StringBuilder("All configuration fields:\n");

        try {
            for (var field : fields.values())
                msg.append("-").append(field.getName()).append(": ").append(field.get(configObj)).append("\n");
        }
        catch (IllegalAccessException ignored) {
            sender.sendMessage("Value list failed");
            return;
        }

        sender.sendMessage(msg.toString());
    }

    @Override
    protected Collection<String> tabComplete(CommandSender sender, LinkedList<String> args) {
        return Collections.emptyList();
    }

    @Override
    public String info() {
        return "Modifies the selected config value";
    }
}
