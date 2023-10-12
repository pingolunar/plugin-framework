package mine.plugins.lunar.plugin_framework.config;

import mine.plugins.lunar.plugin_framework.cmds.args.Arg;
import mine.plugins.lunar.plugin_framework.cmds.args.LinkArg;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigLinkArg extends LinkArg {

    public ConfigLinkArg(Object configObj, String permission) {
        super("config", permission);

        var fields = Stream.of(configObj.getClass().getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, Function.identity()));

        addCmdArgs(List.of(
                new ConfigSetArg(configObj, fields, permission),
                new ConfigListArg(configObj, fields, permission)));
    }

    @Override
    public String info() {
        return "Config base command";
    }

    @Override
    protected List<Arg> tabComplete(CommandSender sender, Collection<Arg> args) {
        return args.stream().toList();
    }
}
