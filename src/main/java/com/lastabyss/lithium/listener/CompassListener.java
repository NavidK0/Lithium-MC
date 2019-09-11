package com.lastabyss.lithium.listener;

import com.lastabyss.lithium.Lithium;
import com.lastabyss.lithium.data.ItemRegistry;
import com.lastabyss.lithium.util.ReflectionUtils;
import com.lastabyss.lithium.util.Util;
import io.hotmail.com.jacob_vejvoda.infernal_mobs.infernal_mobs;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

/**
 * Compass item for tracking Internal Mobs.
 *
 * @author Navid
 */
public class CompassListener extends BaseLithiumListener {

    private infernal_mobs infernalMobs;
    private float spawnMessageRadius;
    private Map<Player, Pair<BukkitTask, Mob>> trackers = new HashMap<>();

    public CompassListener(Lithium plugin) {
        super(plugin);
        infernalMobs = plugin.getInfernalMobs();
        if (infernalMobs == null) return;
        spawnMessageRadius = infernalMobs.getConfig().getLong("spawnMessageRadius");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogout(PlayerQuitEvent evt) {
        if (isTracking(evt.getPlayer())) {
            stopTracking(evt.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRecipeEvent(PrepareItemCraftEvent evt) {
        if (!ItemRegistry.hasCustomAbilities(evt.getInventory().getResult())) return;
        if (evt.getInventory().getResult().getType() != Material.COMPASS) return;
        ItemStack[] matrix = evt.getInventory().getMatrix();
        if (matrix[1] == null || !matrix[1].isSimilar(ItemRegistry.INFERNAL_NETHERSTAR_ITEM)) {
            evt.getInventory().setResult(null);
        } else if (matrix[3] == null || !matrix[3].isSimilar(ItemRegistry.INFERNAL_NETHERSTAR_ITEM)) {
            evt.getInventory().setResult(null);
        } else if (matrix[5] == null || !matrix[5].isSimilar(ItemRegistry.INFERNAL_NETHERSTAR_ITEM)) {
            evt.getInventory().setResult(null);
        } else if (matrix[7] == null || !matrix[7].isSimilar(ItemRegistry.INFERNAL_NETHERSTAR_ITEM)) {
            evt.getInventory().setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEvent(PlayerInteractEvent evt) {
        ItemStack item = evt.getItem();
        Player player = evt.getPlayer();
        if (ItemRegistry.hasCustomAbilities(evt.getItem())) {
            if (evt.getItem().getType() == Material.COMPASS) {
                if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    for (ItemStack i : player.getInventory().getContents()) {
                        if (i == null) continue;
                        if (i.getType() == Material.COMPASS && !i.getItemMeta().hasDisplayName() ||
                                i.getType() == Material.COMPASS && !i.getItemMeta().getDisplayName().equals(ItemRegistry.LITHIUM_COMPASS_NAME)) {
                            player.sendMessage(ChatColor.RED + "A conflicting force stops the " + ItemRegistry.LITHIUM_COMPASS_NAME + ChatColor.RED + " from working... perhaps you have another compass in your inventory?");
                            return;
                        }
                    }
                    Mob mob = getClosestInfernal(player.getLocation());
                    if (mob == null) {
                        Util.sendActionBar(player, ChatColor.RED + "The Lithium Compass remains lifeless in your hand.");
                        return;
                    }
                    if (isTracking(player)) {
                        stopTracking(player);
                    } else
                        trackInfernal(player, mob);
                }
            }
        }
    }

    public void trackInfernal(Player player, Mob mob) {
        if (!isTracking(player)) {
            Util.sendActionBar(player, ChatColor.RED + "The Lithium Compass has found an Infernal!");
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (!mob.getEntity().isValid()) {
                    stopTracking(player);
                    return;
                }
                if (!mob.getEntity().getWorld().getName().equals(player.getWorld().getName())) {
                    stopTracking(player);
                    return;
                }
                boolean hasCompass = false;
                for (ItemStack i : player.getInventory().getContents()) {
                    if (i == null) continue;
                    if (i.getType() == Material.COMPASS && !i.getItemMeta().hasDisplayName() ||
                            i.getType() == Material.COMPASS && !i.getItemMeta().getDisplayName().equals(ItemRegistry.LITHIUM_COMPASS_NAME)) {
                        Util.sendActionBar(player, ChatColor.RED + "A conflicting force stops the " + ItemRegistry.LITHIUM_COMPASS_NAME + ChatColor.RED + " from working... perhaps you have another compass in your inventory?");
                        stopTracking(player);
                        return;
                    }
                    if (i.getType() == Material.COMPASS && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equals(ItemRegistry.LITHIUM_COMPASS_NAME)) {
                        hasCompass = true;
                        break;
                    }
                }
                if (!hasCompass) {
                    stopTracking(player);
                    return;
                }
                if (!(player.getItemInHand().getType() == Material.COMPASS
                        && (player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().getDisplayName().equals(ItemRegistry.LITHIUM_COMPASS_NAME)))) {
                    stopTracking(player);
                    return;
                }
                float dist = (float) mob.getEntity().getLocation().distance(player.getLocation());
                String number = formatDecimal(dist);
                Util.sendActionBar(player, ChatColor.AQUA + "Infernal Distance: " + ChatColor.RED + number + ChatColor.WHITE + " meters");
                player.setCompassTarget(mob.getEntity().getLocation());
                float pitch;
                if (dist >= spawnMessageRadius) {
                    dist = spawnMessageRadius;
                }
                pitch = (float) Util.scale(dist, 0, spawnMessageRadius, 0, 2);
                pitch = (float) Util.reverseNumber(pitch, 0, 2);
                player.getWorld().playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, pitch);
            }, 20L, 10L);
            trackers.put(player, new ImmutablePair<>(task, mob));
        }
    }

    public void stopTracking(Player player) {
        if (isTracking(player)) {
            Pair<BukkitTask, Mob> get = trackers.get(player);
            Util.sendActionBar(player, ChatColor.RED + "The Lithium Compass is silent.");
            get.getLeft().cancel();
            player.setCompassTarget(player.getWorld().getSpawnLocation());
            trackers.remove(player);
        }
    }

    public boolean isTracking(Player p) {
        return trackers.containsKey(p);
    }

    public String formatDecimal(float deci) {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(deci);
    }

    public Mob getClosestInfernal(Location loc) {
        List<Mob> infernals = getInfernals();
        Mob closest = null;
        double distSq = 0;
        if (infernals.isEmpty()) {
            return null;
        }
        int maxSq = 3000 * 3000;
        for (Mob m : infernals) {
            Entity entity = m.getEntity();
            if (!m.getWorld().getName().equals(loc.getWorld().getName())) continue;
            if (!entity.isValid()) continue;
            if (loc.distanceSquared(entity.getLocation()) > maxSq) continue;
            if (closest == null) {
                closest = m;
                distSq = loc.distanceSquared(entity.getLocation());
            } else {
                double newDistSq = loc.distanceSquared(entity.getLocation());
                if (newDistSq < distSq) {
                    closest = m;
                    distSq = newDistSq;
                }
            }
        }
        return closest;
    }

    public List<Mob> getInfernals() {
        try {
            Field infernalListField = infernal_mobs.class.getDeclaredField("infernalList");
            infernalListField.setAccessible(true);
            List<Object> objList;
            List<Mob> mobList = new ArrayList<>();
            try {
                objList = (List<Object>) infernalListField.get(infernalMobs);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck, it didn't fucking work, I blame the fucking InfernalMobs dev"
                        + ", or should I fucking say infernal_mobs, as he PUT IN UNDERSCORE CLASSES FUCKING FUCKER");
                Logger.getLogger(CompassListener.class.getName()).log(Level.SEVERE, null, ex);
                return new ArrayList<>();
            }
            objList.forEach(o -> mobList.add(new Mob(o)));
            return mobList;
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(CompassListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    /**
     * A lot of work to bypass a protected internal mob class.
     */
    public class Mob {
        public Object stupidInternalClass;
        public Class clazz;

        public Mob(Object stupidInternalClass) {
            try {
                this.stupidInternalClass = stupidInternalClass;
                this.clazz = Class.forName("io.hotmail.com.jacob_vejvoda.infernal_mobs.Mob");
            } catch (ClassNotFoundException ex) {
                //Doesn't exist, rip
            }
        }

        public void setStupidInternalClass(Object stupidInternalClass) {
            this.stupidInternalClass = stupidInternalClass;
        }

        public Object getStupidInternalClass() {
            return stupidInternalClass;
        }

        public ArrayList<String> getAbilityList() {
            try {
                Field field = ReflectionUtils.getField(clazz, "abilityList");
                return (ArrayList<String>) field.get(stupidInternalClass);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck");
                return null;
            }
        }

        public String getEffect() {
            try {
                Field field = ReflectionUtils.getField(clazz, "effect");
                return (String) field.get(stupidInternalClass);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck");
                return null;
            }
        }

        public Entity getEntity() {
            try {
                Field field = ReflectionUtils.getField(clazz, "entity");
                return (Entity) field.get(stupidInternalClass);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck");
                return null;
            }
        }

        public UUID getId() {
            try {
                Field field = ReflectionUtils.getField(clazz, "id");
                return (UUID) field.get(stupidInternalClass);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck");
                return null;
            }
        }

        public int getLives() {
            try {
                Field field = ReflectionUtils.getField(clazz, "lives");
                return (int) field.get(stupidInternalClass);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck");
                return 0;
            }
        }

        public World getWorld() {
            try {
                Field field = ReflectionUtils.getField(clazz, "world");
                return (World) field.get(stupidInternalClass);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck");
                return null;
            }
        }

        public void setWorld(World world) {
            try {
                Field field = ReflectionUtils.getField(clazz, "world");
                field.set(stupidInternalClass, world);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck");
            }
        }

        public void setLives(int lives) {
            try {
                Field field = ReflectionUtils.getField(clazz, "lives");
                field.set(stupidInternalClass, lives);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck");
            }
        }

        public void setInfernal(boolean infernal) {
            try {
                Field field = ReflectionUtils.getField(clazz, "infernal");
                field.set(stupidInternalClass, infernal);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck");
            }
        }

        public void setId(UUID id) {
            try {
                Field field = ReflectionUtils.getField(clazz, "id");
                field.set(stupidInternalClass, id);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck");
            }
        }

        public void setEntity(Entity entity) {
            try {
                Field field = ReflectionUtils.getField(clazz, "entity");
                field.set(stupidInternalClass, entity);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck");
            }
        }

        public void setEffect(String effect) {
            try {
                Field field = ReflectionUtils.getField(clazz, "effect");
                field.set(stupidInternalClass, effect);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck");
            }
        }

        public void setAbilityList(ArrayList<String> abilityList) {
            try {
                Field field = ReflectionUtils.getField(clazz, "abilityList");
                field.set(stupidInternalClass, abilityList);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                plugin.getLogger().warning("Fuck");
            }
        }
    }
}
