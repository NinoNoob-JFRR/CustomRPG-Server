package com.miservidor.customrpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Giant;
import org.bukkit.inventory.ItemStack;

public class BossManager {

    public static void spawnTitanZombie(Location loc) {
        Giant titan = loc.getWorld().spawn(loc, Giant.class);

        titan.setCustomName(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Titán Zombie Ancestral");
        titan.setCustomNameVisible(true);
        titan.setMaxHealth(6000.0);
        titan.setHealth(6000.0);

        // Equipamiento visual
        titan.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
        titan.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));

        // Barra de vida en pantalla
        BossBar bossBar = Bukkit.createBossBar(
            ChatColor.DARK_GREEN + "Titán Zombie Ancestral", 
            BarColor.GREEN, 
            BarStyle.SOLID
        );
        bossBar.setProgress(1.0);
    }
}
