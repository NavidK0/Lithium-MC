package com.lastabyss.lithium.data;

import com.lastabyss.lithium.util.DurabilityUtils;
import com.lastabyss.lithium.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;

/**
 * @author Navid
 */
public class ItemRegistry {

    /**
     * Names
     */
    //Lithium names
    public static String LITHIUM_BATTERY_NAME = Util.colorize("&7Lithium &bBattery&r");
    public static String LITHIUM_MINECART_NAME = Util.colorize("&7Lithium &bMinecart&r");
    public static String LITHIUM_COMPASS_NAME = Util.colorize("&7Lithium &bCompass&r");
    public static String LITHIUM_ROD_NAME = Util.colorize("&7Lithium &bRod&r");
    public static String LITHIUM_PICKADE_NAME = Util.colorize("&7Lithium &bPickade&r");
    public static String LITHIUM_TOMAHAXE_NAME = Util.colorize("&7Lithium &bTomahaxe&r");
    public static String LITHIUM_SWORD_NAME = Util.colorize("&7Lithium &bSword&r");

    //Atomic Items
    public static String ARMY_HELMET_NAME = ChatColor.DARK_GREEN + "Army Helmet";
    public static String SOLDIER_VESTMENTS_NAME = ChatColor.DARK_GREEN + "Soldier Vestments";
    public static String CARGO_PANTS_NAME = ChatColor.DARK_GREEN + "Cargo Pants";
    public static String MILITARY_BOOTS_NAME = ChatColor.DARK_GREEN + "Military Boots";
    public static String BROKEN_BAYONETTE_NAME = ChatColor.GREEN + "Broken " + ChatColor.DARK_GREEN + "Bayonette";
    public static String URANIUM_PICKAXE_NAME = ChatColor.GREEN + "Uranium " + ChatColor.DARK_GREEN + "Pickaxe";
    public static String TRENCH_DIGGER_NAME = ChatColor.GREEN + "Trench " + ChatColor.DARK_GREEN + "Digger";

    //Mayor Antebellum set items
    public static String MA_HELMET_NAME = Util.colorize("&6Mayor &eAntebellum's &6Crown&r");
    public static String MA_CHESTPLATE_NAME = Util.colorize("&6Mayor &eAntebellum's &6Chestplate&r");
    public static String MA_LEGGINGS_NAME = Util.colorize("&6Mayor &eAntebellum's &6Leggings&r");
    public static String MA_BOOTS_NAME = Util.colorize("&6Mayor &eAntebellum's &6Boots&r");


    /**
     * Items
     */
    //InfernalMobs items
    public static ItemStack INFERNAL_NETHERSTAR_ITEM;

    //Lithium items
    public static ItemStack LITHIUM_DUST_ITEM;
    public static ItemStack LITHIUM_BATTERY_ITEM;
    public static ItemStack LITHIUM_MINECART_ITEM;
    public static ItemStack LITHIUM_COMPASS_ITEM;
    public static ItemStack LITHIUM_ROD_ITEM;
    public static ItemStack LITHIUM_PICKADE_ITEM;
    public static ItemStack LITHIUM_TOMAHAXE_ITEM;
    public static ItemStack LITHIUM_SWORD_ITEM;

    //Atomic Items
    public static ItemStack ARMY_HELMET_ITEM;
    public static ItemStack SOLDIER_VESTMENTS_ITEM;
    public static ItemStack CARGO_PANTS_ITEM;
    public static ItemStack MILITARY_BOOTS_ITEM;
    public static ItemStack BROKEN_BAYONETTE_ITEM;
    public static ItemStack URANIUM_PICKAXE_ITEM;
    public static ItemStack TRENCH_DIGGER_ITEM;

    //Mayor Antebellum Armor
    public static ItemStack MA_HELMET_ITEM;
    public static ItemStack MA_CHESTPLATE_ITEM;
    public static ItemStack MA_LEGGINGS_ITEM;
    public static ItemStack MA_BOOTS_ITEM;


    public static ItemStack nameToStack(String name) {
        name = name.toUpperCase();
        switch (name) {
            case "LITHIUM_MINECART":
                return LITHIUM_MINECART_ITEM.clone();
            case "LITHIUM_COMPASS":
                return LITHIUM_COMPASS_ITEM.clone();
            case "LITHIUM_ROD":
                return LITHIUM_ROD_ITEM.clone();
            case "LITHIUM_PICKADE":
                return LITHIUM_PICKADE_ITEM.clone();
            case "LITHIUM_TOMAHAXE":
                return LITHIUM_TOMAHAXE_ITEM.clone();
            case "LITHIUM_SWORD":
                return LITHIUM_SWORD_ITEM.clone();
            case "LITHIUM_BATTERY":
                return LITHIUM_BATTERY_ITEM.clone();
            case "INFERNAL_NETHERSTAR":
                return INFERNAL_NETHERSTAR_ITEM.clone();
            case "LITHIUM_DUST":
                return LITHIUM_DUST_ITEM.clone();
            case "ARMY_HELMET":
                return ARMY_HELMET_ITEM.clone();
            case "SOLDIER_VESTMENTS":
                return SOLDIER_VESTMENTS_ITEM.clone();
            case "CARGO_PANTS":
                return CARGO_PANTS_ITEM.clone();
            case "MILITARY_BOOTS":
                return MILITARY_BOOTS_ITEM.clone();
            case "BROKEN_BAYONETTE":
                return BROKEN_BAYONETTE_ITEM.clone();
            case "URANIUM_PICKAXE":
                return URANIUM_PICKAXE_ITEM.clone();
            case "TRENCH_DIGGER":
                return TRENCH_DIGGER_ITEM.clone();
            case "MA_HELMET":
                return MA_HELMET_ITEM.clone();
            case "MA_CHESTPLATE":
                return MA_CHESTPLATE_ITEM.clone();
            case "MA_LEGGINGS":
                return MA_LEGGINGS_ITEM.clone();
            case "MA_BOOTS":
                return MA_BOOTS_ITEM.clone();
            default:
                return new ItemStack(Material.AIR);
        }
    }

    static {
        battery:
        {
            //Lithium Battery
            ItemStack item = new ItemStack(Material.STONE_BUTTON, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ItemRegistry.LITHIUM_BATTERY_NAME);
            meta.setLore(Arrays.asList(ChatColor.RED + "An object of immense power",
                    ChatColor.RED + "storage capabilities!"));
            item.setItemMeta(meta);
            ItemRegistry.LITHIUM_BATTERY_ITEM = lithiumize(item);
        }

        minecart:
        {
            //Lithium Minecart
            ItemStack item = new ItemStack(Material.MINECART, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ItemRegistry.LITHIUM_MINECART_NAME);
            meta.setLore(Arrays.asList(ChatColor.GRAY + "This minecart is super charged!",
                    ChatColor.GRAY + "It's 2.5x as fast as a normal minecart!",
                    ChatColor.GRAY + "How? Dunno, magnets or something.",
                    ChatColor.RED + "Warning: These minecarts will derail",
                    ChatColor.RED + "at extremely high speeds!"));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            item = Util.setItemNBT(item, nbt);
            ItemRegistry.LITHIUM_MINECART_ITEM = lithiumize(item);
        }

        compass:
        {
            //Lithium Compass
            ItemStack item = new ItemStack(Material.COMPASS, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ItemRegistry.LITHIUM_COMPASS_NAME);
            meta.setLore(Arrays.asList(ChatColor.GRAY + "This compass has the ability",
                    ChatColor.GRAY + "to find" + ChatColor.GOLD + " Infernal Mobs!",
                    ChatColor.GRAY + "How? Black (wither) magic. Heh.",
                    ChatColor.RED + "Right Click: Lock in on the closest infernal to you."));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            item = Util.setItemNBT(item, nbt);
            ItemRegistry.LITHIUM_COMPASS_ITEM = lithiumize(item);
        }

        rod:
        {
            //Lithium Rod
            ItemStack item = new ItemStack(Material.IRON_HOE, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ItemRegistry.LITHIUM_ROD_NAME);
            meta.setLore(Arrays.asList(ChatColor.GRAY + "This rod can pull items (Infernal items) out of lava!",
                    ChatColor.GRAY + "It also can get 2-5 fish per catch!",
                    ChatColor.GRAY + "It can also grapple things!",
                    ChatColor.RED + "Right Click: Toggle mode",
                    ChatColor.RED + "Left Click: Use lithium rod"));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            nbt.setString("mode", "fishing");
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 1024, 1024);
            ItemRegistry.LITHIUM_ROD_ITEM = lithiumize(item);
        }

        pickade:
        {
            //Lithium Pickade
            ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE, 1);
            item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 6);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ItemRegistry.LITHIUM_PICKADE_NAME);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(ChatColor.GRAY + "A pick AND a spade? What?",
                    ChatColor.RED + "Right Click: Toggle modes"));
            meta.addEnchant(Enchantment.DIG_SPEED, 10, true);
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            nbt.setString("mode", "mine");
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 6244, 6244);
            ItemRegistry.LITHIUM_PICKADE_ITEM = lithiumize(item);
        }

        tomhaxe:
        {
            //Lithium Tomahaxe
            ItemStack item = new ItemStack(Material.IRON_AXE, 1);
            item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 6);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ItemRegistry.LITHIUM_TOMAHAXE_NAME);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(ChatColor.GRAY + "A tomahawk that's also an axe?",
                    ChatColor.GRAY + "Isn't that a little OP?",
                    ChatColor.RED + "Right Click: Toggle modes"));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            nbt.setString("mode", "chop");
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 6244, 6244);
            ItemRegistry.LITHIUM_TOMAHAXE_ITEM = lithiumize(item);
        }

        sword:
        {
            //Lithium Sword
            ItemStack item = new ItemStack(Material.IRON_SWORD, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ItemRegistry.LITHIUM_SWORD_NAME);
            meta.setLore(Arrays.asList(ChatColor.GRAY + "The base lowest level sword.",
                    ChatColor.GRAY + "Upgrade it using better recipes!",
                    ChatColor.GRAY + "Toggle through different potion effects!",
                    ChatColor.GRAY + "Attacking something will give it that potion effect!",
                    ChatColor.RED + "Right Click: Toggle effects"));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            nbt.setInt("effect", 1);
            nbt.setInt("level", 1);
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 6244, 6244);
            ItemRegistry.LITHIUM_SWORD_ITEM = lithiumize(item);
        }

        //Radioactive items
        army_helmet:
        {
            ItemStack item = new ItemStack(Material.LEATHER_HELMET, 1);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(Color.fromRGB(93, 202, 49));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 8, true);
            meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 8, true);
            meta.addEnchant(Enchantment.PROTECTION_FIRE, 8, true);
            meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 8, true);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            meta.setDisplayName(ItemRegistry.ARMY_HELMET_NAME);
            meta.setLore(Arrays.asList(ChatColor.GREEN + "There are " + ChatColor.BOLD + "dents " + ChatColor.GREEN + "all around the rim.",
                    ChatColor.DARK_GREEN + "Radioactive",
                    ChatColor.RED + "Set Bonus: Immune from poison"));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 363, 363);
            ItemRegistry.ARMY_HELMET_ITEM = irradiate(item);
        }
        
        soldier_vestments:
        {
            ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(Color.fromRGB(93, 202, 49));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 8, true);
            meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 8, true);
            meta.addEnchant(Enchantment.PROTECTION_FIRE, 8, true);
            meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 8, true);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            meta.setDisplayName(ItemRegistry.SOLDIER_VESTMENTS_NAME);
            meta.setLore(Arrays.asList(ChatColor.DARK_AQUA + "Crimson stained... " + ChatColor.BOLD + "smells like ketchup.",
                    ChatColor.DARK_GREEN + "Radioactive",
                    ChatColor.RED + "Set Bonus: Immune from poison"));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 528, 528);
            ItemRegistry.SOLDIER_VESTMENTS_ITEM = irradiate(item);
        }

        cargo_pants:
        {
            ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS, 1);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(Color.fromRGB(93, 202, 49));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 8, true);
            meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 8, true);
            meta.addEnchant(Enchantment.PROTECTION_FIRE, 8, true);
            meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 8, true);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            meta.setDisplayName(ItemRegistry.CARGO_PANTS_NAME);
            meta.setLore(Arrays.asList(ChatColor.DARK_AQUA + "Some " + ChatColor.BOLD + "field rations" + ChatColor.GREEN + " are inside.",
                    ChatColor.DARK_GREEN + "Radioactive",
                    ChatColor.RED + "Set Bonus: Immune from poison"));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 495, 495);
            ItemRegistry.CARGO_PANTS_ITEM = irradiate(item);
        }

        military_boots:
        {
            ItemStack item = new ItemStack(Material.LEATHER_BOOTS, 1);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(Color.fromRGB(93, 202, 49));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 8, true);
            meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 8, true);
            meta.addEnchant(Enchantment.PROTECTION_FALL, 8, true);
            meta.addEnchant(Enchantment.PROTECTION_FIRE, 8, true);
            meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 8, true);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            meta.setDisplayName(ItemRegistry.MILITARY_BOOTS_NAME);
            meta.setLore(Arrays.asList(ChatColor.DARK_AQUA + "A pair of " + ChatColor.BOLD + "very" + ChatColor.DARK_AQUA + " intimidating boots.",
                    ChatColor.DARK_GREEN + "Radioactive",
                    ChatColor.RED + "Set Bonus: Immune from poison"));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 429, 429);
            ItemRegistry.MILITARY_BOOTS_ITEM = irradiate(item);
        }

        broken_bayonette:
        {
            ItemStack item = new ItemStack(Material.IRON_SWORD, 1);
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            meta.setDisplayName(ItemRegistry.BROKEN_BAYONETTE_NAME);
            meta.setLore(Arrays.asList(ChatColor.AQUA + "Wonder what it's been through...",
                    ChatColor.RED + "It has a little ketchup on it...",
                    ChatColor.DARK_GREEN + "Radioactive"));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 2342, 2342);
            ItemRegistry.BROKEN_BAYONETTE_ITEM = irradiate(item);
        }

        uranium_pickaxe:
        {
            ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE, 1);
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 7, true);
            meta.addEnchant(Enchantment.DURABILITY, 5, true);
            meta.addEnchant(Enchantment.DIG_SPEED, 10, true);
            meta.setDisplayName(ItemRegistry.URANIUM_PICKAXE_NAME);
            meta.setLore(Arrays.asList(ChatColor.AQUA + "I would not hold this for long...",
                    ChatColor.DARK_GREEN + "Radioactive"));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 2342, 2342);
            ItemRegistry.URANIUM_PICKAXE_ITEM = irradiate(item);
        }

        trench_digger:
        {
            ItemStack item = new ItemStack(Material.IRON_SPADE, 1);
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 7, true);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            meta.addEnchant(Enchantment.DIG_SPEED, 10, true);
            meta.setDisplayName(ItemRegistry.TRENCH_DIGGER_NAME);
            meta.setLore(Arrays.asList(ChatColor.AQUA + "Wonder what it's been through...",
                    ChatColor.RED + "It has a little ketchup on it...",
                    ChatColor.DARK_GREEN + "Radioactive"));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 2342, 2342);
            ItemRegistry.TRENCH_DIGGER_ITEM = irradiate(item);
        }

        //Mayor Antebellum
        ma_helmet:
        {
            ItemStack item = new ItemStack(Material.GOLD_HELMET, 1);
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
            meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 10, true);
            meta.addEnchant(Enchantment.PROTECTION_FIRE, 10, true);
            meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 10, true);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            meta.setDisplayName(ItemRegistry.MA_HELMET_NAME);
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Not much of a mayor if he had a crown, eh?",
                    ChatColor.RED + "Set Bonus: No hunger",
                    ChatColor.RED + "Set Bonus: Breath underwater",
                    ChatColor.RED + "Set Bonus: Speed 2",
                    ChatColor.RED + "Set Bonus: Damage Resistance",
                    ChatColor.RED + "Set Bonus: No Fall Damage"
                    ));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            nbt.setBoolean("isMayorAntebellum", true);
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 528, 528);
            ItemRegistry.MA_HELMET_ITEM = item;
        }

        ma_chestplate:
        {
            ItemStack item = new ItemStack(Material.IRON_CHESTPLATE, 1);
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
            meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 10, true);
            meta.addEnchant(Enchantment.PROTECTION_FIRE, 10, true);
            meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 10, true);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            meta.setDisplayName(ItemRegistry.MA_CHESTPLATE_NAME);
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Found deep underground in an abandoned city.",
                    ChatColor.RED + "Set Bonus: No hunger",
                    ChatColor.RED + "Set Bonus: Breath underwater",
                    ChatColor.RED + "Set Bonus: Speed 2",
                    ChatColor.RED + "Set Bonus: Damage Resistance",
                    ChatColor.RED + "Set Bonus: No Fall Damage"
            ));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            nbt.setBoolean("isMayorAntebellum", true);
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 528, 528);
            ItemRegistry.MA_CHESTPLATE_ITEM = item;
        }

        ma_leggings:
        {
            ItemStack item = new ItemStack(Material.IRON_LEGGINGS, 1);
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
            meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 10, true);
            meta.addEnchant(Enchantment.PROTECTION_FIRE, 10, true);
            meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 10, true);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            meta.setDisplayName(ItemRegistry.MA_LEGGINGS_NAME);
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Found deep underground in an abandoned city.",
                    ChatColor.RED + "Set Bonus: No hunger",
                    ChatColor.RED + "Set Bonus: Breath underwater",
                    ChatColor.RED + "Set Bonus: Speed 2",
                    ChatColor.RED + "Set Bonus: Damage Resistance",
                    ChatColor.RED + "Set Bonus: No Fall Damage"
            ));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            nbt.setBoolean("isMayorAntebellum", true);
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 528, 528);
            ItemRegistry.MA_LEGGINGS_ITEM = item;
        }

        ma_boots:
        {
            ItemStack item = new ItemStack(Material.DIAMOND_BOOTS, 1);
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
            meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 10, true);
            meta.addEnchant(Enchantment.PROTECTION_FIRE, 10, true);
            meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 10, true);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            meta.setDisplayName(ItemRegistry.MA_BOOTS_NAME);
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Found deep underground in an abandoned city.",
                    ChatColor.RED + "Set Bonus: No hunger",
                    ChatColor.RED + "Set Bonus: Breath underwater",
                    ChatColor.RED + "Set Bonus: Speed 2",
                    ChatColor.RED + "Set Bonus: Damage Resistance",
                    ChatColor.RED + "Set Bonus: No Fall Damage"
            ));
            item.setItemMeta(meta);
            NBTTagCompound nbt = Util.getItemNBT(item);
            nbt.setBoolean("isMayorAntebellum", true);
            item = addExtendedDurability(Util.setItemNBT(item, nbt), 528, 528);
            ItemRegistry.MA_BOOTS_ITEM = item;
        }

        registerRecipes();
    }

    private static ItemStack addCharge(ItemStack item, int charge, int maxCharge) {
        NBTTagCompound nbt = Util.getItemNBT(item);
        nbt.setInt("charge", charge);
        nbt.setInt("maxCharge", maxCharge);
        return DurabilityUtils.updateChargeLore(Util.setItemNBT(item, nbt));
    }

    private static ItemStack addExtendedDurability(ItemStack item, int dura, int maxDura) {
        NBTTagCompound nbt = Util.getItemNBT(item);
        nbt.setInt("extendedDurability", dura);
        nbt.setInt("maxExtendedDurability", maxDura);
        return DurabilityUtils.updateExtendedDurabilityLore(Util.setItemNBT(item, nbt));
    }

    private static ItemStack lithiumize(ItemStack item) {
        NBTTagCompound nbtSword = Util.getItemNBT(item);
        nbtSword.setBoolean("isLithium", true);
        item = Util.setItemNBT(item, nbtSword);
        if (item.getEnchantments().size() < 1) {
            item = Util.addGlow(item);
        }
        return item;
    }

    private static ItemStack irradiate(ItemStack item) {
        NBTTagCompound nbtSword = Util.getItemNBT(item);
        nbtSword.setBoolean("isRadioactive", true);
        item = Util.setItemNBT(item, nbtSword);
        if (item.getEnchantments().size() < 1) {
            item = Util.addGlow(item);
        }
        return item;
    }

    public static boolean isExtendedDurability(ItemStack stack) {
        return stack != null && Util.getItemNBT(stack).getBoolean("isExtendedDurability");
    }

    public static boolean isRadioactive(ItemStack stack) {
        return stack != null && Util.getItemNBT(stack).getBoolean("isRadioactive");
    }

    public static boolean isMayorAntebellumGear(ItemStack stack) {
        return stack != null && Util.getItemNBT(stack).getBoolean("isMayorAntebellum");
    }

    public static boolean hasCustomAbilities(ItemStack stack) {
        return stack != null && Util.getItemNBT(stack).getBoolean("isLithium");
    }

    public static boolean checkDurabilityEnchant(ItemStack item) {
        if (isArmor(item)) {
            int level = item.getEnchantmentLevel(Enchantment.DURABILITY);
            if (level == 0) return false;
            int chance = (60 + (40 / (level + 1)));
            return Math.random() * 100 < chance;
        } else {
            int level = item.getEnchantmentLevel(Enchantment.DURABILITY);
            if (level == 0) return false;
            int chance = 100 / (level + 1);
            return Math.random() * 100 < chance;
        }
    }

    public static boolean isArmor(ItemStack item) {
        Material mat = item.getType();
        String name = mat.name();
        return name.contains("HELMET") ||
                name.contains("CHESTPLATE") ||
                name.contains("LEGGINGS") ||
                name.contains("BOOTS");
    }

    public static void registerRecipes() {
        Bukkit.addRecipe(new ShapedRecipe(ItemRegistry.LITHIUM_MINECART_ITEM)
                .shape("iri", "iii")
                .setIngredient('i', Material.IRON_INGOT)
                .setIngredient('r', Material.REDSTONE));

        Bukkit.addRecipe(new ShapedRecipe(ItemRegistry.LITHIUM_COMPASS_ITEM).shape("qnq", "ncn", "qnq")
                .setIngredient('c', Material.COMPASS)
                .setIngredient('q', Material.QUARTZ)
                .setIngredient('n', Material.NETHER_STAR));

        Bukkit.addRecipe(new ShapedRecipe(ItemRegistry.LITHIUM_ROD_ITEM).shape("  i", " is", "i n")
                .setIngredient('i', Material.IRON_INGOT)
                .setIngredient('s', Material.STRING)
                .setIngredient('n', Material.NETHER_STAR));

        Bukkit.addRecipe(new ShapedRecipe(ItemRegistry.LITHIUM_BATTERY_ITEM).shape("ggg", "ddd", "ggg")
                .setIngredient('g', Material.GOLD_INGOT)
                .setIngredient('d', Material.INK_SACK, 6));

        Bukkit.addRecipe(new ShapedRecipe(ItemRegistry.LITHIUM_PICKADE_ITEM).shape("pas", " b ", " b ")
                .setIngredient('p', Material.DIAMOND_PICKAXE)
                .setIngredient('b', Material.STONE_BUTTON)
                .setIngredient('s', Material.DIAMOND_SPADE)
                .setIngredient('a', Material.SLIME_BALL));

        Bukkit.addRecipe(new ShapedRecipe(ItemRegistry.LITHIUM_TOMAHAXE_ITEM).shape("eae", " b ", " b ")
                .setIngredient('e', Material.ENDER_PEARL)
                .setIngredient('a', Material.DIAMOND_AXE)
                .setIngredient('b', Material.STONE_BUTTON));

        Bukkit.addRecipe(new ShapedRecipe(ItemRegistry.LITHIUM_SWORD_ITEM).shape("ada", " b ", " b ")
                .setIngredient('d', Material.DIAMOND_SWORD)
                .setIngredient('a', Material.DIAMOND_AXE)
                .setIngredient('b', Material.STONE_BUTTON));

    }
}
