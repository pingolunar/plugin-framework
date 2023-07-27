package mine.plugins.lunar.plugin_framework.event.listener;

import mine.plugins.lunar.plugin_framework.event.PlayerDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    private void onPlayerDamageTaken(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player))
            return;

        if (player.getGameMode() == GameMode.SPECTATOR) {
            e.setCancelled(true);
            return;
        }

        if (e.getFinalDamage() < player.getHealth())
            return;

        var playerInv = player.getInventory();
        if (playerInv.getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING
                || playerInv.getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING)
            return;

        var playerDeathEvent = new PlayerDeathEvent(e, null, player);
        Bukkit.getPluginManager().callEvent(playerDeathEvent);
        e.setCancelled(playerDeathEvent.isCancelled());
    }
}
