package com.lastabyss.lithium.listener;

import com.lastabyss.lithium.Lithium;
import com.lastabyss.lithium.data.ItemRegistry;
import com.lastabyss.lithium.entity.CraftLithiumMinecart;
import com.lastabyss.lithium.entity.EntityLithiumMinecart;
import com.lastabyss.lithium.entity.EntityTypes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lastabyss.lithium.util.ReflectionUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftMinecartChest;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftMinecartCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 * @author Navid
 */
public final class MinecartListener extends BaseLithiumListener {

    Lithium plugin;
    Map<Entity, float[]> passengerSizes = new HashMap<>();

    public MinecartListener(Lithium plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEvent(PlayerInteractEvent evt) {
        if (evt.isCancelled()) return;
        if (evt.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (evt.getItem() == null) return;
        if (!isRail(evt.getClickedBlock())) return;
        if (ItemRegistry.hasCustomAbilities(evt.getItem())) {
            evt.setCancelled(true);
            if (evt.getPlayer().getGameMode() != GameMode.CREATIVE) {
                ItemStack item = evt.getPlayer().getItemInHand();
                item.setAmount(item.getAmount() - 1);
                evt.getPlayer().setItemInHand(item);
            }
            Block b = evt.getClickedBlock();
            EntityLithiumMinecart minecart = new EntityLithiumMinecart(((CraftWorld) b.getWorld()).getHandle());
            EntityTypes.spawnEntity(minecart, b.getLocation().add(0.5, 0, 0.5));
        }
    }

    public boolean isRail(Block b) {
        return b.getType().toString().contains("RAIL");
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e) {
        if (isClimbableMinecart(e.getVehicle())) {
            Minecart cart = (Minecart) e.getVehicle();
            Block from = e.getFrom().getBlock();
            Block to = e.getTo().getBlock();
            Object nmsMinecart = ReflectionUtils.getHandle(cart);
            if (from.getType() == Material.LADDER || to.getType() == Material.LADDER) {
                if (!cart.hasMetadata("on_ladder")) {
                    Location loc = cart.getLocation();
                    loc.setX(to.getX() + .5);
                    loc.setZ(to.getZ() + .5);
                    cart.setVelocity(new Vector());
                    cart.teleport(loc);
                    if (cart.getPassenger() != null) {
                        try {
                            float passengerWidth = ReflectionUtils.getField(Entity, "width").getFloat(ReflectionUtils.getHandle(cart.getPassenger()));
                            float passengerLength = ReflectionUtils.getField(Entity, "length").getFloat(ReflectionUtils.getHandle(cart.getPassenger()));

                            if (passengerWidth != 0 && passengerLength != 0) {
                                this.passengerSizes.put(cart.getPassenger(), new float[]{passengerWidth, passengerLength});
                            }
                            this.setEntitySize(ReflectionUtils.getHandle(cart.getPassenger()), 0.1f, 0.1f);
                        } catch (IllegalArgumentException | IllegalAccessException ex) {
                            Logger.getLogger(MinecartListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    cart.setMetadata("on_ladder", new FixedMetadataValue(plugin, System.currentTimeMillis()));
                }
                float yaw = 0;
                float pitch = 0;
                switch (from.getData()) {
                    case 2:
                        yaw = 270;
                        pitch = -90;
                        break;
                    case 3:
                        yaw = 270;
                        pitch = 90f;
                        break;
                    case 4:
                        yaw = 0;
                        pitch = 90f;
                        break;
                    case 5:
                        yaw = 0;
                        pitch = -90f;
                        break;
                    default:
                        break;
                }
                this.setEntityPosition(nmsMinecart, cart.getLocation().getX(), cart.getLocation().getY(), cart.getLocation().getZ(), yaw, pitch);
                this.setEntitySize(nmsMinecart, 0.1f, 0.1f);
                cart.setVelocity(cart.getVelocity().add(new Vector(0, 0.1, 0)));
                cart.teleport(cart.getLocation().add(0, 0.1, 0));
                if (to.getType() != Material.LADDER) {
                    Vector vel = cart.getVelocity();
                    vel.setY(0.25);
                    switch (from.getData()) {
                        case 2:
                            cart.setVelocity(vel.add(new Vector(0, 0, 0.1)));
                            break;
                        case 3:
                            cart.setVelocity(vel.add(new Vector(0, 0, -0.1)));
                            break;
                        case 4:
                            cart.setVelocity(vel.add(new Vector(0.1, 0, 0)));
                            break;
                        case 5:
                            cart.setVelocity(vel.add(new Vector(-0.1, 0, 0)));
                            break;
                        default:
                            break;
                    }
                }
            } else {
                if (cart.hasMetadata("on_ladder")) {
                    long since = cart.getMetadata("on_ladder").get(0).asLong();
                    if (System.currentTimeMillis() - since > 500) {
                        cart.removeMetadata("on_ladder", plugin);
                        this.setEntitySize(nmsMinecart, 0.98f, 0.7f);
                        if (cart.getPassenger() != null) {
                            this.onExit(new VehicleExitEvent(null, (LivingEntity) cart.getPassenger()));
                        }
                    }
                }
            }
        }
    }

    public boolean isClimbableMinecart(Entity m) {
        if (!(m instanceof Minecart)) return false;
        return m instanceof CraftLithiumMinecart || m instanceof CraftMinecartChest || m instanceof CraftMinecartCommand;
    }

    @EventHandler
    public void onExit(VehicleExitEvent e) {
        if (this.passengerSizes.containsKey(e.getExited())) {
            float[] value = this.passengerSizes.get(e.getExited());
            this.setEntitySize(ReflectionUtils.getHandle(e.getExited()), value[0], value[1]);
            this.passengerSizes.remove(e.getExited());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity().getVehicle() != null) {
            if (e.getEntity().getVehicle().hasMetadata("on_ladder")) {
                e.setCancelled(true);
            }
        }
    }

    void setEntitySize(Object entity, float f, float f1) {
        try {
            setSize.invoke(entity, f, f1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setEntityPosition(Object entity, double x, double y, double z, float yaw, float pitch) {
        try {
            setPositionRotation.invoke(entity, x, y, z, yaw, pitch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Class<?> Entity;
    static Class<?> EntityMinecartAbstract;

    static Method setSize;
    static Method setPositionRotation;

    static {
        Entity = ReflectionUtils.getNMSClass("Entity");
        EntityMinecartAbstract = ReflectionUtils.getNMSClass("EntityMinecartAbstract");

        try {
            setSize = Entity.getDeclaredMethod("setSize", float.class, float.class);
            setPositionRotation = Entity.getDeclaredMethod("setPositionRotation", double.class, double.class, double.class, float.class, float.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
