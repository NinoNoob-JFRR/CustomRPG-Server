package com.miservidor.customrpg;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArmorListener implements Listener {

    // Aplica corazones amarillos (Absorción) para la habilidad Hardy
    public static void applyHardyAbsorption(Player player, int level) {
        int absorptionAmplifier = (level * 2) - 1; // Nivel 1 = 2 corazones, Nivel 3 = 6 corazones
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.ABSORPTION, 
            Integer.MAX_VALUE, 
            absorptionAmplifier, 
            false, 
            false
        ));
    }

    // Permite Doble Salto / Vuelo si tiene la habilidad activa
    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().name().equals("SURVIVAL")) {
            event.setCancelled(true);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setVelocity(player.getLocation().getDirection().multiply(1.5).setY(1.0));
            player.sendMessage("§b¡Doble Salto!");
        }
    }
}
