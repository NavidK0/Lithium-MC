package com.lastabyss.lithium.listener;

import com.lastabyss.lithium.Lithium;
import com.lastabyss.lithium.data.ItemRegistry;
import com.lastabyss.lithium.entity.EntityLavaHook;
import com.lastabyss.lithium.util.DurabilityUtils;
import com.lastabyss.lithium.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.StatisticList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * @author Navid
 */
public class RodListener extends BaseLithiumListener {

    public RodListener(Lithium plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEvent(PlayerInteractEvent evt) {
        ItemStack itemInHand = evt.getItem();
        if (!ItemRegistry.hasCustomAbilities(itemInHand)) return;
        NBTTagCompound tag = Util.getItemNBT(itemInHand);
        if (itemInHand.getType() != Material.IRON_HOE) return;
        evt.setCancelled(true);
        if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            EntityPlayer player = ((CraftPlayer) evt.getPlayer()).getHandle();
            String mode = "fishing";
            if (tag.hasKey("mode")) {
                mode = tag.getString("mode");
                switch (mode) {
                    case "grapple":
                        mode = "fishing";
                        break;
                    case "fishing":
                        mode = "grapple";
                        break;
                }
            }
            tag.setString("mode", mode);
            itemInHand = Util.setItemNBT(itemInHand, tag);
            Util.sendActionBar(evt.getPlayer(), ChatColor.GREEN + "Toggled " + ItemRegistry.LITHIUM_ROD_NAME + ChatColor.GREEN + " mode | "
                    + ChatColor.BLUE + mode);
            evt.getPlayer().playSound(evt.getPlayer().getLocation(), "gui.button.press", 1, 2);
            evt.getPlayer().setItemInHand(itemInHand);
            if (player.hookedFish != null) {
                player.hookedFish.die();
                player.bw();
            }
            return;
        } else if (evt.getAction() == Action.LEFT_CLICK_AIR) {
            CraftPlayer craftPlayer = (CraftPlayer) evt.getPlayer();
            EntityPlayer player = craftPlayer.getHandle();
            CraftItemStack cItemStack = (CraftItemStack) itemInHand;
            net.minecraft.server.v1_8_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(cItemStack);
            boolean grapple = tag.hasKey("mode") && (tag.getString("mode").equals("grapple"));
            if (!grapple) {
                if (player.hookedFish != null) {
                    int hookDamage = player.hookedFish.l();
                    itemStack = DurabilityUtils.subtractExtendedDurability(itemStack, hookDamage);
                    player.bw();
                } else {
                    EntityLavaHook hook = new EntityLavaHook(player.getWorld(), player);
                    PlayerFishEvent playerFishEvent = new PlayerFishEvent(player.getBukkitEntity(), null, (Fish) hook.getBukkitEntity(), PlayerFishEvent.State.FISHING);
                    player.getWorld().getServer().getPluginManager().callEvent(playerFishEvent);
                    if (playerFishEvent.isCancelled()) {
                        player.hookedFish = null;
                        return;
                    }
                    player.getWorld().makeSound(player, "random.bow", 0.5F, 0.2F);
                    hook.setGrapple(false);
                    player.getWorld().addEntity(hook);
                    player.bw();
                    player.b(StatisticList.USE_ITEM_COUNT[Item.getId(itemStack.getItem())]);
                }
            } else {
                if (player.hookedFish != null) {
                    int hookDamage = player.hookedFish.l();
                    itemStack = DurabilityUtils.subtractExtendedDurability(itemStack, hookDamage);
                    player.bw();
                } else {
                    Vector v1 = evt.getPlayer().getLocation().getDirection().multiply(0.7);
                    EntityLavaHook hook = new EntityLavaHook(player.getWorld(), player, v1);
                    PlayerFishEvent playerFishEvent = new PlayerFishEvent(player.getBukkitEntity(), null, (Fish) hook.getBukkitEntity(), PlayerFishEvent.State.FISHING);
                    player.getWorld().getServer().getPluginManager().callEvent(playerFishEvent);
                    if (playerFishEvent.isCancelled()) {
                        player.hookedFish = null;
                        return;
                    }
                    player.getWorld().makeSound(player, "mob.zombie.metal", 0.5F, 0.2F);
                    player.getWorld().addEntity(hook);
                    player.bw();
                    player.b(StatisticList.USE_ITEM_COUNT[Item.getId(itemStack.getItem())]);
                }
            }
            ItemStack modified = CraftItemStack.asBukkitCopy(itemStack);
            evt.getPlayer().setItemInHand(modified);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player && evt.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player player = (Player) evt.getEntity();
            ItemStack itemInHand = player.getItemInHand();
            NBTTagCompound tag = Util.getItemNBT(itemInHand);
            if (!tag.hasKey("lithiumRod")) return;
            evt.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFishEvent(PlayerFishEvent evt) {
        CraftEntity cHook = (CraftEntity) evt.getHook();
        if (!(cHook.getHandle() instanceof EntityLavaHook)) return;
        EntityLavaHook hook = (EntityLavaHook) cHook.getHandle();
        if (evt.getState() == PlayerFishEvent.State.IN_GROUND || evt.getState() == PlayerFishEvent.State.FAILED_ATTEMPT) {
            boolean grapple = hook.isGrapple();
            if (grapple) {
                org.bukkit.block.Block block = cHook.getLocation().getBlock();
                boolean enabled = false;
                for (int z = -1; z <= 1; z++) {
                    for (int x = -1; x <= 1; x++) {
                        for (int y = -1; y <= 1; y++) {
                            Material mat = block.getRelative(x, y, z).getType();
                            if (mat.isSolid()) {
                                enabled = true;
                                break;
                            }
                        }
                    }
                }
                if (!enabled) return;
                Player player = evt.getPlayer();
                Vector v1 = cHook.getLocation().toVector();
                Vector v2 = player.getLocation().toVector();
                Vector subtract = v1.subtract(v2).multiply(0.25f);
                player.setVelocity(subtract);
                Location loc = player.getLocation();
                loc.getWorld().playSound(loc, Sound.MAGMACUBE_JUMP, 10f, 1f);
                ItemStack itemInHand = player.getItemInHand();
                itemInHand = DurabilityUtils.subtractExtendedDurability(itemInHand, 1);
                player.setItemInHand(itemInHand);
            }
        } else if (evt.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            Entity caught = evt.getCaught();
            boolean grapple = hook.isGrapple();
            if (grapple) {
                Player player = evt.getPlayer();
                Vector v1 = player.getLocation().toVector();
                Vector v2 = caught.getLocation().toVector();
                Vector subtract = v1.subtract(v2).multiply(0.25f);
                caught.setVelocity(subtract);
                Location loc = caught.getLocation();
                loc.getWorld().playSound(loc, Sound.MAGMACUBE_JUMP, 10f, 1f);
                if (caught instanceof HumanEntity ||
                        caught instanceof EnderDragon
                        || caught instanceof Wither) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        ItemStack itemInHand = player.getItemInHand();
                        itemInHand = DurabilityUtils.subtractExtendedDurability(itemInHand, 100);
                        player.setItemInHand(itemInHand);
                        player.playSound(player.getLocation(), Sound.FIZZ, 2, 1);
                        player.playSound(player.getLocation(), Sound.ZOMBIE_WOODBREAK, 2, 1);
                        Util.sendActionBar(player, ChatColor.GOLD + "\u26A0 OVERLOAD! \u26A0");
                    });
                }
            }
        } else if (evt.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if (!hook.isGrapple()) {
                org.bukkit.entity.Item caught = (org.bukkit.entity.Item) evt.getCaught();
                ItemStack itemStack = caught.getItemStack();
                itemStack.setAmount(Util.random.nextInt((5 - 2) + 1) + 2);
                if (hook.isInLava()) {
                    if (plugin.getInfernalMobs() != null) {
                        int i = Util.random.nextInt(10);
                        if (i == 9) {
                            FileConfiguration imConfig = plugin.getInfernalMobs().getConfig();
                            int min = imConfig.getInt("minpowers");
                            int max = imConfig.getInt("maxpowers");
                            int diff = Util.random.nextInt(max - min + 1) + min;
                            ItemStack loot = plugin.getInfernalMobs().getRandomLoot(evt.getPlayer(), plugin.getInfernalMobs().getRandomMob(), diff);
                            if (loot != null) {
                                itemStack = loot;
                            }
                            Util.sendActionBar(evt.getPlayer(), ChatColor.GREEN + "You caught some Infernal loot!");
                        }
                    }
                }
                caught.setItemStack(itemStack);
                if (hook.isInLava()) {
                    evt.getPlayer().getWorld().dropItem(evt.getPlayer().getLocation(), caught.getItemStack());
                    caught.remove();
                }
            } else {
                evt.setCancelled(true);
                Util.sendActionBar(evt.getPlayer(), ChatColor.RED + "Fish cannot be caught in grapple mode!");
            }
        }
    }

}
