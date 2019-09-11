package com.lastabyss.lithium.listener;

import com.lastabyss.lithium.Lithium;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Navid
 */
public class DurabilityListener implements Listener {

    private Lithium plugin;

    public DurabilityListener(Lithium plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Checks for changes in durability for most tools when a player breaks a block
     *
     * @param event event details
     */
    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getItemInHand() != null && hasDurability(event.getPlayer().getItemInHand())) {
            Bukkit.getPluginManager().callEvent(new ItemLosesDurabilityEvent(event.getPlayer(), event.getPlayer().getItemInHand(), -1));
        }
    }

    /**
     * Checks for changes in durability for armor when a player is damaged in any way
     *
     * @param event event details
     */
    @EventHandler(ignoreCancelled = true)
    public void onDamaged(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack[] armorContents = player.getInventory().getArmorContents();
            for (int i = 0; i < armorContents.length; i++) {
                ItemStack item = armorContents[i];
                if (item != null && hasDurability(item)) {
                    Bukkit.getPluginManager().callEvent(new ItemLosesDurabilityEvent(player, item, i));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack item = player.getItemInHand();
            if (item != null && hasDurability(item)) {
                Bukkit.getPluginManager().callEvent(new ItemLosesDurabilityEvent(player, item, -1));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLaunch(EntityShootBowEvent event) {
        Bukkit.getPluginManager().callEvent(new ItemLosesDurabilityEvent(event.getEntity(), event.getBow(), -1));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getPlayer().getItemInHand();
            if (item != null && hasDurability(event.getPlayer().getItemInHand())) {
                Bukkit.getPluginManager().callEvent(new ItemLosesDurabilityEvent(event.getPlayer(), item, -1));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        new ItemLosesDurabilityEvent(event.getPlayer(), event.getPlayer().getItemInHand(), -1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == State.CAUGHT_ENTITY || event.getState() == State.CAUGHT_FISH)
            new ItemLosesDurabilityEvent(event.getPlayer(), event.getPlayer().getItemInHand(), -1);
    }

    private boolean hasDurability(ItemStack item) {
        return item.getType().getMaxDurability() > 0;
    }
}

class ItemLosesDurabilityEvent extends Event {

    private LivingEntity entity;
    private ItemStack itemStack;
    private int slot;

    public ItemLosesDurabilityEvent(LivingEntity entity, ItemStack item, int armorSlot) {
        this.entity = entity;
        this.itemStack = item;
        this.slot = armorSlot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * If this returns -1, this item is in the hand.
     * @return
     */
    public int getSlot() {
        return slot;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}