package com.lastabyss.lithium.executor;

import com.lastabyss.lithium.Lithium;
import com.lastabyss.lithium.data.ItemRegistry;
import com.lastabyss.lithium.util.Util;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Navid
 */
public class LithiumExecutor implements CommandExecutor {

    Lithium plugin;

    public LithiumExecutor(Lithium plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lithium.admin")) {
            denyPerms(sender);
            return true;
        }
        if (args.length == 0) {
            return true;
        }

        if (args[0].equalsIgnoreCase("testnbt")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command!");
            }
            Player p = (Player) sender;
            ItemStack item = p.getItemInHand();
            if (item == null) {
                p.sendMessage(ChatColor.RED + "You have no item in your hand!");
                return true;
            }
            NBTTagCompound tag = Util.getItemNBT(item);
            tag.setString("testNBT", "This is test NBT data!");
            item = Util.setItemNBT(item, tag);
            p.setItemInHand(item);
            p.sendMessage(ChatColor.RED + "Appended test nbt data to your itemstack!");
            return true;
        }

        if (args[0].equalsIgnoreCase("nbt")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command!");
            }
            Player p = (Player) sender;
            ItemStack item = p.getItemInHand();
            if (item == null) {
                p.sendMessage(ChatColor.RED + "You have no item in your hand!");
                return true;
            }
            NBTTagCompound nbt = Util.getItemNBT(item);
            p.sendMessage(ChatColor.GREEN + nbt.toString());
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            Player p = Bukkit.getPlayer(args[1]);
            if (p == null) {
                sender.sendMessage(ChatColor.RED + "Player is offline!");
                return true;
            }
            if (args.length == 3) {
                int amount = 1;
                String item = args[2];
                ItemStack itemStack = ItemRegistry.nameToStack(item);
                if (itemStack == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid item!");
                    return true;
                }
                itemStack.setAmount(amount);
                p.getInventory().addItem(itemStack);
                p.updateInventory();
            } else if (args.length == 4) {
                int amount = Integer.parseInt(args[3]);
                String item = args[2];
                ItemStack itemStack = ItemRegistry.nameToStack(item);
                if (itemStack == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid item!");
                    return true;
                }
                itemStack.setAmount(amount);
                p.getInventory().addItem(itemStack);
                p.updateInventory();
            } else {
                sender.sendMessage(ChatColor.RED + "/lithium give <player> <item> [amount]");
            }
        }
        return true;
    }

    private void denyPerms(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You don't have enough perms to do this!");
    }
}
