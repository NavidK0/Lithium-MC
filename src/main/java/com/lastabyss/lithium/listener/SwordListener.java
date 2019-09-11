package com.lastabyss.lithium.listener;

import com.lastabyss.lithium.Lithium;
import com.lastabyss.lithium.data.ItemRegistry;
import com.lastabyss.lithium.util.DurabilityUtils;
import com.lastabyss.lithium.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Navid
 */
public class SwordListener extends BaseLithiumListener {

    public SwordListener(Lithium plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRecipeEvent(PrepareItemCraftEvent evt) {
        if (!ItemRegistry.hasCustomAbilities(evt.getInventory().getResult())) return;
        if (evt.getInventory().getResult().getType() != Material.IRON_SWORD) return;
        ItemStack[] matrix = evt.getInventory().getMatrix();
        if (matrix[4] == null || !matrix[4].isSimilar(ItemRegistry.LITHIUM_BATTERY_ITEM))
            evt.getInventory().setResult(null);
        else if (matrix[7] == null || !matrix[7].isSimilar(ItemRegistry.LITHIUM_BATTERY_ITEM))
            evt.getInventory().setResult(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEvent(PlayerInteractEvent evt) {
        ItemStack itemInHand = evt.getItem();
        if (!ItemRegistry.hasCustomAbilities(itemInHand)) return;
        NBTTagCompound tag = Util.getItemNBT(itemInHand);
        if (itemInHand.getType() != Material.IRON_SWORD) return;
        if (DurabilityUtils.getCharge(itemInHand) <= 0) {
            Util.sendActionBar(evt.getPlayer(), ChatColor.RED + "\u26A0 CHARGE DEPLETED! \u26A0");
            evt.setCancelled(true);
            return;
        }
        if (evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            int curr = tag.getInt("effect");
            int level = tag.getInt("level");
            int max = 23;
            curr++;
            if (curr > max) {
                curr = 1;
            }
            PotionEffectType effect = PotionEffectType.getById(curr);
            String name = effect.getName().replace("_", " ");
            tag.setInt("effect", curr);
            itemInHand = Util.setItemNBT(itemInHand, tag);
            Util.sendActionBar(evt.getPlayer(), ChatColor.GREEN + "Toggled " + ItemRegistry.LITHIUM_SWORD_NAME + ChatColor.GREEN + " mode | "
                    + ChatColor.BLUE + name + " lvl. " + level);
            ItemMeta itemMeta = itemInHand.getItemMeta();
            itemMeta.setDisplayName(ItemRegistry.LITHIUM_SWORD_NAME + ChatColor.GRAY + " - " + ChatColor.RED + name + " lvl. " + level);
            itemInHand.setItemMeta(itemMeta);
            evt.getPlayer().playSound(evt.getPlayer().getLocation(), "gui.button.press", 1, 2);
            evt.getPlayer().setItemInHand(itemInHand);
            evt.getPlayer().updateInventory();
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Player) {
            Player p = (Player) evt.getDamager();
            if (ItemRegistry.hasCustomAbilities(p.getItemInHand())) {
                if (p.getItemInHand().getType() == Material.IRON_SWORD) {
                    if (!(evt.getEntity() instanceof LivingEntity)) return;
                    LivingEntity entity = (LivingEntity) evt.getEntity();
                    PotionEffectType[] values = PotionEffectType.values();
                    NBTTagCompound nbt = Util.getItemNBT(p.getItemInHand());
                    int curr = nbt.getInt("effect");
                    int level = nbt.getInt("level");
                    PotionEffectType value = values[curr];
                    entity.addPotionEffect(new PotionEffect(value, 100, level, false, true), true);
                }
            }
        }
    }

}
