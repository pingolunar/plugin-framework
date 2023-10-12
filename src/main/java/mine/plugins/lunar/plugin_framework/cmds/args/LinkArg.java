package mine.plugins.lunar.plugin_framework.cmds.args;

import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class LinkArg extends Arg {

	private final Map<String, Arg> cmdArgs = new HashMap<>();

	protected void addCmdArgs(List<Arg> args) {
		cmdArgs.putAll(args.stream().collect(Collectors.toMap(Arg::getName, Function.identity())));
	}

	protected LinkArg(String name, @NonNull List<Arg> args) {
		this(name, args, null);
	}

	protected LinkArg(String name, @NonNull List<Arg> args, @Nullable String permission) {
		super(name, null, permission);
		addCmdArgs(args);
	}

	protected LinkArg(String name, @Nullable String permission) {
		super(name, null, permission);
	}

	@Override
	protected void execute(CommandSender sender, String base, LinkedList<String> args) {

		if (args.isEmpty()) {
			var info = new StringBuilder("'/"+ChatColor.AQUA+base+" "+ChatColor.RESET+
				"': "+info()+"\nValid arguments:\n");
			for (Arg arg : cmdArgs.values()) {

				var permission = arg.getPermission();
				if (permission != null && !sender.hasPermission(permission)) continue;

				info.append("-").append(ChatColor.AQUA).append(arg.getName()).append(ChatColor.RESET)
					.append(": ").append(arg.info()).append("\n");
			}

			sender.sendMessage(info.toString());
			return;
		}

		String argName = args.getFirst().toLowerCase();
		Arg arg = cmdArgs.get(argName);

		if (arg == null) {
			sender.sendMessage("Invalid argument");
			return;
		}

		arg.work(sender, base, args);
	}

	protected abstract List<Arg> tabComplete(CommandSender sender, Collection<Arg> args);

	@Override
	public final Collection<String> tabComplete(CommandSender sender, LinkedList<String> args) {
		if (args.size() <= 1) {
			var tabs = new LinkedList<String>();

			for (Arg arg : tabComplete(sender, cmdArgs.values())) {
				var permission = arg.getPermission();
				if (permission != null && !sender.hasPermission(permission)) continue;

				tabs.add(arg.getName());
			}

			return tabs;
		}
		
		Arg arg = cmdArgs.get(args.getFirst().toLowerCase());
		if (arg == null) return Collections.emptyList();
		
		return arg.tab(sender, args);
	}
	
}
