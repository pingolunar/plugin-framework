package mine.plugins.lunar.plugin_framework.cmds.args;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public abstract class PlayerArg extends Arg {

	protected PlayerArg(String name) {
		this(name, null);
	}

	protected PlayerArg(String name, @Nullable String[] argsInfo) {
		this(name, argsInfo, null);
	}

    public PlayerArg(String name, String[] argsInfo, String permission) {
        super(name, argsInfo, permission);
    }

    protected abstract void execute(Player player, LinkedList<String> args);
	protected abstract Collection<String> tabComplete(Player player, LinkedList<String> args);
	
	@Override
	protected final void execute(CommandSender sender, String base, LinkedList<String> args) {
		if (!(sender instanceof Player player)) return;
		execute(player, args);
	}
	
	@Override
	protected final Collection<String> tabComplete(CommandSender sender, LinkedList<String> args) {
		if (!(sender instanceof Player player)) return Collections.emptyList();
		return tabComplete(player, args);
	}
}
