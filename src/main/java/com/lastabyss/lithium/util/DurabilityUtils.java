package com.lastabyss.lithium.util;

import com.lastabyss.lithium.data.ItemRegistry;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Charge - An item can never break, only run out of charge
 * Extended Durability - Items that CAN break, but have extended durability
 *
 * @author Navid
 */
public class DurabilityUtils {

    //Extended Durability

    public static boolean hasExtendedDurability(ItemStack item) {
        if (item == null) return false;
        return Util.getItemNBT(item).hasKey("extendedDurability");
    }

    public static boolean hasExtendedDurability(net.minecraft.server.v1_8_R3.ItemStack item) {
        if (item == null) return false;
        return item.getTag().hasKey("extendedDurability");
    }

    public static net.minecraft.server.v1_8_R3.ItemStack subtractExtendedDurability(net.minecraft.server.v1_8_R3.ItemStack item, int x) {
        if (!hasExtendedDurability(item)) return item;
        if (ItemRegistry.checkDurabilityEnchant(CraftItemStack.asBukkitCopy(item)))
            return updateExtendedDurabilityLore(item);
        NBTTagCompound nbt = item.getTag();
        int eD = nbt.getInt("extendedDurability");
        eD -= x;
        nbt.setInt("extendedDurability", eD);
        if (eD <= 0) {
            return null;
        }
        return updateExtendedDurabilityLore(item);
    }

    public static net.minecraft.server.v1_8_R3.ItemStack addExtendedDurability(net.minecraft.server.v1_8_R3.ItemStack item, int x) {
        if (!hasExtendedDurability(item)) return item;
        NBTTagCompound nbt = item.getTag();
        int eD = nbt.getInt("extendedDurability");
        int max = nbt.getInt("maxDurability");
        eD += x;
        if (eD > max) eD = max;
        nbt.setInt("extendedDurability", eD);
        return updateExtendedDurabilityLore(item);
    }

    public static int getExtendedDurability(net.minecraft.server.v1_8_R3.ItemStack item) {
        if (!hasExtendedDurability(item)) return 0;
        NBTTagCompound nbt = item.getTag();
        int eD = nbt.getInt("extendedDurability");
        return eD;
    }

    public static CraftItemStack subtractExtendedDurability(ItemStack item, int x) {
        if (!hasExtendedDurability(item)) return (CraftItemStack) item;
        if (ItemRegistry.checkDurabilityEnchant(item)) return (CraftItemStack) updateExtendedDurabilityLore(item);
        NBTTagCompound nbt = Util.getItemNBT(item);
        int eD = nbt.getInt("extendedDurability");
        eD -= x;
        nbt.setInt("extendedDurability", eD);
        if (eD <= 0) {
            return null;
        }
        return (CraftItemStack) updateExtendedDurabilityLore(Util.setItemNBT(item, nbt));
    }

    public static CraftItemStack addExtendedDurability(ItemStack item, int x) {
        if (!hasExtendedDurability(item)) return (CraftItemStack) item;
        NBTTagCompound nbt = Util.getItemNBT(item);
        int eD = nbt.getInt("extendedDurability");
        int max = nbt.getInt("maxExtendedDurability");
        eD += x;
        if (eD > max) eD = max;
        nbt.setInt("extendedDurability", eD);
        return (CraftItemStack) updateExtendedDurabilityLore(Util.setItemNBT(item, nbt));
    }

    public static int getExtendedDurability(ItemStack item) {
        if (!hasExtendedDurability(item)) return 0;
        NBTTagCompound nbt = Util.getItemNBT(item);
        int eD = nbt.getInt("extendedDurability");
        return eD;
    }

    public static boolean hasFullExtendedDurability(ItemStack item) {
        if (!hasExtendedDurability(item)) return false;
        NBTTagCompound nbt = Util.getItemNBT(item);
        int eD = nbt.getInt("extendedDurability");
        int max = nbt.getInt("maxExtendedDurability");
        return eD >= max;
    }

    public static boolean hasFullExtendedDurability(net.minecraft.server.v1_8_R3.ItemStack item) {
        if (!hasExtendedDurability(item)) return false;
        NBTTagCompound nbt = Util.getItemNBT(CraftItemStack.asBukkitCopy(item));
        int eD = nbt.getInt("extendedDurability");
        int max = nbt.getInt("maxExtendedDurability");
        return eD >= max;
    }

    public static ItemStack updateExtendedDurabilityLore(ItemStack item) {
        if (!hasExtendedDurability(item)) return item;
        NBTTagCompound nbt = Util.getItemNBT(item);
        int eD = nbt.getInt("extendedDurability");
        int max = nbt.getInt("maxExtendedDurability");
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        String s = lore.get(lore.size() - 1);
        if (s.contains("Durability")) {
            s = ChatColor.GREEN + "Durability: " + eD + "/" + max;
            lore.set(lore.size() - 1, s);
        } else {
            String c = ChatColor.GREEN + "Durability: " + eD + "/" + max;
            lore.add(c);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        short maxDura = item.getType().getMaxDurability();
        double cPercent = (double) eD / (double) max;
        double finalDurability = Math.round(cPercent * maxDura);
        finalDurability = maxDura - finalDurability;
        if (finalDurability >= maxDura) {
            finalDurability = maxDura - 1;
        }
        item.setDurability((short) finalDurability);
        return item;
    }

    public static net.minecraft.server.v1_8_R3.ItemStack updateExtendedDurabilityLore(net.minecraft.server.v1_8_R3.ItemStack nmsItem) {
        if (!hasExtendedDurability(nmsItem)) return nmsItem;
        CraftItemStack item = CraftItemStack.asCraftMirror(nmsItem);
        NBTTagCompound nbt = Util.getItemNBT(item);
        int eD = nbt.getInt("extendedDurability");
        int max = nbt.getInt("maxExtendedDurability");
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        String s = lore.get(lore.size() - 1);
        if (s.contains("Durability:")) {
            s = ChatColor.GREEN + "Durability: " + eD + "/" + max;
            lore.set(lore.size() - 1, s);
        } else {
            String c = ChatColor.GREEN + "Durability: " + eD + "/" + max;
            lore.add(c);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        short maxDura = item.getType().getMaxDurability();
        double ePercent = (double) eD / (double) max;
        double finalDurability = Math.round(ePercent * maxDura);
        finalDurability = maxDura - finalDurability;
        if (finalDurability >= maxDura) {
            finalDurability = maxDura - 2;
        }
        item.setDurability((short) finalDurability);
        return CraftItemStack.asNMSCopy(item);
    }

    //Charge Related

    public static boolean isChargeable(ItemStack item) {
        if (item == null) return false;
        return Util.getItemNBT(item).hasKey("charge");
    }

    public static boolean isChargeable(net.minecraft.server.v1_8_R3.ItemStack item) {
        if (item == null) return false;
        return item.getTag().hasKey("charge");
    }

    public static net.minecraft.server.v1_8_R3.ItemStack subtractCharge(net.minecraft.server.v1_8_R3.ItemStack item, int x) {
        if (!isChargeable(item)) return item;
        if (ItemRegistry.checkDurabilityEnchant(CraftItemStack.asBukkitCopy(item)))
            return updateChargeLore(item);
        NBTTagCompound nbt = item.getTag();
        int charge = nbt.getInt("charge");
        charge -= x;
        if (charge < 0) charge = 0;
        nbt.setInt("charge", charge);
        return updateChargeLore(item);
    }

    public static net.minecraft.server.v1_8_R3.ItemStack addCharge(net.minecraft.server.v1_8_R3.ItemStack item, int x) {
        if (!isChargeable(item)) return item;
        NBTTagCompound nbt = item.getTag();
        int charge = nbt.getInt("charge");
        int max = nbt.getInt("maxCharge");
        charge += x;
        if (charge > max) charge = max;
        nbt.setInt("charge", charge);
        return updateChargeLore(item);
    }

    public static int getCharge(net.minecraft.server.v1_8_R3.ItemStack item) {
        if (!isChargeable(item)) return 0;
        NBTTagCompound nbt = item.getTag();
        int charge = nbt.getInt("charge");
        return charge;
    }

    public static CraftItemStack subtractCharge(ItemStack item, int x) {
        if (!isChargeable(item)) return (CraftItemStack) item;
        if (ItemRegistry.checkDurabilityEnchant(item))
            return (CraftItemStack) updateChargeLore(item);
        NBTTagCompound nbt = Util.getItemNBT(item);
        int charge = nbt.getInt("charge");
        charge -= x;
        if (charge < 0) charge = 0;
        nbt.setInt("charge", charge);
        return (CraftItemStack) updateChargeLore(Util.setItemNBT(item, nbt));
    }

    public static CraftItemStack addCharge(ItemStack item, int x) {
        if (!isChargeable(item)) return (CraftItemStack) item;
        NBTTagCompound nbt = Util.getItemNBT(item);
        int charge = nbt.getInt("charge");
        int max = nbt.getInt("maxCharge");
        charge += x;
        if (charge > max) charge = max;
        nbt.setInt("charge", charge);
        return (CraftItemStack) updateChargeLore(Util.setItemNBT(item, nbt));
    }

    public static int getCharge(ItemStack item) {
        if (!isChargeable(item)) return 0;
        NBTTagCompound nbt = Util.getItemNBT(item);
        int charge = nbt.getInt("charge");
        return charge;
    }

    public static boolean isFullyCharged(ItemStack item) {
        if (!isChargeable(item)) return false;
        NBTTagCompound nbt = Util.getItemNBT(item);
        int charge = nbt.getInt("charge");
        int max = nbt.getInt("maxCharge");
        return charge >= max;
    }

    public static boolean isFullyCharged(net.minecraft.server.v1_8_R3.ItemStack item) {
        if (!isChargeable(item)) return false;
        NBTTagCompound nbt = Util.getItemNBT(CraftItemStack.asBukkitCopy(item));
        int charge = nbt.getInt("charge");
        int max = nbt.getInt("maxCharge");
        return charge >= max;
    }

    public static int getHeldCharge(ItemStack stack) {
        if (stack == null) return 0;
        return Util.getItemNBT(stack).getInt("heldCharge");
    }

    public static ItemStack updateChargeLore(ItemStack item) {
        if (!isChargeable(item)) return item;
        NBTTagCompound nbt = Util.getItemNBT(item);
        int charge = nbt.getInt("charge");
        int max = nbt.getInt("maxCharge");
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        String s = lore.get(lore.size() - 1);
        if (s.contains("Charge:")) {
            s = ChatColor.GREEN + "Charge: " + charge + "/" + max;
            lore.set(lore.size() - 1, s);
        } else {
            String c = ChatColor.GREEN + "Charge: " + charge + "/" + max;
            lore.add(c);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        short maxDura = item.getType().getMaxDurability();
        short currDura = item.getDurability();
        double cPercent = (double) charge / (double) max;
        double finalDurability = Math.round(cPercent * maxDura);
        finalDurability = maxDura - finalDurability;
        if (finalDurability >= maxDura) {
            finalDurability = maxDura - 1;
        }
        item.setDurability((short) finalDurability);
        return item;
    }

    public static net.minecraft.server.v1_8_R3.ItemStack updateChargeLore(net.minecraft.server.v1_8_R3.ItemStack nmsItem) {
        if (!isChargeable(nmsItem)) return nmsItem;
        CraftItemStack item = CraftItemStack.asCraftMirror(nmsItem);
        NBTTagCompound nbt = Util.getItemNBT(item);
        int charge = nbt.getInt("charge");
        int max = nbt.getInt("maxCharge");
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        String s = lore.get(lore.size() - 1);
        if (s.contains("Charge:")) {
            s = ChatColor.GREEN + "Charge: " + charge + "/" + max;
            lore.set(lore.size() - 1, s);
        } else {
            String c = ChatColor.GREEN + "Charge: " + charge + "/" + max;
            lore.add(c);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        short maxDura = item.getType().getMaxDurability();
        double cPercent = (double) charge / (double) max;
        double finalDurability = Math.round(cPercent * maxDura);
        finalDurability = maxDura - finalDurability;
        if (finalDurability >= maxDura) {
            finalDurability = maxDura - 2;
        }
        item.setDurability((short) finalDurability);
        return CraftItemStack.asNMSCopy(item);
    }

}