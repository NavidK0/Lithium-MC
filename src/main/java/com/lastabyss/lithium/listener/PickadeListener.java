package com.lastabyss.lithium.listener;


import com.lastabyss.lithium.Lithium;
import com.lastabyss.lithium.data.ItemRegistry;
import com.lastabyss.lithium.util.DurabilityUtils;
import com.lastabyss.lithium.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Navid
 */
public class PickadeListener extends BaseLithiumListener {

    public PickadeListener(Lithium plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRecipeEvent(PrepareItemCraftEvent evt) {
        if (!ItemRegistry.hasCustomAbilities(evt.getInventory().getResult())) return;
        if (evt.getInventory().getResult().getType() != Material.DIAMOND_PICKAXE) return;
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
        if (itemInHand.getType() != Material.DIAMOND_PICKAXE) return;
        if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            String mode = "mine";
            if (tag.hasKey("mode")) {
                mode = tag.getString("mode");
                switch (mode) {
                    case "mine":
                        mode = "dig";
                        break;
                    case "dig":
                        mode = "blast";
                        break;
                    case "blast":
                        mode = "mine";
                        break;
                }
            }
            tag.setString("mode", mode);
            itemInHand = Util.setItemNBT(itemInHand, tag);
            Util.sendActionBar(evt.getPlayer(), ChatColor.GREEN + "Toggled " + ItemRegistry.LITHIUM_PICKADE_NAME + ChatColor.GREEN + " mode | "
                    + ChatColor.BLUE + mode);
            evt.getPlayer().playSound(evt.getPlayer().getLocation(), "gui.button.press", 1, 2);
            evt.getPlayer().setItemInHand(itemInHand);
            evt.getPlayer().updateInventory();
            return;
        } else if (evt.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (evt.isCancelled()) return;
            CraftItemStack cItemStack = (CraftItemStack) itemInHand;
            net.minecraft.server.v1_8_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(cItemStack);
            String mode = tag.getString("mode");
            if (mode == null) mode = "mine";
            if (mode.equals("mine")) {
                if (evt.isCancelled()) return;
                if (!isDiggableBlock(evt.getClickedBlock())) {
                    evt.getPlayer().removePotionEffect(PotionEffectType.SLOW_DIGGING);
                } else {
                    evt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 2, true), true);
                }
            } else if (mode.equals("dig")) {
                if (evt.isCancelled()) return;
                if (isDiggableBlock(evt.getClickedBlock())) {
                    evt.getPlayer().removePotionEffect(PotionEffectType.SLOW_DIGGING);
                    evt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20, 100, true), true);
                } else {
                    evt.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
                    evt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 2, true), true);
                }
            }
            ItemStack modified = CraftItemStack.asBukkitCopy(itemStack);
            evt.getPlayer().setItemInHand(modified);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent evt) {
        if (!ItemRegistry.hasCustomAbilities(evt.getPlayer().getItemInHand())) return;
        if (evt.getPlayer().getItemInHand().getType() != Material.DIAMOND_PICKAXE) return;
        ItemStack itemInHand = evt.getPlayer().getItemInHand();
        CraftItemStack cItemStack = (CraftItemStack) itemInHand;
        NBTTagCompound tag = Util.getItemNBT(itemInHand);
        net.minecraft.server.v1_8_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(cItemStack);
        String mode = tag.getString("mode");
        if (mode.equals("blast")) {
            if (!isStoneBlock(evt.getBlock())) return;
            Location loc = evt.getBlock().getLocation();
            int radius = 2;
            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();
            int minX = x - radius;
            int minY = y - radius;
            int minZ = z - radius;
            int maxX = x + radius;
            int maxY = y + radius;
            int maxZ = z + radius;
            for (int intX = minX; intX <= maxX; intX++) {
                for (int intZ = minZ; intZ <= maxZ; intZ++) {
                    for (int intY = minY; intY <= maxY; intY++) {
                        Block block = loc.getWorld().getBlockAt(intX, intY, intZ);
                        if (isStoneBlock(block)) {
                            block.breakNaturally();
                            if (Lithium.coreProtectAPI != null) {
                                Lithium.coreProtectAPI.logRemoval(
                                        evt.getPlayer().getName(),
                                        block.getLocation(),
                                        block.getTypeId(),
                                        block.getData()
                                );
                            }
                            itemStack = DurabilityUtils.subtractExtendedDurability(itemStack, 1);
                            loc.getWorld().spigot().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 0, 0, 2, 2, 2, 0.1f, 10, 2);
                            loc.getWorld().playSound(block.getLocation(), Sound.PISTON_EXTEND, 0.3f, 1);
                        }
                    }
                }
            }
        } else {
            itemStack = DurabilityUtils.subtractExtendedDurability(itemStack, 1);
        }
        ItemStack modified = CraftItemStack.asBukkitCopy(itemStack);
        evt.getPlayer().setItemInHand(modified);
    }

    public boolean isStoneBlock(Block block) {
        Material type = block.getType();
        return type == Material.STONE ||
                type == Material.RED_SANDSTONE ||
                type == Material.SANDSTONE ||
                type == Material.ENDER_STONE ||
                type == Material.COBBLESTONE ||
                type == Material.MOSSY_COBBLESTONE;
    }

    public boolean isDiggableBlock(Block block) {
        Material type = block.getType();
        return type == Material.DIRT ||
                type == Material.GRAVEL ||
                type == Material.SAND ||
                type == Material.GRASS ||
                type == Material.SOUL_SAND ||
                type == Material.MYCEL ||
                type == Material.SNOW ||
                type == Material.SNOW_BLOCK ||
                type == Material.HARD_CLAY ||
                type == Material.STAINED_CLAY;
    }
}
