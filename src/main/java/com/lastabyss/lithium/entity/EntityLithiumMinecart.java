package com.lastabyss.lithium.entity;

import com.lastabyss.lithium.data.ItemRegistry;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.BlockMinecartTrackAbstract;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.BlockPoweredRail;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityMinecartAbstract;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.Vec3D;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;

/**
 *
 * @author Navid
 */
public class EntityLithiumMinecart extends EntityMinecartAbstract {
    
    public final static double MAX_SPEED = 0.8d;
    private boolean a;

    public EntityLithiumMinecart(World world) {
        super(world);
        maxSpeed = 1d;
    }
    
    @Override
    public void t_() {
        // CraftBukkit start
        double prevX = this.locX;
        double prevY = this.locY;
        double prevZ = this.locZ;
        float prevYaw = this.yaw;
        float prevPitch = this.pitch;
        // CraftBukkit end
        
        if (this.getType() > 0) {
            this.j(this.getType() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        if (this.locY < -64.0D) {
            this.O();
        }

        int i;

        if (!this.world.isClientSide && this.world instanceof WorldServer) {
            this.world.methodProfiler.a("portal");
            i = this.L();
            if (this.ak) {
                    if (this.vehicle == null && this.al++ >= i) {
                        this.al = i;
                        this.portalCooldown = this.aq();
                        byte b0;

                        if (this.world.worldProvider.getDimension() == -1) {
                            b0 = 0;
                        } else {
                            b0 = -1;
                        }

                        this.c(b0);
                    }

                    this.ak = false;
            } else {
                if (this.al > 0) {
                    this.al -= 4;
                }

                if (this.al < 0) {
                    this.al = 0;
                }
            }

            if (this.portalCooldown > 0) {
                --this.portalCooldown;
            }

            this.world.methodProfiler.b();
        }
            this.lastX = this.locX;
            this.lastY = this.locY;
            this.lastZ = this.locZ;
            this.motY -= 0.03999999910593033D;
            int x = MathHelper.floor(this.locX);

            i = MathHelper.floor(this.locY);
            int z = MathHelper.floor(this.locZ);

            if (BlockMinecartTrackAbstract.e(this.world, new BlockPosition(x, i - 1, z))) {
                --i;
            }

            BlockPosition blockposition = new BlockPosition(x, i, z);
            IBlockData iblockdata = this.world.getType(blockposition);

            if (BlockMinecartTrackAbstract.d(iblockdata)) {
                this.a(blockposition, iblockdata);
                if (iblockdata.getBlock() == Blocks.ACTIVATOR_RAIL) {
                    this.a(x, i, z, (iblockdata.get(BlockPoweredRail.POWERED)));
                }
            } else {
                this.n();
            }

            this.checkBlockCollisions();
            this.pitch = 0.0F;
            double d4 = this.lastX - this.locX;
            double d5 = this.lastZ - this.locZ;

            if (d4 * d4 + d5 * d5 > 0.001D) {
                this.yaw = (float) (MathHelper.b(d5, d4) * 180.0D / 3.141592653589793D);
                if (this.a) {
                    this.yaw += 180.0F;
                }
            }

            double d6 = (double) MathHelper.g(this.yaw - this.lastYaw);

            if (d6 < -170.0D || d6 >= 170.0D) {
                this.yaw += 180.0F;
                this.a = !this.a;
            }

            this.setYawPitch(this.yaw, this.pitch);

            // CraftBukkit start
            org.bukkit.World bworld = this.world.getWorld();
            Location from = new Location(bworld, prevX, prevY, prevZ, prevYaw, prevPitch);
            Location to = new Location(bworld, this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            Vehicle v = (Vehicle) this.getBukkitEntity();
            this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleUpdateEvent(v));
            if (!from.equals(to)) {
                this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleMoveEvent(v, from, to));
            }
        // CraftBukkit end
            this.world.getEntities(this, this.getBoundingBox().grow(0.20000000298023224D, 0.0D, 0.20000000298023224D))
                    .stream()
                    .filter((entity) -> (entity != this.passenger && entity.ae() && entity instanceof EntityMinecartAbstract))
                    .forEach((entity) -> {
                        entity.collide(this);
                });

            if (this.passenger != null && this.passenger.dead) {
                if (this.passenger.vehicle == this) {
                    this.passenger.vehicle = null;
                }

                this.passenger = null;
            }
            this.W();
    }
    
    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.fallDistance = 0.0F;
        Vec3D vec3d = this.k(this.locX, this.locY, this.locZ);

        this.locY = (double) blockposition.getY();
        boolean isPoweredRail = false;
        boolean flag = false;
        BlockMinecartTrackAbstract track = (BlockMinecartTrackAbstract) iblockdata.getBlock();

        if (track == Blocks.GOLDEN_RAIL) {
            isPoweredRail = (iblockdata.get(BlockPoweredRail.POWERED));
            flag = !isPoweredRail;
        }

        double modifer = 0.0078125d;
        BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(track.n());

        switch (TrackPositions.trackPositions[blockminecarttrackabstract_enumtrackposition.ordinal()]) {
        case 1:
            this.motX -= modifer;
            ++this.locY;
            break;

        case 2:
            this.motX += modifer;
            ++this.locY;
            break;

        case 3:
            this.motZ += modifer;
            ++this.locY;
            break;

        case 4:
            this.motZ -= modifer;
            ++this.locY;
        }

        Field f = null;
        try {
            f = EntityMinecartAbstract.class.getDeclaredField("matrix");
            f.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(EntityLithiumMinecart.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        int[][][] matrix;
        try {
            matrix = (int[][][]) f.get(null);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(EntityLithiumMinecart.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        int[][] aint = matrix[blockminecarttrackabstract_enumtrackposition.a()];
        double d1 = (double) (aint[1][0] - aint[0][0]);
        double d2 = (double) (aint[1][2] - aint[0][2]);
        double d3 = Math.sqrt(d1 * d1 + d2 * d2);
        double d4 = this.motX * d1 + this.motZ * d2;

        if (d4 < 0.0D) {
            d1 = -d1;
            d2 = -d2;
        }

        double d5 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);

        if (d5 > 2.0D) {
            d5 = 2.0D;
        }

        this.motX = d5 * d1 / d3;
        this.motZ = d5 * d2 / d3;
        double d6;
        double d7;
        double d8;
        double d9;

        if (this.passenger instanceof EntityLiving) {
            d6 = (double) ((EntityLiving) this.passenger).ba;
            if (d6 > 0.0D) {
                d7 = -Math.sin((double) (this.passenger.yaw * 3.1415927F / 180.0F));
                d8 = Math.cos((double) (this.passenger.yaw * 3.1415927F / 180.0F));
                d9 = this.motX * this.motX + this.motZ * this.motZ;
                if (d9 < 0.01D) {
                    this.motX += d7 * 0.1D;
                    this.motZ += d8 * 0.1D;
                    flag = false;
                }
            }
        }

        if (flag) {
            d6 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
            if (d6 < 0.03D) {
                this.motX *= 0.0D;
                this.motY *= 0.0D;
                this.motZ *= 0.0D;
            } else {
                this.motX *= 0.5D;
                this.motY *= 0.0D;
                this.motZ *= 0.5D;
            }
        }

        d7 = (double) blockposition.getX() + 0.5D + (double) aint[0][0] * 0.5D;
        d8 = (double) blockposition.getZ() + 0.5D + (double) aint[0][2] * 0.5D;
        d9 = (double) blockposition.getX() + 0.5D + (double) aint[1][0] * 0.5D;
        double d10 = (double) blockposition.getZ() + 0.5D + (double) aint[1][2] * 0.5D;

        d1 = d9 - d7;
        d2 = d10 - d8;
        double d11;
        double d12;

        if (d1 == 0.0D) {
            this.locX = (double) blockposition.getX() + 0.5D;
            d6 = this.locZ - (double) blockposition.getZ();
        } else if (d2 == 0.0D) {
            this.locZ = (double) blockposition.getZ() + 0.5D;
            d6 = this.locX - (double) blockposition.getX();
        } else {
            d11 = this.locX - d7;
            d12 = this.locZ - d8;
            d6 = (d11 * d1 + d12 * d2) * 2.0D;
        }

        this.locX = d7 + d1 * d6;
        this.locZ = d8 + d2 * d6;
        this.setPosition(this.locX, this.locY, this.locZ);
        d11 = this.motX;
        d12 = this.motZ;
        if (this.passenger != null) {
            d11 *= 0.75D;
            d12 *= 0.75D;
        }

        double mSpeed = this.m();

        d11 = MathHelper.a(d11, -mSpeed, mSpeed);
        d12 = MathHelper.a(d12, -mSpeed, mSpeed);
        this.move(d11, 0.0D, d12);
        if (aint[0][1] != 0 && MathHelper.floor(this.locX) - blockposition.getX() == aint[0][0] && MathHelper.floor(this.locZ) - blockposition.getZ() == aint[0][2]) {
            this.setPosition(this.locX, this.locY + (double) aint[0][1], this.locZ);
        } else if (aint[1][1] != 0 && MathHelper.floor(this.locX) - blockposition.getX() == aint[1][0] && MathHelper.floor(this.locZ) - blockposition.getZ() == aint[1][2]) {
            this.setPosition(this.locX, this.locY + (double) aint[1][1], this.locZ);
        }

        this.o();
        Vec3D vec3d1 = this.k(this.locX, this.locY, this.locZ);

        if (vec3d1 != null && vec3d != null) {
            double d14 = (vec3d.b - vec3d1.b) * 0.05D;

            d5 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
            if (d5 > 0.0D) {
                this.motX = this.motX / d5 * (d5 + d14);
                this.motZ = this.motZ / d5 * (d5 + d14);
            }

            this.setPosition(this.locX, vec3d1.b, this.locZ);
        }

        int x = MathHelper.floor(this.locX);
        int z = MathHelper.floor(this.locZ);

        if (x != blockposition.getX() || z != blockposition.getZ()) {
            d5 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
            this.motX = d5 * (double) (x - blockposition.getX());
            this.motZ = d5 * (double) (z - blockposition.getZ());
        }

        if (isPoweredRail) {
            double d15 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
            if (d15 > 0.01D) {
                double d16 = 0.06D;
                this.motX += this.motX / d15 * d16;
                this.motZ += this.motZ / d15 * d16;
            } else if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST) {
                if (this.world.getType(blockposition.west()).getBlock().isOccluding()) {
                    this.motX = 0.02D;
                } else if (this.world.getType(blockposition.east()).getBlock().isOccluding()) {
                    this.motX = -0.02D;
                }
            } else if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH) {
                if (this.world.getType(blockposition.north()).getBlock().isOccluding()) {
                    this.motZ = 0.02D;
                } else if (this.world.getType(blockposition.south()).getBlock().isOccluding()) {
                    this.motZ = -0.02D;
                }
            }
        }

    }
    
    
    
    @Override
    protected void n() {
        double d0 = this.m();
        if (this.motX < -d0) {
            this.motX = -d0;
        }
        if (this.motX > d0) {
            this.motX = d0;
        }
        if (this.motZ < -d0) {
            this.motZ = -d0;
        }
        if (this.motZ > d0) {
            this.motZ = d0;
        }
        Vector derailed = getDerailedVelocityMod();
        Vector flying = getFlyingVelocityMod();
        if (this.onGround) {
            this.motX *= derailed.getX();
            this.motY *= derailed.getY();
            this.motZ *= derailed.getZ();
        }
        this.move(this.motX, this.motY, this.motZ);
        if (!this.onGround) {
            this.motX *= flying.getX();
            this.motY *= flying.getY();
            this.motZ *= flying.getZ();
        }

    }
    
    @Override
    public void a(DamageSource damagesource) {
        this.die();
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            ItemStack itemstack = CraftItemStack.asNMSCopy(ItemRegistry.LITHIUM_MINECART_ITEM);
            this.a(itemstack, 0.0F);
        }

    }

    @Override
    public EnumMinecartType s() {
        return EnumMinecartType.RIDEABLE;
    }
    
    static class TrackPositions {
        static final int[] trackPositions = new int[BlockMinecartTrackAbstract.EnumTrackPosition.values().length];
        static {
            trackPositions[BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST.ordinal()] = 1;
            trackPositions[BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST.ordinal()] = 2;
            trackPositions[BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH.ordinal()] = 3;
            trackPositions[BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH.ordinal()] = 4;
        }
    }
    
    @Override
    protected void o() {
        if (this.passenger != null || !this.slowWhenEmpty) {
            this.motX *= 0.996999979019165D;
            this.motY *= 0.0D;
            this.motZ *= 0.996999979019165D;
        } else {
            this.motX *= 0.9599999785423279D;
            this.motY *= 0.0D;
            this.motZ *= 0.9599999785423279D;
        }

    }

    @Override
    public String getName() {
        return "Lithium Minecart";
    }

    @Override
    public CraftLithiumMinecart getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = new CraftLithiumMinecart(MinecraftServer.getServer().server, this);
            bukkitEntity.setCustomName(ChatColor.AQUA + "Lithium Minecart");
            bukkitEntity.setCustomNameVisible(true);
        }
        return (CraftLithiumMinecart) bukkitEntity;
    }
    
    public boolean e(EntityHuman entityhuman) {
        if (this.passenger != null && this.passenger instanceof EntityHuman && this.passenger != entityhuman) {
            return true;
        } else if (this.passenger != null && this.passenger != entityhuman) {
            return false;
        } else {
            if (!this.world.isClientSide) {
                entityhuman.mount(this);
            }

            return true;
        }
    }

    public void a(int i, int j, int k, boolean flag) {
        if (flag) {
            if (this.passenger != null) {
                this.passenger.mount((Entity) null);
            }

            if (this.getType() == 0) {
                this.k(-this.r());
                this.j(10);
                this.setDamage(50.0F);
                this.ac();
            }
        }

    }

}
