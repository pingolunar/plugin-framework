package mine.plugins.lunar.plugin_framework.event.listener;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import mine.plugins.lunar.plugin_framework.event.BarrierProtection;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.List;
import java.util.logging.Level;

@AllArgsConstructor
public abstract class BarrierProtectionListener implements Listener {

    protected final JavaPlugin plugin;
    
    //region Abstract
    /**
     * @param attacker It may be a player
     */
    protected abstract boolean isVictimHitAllowed(Entity victim, Entity attacker);
    protected abstract boolean isPlayerInteractionAllowed(@NonNull Player player, Location location,
                                                          BarrierProtection barrierProtection);

    protected abstract boolean isEntityInteractionAllowed(Entity entity, Location location,
                                                          BarrierProtection barrierProtection);
    protected abstract boolean isEntitySpawnAllowed(Entity entity);

    protected abstract boolean areLocationsBetweenBorder(Location location, @Nullable Location otherlocation);
    protected abstract boolean areBlocksBetweenBorder(List<BlockState> blocks, Location sourceLocation);

    /**
     * @param blocks Should be modified
     */
    protected abstract void removeDisallowedBlocks(List<Block> blocks, Location sourceLocation);
    protected abstract void removeDisallowedBlocks(List<Block> blocks, Entity entity);
    //endregion

    //region Entity Utils
    private Entity getPotentialShooter(Entity attacker) {
        if (!(attacker instanceof Projectile projectile))
            return attacker;

        var shooter = projectile.getShooter();
        if (!(shooter instanceof Entity shooterEntity)) return attacker;

        return shooterEntity;
    }

    private boolean isEntityInteractionAllowed(Entity victim, Entity attacker) {
        return isVictimHitAllowed(victim, getPotentialShooter(attacker));
    }
    //endregion

    //region Events
    //region PLAYER_MOVEMENT
    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        var from = e.getFrom();

        var to = e.getTo();
        if (to == null) return;

        if (from.getX() == to.getX()
        && from.getY() == to.getY()
        && from.getZ() == to.getZ())
            return;

        var player = e.getPlayer();
        e.setCancelled(!isPlayerInteractionAllowed(player, to, BarrierProtection.PLAYER_MOVEMENT));
    }
    //endregion

    //region PLAYER_TELEPORT
    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        var from = e.getFrom();
        var to = e.getTo();

        var player = e.getPlayer();
        e.setCancelled(!isPlayerInteractionAllowed(player, to, BarrierProtection.PLAYER_TELEPORT));
    }

    @EventHandler(ignoreCancelled = true)
    private void respawnPlayer(PlayerRespawnEvent e) {
        isPlayerInteractionAllowed(e.getPlayer(), e.getRespawnLocation(), BarrierProtection.PLAYER_TELEPORT);
    }
    //endregion

    //region PLAYER_INTERACT
    @EventHandler(ignoreCancelled = true)
    public void onPlayerEmptyBucket(PlayerBucketEmptyEvent e) {
        e.setCancelled(!isPlayerInteractionAllowed(e.getPlayer(), e.getBlock().getLocation(),
                BarrierProtection.PLAYER_INTERACT));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFillBucket(PlayerBucketFillEvent e) {
        e.setCancelled(!isPlayerInteractionAllowed(e.getPlayer(), e.getBlock().getLocation(),
                BarrierProtection.PLAYER_INTERACT));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerUse(PlayerInteractEvent e) {

        var player = e.getPlayer();
        var clickedBlock = e.getClickedBlock();

        if (clickedBlock == null) {
            e.setCancelled(!isPlayerInteractionAllowed(player, player.getLocation(),
                    BarrierProtection.PLAYER_INTERACT));
            return;
        }

        e.setCancelled(!isPlayerInteractionAllowed(player, clickedBlock.getLocation(),
                BarrierProtection.PLAYER_INTERACT)
            || !isPlayerInteractionAllowed(player, clickedBlock.getRelative(e.getBlockFace()).getLocation(),
                BarrierProtection.PLAYER_INTERACT));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBuild(BlockPlaceEvent e) {
        e.setCancelled(!isPlayerInteractionAllowed(e.getPlayer(), e.getBlock().getLocation(),
                BarrierProtection.PLAYER_INTERACT));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDestroy(BlockBreakEvent e) {
        e.setCancelled(!isPlayerInteractionAllowed(e.getPlayer(), e.getBlock().getLocation(),
                BarrierProtection.PLAYER_INTERACT));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerHangingPlace(HangingPlaceEvent e) {
        var player = e.getPlayer();
        if (player == null) return;

        e.setCancelled(!isPlayerInteractionAllowed(player, e.getBlock().getLocation(),
                BarrierProtection.PLAYER_INTERACT));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerEntityInteract(PlayerInteractEntityEvent e) {
        e.setCancelled(!isPlayerInteractionAllowed(e.getPlayer(), e.getRightClicked().getLocation(),
                BarrierProtection.PLAYER_INTERACT));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerEntityAtInteract(PlayerInteractAtEntityEvent e) {
        e.setCancelled(!isPlayerInteractionAllowed(e.getPlayer(), e.getRightClicked().getLocation(),
                BarrierProtection.PLAYER_INTERACT));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerHarvest(PlayerHarvestBlockEvent e) {
        e.setCancelled(!isPlayerInteractionAllowed(e.getPlayer(), e.getHarvestedBlock().getLocation(),
                BarrierProtection.PLAYER_INTERACT));
    }
    //endregion

    //region ITEM
    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDrop(PlayerDropItemEvent e) {
        e.setCancelled(!isPlayerInteractionAllowed(e.getPlayer(), e.getItemDrop().getLocation(),
                BarrierProtection.ITEM));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityItemDrop(EntityDropItemEvent e) {
        e.setCancelled(!isEntityInteractionAllowed(e.getEntity(), e.getItemDrop().getLocation(),
                BarrierProtection.ITEM));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityItemPickup(EntityPickupItemEvent e) { /* Includes players */
        var entity = e.getEntity();
        var itemLoc = e.getItem().getLocation();

        e.setCancelled(entity instanceof Player player ?
            !isPlayerInteractionAllowed(player, itemLoc, BarrierProtection.ITEM) :
            !isEntityInteractionAllowed(entity, itemLoc, BarrierProtection.ITEM));
    }
    //endregion

    //region ENTITY_ATTACK
    @EventHandler(ignoreCancelled = true)
    public void onVehicleHurtEntity(VehicleDamageEvent e) {
        e.setCancelled(!isEntityInteractionAllowed(e.getVehicle(), e.getAttacker()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityHurtEntity(EntityDamageByEntityEvent e) {
        e.setCancelled(!isEntityInteractionAllowed(e.getEntity(), e.getDamager()));
    }
    //endregion

    //region PORTAL
    @EventHandler(ignoreCancelled = true)
    public void onEntityPortalEntry(EntityPortalEvent e) {
        e.setCancelled(!isEntityInteractionAllowed(e.getEntity(), e.getTo(), BarrierProtection.PORTAL));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPortalEntry(PlayerPortalEvent e) {
        e.setCancelled(!isPlayerInteractionAllowed(e.getPlayer(), e.getTo(), BarrierProtection.PORTAL));
    }
    //endregion

    //region WORLD
    /** includes dragon egg **/
    @EventHandler(ignoreCancelled = true)
    public void onLiquidFlow(BlockFromToEvent e) {
        e.setCancelled(areLocationsBetweenBorder(e.getToBlock().getLocation(), e.getBlock().getLocation()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent e) {
        e.setCancelled(areLocationsBetweenBorder(e.getSource().getLocation(), e.getBlock().getLocation()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent e) {
        Block source = e.getIgnitingBlock();
        if (source == null) return;

        e.setCancelled(areLocationsBetweenBorder(source.getLocation(), e.getBlock().getLocation()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent e) {

        var block = e.getBlock();
        var blockData = block.getBlockData();

        if (!(blockData instanceof Directional directionalBlock)) {
            plugin.getLogger().log(Level.WARNING,
                    e.getEventName()+" triggered with a non-directional block: "+block.getType());
            return;
        }

        var dispenseLocation = block.getRelative(directionalBlock.getFacing()).getLocation();
        e.setCancelled(areLocationsBetweenBorder(block.getLocation(), dispenseLocation));
    }
    //endregion

    //region PISTON
    public void preventBorderPistons(BlockPistonEvent e, List<Block> blocks) {
        var lastBlock = blocks.isEmpty() ? e.getBlock() : blocks.get(blocks.size()-1);

        e.setCancelled(areLocationsBetweenBorder(lastBlock.getLocation(),
                lastBlock.getRelative(e.getDirection()).getLocation()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonPush(BlockPistonExtendEvent e) {
        preventBorderPistons(e, e.getBlocks());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent e) {
        preventBorderPistons(e, e.getBlocks());
    }
    //endregion

    //region STATE
    @EventHandler(ignoreCancelled = true)
    public void onStructureCreation(StructureGrowEvent e) {
        e.setCancelled(areBlocksBetweenBorder(e.getBlocks(), e.getLocation()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpongeAbsorb(SpongeAbsorbEvent e) {
        e.setCancelled(areBlocksBetweenBorder(e.getBlocks(), e.getBlock().getLocation()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onMultiBlockBuild(BlockMultiPlaceEvent e) {
        e.setCancelled(areBlocksBetweenBorder(e.getReplacedBlockStates(), e.getBlock().getLocation()));
    }
    //endregion

    //region EXPLOSION
    @EventHandler(ignoreCancelled = true)
    public void onBlockExplosion(BlockExplodeEvent e) {
        removeDisallowedBlocks(e.blockList(), e.getBlock().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplosion(EntityExplodeEvent e) {
        removeDisallowedBlocks(e.blockList(), getPotentialShooter(e.getEntity()));
    }
    //endregion

    //region ENTITY_SPAWN
    @EventHandler(ignoreCancelled = true)
    public void onLingeringSplash(LingeringPotionSplashEvent e) {
        e.setCancelled(!isEntitySpawnAllowed(e.getAreaEffectCloud()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawnerSpawn(SpawnerSpawnEvent e) {
        e.setCancelled(areLocationsBetweenBorder(e.getEntity().getLocation(), e.getSpawner().getLocation()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityPlace(EntityPlaceEvent e) {
        var player = e.getPlayer();

        var isPlayerPlacementAllowed = player == null ||
                isPlayerInteractionAllowed(player, e.getBlock().getLocation(), BarrierProtection.ENTITY_SPAWN);

        e.setCancelled(!isEntitySpawnAllowed(e.getEntity()) || !isPlayerPlacementAllowed);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTransform(EntityTransformEvent e) {
        var allowedEntities = e.getTransformedEntities().stream().filter(this::isEntitySpawnAllowed).toList();
        e.setCancelled(allowedEntities.isEmpty());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent e) {
        e.setCancelled(!isEntitySpawnAllowed(e.getEntity()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleSpawn(VehicleCreateEvent e) {
        e.setCancelled(!isEntitySpawnAllowed(e.getVehicle()));
    }
    //endregion

    //region ENTITY_INTERACT
    /** Falling blocks trigger this event when it transforms to a block **/
    @EventHandler(ignoreCancelled = true)
    public void onEntityBlockDestruction(EntityChangeBlockEvent e) {
        e.setCancelled(!isEntityInteractionAllowed(getPotentialShooter(e.getEntity()), e.getBlock().getLocation(),
            BarrierProtection.ENTITY_INTERACT));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityBlockForm(EntityBlockFormEvent e) {
        e.setCancelled(!isEntityInteractionAllowed(e.getEntity(), e.getBlock().getLocation(),
                BarrierProtection.ENTITY_INTERACT));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityInteract(EntityInteractEvent e) {
        e.setCancelled(!isEntityInteractionAllowed(e.getEntity(), e.getBlock().getLocation(),
            BarrierProtection.ENTITY_INTERACT));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent e) {
        var thrower = getPotentialShooter(e.getPotion());

        for (var entity : e.getAffectedEntities()) {
            if (isEntityInteractionAllowed(thrower, entity.getLocation(), BarrierProtection.ENTITY_INTERACT))
                continue;

            e.setIntensity(entity, 0);
        }
    }
    //endregion
    //endregion
}
