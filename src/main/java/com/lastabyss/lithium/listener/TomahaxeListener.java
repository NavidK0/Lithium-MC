package com.lastabyss.lithium.listener;

import com.lastabyss.lithium.Lithium;
import com.lastabyss.lithium.data.ItemRegistry;
import com.lastabyss.lithium.util.DurabilityUtils;
import com.lastabyss.lithium.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Navid
 */
public class TomahaxeListener extends BaseLithiumListener {

    public TomahaxeListener(Lithium plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRecipeEvent(PrepareItemCraftEvent evt) {
        if (!ItemRegistry.hasCustomAbilities(evt.getInventory().getResult())) return;
        if (evt.getInventory().getResult().getType() != Material.IRON_AXE) return;
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
        if (itemInHand.getType() != Material.IRON_AXE) return;
        if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            String mode = "chop";
            if (tag.hasKey("mode")) {
                mode = tag.getString("mode");
                switch (mode) {
                    case "chop":
                        mode = "throw";
                        break;
                    case "throw":
                        mode = "timber";
                        break;
                    case "timber":
                        mode = "chop";
                        break;
                }
            }
            tag.setString("mode", mode);
            itemInHand = Util.setItemNBT(itemInHand, tag);
            Util.sendActionBar(evt.getPlayer(), ChatColor.GREEN + "Toggled " + ItemRegistry.LITHIUM_TOMAHAXE_NAME + ChatColor.GREEN + " mode | "
                    + ChatColor.BLUE + mode);
            evt.getPlayer().playSound(evt.getPlayer().getLocation(), "gui.button.press", 1, 2);
            evt.getPlayer().setItemInHand(itemInHand);
            evt.getPlayer().updateInventory();
            return;
        } else if (evt.getAction() == Action.LEFT_CLICK_BLOCK || evt.getAction() == Action.LEFT_CLICK_AIR) {
            CraftItemStack cItemStack = (CraftItemStack) itemInHand;
            net.minecraft.server.v1_8_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(cItemStack);
            String mode = tag.getString("mode");
            if (mode == null) mode = "chop";
            if (mode.equals("chop")) {
                if (evt.isCancelled()) return;
            } else if (mode.equals("throw")) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    evt.getPlayer().getInventory().setItemInHand(new ItemStack(Material.AIR, 0));
                    evt.getPlayer().updateInventory();
                });
                evt.getPlayer().getLocation().getWorld().playSound(evt.getPlayer().getLocation(), Sound.SHOOT_ARROW, 10, 1);
                Item item = evt.getPlayer().getWorld().dropItem(evt.getPlayer().getLocation(), itemInHand);
                Vector vel = evt.getPlayer().getLocation().getDirection().multiply(1.5).add(new Vector(0, 0.5, 0));
                item.setVelocity(vel);
                BukkitTask bukkitTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        List<Entity> nearbyEntities = item.getNearbyEntities(1, 1, 1);
                        for (Entity e : nearbyEntities) {
                            if (e instanceof LivingEntity) {
                                LivingEntity l = (LivingEntity) e;
                                if (l.getUniqueId().equals(evt.getPlayer().getUniqueId())) return;
                                int damage = 5;
                                double dist = evt.getPlayer().getLocation().distance(l.getLocation());
                                dist -= (dist * .75);
                                damage += dist;
                                l.damage(damage, evt.getPlayer());
                                l.getLocation().getWorld().playSound(l.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
                                evt.getPlayer().playSound(l.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
                                item.setItemStack(CraftItemStack.asBukkitCopy(DurabilityUtils.subtractExtendedDurability(itemStack, 2)));
                                item.setVelocity(vel.multiply(-1.5).add(new Vector(0, 0.5, 0)));
                                Bukkit.getScheduler().runTaskLater(plugin, () -> checkValidity(evt.getPlayer(), item), 20L);
                                cancel();
                                break;
                            }
                        }
                    }
                }.runTaskTimer(plugin, 0L, 1L);
                Bukkit.getScheduler().runTaskLater(plugin, () -> checkValidity(evt.getPlayer(), item), 20 * 5);
                Bukkit.getScheduler().runTaskLater(plugin, bukkitTask::cancel, 20 * 60);
            } else if (mode.equals("timber")) {
                if (evt.isCancelled()) return;
                if (isChoppableBlock(evt.getClickedBlock()))
                    evt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 1, true), true);
                else
                    evt.getPlayer().removePotionEffect(PotionEffectType.SLOW_DIGGING);
            }
            ItemStack modified = CraftItemStack.asBukkitCopy(itemStack);
            evt.getPlayer().setItemInHand(modified);
        }
    }

    private void checkValidity(Player player, Item item) {
        if (item.isValid()) {
            ItemStack is = item.getItemStack();
            player.getInventory().addItem(is);
            player.updateInventory();
            item.remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent evt) {
        if (!ItemRegistry.hasCustomAbilities(evt.getPlayer().getItemInHand())) return;
        if (evt.getPlayer().getItemInHand().getType() != Material.IRON_AXE) return;
        CraftPlayer craftPlayer = (CraftPlayer) evt.getPlayer();
        ItemStack itemInHand = evt.getPlayer().getItemInHand();
        EntityPlayer player = craftPlayer.getHandle();
        CraftItemStack cItemStack = (CraftItemStack) itemInHand;
        NBTTagCompound tag = Util.getItemNBT(itemInHand);
        net.minecraft.server.v1_8_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(cItemStack);
        String mode = tag.getString("mode");
        if (mode.equals("timber")) {
            if (isLog(evt.getBlock().getType())) {
                int chop = chop(evt.getBlock(), evt.getPlayer());
                itemStack = DurabilityUtils.subtractExtendedDurability(itemStack, chop);
            } else {
                itemStack = DurabilityUtils.subtractExtendedDurability(itemStack, 1);
            }
        } else {
            itemStack = DurabilityUtils.subtractExtendedDurability(itemStack, 1);
        }
        ItemStack modified = CraftItemStack.asBukkitCopy(itemStack);
        evt.getPlayer().setItemInHand(modified);
    }

    public int chop(Block block, Player player) {
        List blocks = new LinkedList();
        Block highest = getHighestLog(block);
        int chopped = 0;
        if (isTree(highest)) {
            getBlocksToChop(block, highest, blocks);
            chopped = blocks.size() * 2;
            popLogs(blocks, player);
        }
        return chopped;
    }

    public void getBlocksToChop(Block block, Block highest, List<Block> blocks) {
        getHighestLog(highest);
        while (block.getY() <= highest.getY()) {
            if (!blocks.contains(block)) {
                blocks.add(block);
            }
            getBranches(blocks, block.getRelative(BlockFace.NORTH));
            getBranches(blocks, block.getRelative(BlockFace.NORTH_EAST));
            getBranches(blocks, block.getRelative(BlockFace.EAST));
            getBranches(blocks, block.getRelative(BlockFace.SOUTH_EAST));
            getBranches(blocks, block.getRelative(BlockFace.SOUTH));
            getBranches(blocks, block.getRelative(BlockFace.SOUTH_WEST));
            getBranches(blocks, block.getRelative(BlockFace.WEST));
            getBranches(blocks, block.getRelative(BlockFace.NORTH_WEST));

            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH))) {
                getBranches(blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH));
            }

            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST))) {
                getBranches(blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST));
            }

            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST))) {
                getBranches(blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST));
            }

            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST))) {
                getBranches(blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST));
            }

            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH))) {
                getBranches(blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH));
            }

            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST))) {
                getBranches(blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST));
            }

            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST))) {
                getBranches(blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST));
            }

            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST))) {
                getBranches(blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST));
            }

            if ((blocks.contains(block.getRelative(BlockFace.UP))) || !isLog(block.getRelative(BlockFace.UP).getType()))
                break;
            block = block.getRelative(BlockFace.UP);
        }
    }

    public void getBranches(List<Block> blocks, Block other) {
        if (!blocks.contains(other) && isLog(other.getType()))
            getBlocksToChop(other, getHighestLog(other), blocks);
    }

    public Block getHighestLog(Block block) {
        while (isLog(block.getRelative(BlockFace.UP).getType())) {
            block = block.getRelative(BlockFace.UP);
        }
        return block;
    }

    public boolean isTree(Block block) {
            int counter = 0;
            Block brick = getHighestLog(block);
            if (getHighestLog(brick).getY() - block.getY() < 24) {
                if (isLeaves(block.getRelative(BlockFace.UP).getType())) counter++;
                if (isLeaves(block.getRelative(BlockFace.DOWN).getType())) counter++;
                if (isLeaves(block.getRelative(BlockFace.NORTH).getType())) counter++;
                if (isLeaves(block.getRelative(BlockFace.EAST).getType())) counter++;
                if (isLeaves(block.getRelative(BlockFace.SOUTH).getType())) counter++;
                if (isLeaves(block.getRelative(BlockFace.WEST).getType())) counter++;
                if (counter >= 2) {
                    return true;
                }
                if ((counter < 2) &&
                        block.getRelative(BlockFace.DOWN).getType() == Material.DIRT &&
                        isLog(block.getRelative(BlockFace.UP).getType())
                        && (block.getRelative(BlockFace.NORTH).getType() == Material.AIR)
                        && (block.getRelative(BlockFace.EAST).getType() == Material.AIR)
                        && (block.getRelative(BlockFace.SOUTH).getType() == Material.AIR)
                        && (block.getRelative(BlockFace.WEST).getType() == Material.AIR)) {
                    return true;
                }

                if (block.getData() == 1) {
                    block = block.getRelative(BlockFace.UP);
                    if (isLeaves(block.getRelative(BlockFace.UP).getType()) &&
                            isLeaves(block.getRelative(BlockFace.DOWN).getType())) counter++;
                    if (isLeaves(block.getRelative(BlockFace.NORTH).getType())) counter++;
                    if (isLeaves(block.getRelative(BlockFace.EAST).getType())) counter++;
                    if (isLeaves(block.getRelative(BlockFace.SOUTH).getType())) counter++;
                    if (isLeaves(block.getRelative(BlockFace.WEST).getType())) counter++;
                    if (counter >= 2) {
                        return true;
                    }
                    if ((counter < 2) &&
                            (block.getRelative(BlockFace.DOWN).getType() == Material.DIRT)
                            && isLog(block.getRelative(BlockFace.UP).getType())
                            && (block.getRelative(BlockFace.NORTH).getType() == Material.AIR)
                            && (block.getRelative(BlockFace.EAST).getType() == Material.AIR)
                            && (block.getRelative(BlockFace.SOUTH).getType() == Material.AIR)
                            && (block.getRelative(BlockFace.WEST).getType() == Material.AIR)) {
                        return true;
                    }

                    return false;
                }
                return false;
            }
            return false;
    }

    public boolean isLog(Material mat) {
        return mat == Material.LOG || mat == Material.LOG_2;
    }
    public boolean isLeaves(Material mat) {
        return mat == Material.LEAVES || mat == Material.LEAVES_2;
    }


    public void popLogs(List<Block> blocks, Player player) {
        ItemStack item = new ItemStack(1, 1, (short) 0);
        item.setAmount(1);
        for (int counter = 0; counter < blocks.size(); counter++) {
            Block block = blocks.get(counter);
            block.breakNaturally();
            if (Lithium.coreProtectAPI != null) {
                Lithium.coreProtectAPI.logRemoval(
                        player.getName(),
                        block.getLocation(),
                        block.getTypeId(),
                        block.getData()
                );
            }
            block.getLocation().getWorld().spigot().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 0, 0, 3, 3, 3, 0.1f, 10, 3);
            block.getLocation().getWorld().playSound(block.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5f, 1);
        }
    }

    public boolean isChoppableBlock(Block block) {
        Material type = block.getType();
        return type == Material.LOG ||
                type == Material.LOG_2 ||
                type == Material.WOOD ||
                type == Material.BIRCH_WOOD_STAIRS ||
                type == Material.ACACIA_STAIRS ||
                type == Material.DARK_OAK_STAIRS ||
                type == Material.JUNGLE_WOOD_STAIRS ||
                type == Material.WOOD_STAIRS ||
                type == Material.WOOD_STEP ||
                type == Material.WOOD_DOUBLE_STEP;
    }
}
