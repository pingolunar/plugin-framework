package mine.plugins.lunar.plugin_framework.cmds;

import lombok.NonNull;
import mine.plugins.lunar.plugin_framework.cmds.args.Arg;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class BaseCmd extends BukkitCommand {

	private final Arg arg;
	public BaseCmd(Arg arg, List<String> aliases) {
        super(arg.getName());
        
        this.description = arg.info();
        this.usageMessage = "Type /" + arg.getName();
        this.setPermission(arg.getPermission());
        this.setAliases(aliases);
        
        this.arg = arg;

		register();
	}
	
	private LinkedList<String> getAllArgs(String base, String[] args) {
		var allArgs = new LinkedList<>(Arrays.asList(args));
		allArgs.addFirst(base);
		return allArgs;
	}
	
	@Override
	public boolean execute(@NonNull CommandSender sender, @NonNull String base, @NonNull String[] args) {
		arg.work(sender, "", getAllArgs(base, args));
		return true;
	}
	
	@Override
	public @NotNull List<String> tabComplete(@NonNull CommandSender sender, @NonNull String base, @NonNull String[] args) {
		return new LinkedList<>(arg.tab(sender, getAllArgs(base, args)));
	}
	
	private void register() {
		try {
			Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			bukkitCommandMap.setAccessible(true);
			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

			commandMap.register(getName(), this);
			
		} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
			e.printStackTrace();
		} 
	}
	
}
