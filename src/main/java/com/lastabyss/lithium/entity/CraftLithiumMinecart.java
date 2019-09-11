package com.lastabyss.lithium.entity;

import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftMinecart;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;

/**
 *
 * @author Navid
 */
public class CraftLithiumMinecart extends CraftMinecart implements Minecart {

    public CraftLithiumMinecart(CraftServer server, EntityLithiumMinecart entity) {
        super(server, entity);
    }

    @Override
    public EntityType getType() {
        return EntityType.UNKNOWN;
    }

    @Override
    public EntityLithiumMinecart getHandle() {
        return (EntityLithiumMinecart) super.getHandle();
    }


    @Deprecated
    public void _INVALID_setDamage(int damage) {}

    @Deprecated
    public int _INVALID_getDamage() {
        return 0;
    }
}
