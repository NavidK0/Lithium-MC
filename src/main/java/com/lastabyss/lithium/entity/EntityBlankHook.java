package com.lastabyss.lithium.entity;

import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.World;

/**
 * @author Navid
 */
public class EntityBlankHook extends EntityFishingHook {
    public EntityBlankHook(World world) {
        super(world);
    }

    public EntityBlankHook(World world, EntityHuman entityhuman) {
        super(world, entityhuman);
    }
}
