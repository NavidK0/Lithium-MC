package com.lastabyss.lithium.entity;

import com.lastabyss.lithium.util.ReflectionUtils;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EnchantmentManager;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.Items;
import net.minecraft.server.v1_8_R3.Material;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.Vec3D;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.util.Vector;

import java.util.List;

public class EntityLavaHook extends EntityFishingHook {

    public boolean grapple;
    private boolean inLava;

    public EntityLavaHook(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.ah = true;
    }

    public EntityLavaHook(World world, EntityHuman entityhuman) {
        super(world, entityhuman);
        this.ah = true;
        this.owner = entityhuman;
        this.owner.hookedFish = this;
        this.setSize(0.25F, 0.25F);
        this.setPositionRotation(entityhuman.locX, entityhuman.locY + (double)entityhuman.getHeadHeight(), entityhuman.locZ, entityhuman.yaw, entityhuman.pitch);
        this.locX -= (double)(MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.locY -= 0.10000000149011612D;
        this.locZ -= (double)(MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.setPosition(this.locX, this.locY, this.locZ);
        float f = 0.4F;
        this.motX = (double)(-MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
        this.motZ = (double)(MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
        this.motY = (double)(-MathHelper.sin(this.pitch / 180.0F * 3.1415927F) * f);
        this.c(this.motX, this.motY, this.motZ, 1.5F, 1.0F);
    }

    public EntityLavaHook(World world, EntityHuman entityhuman, Vector velocity) {
        super(world, entityhuman);
        this.ah = true;
        this.owner = entityhuman;
        this.owner.hookedFish = this;
        this.setSize(0.25F, 0.25F);
        this.setPositionRotation(entityhuman.locX, entityhuman.locY + (double) entityhuman.getHeadHeight(), entityhuman.locZ, entityhuman.yaw, entityhuman.pitch);
        this.locX -= (double)(MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.locY -= 0.10000000149011612D;
        this.locZ -= (double)(MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.setPosition(this.locX, this.locY, this.locZ);
        float f = 0.4f;
        this.motX = (double)(-MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
        this.motZ = (double)(MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
        this.motY = (double)(-MathHelper.sin(this.pitch / 180.0F * 3.1415927F) * f);
        if (velocity != null) {
            setGrapple(true);
            f = 1F;
            this.motX = (double)(-MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
            this.motZ = (double)(MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
            this.motY = (double)(-MathHelper.sin(this.pitch / 180.0F * 3.1415927F) * f);
        }
        this.c(this.motX, this.motY, this.motZ, 1.5f, 1.0F);
    }

    public void setGrapple(boolean grapple) {
        this.grapple = grapple;
    }

    public boolean isGrapple() {
        return grapple;
    }

    public boolean isInLava() {
        return inLava;
    }

    public void setInLava(boolean inLava) {
        this.inLava = inLava;
    }

    @Override
    public void t_() {
        super.K();
        fireTicks = 0;
        maxFireTicks = 0;
        final Block ar = ReflectionUtils.getFieldValue(EntityFishingHook.class, "ar", this);
        final int g = ReflectionUtils.getFieldValue(EntityFishingHook.class, "g", this);
        final int h = ReflectionUtils.getFieldValue(EntityFishingHook.class, "h", this);
        final int i = ReflectionUtils.getFieldValue(EntityFishingHook.class, "i", this);
        boolean as = ReflectionUtils.getFieldValue(EntityFishingHook.class, "as", this);
        int at = ReflectionUtils.getFieldValue(EntityFishingHook.class, "at", this);
        int au = ReflectionUtils.getFieldValue(EntityFishingHook.class, "au", this);
        int av = ReflectionUtils.getFieldValue(EntityFishingHook.class, "av", this);
        int aw = ReflectionUtils.getFieldValue(EntityFishingHook.class, "aw", this);
        int ax = ReflectionUtils.getFieldValue(EntityFishingHook.class, "ax", this);
        float ay = ReflectionUtils.getFieldValue(EntityFishingHook.class, "ay", this);
        int az = ReflectionUtils.getFieldValue(EntityFishingHook.class, "az", this);
        final double aA = ReflectionUtils.getFieldValue(EntityFishingHook.class, "aA", this);
        final double aB = ReflectionUtils.getFieldValue(EntityFishingHook.class, "aB", this);
        final double aC = ReflectionUtils.getFieldValue(EntityFishingHook.class, "aC", this);
        final double aD = ReflectionUtils.getFieldValue(EntityFishingHook.class, "aD", this);
        final double aE = ReflectionUtils.getFieldValue(EntityFishingHook.class, "aE", this);

        if (az > 0) {
            double d0 = this.locX + (aA - this.locX) / (double) az;
            double d1 = this.locY + (aB - this.locY) / (double) az;
            double d2 = this.locZ + (aC - this.locZ) / (double) az;
            double d3 = MathHelper.g(aD - (double) this.yaw);
            this.yaw = (float) ((double) this.yaw + d3 / (double) az);
            this.pitch = (float) ((double) this.pitch + (aE - (double) this.pitch) / (double) az);
            --az;
            this.setPosition(d0, d1, d2);
            this.setYawPitch(this.yaw, this.pitch);
        } else {
            ItemStack item = this.owner.bZ();
            if (this.owner.dead || !this.owner.isAlive() || item == null || item.getItem() != Items.IRON_HOE || this.h(this.owner) > 3072.0D) {
                this.die();
                this.owner.hookedFish = null;
                return;
            }

            if (this.hooked != null) {
                if (!this.hooked.dead) {
                    this.locX = this.hooked.locX;
                    double d4 = (double) this.hooked.length;
                    this.locY = this.hooked.getBoundingBox().b + d4 * 0.8D;
                    this.locZ = this.hooked.locZ;
                    return;
                }

                this.hooked = null;
            }

            if (this.a > 0) {
                --this.a;
            }

            if (as) {
                if (this.world.getType(new BlockPosition(g, h, i)).getBlock() == ar) {
                    ++at;
                    if (at == 1200) {
                        this.die();
                    }

                    return;
                }

                as = false;
                this.motX *= (double) (this.random.nextFloat() * 0.2F);
                this.motY *= (double) (this.random.nextFloat() * 0.2F);
                this.motZ *= (double) (this.random.nextFloat() * 0.2F);
                at = 0;
                au = 0;
            } else {
                ++au;
            }

            Vec3D var47 = new Vec3D(this.locX, this.locY, this.locZ);
            Vec3D vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
            MovingObjectPosition movingobjectposition = this.world.rayTrace(var47, vec3d1);
            var47 = new Vec3D(this.locX, this.locY, this.locZ);
            vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
            if (movingobjectposition != null) {
                vec3d1 = new Vec3D(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
            }

            Entity entity = null;
            List list = this.world.getEntities(this, this.getBoundingBox().a(this.motX, this.motY, this.motZ).grow(1.0D, 1.0D, 1.0D));
            double d5 = 0.0D;

            double d6;
            for (int f1 = 0; f1 < list.size(); ++f1) {
                Entity f2 = (Entity) list.get(f1);
                if (f2.ad() && (f2 != this.owner || au >= 5)) {
                    float b0 = 0.3F;
                    AxisAlignedBB axisalignedbb = f2.getBoundingBox().grow((double) b0, (double) b0, (double) b0);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb.a(var47, vec3d1);
                    if (movingobjectposition1 != null) {
                        d6 = var47.distanceSquared(movingobjectposition1.pos);
                        if (d6 < d5 || d5 == 0.0D) {
                            entity = f2;
                            d5 = d6;
                        }
                    }
                }
            }

            if (entity != null) {
                movingobjectposition = new MovingObjectPosition(entity);
            }

            if (movingobjectposition != null) {
                CraftEventFactory.callProjectileHitEvent(this);
                if (movingobjectposition.entity != null) {
                    if (movingobjectposition.entity.damageEntity(DamageSource.projectile(this, this.owner), 0.0F)) {
                        this.hooked = movingobjectposition.entity;
                    }
                } else {
                    as = true;
                }
            }

            if (!as) {
                this.move(this.motX, this.motY, this.motZ);
                float var48 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                this.yaw = (float) (MathHelper.b(this.motX, this.motZ) * 180.0D / 3.1415927410125732D);

                for (this.pitch = (float) (MathHelper.b(this.motY, (double) var48) * 180.0D / 3.1415927410125732D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
                }

                while (this.pitch - this.lastPitch >= 180.0F) {
                    this.lastPitch += 360.0F;
                }

                while (this.yaw - this.lastYaw < -180.0F) {
                    this.lastYaw -= 360.0F;
                }

                while (this.yaw - this.lastYaw >= 180.0F) {
                    this.lastYaw += 360.0F;
                }

                this.pitch = this.lastPitch + (this.pitch - this.lastPitch) * 0.2F;
                this.yaw = this.lastYaw + (this.yaw - this.lastYaw) * 0.2F;
                float var49 = 0.92F;
                if (this.onGround || this.positionChanged) {
                    var49 = 0.5F;
                }

                byte var50 = 5;
                double d7 = 0.0D;

                double d8;
                for (int worldserver = 0; worldserver < var50; ++worldserver) {
                    AxisAlignedBB k = this.getBoundingBox();
                    double d9 = k.e - k.b;
                    double d10 = k.b + d9 * (double) worldserver / (double) var50;
                    d8 = k.b + d9 * (double) (worldserver + 1) / (double) var50;
                    AxisAlignedBB axisalignedbb2 = new AxisAlignedBB(k.a, d10, k.c, k.d, d8, k.f);
                    if (this.world.b(axisalignedbb2, Material.WATER)) {
                        d7 += 1.0D / (double) var50;
                    }
                    if (this.world.b(axisalignedbb2, Material.LAVA)) {
                        d7 += 1.0D / (double) var50;
                    }
                }

                if (d7 > 0.0D) {
                    WorldServer var51 = (WorldServer) this.world;
                    int var52 = 1;
                    BlockPosition blockposition = (new BlockPosition(this)).up();
                    if (this.random.nextFloat() < 0.25F && this.world.isRainingAt(blockposition)) {
                        var52 = 2;
                    }

                    if (this.random.nextFloat() < 0.5F && !this.world.i(blockposition)) {
                        --var52;
                    }

                    if (av > 0) {
                        --av;
                        if (av <= 0) {
                            aw = 0;
                            ax = 0;
                        }
                    } else {
                        float f3;
                        float f5;
                        float f4;
                        double d12;
                        double d11;
                        Block block;
                        if (!grapple) {
                            if (ax > 0) {
                                ax -= var52;
                                if (ax <= 0) {
                                    this.motY -= 0.20000000298023224D;
                                    this.makeSound("random.splash", 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                                    f3 = (float) MathHelper.floor(this.getBoundingBox().b);
                                    var51.a(EnumParticle.WATER_BUBBLE, this.locX, (double) (f3 + 1.0F), this.locZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D);
                                    var51.a(EnumParticle.WATER_WAKE, this.locX, (double) (f3 + 1.0F), this.locZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D);
                                    av = MathHelper.nextInt(this.random, 10, 30);
                                } else {
                                    ay = (float) ((double) ay + this.random.nextGaussian() * 4.0D);
                                    f3 = ay * 0.017453292F;
                                    f5 = MathHelper.sin(f3);
                                    f4 = MathHelper.cos(f3);
                                    d8 = this.locX + (double) (f5 * (float) ax * 0.1F);
                                    d12 = (double) ((float) MathHelper.floor(this.getBoundingBox().b) + 1.0F);
                                    d11 = this.locZ + (double) (f4 * (float) ax * 0.1F);
                                    block = var51.getType(new BlockPosition((int) d8, (int) d12 - 1, (int) d11)).getBlock();
                                    if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                                        if (this.random.nextFloat() < 0.15F) {
                                            var51.a(EnumParticle.WATER_BUBBLE, d8, d12 - 0.10000000149011612D, d11, 1, (double) f5, 0.1D, (double) f4, 0.0D);
                                        }
                                        float f6 = f5 * 0.04F;
                                        float f7 = f4 * 0.04F;
                                        var51.a(EnumParticle.WATER_WAKE, d8, d12, d11, 0, (double) f7, 0.01D, (double) (-f6), 1.0D);
                                        var51.a(EnumParticle.WATER_WAKE, d8, d12, d11, 0, (double) (-f7), 0.01D, (double) f6, 1.0D);
                                        inLava = false;
                                    }

                                    if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
                                        if (this.random.nextFloat() < 0.15F) {
                                            var51.a(EnumParticle.WATER_BUBBLE, d8, d12 - 0.10000000149011612D, d11, 1, (double) f5, 0.1D, (double) f4, 0.0D);
                                        }
                                        float f6 = f5 * 0.04F;
                                        float f7 = f4 * 0.04F;
                                        var51.a(EnumParticle.WATER_WAKE, d8, d12, d11, 0, (double) f7, 0.01D, (double) (-f6), 1.0D);
                                        var51.a(EnumParticle.WATER_WAKE, d8, d12, d11, 0, (double) (-f7), 0.01D, (double) f6, 1.0D);
                                        inLava = true;
                                    }
                                }
                            } else if (aw > 0) {
                                aw -= var52;
                                f3 = 0.15F;
                                if (aw < 20) {
                                    f3 = (float) ((double) f3 + (double) (20 - aw) * 0.05D);
                                } else if (aw < 40) {
                                    f3 = (float) ((double) f3 + (double) (40 - aw) * 0.02D);
                                } else if (aw < 60) {
                                    f3 = (float) ((double) f3 + (double) (60 - aw) * 0.01D);
                                }

                                if (this.random.nextFloat() < f3) {
                                    f5 = MathHelper.a(this.random, 0.0F, 360.0F) * 0.017453292F;
                                    f4 = MathHelper.a(this.random, 25.0F, 60.0F);
                                    d8 = this.locX + (double) (MathHelper.sin(f5) * f4 * 0.1F);
                                    d12 = (double) ((float) MathHelper.floor(this.getBoundingBox().b) + 1.0F);
                                    d11 = this.locZ + (double) (MathHelper.cos(f5) * f4 * 0.1F);
                                    block = var51.getType(new BlockPosition((int) d8, (int) d12 - 1, (int) d11)).getBlock();
                                    if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                                        var51.a(EnumParticle.WATER_SPLASH, d8, d12, d11, 2 + this.random.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
                                        inLava = false;
                                    }
                                    if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
                                        var51.a(EnumParticle.WATER_SPLASH, d8, d12, d11, 2 + this.random.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
                                        inLava = true;
                                    }
                                }

                                if (aw <= 0) {
                                    ay = MathHelper.a(this.random, 0.0F, 360.0F);
                                    ax = MathHelper.nextInt(this.random, 20, 80);
                                }
                            } else {
                                aw = MathHelper.nextInt(this.random, 100, 900);
                                aw -= EnchantmentManager.h(this.owner) * 20 * 5;
                            }
                        }
                    }

                    if (av > 0) {
                        this.motY -= (double) (this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat()) * 0.2D;
                    }
                }

                d6 = d7 * 2.0D - 1.0D;
                this.motY += 0.03999999910593033D * d6;
                if (d7 > 0.0D) {
                    var49 = (float) ((double) var49 * 0.9D);
                    this.motY *= 0.8D;
                }

                this.motX *= (double) var49;
                this.motY *= (double) var49;
                this.motZ *= (double) var49;
                this.setPosition(this.locX, this.locY, this.locZ);
            }
        }

        ReflectionUtils.setFieldValue(EntityFishingHook.class, "as", this, as);
        ReflectionUtils.setFieldValue(EntityFishingHook.class, "at", this, at);
        ReflectionUtils.setFieldValue(EntityFishingHook.class, "au", this, au);
        ReflectionUtils.setFieldValue(EntityFishingHook.class, "av", this, av);
        ReflectionUtils.setFieldValue(EntityFishingHook.class, "aw", this, aw);
        ReflectionUtils.setFieldValue(EntityFishingHook.class, "ax", this, ax);
        ReflectionUtils.setFieldValue(EntityFishingHook.class, "ay", this, ay);
        ReflectionUtils.setFieldValue(EntityFishingHook.class, "az", this, az);
    }

    @Override
    public CraftEntity getBukkitEntity() {
        CraftEntity bukkitEntity = super.getBukkitEntity();
        bukkitEntity.setCustomName("LavaHook");
        return bukkitEntity;
    }
}
