package com.miservidor.customrpg;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class SafeDropListener implements Listener {

    // Intercepta drops de AuraSkills o multiplicadores masivos y evita que superen el stack de 64
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Item itemEntity = event.getEntity();
        ItemStack stack = itemEntity.getItemStack();

        // Si la cantidad producida supera los 64 por bugs de Suerte/AuraSkills
        if (stack.getAmount() > 64) {
            stack.setAmount(64); // Ajusta de forma segura al límite máximo
            itemEntity.setItemStack(stack);
        }
    }
}
