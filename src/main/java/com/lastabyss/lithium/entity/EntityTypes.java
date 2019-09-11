package com.lastabyss.lithium.entity;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

/**
 * @author Navid
 */
public enum EntityTypes {
    //NAME("Entity name", Entity ID, yourcustomclass.class);
    LITHIUM_MINECART("LithiumMinecart", 42, EntityLithiumMinecart.class);

    private EntityTypes(String name, int id, Class<? extends Entity> custom) {
        addToMaps(custom, name, id);
    }

    public static void spawnEntity(Entity entity, Location loc) {
        entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(entity);
    }

    public static void spawnEntity(Entity entity, World world) {
        world.addEntity(entity);
    }

    private static void addToMaps(Class clazz, String name, int id) {
        ((Map) getPrivateField("c", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(name, clazz);
        ((Map) getPrivateField("d", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(clazz, name);
        if (id != -1)
            ((Map) getPrivateField("f", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(clazz, id);
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;
        try {
            field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);

            o = field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace(System.err);
        }

        return o;
    }
}
