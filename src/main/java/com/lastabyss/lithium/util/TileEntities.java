package com.lastabyss.lithium.util;

import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R3.BlockJukeBox;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.TileEntityBeacon;
import net.minecraft.server.v1_8_R3.TileEntityBrewingStand;
import net.minecraft.server.v1_8_R3.TileEntityChest;
import net.minecraft.server.v1_8_R3.TileEntityCommand;
import net.minecraft.server.v1_8_R3.TileEntityDispenser;
import net.minecraft.server.v1_8_R3.TileEntityDropper;
import net.minecraft.server.v1_8_R3.TileEntityFurnace;
import net.minecraft.server.v1_8_R3.TileEntityHopper;
import net.minecraft.server.v1_8_R3.TileEntityMobSpawner;
import net.minecraft.server.v1_8_R3.TileEntityNote;
import net.minecraft.server.v1_8_R3.TileEntitySign;
import net.minecraft.server.v1_8_R3.TileEntitySkull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBeacon;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBrewingStand;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftChest;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftCommandBlock;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftDispenser;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftDropper;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftFurnace;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftHopper;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftJukebox;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftNoteBlock;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSign;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSkull;

public class TileEntities {

    public static TileEntity getGeneralTileEntity(Location loc) {
        BlockState state = loc.getBlock().getState();

        try {
            if (state instanceof Beacon) return getBeaconTileEntity(loc);
            else if (state instanceof BrewingStand) return getBrewingStandTileEntity(loc);
            else if (state instanceof Chest) return getChestTileEntity(loc);
            else if (state instanceof CommandBlock) return getCommandBlockTileEntity(loc);
            else if (state instanceof CreatureSpawner) return getCreatureSpawnerTileEntity(loc);
            else if (state instanceof Dispenser) return getDispenserTileEntity(loc);
            else if (state instanceof Dropper) return getDropperTileEntity(loc);
            else if (state instanceof Furnace) return getFurnaceTileEntity(loc);
            else if (state instanceof Hopper) return getHopperTileEntity(loc);
            else if (state instanceof Jukebox) return getJukeBoxTileEntity(loc);
            else if (state instanceof NoteBlock) return getNoteBlockTileEntity(loc);
            else if (state instanceof Sign) return getSignTileEntity(loc);
            else if (state instanceof Skull) return getSkullTileEntity(loc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TileEntityBeacon getBeaconTileEntity(Location loc) throws NotCorrectTileEntityException {
        Block b = loc.getBlock();
        if (b.getType() != Material.BEACON) {
            throw new NotCorrectTileEntityException("Beacon", loc);
        }

        CraftBeacon beacon = (CraftBeacon) b.getState();

        try {
            Field beaconField = beacon.getClass().getDeclaredField("beacon");
            beaconField.setAccessible(true);
            return (TileEntityBeacon) beaconField.get(beacon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TileEntityBrewingStand getBrewingStandTileEntity(Location loc) throws NotCorrectTileEntityException {
        Block b = loc.getBlock();
        if (b.getType() != Material.BREWING_STAND) {
            throw new NotCorrectTileEntityException("Brewing Stand", loc);
        }

        CraftBrewingStand brewingStand = (CraftBrewingStand) b.getState();

        try {
            Field brewingStandField = brewingStand.getClass().getDeclaredField("brewingStand");
            brewingStandField.setAccessible(true);
            return (TileEntityBrewingStand) brewingStandField.get(brewingStand);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TileEntityChest getChestTileEntity(Location loc) throws NotCorrectTileEntityException {
        Block b = loc.getBlock();
        if (b.getType() != Material.CHEST && b.getType() != Material.TRAPPED_CHEST) {
            throw new NotCorrectTileEntityException("Chest", loc);
        }

        CraftChest chest = (CraftChest) b.getState();

        try {
            Field chestField = chest.getClass().getDeclaredField("chest");
            chestField.setAccessible(true);
            return (TileEntityChest) chestField.get(chest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TileEntityCommand getCommandBlockTileEntity(Location loc) throws NotCorrectTileEntityException {
        Block b = loc.getBlock();
        if (b.getType() != Material.COMMAND) {
            throw new NotCorrectTileEntityException("Command Block", loc);
        }

        CraftCommandBlock command = (CraftCommandBlock) b.getState();

        try {
            Field commandField = command.getClass().getDeclaredField("commandBlock");
            commandField.setAccessible(true);
            return (TileEntityCommand) commandField.get(command);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TileEntityMobSpawner getCreatureSpawnerTileEntity(Location loc) throws NotCorrectTileEntityException {
        Block b = loc.getBlock();
        if (b.getType() != Material.MOB_SPAWNER) {
            throw new NotCorrectTileEntityException("Creature Spawner", loc);
        }

        CraftCreatureSpawner creatureSpawner = (CraftCreatureSpawner) b.getState();

        try {
            Field creatureSpawnerField = creatureSpawner.getClass().getDeclaredField("spawner");
            creatureSpawnerField.setAccessible(true);
            return (TileEntityMobSpawner) creatureSpawnerField.get(creatureSpawner);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TileEntityDispenser getDispenserTileEntity(Location loc) throws NotCorrectTileEntityException {
        Block b = loc.getBlock();
        if (b.getType() != Material.DISPENSER) {
            throw new NotCorrectTileEntityException("Dispenser", loc);
        }

        CraftDispenser dispenser = (CraftDispenser) b.getState();

        try {
            Field dispenserField = dispenser.getClass().getDeclaredField("dispenser");
            dispenserField.setAccessible(true);
            return (TileEntityDispenser) dispenserField.get(dispenser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TileEntityDropper getDropperTileEntity(Location loc) throws NotCorrectTileEntityException {
        Block b = loc.getBlock();
        if (b.getType() != Material.DROPPER) {
            throw new NotCorrectTileEntityException("Dropper", loc);
        }

        CraftDropper dropper = (CraftDropper) b.getState();

        try {
            Field dropperField = dropper.getClass().getDeclaredField("dropper");
            dropperField.setAccessible(true);
            return (TileEntityDropper) dropperField.get(dropper);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TileEntityFurnace getFurnaceTileEntity(Location loc) throws NotCorrectTileEntityException {
        Block b = loc.getBlock();
        if (b.getType() != Material.FURNACE) {
            throw new NotCorrectTileEntityException("Furnace", loc);
        }

        CraftFurnace furnace = (CraftFurnace) b.getState();

        try {
            Field furnaceField = furnace.getClass().getDeclaredField("furnace");
            furnaceField.setAccessible(true);
            return (TileEntityFurnace) furnaceField.get(furnace);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TileEntityHopper getHopperTileEntity(Location loc) throws NotCorrectTileEntityException {
        Block b = loc.getBlock();
        if (b.getType() != Material.HOPPER) {
            throw new NotCorrectTileEntityException("Hopper", loc);
        }

        CraftHopper hopper = (CraftHopper) b.getState();

        try {
            Field hopperField = hopper.getClass().getDeclaredField("hopper");
            hopperField.setAccessible(true);
            return (TileEntityHopper) hopperField.get(hopper);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static BlockJukeBox.TileEntityRecordPlayer getJukeBoxTileEntity(Location loc) throws NotCorrectTileEntityException {
        Block b = loc.getBlock();
        if (b.getType() != Material.JUKEBOX) {
            throw new NotCorrectTileEntityException("Jukebox", loc);
        }

        CraftJukebox jukeBox = (CraftJukebox) b.getState();

        try {
            Field jukeBoxField = jukeBox.getClass().getDeclaredField("jukebox");
            jukeBoxField.setAccessible(true);
            return (BlockJukeBox.TileEntityRecordPlayer) jukeBoxField.get(jukeBox);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TileEntityNote getNoteBlockTileEntity(Location loc) throws NotCorrectTileEntityException {
        Block b = loc.getBlock();
        if (b.getType() != Material.NOTE_BLOCK) {
            throw new NotCorrectTileEntityException("NoteBlock", loc);
        }

        CraftNoteBlock noteBlock = (CraftNoteBlock) b.getState();

        try {
            Field noteBlockField = noteBlock.getClass().getDeclaredField("note");
            noteBlockField.setAccessible(true);
            return (TileEntityNote) noteBlockField.get(noteBlock);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TileEntitySign getSignTileEntity(Location loc) throws NotCorrectTileEntityException {
        Block b = loc.getBlock();
        if (b.getType() != Material.WALL_SIGN && b.getType() != Material.SIGN_POST) {
            throw new NotCorrectTileEntityException("Sign", loc);
        }

        CraftSign sign = (CraftSign) b.getState();

        try {
            Field signField = sign.getClass().getDeclaredField("sign");
            signField.setAccessible(true);
            return (TileEntitySign) signField.get(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TileEntitySkull getSkullTileEntity(Location loc) throws NotCorrectTileEntityException {
        Block b = loc.getBlock();
        if (b.getType() != Material.SKULL) {
            throw new NotCorrectTileEntityException("Skull", loc);
        }

        CraftSkull skull = (CraftSkull) b.getState();

        try {
            Field skullField = skull.getClass().getDeclaredField("skull");
            skullField.setAccessible(true);
            return (TileEntitySkull) skullField.get(skull);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}