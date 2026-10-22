package com.miservidor.customrpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
        if (loc == null || loc.getWorld() == null) return;
        Giant titan = loc.getWorld().spawn(loc, Giant.class);
        
        titan.setCustomName(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Titán Zombie Ancestral");
        titan.setCustomNameVisible(true);

        // Equipamiento visual del jefe
        if (titan.getEquipment() != null) {
            titan.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
            titan.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
            titan.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
        }

        // Crear BossBar en pantalla
        BossBar bossBar = Bukkit.createBossBar(
            ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Titán Zombie Ancestral", 
            BarColor.GREEN, 
            BarStyle.SOLID
        );
        bossBar.setProgress(1.0);

        UUID bossId = titan.getUniqueId();
        activeBossBars.put(bossId, bossBar);

        // Tarea repetitiva: Muestra la barra solo a jugadores a menos de 100 bloques (100^2 = 10000)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (titan.isDead() || !titan.isValid()) {
                    bossBar.removeAll();
                    activeBossBars.remove(bossId);
                    this.cancel();
                    return;
                }

                double radiusSquared = 100.0 * 100.0;
                Location bossLoc = titan.getLocation();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().equals(bossLoc.getWorld()) && 
                        player.getLocation().distanceSquared(bossLoc) <= radiusSquared) {
                        if (!bossBar.getPlayers().contains(player)) {
                            bossBar.addPlayer(player);
                        }
                    } else {
                        if (bossBar.getPlayers().contains(player)) {
                            bossBar.removePlayer(player);
                        }
                    }
                }
            }
        }.runTaskTimer(CustomRPG.getInstance(), 0L, 20L);
    }

    @EventHandler
    public void onBossDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Giant giant) {
            UUID id = giant.getUniqueId();
            if (activeBossBars.containsKey(id)) {
                BossBar bar = activeBossBars.get(id);
                double maxHp = giant.getMaxHealth();
                double currentHp = Math.max(0, giant.getHealth() - event.getFinalDamage());
                
                bar.setProgress(Math.max(0.0, Math.min(1.0, currentHp / maxHp)));
            }
        }
    }

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
