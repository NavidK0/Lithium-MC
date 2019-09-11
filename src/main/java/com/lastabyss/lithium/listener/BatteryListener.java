package com.lastabyss.lithium.listener;

import com.lastabyss.lithium.Lithium;
import com.lastabyss.lithium.data.ItemRegistry;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Navid
 */
public class BatteryListener extends BaseLithiumListener {

    public BatteryListener(Lithium plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRecipeEvent(PrepareItemCraftEvent evt) {
        if (!ItemRegistry.hasCustomAbilities(evt.getInventory().getResult())) return;
        if (evt.getInventory().getResult().getType() != Material.STONE_BUTTON) return;
        ItemStack[] matrix = evt.getInventory().getMatrix();
        if (matrix[3] == null || !matrix[3].isSimilar(ItemRegistry.LITHIUM_DUST_ITEM))
            evt.getInventory().setResult(null);
        else if (matrix[4] == null || !matrix[4].isSimilar(ItemRegistry.LITHIUM_DUST_ITEM))
            evt.getInventory().setResult(null);
        else if (matrix[5] == null || !matrix[5].isSimilar(ItemRegistry.LITHIUM_DUST_ITEM))
            evt.getInventory().setResult(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlaceEvent(BlockPlaceEvent evt) {
        if (ItemRegistry.hasCustomAbilities(evt.getItemInHand())) evt.setCancelled(true);
    }
}
