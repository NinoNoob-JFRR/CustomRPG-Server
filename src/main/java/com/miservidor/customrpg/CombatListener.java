package com.miservidor.customrpg;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class CombatListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        // 1. BACKSTAB: Verificación de ángulo de ataque
        Vector attackerDir = attacker.getLocation().getDirection().setY(0).normalize();
        Vector targetDir = target.getLocation().getDirection().setY(0).normalize();

        // Si el jugador está detrás del objetivo (producto punto > 0.8)
        if (attackerDir.dot(targetDir) > 0.8) {
            event.setDamage(event.getDamage() * 2.5); // Multiplicador x2.5 por la espalda
            attacker.sendMessage("§c§l¡BACKSTAB! §7Ataque crítico por la espalda.");
            target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 15);
        }

        // 2. VAMPIRIC REGENERATION: Cura segura usando PotionEffects (evita errores de API)
        double damageDealt = event.getFinalDamage();
        double healAmount = damageDealt * 0.20; // Roba el 20% del daño realizado

        if (healAmount > 0) {
            double currentHealth = attacker.getHealth();
            double maxHealth = attacker.getMaxHealth();
            attacker.setHealth(Math.min(maxHealth, currentHealth + healAmount));
            attacker.getWorld().spawnParticle(Particle.HEART, attacker.getLocation().add(0, 2, 0), 3);
        }

        // 3. CALL LIGHTNING: 15% de probabilidad de generar rayo
        if (Math.random() < 0.15) {
            target.getWorld().strikeLightningEffect(target.getLocation());
            target.damage(6.0, attacker); // Daño extra del rayo
        }
    }
}
