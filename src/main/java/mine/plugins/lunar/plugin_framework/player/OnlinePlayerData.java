package mine.plugins.lunar.plugin_framework.player;

import lombok.Getter;
import lombok.NonNull;
import mine.plugins.lunar.plugin_framework.task.TaskHandler;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class OnlinePlayerData {

    @NonNull public final Player player;

    public OnlinePlayerData(JavaPlugin plugin, @NonNull Player player) {
        this.player = player;

        actionBarTask = new TaskHandler(plugin, actionBarTaskPeriod);
    }

    //region Utils
    public void resetModifiers() {
        for (var attributeType : Attribute.values()) {
            var attribute = player.getAttribute(attributeType);
            if (attribute == null) continue;

            for (var modifier : attribute.getModifiers())
                attribute.removeModifier(modifier);
        }
    }

    public void clearEffects() {
        for (var effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
    }

    public void resetContents() {
        clearEffects();

        player.getInventory().clear();
        player.getOpenInventory().close();
        player.setExp(0);
        player.setLevel(0);
    }

    public boolean isInventoryOpen() {
        return player.getOpenInventory().getType() != InventoryType.CRAFTING;
    }

    public void heal() {
        var maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttribute != null) player.setHealth(maxHealthAttribute.getValue());

        player.setFoodLevel(20);
        player.setSaturation(20f);
    }

    public void setToSpectate(@Nullable Location location) {
        player.setGameMode(GameMode.SPECTATOR);
        if (location != null) player.teleport(location);
    }
    //endregion

    //region Action Bar
    private static final long actionBarTaskPeriod = 10; //ticks
    private static final long restartActionBarTaskDelay = 40; //ticks

    @Getter private final TaskHandler actionBarTask;
    private Callable<Boolean> activeActionBarMsgTask;

    public void stopActiveActionBarMsg() {
        actionBarTask.stop();
    }

    public void setActiveActionBarMsg(Callable<ActionBarMsg> activeActionBarMsg) {
        this.activeActionBarMsgTask = () -> {
            sendMsg(activeActionBarMsg.call());
            return true;
        };

        actionBarTask.restart(activeActionBarMsgTask);
    }

    public void sendMsgOverride(ActionBarMsg msg) {
        sendMsg(msg);
        if (activeActionBarMsgTask == null) return;
        actionBarTask.restart(restartActionBarTaskDelay, activeActionBarMsgTask);
    }

    public void sendMsg(ActionBarMsg msg) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, msg.textComponent);
    }
    //endregion

}
