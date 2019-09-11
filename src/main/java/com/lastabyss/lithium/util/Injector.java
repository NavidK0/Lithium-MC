package com.lastabyss.lithium.util;

import com.sun.jna.platform.mac.Carbon;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.Enchantment;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.Items;
import net.minecraft.server.v1_8_R3.MinecraftKey;
import net.minecraft.server.v1_8_R3.PotionBrewer;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.WorldGenFactory;

import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Modified version of the Injector class from
 * Carbon. I wrote it originally anyways, so it doesn't matter
 *
 * @author Navid
 */
public class Injector {

    private Carbon plugin;
    public Injector(Carbon plugin) {
        this.plugin = plugin;
    }

    private static boolean injectionFinished;

    public static void registerBlock(int id, String name, Block block) {
        MinecraftKey stringkey = new MinecraftKey(name);
        Block.REGISTRY.a(id, stringkey, block);
        for (IBlockData blockdata : block.P().a()) {
            final int stateId = (id << 4) | block.toLegacyData(blockdata);
            Block.d.a(blockdata, stateId);
        }
    }

    public static void registerBlock(int id, String name, Block block, Item item) {
        MinecraftKey stringkey = new MinecraftKey(name);
        Block.REGISTRY.a(id, stringkey, block);
        for (IBlockData blockdata : block.P().a()) {
            final int stateId = (id << 4) | block.toLegacyData(blockdata);
            Block.d.a(blockdata, stateId);
        }
        Item.REGISTRY.a(id, stringkey, item);
        ReflectionUtils.<Map<Block, Item>>getFieldValue(Item.class, "a", null).put(block, item);
    }

    public static void registerItem(int id, String name, Item item) {
        Item.REGISTRY.a(id, new MinecraftKey(name), item);
    }

    public static void registerTileEntity(Class<? extends TileEntity> entityClass, String name) {
        ReflectionUtils.<Map<String, Class<? extends TileEntity>>>getFieldValue(TileEntity.class, "f", null).put(name, entityClass);
        ReflectionUtils.<Map<Class<? extends TileEntity>, String>>getFieldValue(TileEntity.class, "g", null).put(entityClass, name);
    }

    public static void registerEntity(Class<? extends Entity> entityClass, String name, int id) {
        ReflectionUtils.<Map<String, Class<? extends Entity>>>getFieldValue(EntityTypes.class, "c", null).put(name, entityClass);
        ReflectionUtils.<Map<Class<? extends Entity>, String>>getFieldValue(EntityTypes.class, "d", null).put(entityClass, name);
        ReflectionUtils.<Map<Integer, Class<? extends Entity>>>getFieldValue(EntityTypes.class, "e", null).put(id, entityClass);
        ReflectionUtils.<Map<Class<? extends Entity>, Integer>>getFieldValue(EntityTypes.class, "f", null).put(entityClass, id);
        ReflectionUtils.<Map<String, Integer>>getFieldValue(EntityTypes.class, "g", null).put(name, id);
    }

    public static void registerEntity(Class<? extends Entity> entityClass, String name, int id, int monsterEgg, int monsterEggData) {
        registerEntity(entityClass, name, id);
        EntityTypes.eggInfo.put(id, new EntityTypes.MonsterEggInfo(id, monsterEgg, monsterEggData));
    }

    public static void prepareMobEffectRegistration(int additionalSlots) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        ReflectionUtils.setFieldValue(PotionEffectType.class, "acceptingNew", null, true);
        PotionEffectType[] oldById = ReflectionUtils.getFieldValue(PotionEffectType.class, "byId", null);
        PotionEffectType[] newById = new PotionEffectType[oldById.length + 2];
        System.arraycopy(oldById, 0, newById, 0, oldById.length);
        ReflectionUtils.setStaticFinalField(PotionEffectType.class, "byId", newById);
    }

    public static void registerPotionEffect(int effectId, String durations, String amplifier) {
        ReflectionUtils.<Map<Integer, String>>getFieldValue(PotionBrewer.class, "effectDurations", null).put(effectId, durations);
        ReflectionUtils.<Map<Integer, String>>getFieldValue(PotionBrewer.class, "effectAmplifiers", null).put(effectId, amplifier);
    }

    public static void registerEnchantment(Enchantment enhcantment) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        ReflectionUtils.setAccessible(org.bukkit.enchantments.Enchantment.class.getDeclaredField("acceptingNew")).set(null, true);
        ArrayList<Enchantment> enchants = new ArrayList<>(Arrays.asList(Enchantment.b));
        enchants.add(enhcantment);
        ReflectionUtils.setFinalField(Enchantment.class.getField("b"), null, enchants.toArray(new Enchantment[enchants.size()]));
        ReflectionUtils.setAccessible(org.bukkit.enchantments.Enchantment.class.getDeclaredField("acceptingNew")).set(null, false);
    }

    public static void registerWorldGenFactoryAddition(boolean isStructureStart, Class<?> clazz, String string) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        ReflectionUtils.setAccessible(WorldGenFactory.class.getDeclaredMethod(isStructureStart ? "b" : "a", Class.class, String.class)).invoke(null, clazz, string);
    }

    /**
     * If any old ids have been replaced with new ids, this will overwrite
     * all old ids with the new ones.
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static void fixBlocksRefs() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        for (Field field : Blocks.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (Block.class.isAssignableFrom(field.getType())) {
                Block block = (Block) field.get(null);
                Block newblock = Block.getById(Block.getId(block));
                if (block != newblock) {
                    ReflectionUtils.setFinalField(field, null, newblock);
                }
            }
        }
    }

    /**
     * If any old ids have been replaced with new ids, this will overwrite
     * all old ids with the new ones.
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static void fixItemsRefs() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        for (Field field : Items.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (Item.class.isAssignableFrom(field.getType())) {
                Item block = (Item) field.get(null);
                Item newblock = Item.getById(Item.getId(block));
                if (block != newblock) {
                    ReflectionUtils.setFinalField(field, null, newblock);
                }
            }
        }
    }

}