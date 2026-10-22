package com.miservidor.customrpg;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomRPG extends JavaPlugin {

    private static CustomRPG instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Registrar todos los eventos (Listeners) del plugin
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(new ArmorListener(), this);
        getServer().getPluginManager().registerEvents(new SafeDropListener(), this);
        getServer().getPluginManager().registerEvents(new BossManager(), this);

        getLogger().info(ChatColor.GREEN + "CustomRPG v1.0.0 cargado exitosamente en Purpur 26.3");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "CustomRPG desactivado.");
    }

    public static CustomRPG getInstance() {
        return instance;
    }
}
