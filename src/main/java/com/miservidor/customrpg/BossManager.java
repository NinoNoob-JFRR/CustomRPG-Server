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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossManager implements Listener {

    private static final Map<UUID, BossBar> activeBossBars = new HashMap<>();

    public static void spawnTitanZombie(Location loc) {
        Giant titan = loc.getWorld().spawn(loc, Giant.class);
        
        titan.setCustomName(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Titán Zombie Ancestral");
        titan.setCustomNameVisible(true);
        titan.setAI(true);

        // Vida máxima y actual
        if (titan.getAttribute(Attribute.MAX_HEALTH) != null) {
            titan.getAttribute(Attribute.MAX_HEALTH).setBaseValue(6000.0);
        }
        titan.setHealth(6000.0);

        // Equipamiento
        if (titan.getEquipment() != null) {
            titan.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
            titan.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
            titan.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
        }

        // Crear BossBar
        BossBar bossBar = Bukkit.createBossBar(
            ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Titán Zombie Ancestral " + ChatColor.GRAY + "[6000 / 6000 HP]", 
            BarColor.GREEN, 
            BarStyle.SOLID
        );
        bossBar.setProgress(1.0);

        UUID bossId = titan.getUniqueId();
        activeBossBars.put(bossId, bossBar);

        // Tarea repetitiva: Revisa cada 1 segundo (20 ticks) los jugadores a menos de 100 bloques
        new BukkitRunnable() {
            @Override
            public void run() {
                if (titan.isDead() || !titan.isValid()) {
                    bossBar.removeAll();
                    activeBossBars.remove(bossId);
                    this.cancel();
                    return;
                }

                double radiusSquared = 100.0 * 100.0; // 100 bloques de distancia
                Location bossLoc = titan.getLocation();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().equals(bossLoc.getWorld()) && 
                        player.getLocation().distanceSquared(bossLoc) <= radiusSquared) {
                        
                        // Si está a menos de 100 bloques y no tiene la barra, se la muestra
                        if (!bossBar.getPlayers().contains(player)) {
                            bossBar.addPlayer(player);
                        }
                    } else {
                        // Si se aleja a más de 100 bloques, se le quita la barra
                        if (bossBar.getPlayers().contains(player)) {
                            bossBar.removePlayer(player);
                        }
                    }
                }
            }
        }.runTaskTimer(CustomRPG.getInstance(), 0L, 20L);
    }

    // Actualiza la barra y el texto de vida al recibir daño
    @EventHandler
    public void onBossDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Giant giant) {
            UUID id = giant.getUniqueId();
            if (activeBossBars.containsKey(id)) {
                BossBar bar = activeBossBars.get(id);
                double maxHp = giant.getAttribute(Attribute.MAX_HEALTH) != null ? 
                        giant.getAttribute(Attribute.MAX_HEALTH).getValue() : 6000.0;
                double currentHp = Math.max(0, giant.getHealth() - event.getFinalDamage());
                
                bar.setTitle(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Titán Zombie Ancestral " + 
                        ChatColor.RED + "[" + (int) currentHp + " / " + (int) maxHp + " HP]");
                
                bar.setProgress(Math.max(0.0, Math.min(1.0, currentHp / maxHp)));
            }
        }
    }

    // Elimina la barra cuando el jefe muere
    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Giant giant) {
            UUID id = giant.getUniqueId();
            if (activeBossBars.containsKey(id)) {
                BossBar bar = activeBossBars.remove(id);
                bar.removeAll();
            }
        }
    }
}
