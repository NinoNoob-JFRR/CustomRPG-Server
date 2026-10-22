package com.miservidor.customrpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossManager implements Listener {

    private static final Map<UUID, BossBar> activeBossBars = new HashMap<>();

    public static void spawnTitanZombie(Location loc) {
        Giant titan = loc.getWorld().spawn(loc, Giant.class);
        
        titan.setCustomName(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Titán Zombie Ancestral");
        titan.setCustomNameVisible(true);
        titan.setAI(true); // Activa la Inteligencia Artificial para que se mueva y ataque

        // Vida máxima y actual de 6000 HP
        if (titan.getAttribute(Attribute.MAX_HEALTH) != null) {
            titan.getAttribute(Attribute.MAX_HEALTH).setBaseValue(6000.0);
        }
        titan.setHealth(6000.0);

        // Equipamiento del Gigante
        if (titan.getEquipment() != null) {
            titan.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
            titan.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
            titan.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
        }

        // Crear la BossBar en pantalla
        BossBar bossBar = Bukkit.createBossBar(
            ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Titán Zombie Ancestral " + ChatColor.GRAY + "[6000 / 6000 HP]", 
            BarColor.GREEN, 
            BarStyle.SOLID
        );
        bossBar.setProgress(1.0);

        // Hace que la barra aparezca en la pantalla de todos los jugadores conectados
        for (Player p : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(p);
        }

        // Guarda la barra asociada a la UUID única del jefe
        activeBossBars.put(titan.getUniqueId(), bossBar);
    }

    // Evento que actualiza el texto y la barra de vida cuando recibe un golpe
    @EventHandler
    public void onBossDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Giant giant) {
            UUID id = giant.getUniqueId();
            if (activeBossBars.containsKey(id)) {
                BossBar bar = activeBossBars.get(id);
                double maxHp = giant.getAttribute(Attribute.MAX_HEALTH) != null ? 
                        giant.getAttribute(Attribute.MAX_HEALTH).getValue() : 6000.0;
                double currentHp = Math.max(0, giant.getHealth() - event.getFinalDamage());
                
                // Actualiza el texto con la vida numérica exacta
                bar.setTitle(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Titán Zombie Ancestral " + 
                        ChatColor.RED + "[" + (int) currentHp + " / " + (int) maxHp + " HP]");
                
                // Actualiza el porcentaje visual de la barra de color
                bar.setProgress(Math.max(0.0, Math.min(1.0, currentHp / maxHp)));
            }
        }
    }

    // Evento que elimina la barra de la pantalla cuando el jefe muere
    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Giant giant) {
            UUID id = giant.getUniqueId();
            if (activeBossBars.containsKey(id)) {
                BossBar bar = activeBossBars.remove(id);
                bar.removeAll(); // Borra la barra de la pantalla de todos
            }
        }
    }
}
