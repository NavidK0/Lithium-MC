package com.lastabyss.lithium.listener;

import com.lastabyss.lithium.Lithium;
import com.lastabyss.lithium.data.ItemRegistry;
import com.lastabyss.lithium.effect.RadioactiveDamageEffect;
import com.lastabyss.lithium.util.DurabilityUtils;
import com.lastabyss.lithium.util.Util;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.DynamicLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Navid
 */
public class BaseLithiumListener implements Listener {

    protected Lithium plugin;
    private EffectManager em;
    private BukkitTask inventoryTask;
    private int poisonDistSq = 100;

    public BaseLithiumListener(Lithium plugin) {
        this.plugin = plugin;
        this.em = new EffectManager(plugin);
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().registerEvents(this, plugin));
        inventoryTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            final int[] count = new int[1];
            players.stream()
                    .filter(p -> (count[0] = isRadioactive(p)) > 0)
                    .forEach(p -> {
                        List<Entity> entities = Util.getEntitiesInChunk(p.getLocation());
                        List<Entity> collect = entities.stream()
                                .filter(e -> e instanceof Monster)
                                .filter(e -> p.getLocation().distanceSquared(e.getLocation()) <= poisonDistSq)
                                .collect(Collectors.toList());
                        collect.stream().forEach(e -> {
                            Monster living = (Monster) e;
                            double dmg = .5 * count[0];
                            if (living.getHealth() - dmg > 0) {
                                living.damage(dmg, p);
                                RadioactiveDamageEffect effect = new RadioactiveDamageEffect(em);
                                effect.color = Material.EMERALD_BLOCK.getId();
                                effect.type = EffectType.INSTANT;
                                effect.duration = 1;
                                effect.heightMod = 0.5f;
                                effect.setDynamicOrigin(new DynamicLocation(living));
                                effect.setDynamicTarget(new DynamicLocation(living));
                                effect.start();
                            }
                        });
                        if (hasRadioactiveSet(p)) {
                            if (p.hasPotionEffect(PotionEffectType.POISON)) {
                                p.removePotionEffect(PotionEffectType.POISON);
                            }
                        }
                    });
            players.stream()
                    .filter(p -> isAntebellum(p) > 0)
                    .forEach(p -> {
                        if (hasAntebellumSet(p)) {
                            p.setSaturation(20);
                            p.setFoodLevel(20);
                            p.setRemainingAir(p.getMaximumAir());
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1, true, false), true);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 1, true, false), false);
                        }
                    });
        }, 0, 20);
    }

    public void end() {
        inventoryTask.cancel();
    }

    @EventHandler(ignoreCancelled = true)
    public void onFall(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player && evt.getCause() == DamageCause.FALL) {
            Player player = (Player) evt.getEntity();
            if (hasAntebellumSet(player)) {
                evt.setCancelled(true);
            }
        }
    }

    private boolean hasRadioactiveSet(Player p) {
        boolean has = true;
        for (ItemStack item : p.getInventory().getArmorContents()) {
            if (item.getType() != ItemRegistry.ARMY_HELMET_ITEM.getType() &&
                    item.getType() != ItemRegistry.SOLDIER_VESTMENTS_ITEM.getType() &&
                    item.getType() != ItemRegistry.CARGO_PANTS_ITEM.getType() &&
                    item.getType() != ItemRegistry.MILITARY_BOOTS_ITEM.getType()) {
                if (ItemRegistry.isRadioactive(item))
                    has = false;
            }
        }
        return has;
    }

    private boolean hasAntebellumSet(Player p) {
        boolean has = true;
        for (ItemStack item : p.getInventory().getArmorContents()) {
            if (!ItemRegistry.isMayorAntebellumGear(item)) {
                has = false;
            }
        }
        return has;
    }

    private int isRadioactive(Player p) {
        int count = 0;
        for (ItemStack content : p.getInventory().getContents()) {
            if (ItemRegistry.isRadioactive(content)) count++;
        }
        for (ItemStack content : p.getInventory().getArmorContents()) {
            if (ItemRegistry.isRadioactive(content)) count++;
        }
        return count;
    }

    private int isAntebellum(Player p) {
        int count = 0;
        for (ItemStack content : p.getInventory().getArmorContents()) {
            if (ItemRegistry.isMayorAntebellumGear(content)) count++;
        }
        return count;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDurabilityLost(ItemLosesDurabilityEvent evt) {
        EntityEquipment inventory = evt.getEntity().getEquipment();
        if (ItemRegistry.hasCustomAbilities(evt.getItemStack())) return;
        if (inventory != null) {
            ItemStack itemStack = evt.getItemStack();
            if (DurabilityUtils.hasExtendedDurability(itemStack)) {
                CraftItemStack item = DurabilityUtils.subtractExtendedDurability(itemStack, 1);
                setEntityEquipment(inventory, item, evt.getSlot());
            } else if (DurabilityUtils.isChargeable(itemStack)) {
                CraftItemStack item = DurabilityUtils.subtractCharge(itemStack, 1);
                setEntityEquipment(inventory, item, evt.getSlot());
            }
        }
    }

    private void setEntityEquipment(EntityEquipment inventory, ItemStack item, int slot) {
        if (slot == -1) {
            inventory.setItemInHand(item);
        } else {
            ItemStack[] armorContents = inventory.getArmorContents();
            armorContents[slot] = item;
            inventory.setArmorContents(armorContents);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent evt) {
        Collection<ItemStack> drops = evt.getBlock().getDrops();
        if (evt.getBlock().getType() == Material.DIAMOND_ORE) {
            int i = Util.random.nextInt(500);
            if (i == 0) {
                ItemStack clone = ItemRegistry.LITHIUM_DUST_ITEM.clone();
                clone.setAmount(1);
                drops.add(clone);
            }
        }
        if (evt.getBlock().getType() == Material.EMERALD_ORE) {
            int i = Util.random.nextInt(100);
            if (i == 0) {
                ItemStack clone = ItemRegistry.LITHIUM_DUST_ITEM.clone();
                clone.setAmount(1);
                drops.add(clone);
            }
        }
    }
}
