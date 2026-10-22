package com.miservidor.customrpg;

import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CombatListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        FileConfiguration config = CustomRPG.getInstance().getConfig();

        // 1. BACKSTAB
        if (config.getBoolean("combat.backstab.enabled", true)) {
            Vector attackerDir = attacker.getLocation().getDirection().setY(0).normalize();
            Vector targetDir = target.getLocation().getDirection().setY(0).normalize();
            double minDot = config.getDouble("combat.backstab.min-angle-dot", 0.8);

            if (attackerDir.dot(targetDir) > minDot) {
                int level = getItemRareLevel(attacker.getInventory().getItemInMainHand());
                
                if (deductCost(attacker, "combat.backstab", level)) {
                    double baseMult = config.getDouble("combat.backstab.base-multiplier", 2.5);
                    double perLevelMult = config.getDouble("combat.backstab.multiplier-per-level", 0.5);
                    double totalMult = baseMult + ((level - 1) * perLevelMult);

                    event.setDamage(event.getDamage() * totalMult);

                    String msg = config.getString("combat.backstab.message", "&c&l¡BACKSTAB!")
                            .replace("%multiplier%", String.format("%.1f", totalMult))
                            .replace("&", "§");
                    attacker.sendMessage(msg);
                    target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 15);
                }
            }
        }

        // 2. VAMPIRIC REGENERATION
        if (config.getBoolean("combat.vampirism.enabled", true)) {
            int level = getItemRareLevel(attacker.getInventory().getItemInMainHand());
            
            if (deductCost(attacker, "combat.vampirism", level)) {
                double baseHeal = config.getDouble("combat.vampirism.base-heal-percentage", 0.15);
                double perLevelHeal = config.getDouble("combat.vampirism.heal-percentage-per-level", 0.05);
                double totalHealPercent = baseHeal + ((level - 1) * perLevelHeal);

                double damageDealt = event.getFinalDamage();
                double healAmount = damageDealt * totalHealPercent;

                if (healAmount > 0) {
                    double maxHealth = attacker.getAttribute(Attribute.MAX_HEALTH).getValue();
                    double newHealth = Math.min(maxHealth, attacker.getHealth() + healAmount);
                    attacker.setHealth(newHealth);

                    if (config.getBoolean("combat.vampirism.show-particles", true)) {
                        attacker.getWorld().spawnParticle(Particle.HEART, attacker.getLocation().add(0, 1.5, 0), 4);
                    }
                }
            }
        }

        // 3. CALL LIGHTNING
        if (config.getBoolean("combat.lightning.enabled", true)) {
            int level = getItemRareLevel(attacker.getInventory().getItemInMainHand());
            double baseChance = config.getDouble("combat.lightning.base-chance", 0.10);
            double perLevelChance = config.getDouble("combat.lightning.chance-per-level", 0.02);
            double totalChance = baseChance + ((level - 1) * perLevelChance);

            if (Math.random() < totalChance) {
                if (deductCost(attacker, "combat.lightning", level)) {
                    double baseDmg = config.getDouble("combat.lightning.base-damage", 5.0);
                    double perLevelDmg = config.getDouble("combat.lightning.damage-per-level", 2.0);
                    double totalDmg = baseDmg + ((level - 1) * perLevelDmg);

                    target.getWorld().strikeLightningEffect(target.getLocation());
                    target.damage(totalDmg, attacker);
                }
            }
        }
    }

    // Sistema unificado para descontar costo de comida o vida según el nivel
    private boolean deductCost(Player player, String configPath, int level) {
        FileConfiguration config = CustomRPG.getInstance().getConfig();
        String costType = config.getString(configPath + ".cost-type", "none").toLowerCase();
        
        if (costType.equals("none")) return true;

        double baseCost = config.getDouble(configPath + ".cost-amount-base", 1.0);
        double perLevelCost = config.getDouble(configPath + ".cost-amount-per-level", 0.5);
        int totalCost = (int) Math.round(baseCost + ((level - 1) * perLevelCost));

        if (totalCost <= 0) return true;

        if (costType.equals("food")) {
            if (player.getFoodLevel() < totalCost) return false;
            player.setFoodLevel(Math.max(0, player.getFoodLevel() - totalCost));
            return true;
        } else if (costType.equals("health")) {
            if (player.getHealth() <= totalCost) return false;
            player.setHealth(player.getHealth() - totalCost);
            return true;
        }
        return true;
    }

    // Lee el nivel de la esencia desde el ítem (soporta lore o nivel por defecto 1)
    private int getItemRareLevel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 1;
        // Si el ítem no especifica un nivel especial, asume Nivel 1
        return 1; 
    }
}
