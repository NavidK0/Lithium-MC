package com.lastabyss.lithium;

import com.lastabyss.lithium.data.ItemRegistry;
import com.lastabyss.lithium.executor.LithiumExecutor;
import com.lastabyss.lithium.listener.BaseLithiumListener;
import com.lastabyss.lithium.listener.BatteryListener;
import com.lastabyss.lithium.listener.CompassListener;
import com.lastabyss.lithium.listener.DurabilityListener;
import com.lastabyss.lithium.listener.MinecartListener;
import com.lastabyss.lithium.listener.PickadeListener;
import com.lastabyss.lithium.listener.RodListener;
import com.lastabyss.lithium.listener.SwordListener;
import com.lastabyss.lithium.listener.TomahaxeListener;
import com.lastabyss.lithium.util.Util;
import io.hotmail.com.jacob_vejvoda.infernal_mobs.infernal_mobs;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lithium extends JavaPlugin {
    public static Lithium instance;
    public static CoreProtectAPI coreProtectAPI;
    private LithiumExecutor executor;
    private infernal_mobs infernalMobs;

    private List<Listener> listeners = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        Util.initialize(this);

        infernalMobs = (infernal_mobs) Bukkit.getPluginManager().getPlugin("InfernalMobs");
        if (infernalMobs == null) {
            getLogger().info("InfernalMobs not found! Some features won't function!");
        }

        coreProtectAPI = getCoreProtect();
        if (coreProtectAPI != null) {
            coreProtectAPI.testAPI();
        }

        ItemRegistry.INFERNAL_NETHERSTAR_ITEM = infernalMobs.getItem(67);
        ItemRegistry.LITHIUM_DUST_ITEM = infernalMobs.getItem(90);
        //Initialize Listeners
        {
            listeners.add(new DurabilityListener(this));
            listeners.add(new BaseLithiumListener(this));
            listeners.add(new MinecartListener(this));
            listeners.add(new CompassListener(this));
            listeners.add(new RodListener(this));
            listeners.add(new MinecartListener(this));
            listeners.add(new PickadeListener(this));
            listeners.add(new BatteryListener(this));
            listeners.add(new TomahaxeListener(this));
            listeners.add(new SwordListener(this));
        }

        executor = new LithiumExecutor(this);
        try {
            saveResources();
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(Lithium.class.getName()).log(Level.SEVERE, null, ex);
        }
        reload();
        getCommand("lithium").setExecutor(executor);
    }

    @Override
    public void onDisable() {
        listeners.stream()
                .filter(listener -> listener instanceof BaseLithiumListener)
                .forEach(listener -> ((BaseLithiumListener) listener).end());
    }

    public void reload() {
        reloadConfig();
    }

    private CoreProtectAPI getCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");
        if (plugin == null || !(plugin instanceof CoreProtect)) {
            return null;
        }
        CoreProtectAPI coreProtect = ((CoreProtect) plugin).getAPI();
        if (coreProtect.isEnabled() == false) {
            return null;
        }
        if (coreProtect.APIVersion() < 4) {
            return null;
        }
        return coreProtect;
    }

    public void saveResources() throws IOException, URISyntaxException {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
            getLogger().info("Saved new data folder.");
        }
        saveDefaultConfig();
    }

    public infernal_mobs getInfernalMobs() {
        return infernalMobs;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return super.getDefaultWorldGenerator(worldName, id);
    }

}
