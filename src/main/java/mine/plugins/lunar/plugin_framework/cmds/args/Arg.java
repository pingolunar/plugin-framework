package mine.plugins.lunar.plugin_framework.cmds.args;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mine.plugins.lunar.plugin_framework.cmds.LengthState;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

@AllArgsConstructor
public abstract class Arg {
	@Getter private final String name;
	private final @Nullable String[] argsInfo;
	@Getter protected final @Nullable String permission;

	protected Arg(String name) {
		this.name = name;
		this.argsInfo = null;
		this.permission = null;
	}

	protected Arg(String name, @Nullable String[] argsInfo) {
		this.name = name;
		this.argsInfo = argsInfo;
		this.permission = null;
	}

	protected abstract void execute(CommandSender sender, String base, LinkedList<String> args);
	protected abstract Collection<String> tabComplete(CommandSender sender, LinkedList<String> args);
	public abstract String info();
	
	private LengthState isLengthCorrect(LinkedList<String> args) {
		if (argsInfo == null)
			return LengthState.CORRECT;

		int lengthDif = argsInfo.length-args.size();
		if (lengthDif == 0) return LengthState.CORRECT;
		if (lengthDif < 0) return LengthState.EXCESSIVE;

		return LengthState.MISSING;
	}
	
	public final void work(CommandSender sender, String base, LinkedList<String> args) {
		base += args.removeFirst()+" ";

		if (permission != null && !sender.hasPermission(permission)) {
			sender.sendMessage("You don't have permission to run this command");
			return;
		}

		var lengthState = isLengthCorrect(args);
		if (lengthState == LengthState.EXCESSIVE) {
			sender.sendMessage("Too many arguments");
			return;
		}

		if (lengthState == LengthState.MISSING) {
			if (argsInfo != null) sender.sendMessage(argsInfo[args.size()]);
			return;
		}

		execute(sender, base, args);
	}
	
	public final Collection<String> tab(CommandSender sender, LinkedList<String> args) {
		if (permission != null && !sender.hasPermission(permission)) return Collections.emptyList();

		args.removeFirst();
		if (isLengthCorrect(args) == LengthState.EXCESSIVE) return Collections.emptyList();

		return tabComplete(sender, args);
	}
}
