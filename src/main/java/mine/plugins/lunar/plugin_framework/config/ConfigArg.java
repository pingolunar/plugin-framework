package mine.plugins.lunar.plugin_framework.config;

import mine.plugins.lunar.plugin_framework.cmds.args.Arg;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigArg extends Arg {

    private final Map<String, Field> fields;
    private final Object skillConfig;

    public ConfigArg(Object skillConfig, String permission) {
        super("set", new String[] {"Missing field name", "Missing field value"}, permission);

        this.skillConfig = skillConfig;
        fields = Stream.of(skillConfig.getClass().getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, Function.identity()));
    }

    @Override
    protected void execute(CommandSender sender, String base, LinkedList<String> args) {

        var fieldSelected = fields.get(args.getFirst());
        if (fieldSelected == null) {
            sender.sendMessage("Invalid field name");
            return;
        }

        var value = switch (fieldSelected.getType().toString()) {
            case "float":
                try {
                    yield Float.parseFloat(args.getLast());
                } catch (NumberFormatException ignored) {
                    yield null;
                }

            case "String":
                yield args.getLast();

            case "int":
                try {
                    yield Integer.parseInt(args.getLast());
                } catch (NumberFormatException ignored) {
                    yield null;
                }

            default:
                yield null;
        };

        if (value == null) {
            sender.sendMessage("Invalid value");
            return;
        }

        try {
            fieldSelected.set(skillConfig, value);
            sender.sendMessage("Field '"+fieldSelected.getName()+"' set to: "+value);

        } catch (IllegalAccessException | IllegalArgumentException exception) {
            sender.sendMessage("Failed to set value");
        }
    }

    @Override
    protected Collection<String> tabComplete(CommandSender sender, LinkedList<String> args) {
        return switch (args.size()) {
            case 0, 1 -> fields.keySet();
            case 2 -> {
                var fieldSelected = fields.get(args.getFirst());
                if (fieldSelected == null) yield Collections.emptyList();
                yield List.of("<"+fieldSelected.getType()+">");
            }
            default -> Collections.emptyList();
        };
    }

    @Override
    public String info() {
        return "Modifies the selected config value";
    }
}
