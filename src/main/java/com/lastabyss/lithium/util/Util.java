package com.lastabyss.lithium.util;

import com.lastabyss.lithium.Lithium;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * @author Navid
 */
public class Util {
    private static Lithium plugin;
    public final static Random random = new SecureRandom();
    public final static String PREFIX = ChatColor.translateAlternateColorCodes('&', "&f[&b&l>>&f] ");

    private Util() {
        throw new UnsupportedOperationException("Noice try m8");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        throw new CloneNotSupportedException("Noice try m8");
    }

    public static void initialize(Lithium plugin) {
        Util.plugin = plugin;
    }

    /**
     * Adds entity type to bukkit entity types enum and returns it
     *
     * @param name        - name of the entitytype
     * @param id          - id of the entitytype
     * @param entityClass - entity class
     * @return
     */
    public static EntityType addEntity(String name, int id, Class<? extends Entity> entityClass) {
        EntityType entityType = DynamicEnumType.addEnum(EntityType.class, name, new Class[]{String.class, entityClass.getClass(), Integer.TYPE}, new Object[]{name, entityClass.getClass(), id});
        ReflectionUtils.<Map<String, EntityType>>getFieldValue(EntityType.class, "NAME_MAP", null).put(name, entityType);
        ReflectionUtils.<Map<Short, EntityType>>getFieldValue(EntityType.class, "ID_MAP", null).put((short) id, entityType);
        return entityType;
    }

    /**
     * Adds material to bukkit material enum end returns it
     *
     * @param name - name of the material
     * @param id   - id of the material
     * @return
     */
    public static Material addMaterial(String name, int id) {
        Material material = DynamicEnumType.addEnum(Material.class, name, new Class[]{Integer.TYPE}, new Object[]{id});
        ReflectionUtils.<Map<String, Material>>getFieldValue(Material.class, "BY_NAME", null).put(name, material);
        Material[] byId = ReflectionUtils.getFieldValue(Material.class, "byId", null);
        byId[id] = material;
        ReflectionUtils.setFieldValue(Material.class, "byId", null, byId);
        return material;
    }

    /**
     * Adds material with data to bukkit material enum end returns it
     *
     * @param name
     * @param id
     * @param data
     * @return
     */
    public static Material addMaterial(String name, int id, short data) {
        Material material = DynamicEnumType.addEnum(Material.class, name, new Class[]{Integer.TYPE}, new Object[]{id});
        ReflectionUtils.<Map<String, Material>>getFieldValue(Material.class, "BY_NAME", null).put(name, material);
        Material[] byId = ReflectionUtils.getFieldValue(Material.class, "byId", null);
        byId[id] = material;
        ReflectionUtils.setFieldValue(Material.class, "byId", null, byId);
        Material[] durability = ReflectionUtils.getFieldValue(Material.class, "durability", null);
        durability[data] = material;
        ReflectionUtils.setFieldValue(Material.class, "durability", null, byId);
        return material;
    }

    /**
     * Colors a text.
     * This is just shorthand for ChatColor.translateAlternateColorCodes
     *
     * @param text
     * @return
     */
    public static String colorize(String text) {
        if (text == null) return null;
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void sendActionBar(Player player, String message) {
        CraftPlayer p = (CraftPlayer) player;
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        p.getHandle().playerConnection.sendPacket(ppoc);
    }

    public static void sendTitle(Player player, Integer fadeInTicks, Integer stayTicks, Integer fadeOutTicks, String title, String subtitle) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, fadeInTicks, stayTicks, fadeOutTicks);
        connection.sendPacket(packetPlayOutTimes);

        if (subtitle != null) {
            subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
            IChatBaseComponent titleSub = ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, titleSub);
            connection.sendPacket(packetPlayOutSubTitle);
        }

        if (title != null) {
            title = title.replaceAll("%player%", player.getDisplayName());
            title = ChatColor.translateAlternateColorCodes('&', title);
            IChatBaseComponent titleMain = ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleMain);
            connection.sendPacket(packetPlayOutTitle);
        }
    }

    public static String formatHealth(String hf, double health, double maxHealth) {
        hf = hf.replace("%n", String.valueOf(Math.round(health)));
        hf = hf.replace("%d", String.valueOf(Math.round(maxHealth)));
        return hf;
    }

    public static boolean isStairs(Material mat) {
        return mat == Material.WOOD_STAIRS ||
                mat == Material.COBBLESTONE_STAIRS || mat == Material.NETHER_BRICK_STAIRS ||
                mat == Material.QUARTZ_STAIRS || mat == Material.RED_SANDSTONE_STAIRS ||
                mat == Material.BRICK_STAIRS || mat == Material.SANDSTONE_STAIRS || mat == Material.SMOOTH_STAIRS;
    }

    /**
     * NOT MINE
     * <p>
     * Credit: Rprrr
     *
     * @param player
     */
    public void spawnFirework(Player player) {
        //Spawn the Firework, get the FireworkMeta.
        Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        //Our random generator
        Random r = new Random();

        //Get the type
        int rt = r.nextInt(5) + 1;
        FireworkEffect.Type fType = FireworkEffect.Type.BALL;
        if (rt == 1) fType = FireworkEffect.Type.BALL;
        if (rt == 2) fType = FireworkEffect.Type.BALL_LARGE;
        if (rt == 3) fType = FireworkEffect.Type.BURST;
        if (rt == 4) fType = FireworkEffect.Type.CREEPER;
        if (rt == 5) fType = FireworkEffect.Type.STAR;

        //Get our random colours
        int r1i = r.nextInt(10);
        int r2i = r.nextInt(10);
        Color c1 = getColor(r1i);
        Color c2 = getColor(r2i);

        //Create our effect with this
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(fType).trail(r.nextBoolean()).build();

        //Then apply the effect to the meta
        fwm.addEffect(effect);

        //Generate some random power and set it
        int rp = r.nextInt(2) + 1;
        fwm.setPower(rp);

        //Then apply this to our rocket
        fw.setFireworkMeta(fwm);
    }

    private Color getColor(int i) {
        Color c = null;
        if (i == 0) {
            c = Color.AQUA;
        }
        if (i == 1) {
            c = Color.BLACK;
        }
        if (i == 2) {
            c = Color.BLUE;
        }
        if (i == 3) {
            c = Color.FUCHSIA;
        }
        if (i == 4) {
            c = Color.GRAY;
        }
        if (i == 5) {
            c = Color.GREEN;
        }
        if (i == 6) {
            c = Color.LIME;
        }
        if (i == 7) {
            c = Color.MAROON;
        }
        if (i == 8) {
            c = Color.NAVY;
        }
        if (i == 9) {
            c = Color.OLIVE;
        }
        return c;
    }

    /**
     * Converts minutes and seconds to just seconds.
     *
     * @param minutes
     * @param seconds
     * @return
     */
    public static int convertToSeconds(int minutes, int seconds) {
        return (minutes * 60) + seconds;
    }

    public static ItemStack setItemNBT(ItemStack itemStack, NBTTagCompound tag) {
        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = getNMSCopy(itemStack);
        nmsCopy.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsCopy);
    }

    public static NBTTagCompound getItemNBT(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = getNMSCopy(item);
        if (nmsStack == null) return new NBTTagCompound();
        NBTTagCompound tag = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
        return tag;
    }

    public static void setEntityNBT(Entity entity, NBTTagCompound tag) {
        net.minecraft.server.v1_8_R3.Entity handle = ((CraftEntity)entity).getHandle();
        Method load = ReflectionUtils.getMethod(net.minecraft.server.v1_8_R3.Entity.class, "a", NBTTagCompound.class);
        try {
            load.invoke(handle, tag);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static NBTTagCompound getEntityNBT(Entity entity) {
        net.minecraft.server.v1_8_R3.Entity handle = ((CraftEntity)entity).getHandle();
        NBTTagCompound tag = handle.getNBTTag();
        return tag != null ? tag : new NBTTagCompound();
    }

    public static ItemStack setMaxDurability(ItemStack itemStack, int durability) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        Item item = nmsStack.getItem();
        ReflectionUtils.setFieldValue(Item.class, "durability", item, durability);
        nmsStack.setItem(item);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    public static net.minecraft.server.v1_8_R3.ItemStack getNMSCopy(ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }

    /**
     * Adds the glow.
     *
     * @param item
     *            the item
     * @return the item stack
     */
    public static ItemStack addGlow(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    /**
     * Removes the glow.
     *
     * @param item
     *            the item
     * @return the item stack
     */
    public static ItemStack removeGlow(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) return item;
        tag = nmsStack.getTag();
        tag.set("ench", null);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    /**
     * Seconds must be 0 or greater.
     *
     * @param seconds
     * @return
     */
    public static String formatTime(int seconds) {
        String formatted = "N/A";
        int hours = (int) seconds / 3600,
                remainder = (int) (seconds % 3600),
                minutes = remainder / 60,
                sec = remainder % 60;

        if (hours > 0) {
            formatted = String.valueOf(hours + " hour(s), " + minutes + " min(s), " + sec + " second(s)");
        } else if (minutes > 0) {
            formatted = String.valueOf(minutes + " min(s), " + sec + " second(s)");
        } else if (sec > 0) {
            formatted = String.valueOf(sec + " second(s)");
        } else if (sec == 0) {
            formatted = " 0 second(s)";
        }
        return formatted;
    }

    public static String formatShortenedTime(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        String str = String.format("%02d:%02d", m, s);
        return str;
    }

    public static <T> T getRandomObject(List<? extends T> list) {
        Collections.shuffle((List<? extends T>) list, random);
        return list.get(0);
    }

    /**
     * round n down to nearest multiple of m
     *
     * @param n
     * @param m
     * @return
     */
    public static long roundDown(long n, long m) {
        return n >= 0 ? (n / m) * m : ((n - m + 1) / m) * m;
    }

    /**
     * round n up to nearest multiple of m
     *
     * @param n
     * @param m
     * @return
     */
    public static long roundUp(long n, long m) {
        return n >= 0 ? ((n + m - 1) / m) * m : (n / m) * m;
    }


    public static double scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin, final double limitMax) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
    }

    public static double reverseNumber(double num, double min, double max) {
        return (max + min) - num;
    }

    public static List<Entity> getEntitiesInChunk(Location l) {
        return Arrays.asList(l.getChunk().getEntities());
    }

    public static Set<Entity> getEntitiesInChunks(Location l, int chunkRadius) {
        Block b = l.getBlock();
        Set<Entity> entities = new HashSet<>();
        for (int x = -16 * chunkRadius; x <= 16 * chunkRadius; x += 16) {
            for (int z = -16 * chunkRadius; z <= 16 * chunkRadius; z += 16) {
                for (Entity e : b.getRelative(x, 0, z).getChunk().getEntities()) {
                    entities.add(e);
                }
            }
        }
        return entities;
    }

}
