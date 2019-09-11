package com.lastabyss.lithium.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R3.EntityBat;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;


/**
 * Credits to [USER=90856537]Desle[/USER] for the original resource. (Uses entities, not packets)
 *
 * @author Goblom
 * @author Jordan
 */
public class Rope {

    private static final List<Rope> ropes = new ArrayList<Rope>();

    private Location locEnd;
    private EntityBat entBatEnd;
    private Entity holder;

    public Rope(Location end, Entity holder) {
        this.locEnd = end;
        this.holder = holder;
        new ArrayList<>(ropes)
                .stream()
                .filter(r -> r.holder != null && r.holder.equals(holder))
                .forEach(r -> {
                    r.holder = null;
                    r.despawn();
                });
        ropes.add(this);

        spawn();
    }

    public void setEnd(Location end) {
        this.locEnd = end;
        spawn();
    }

    public Location getEnd() {
        return locEnd;
    }

    private void makeEnt() {
        WorldServer world = ((CraftWorld) locEnd.getWorld()).getHandle();

        if (entBatEnd == null) {
            this.entBatEnd = new EntityBat(world);
            entBatEnd.setInvisible(true);
        }

        this.entBatEnd.setLocation(locEnd.getX(), locEnd.getY(), locEnd.getZ(), 0, 0);

        entBatEnd.setLeashHolder(((CraftEntity) holder).getHandle(), true);
    }

    public void spawn() {
        if (holder == null) {
            despawn();
            return;
        }
        makeEnt();
        PacketPlayOutSpawnEntityLiving bat_end = new PacketPlayOutSpawnEntityLiving(entBatEnd);
        PacketPlayOutAttachEntity attach = new PacketPlayOutAttachEntity(1, entBatEnd, ((CraftPlayer) holder).getHandle());

        for (Player player : Bukkit.getOnlinePlayers()) {
            CraftPlayer cP = (CraftPlayer) player;
            cP.getHandle().playerConnection.sendPacket(bat_end);
            cP.getHandle().playerConnection.sendPacket(attach);
        }
    }

    public void despawn() {
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entBatEnd.getId());

        for (Player player : Bukkit.getOnlinePlayers()) {
            CraftPlayer cP = (CraftPlayer) player;
            cP.getHandle().playerConnection.sendPacket(destroy);
        }
        ropes.remove(this);
    }

    public void glueEndTo(Entity att) {
        PacketPlayOutAttachEntity attach = new PacketPlayOutAttachEntity(0, entBatEnd, ((CraftEntity) att).getHandle());
        for (Player player : Bukkit.getOnlinePlayers()) {
            CraftPlayer cP = (CraftPlayer) player;
            cP.getHandle().playerConnection.sendPacket(attach);
        }
    }
}