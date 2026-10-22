package com.miservidor.customrpg;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomRPG extends JavaPlugin {

    private static CustomRPG instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        // Registrar Eventos (Listeners)
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(new ArmorListener(), this);
        getServer().getPluginManager().registerEvents(new SafeDropListener(), this);
        getServer().getPluginManager().registerEvents(new BossManager(), this); // <- Esta línea es crucial
        
        getLogger().info(ChatColor.GREEN + "CustomRPG v1.0.0 cargado exitosamente en Purpur 26.3");
    }
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "CustomRPG desactivado.");
    }

    public static CustomRPG getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("customrpg")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "[CustomRPG] Configuración recargada.");
                return true;
            }
            if (args.length > 1 && args[0].equalsIgnoreCase("spawn") && sender instanceof Player) {
                Player player = (Player) sender;
                if (args[1].equalsIgnoreCase("giant")) {
                    BossManager.spawnTitanZombie(player.getLocation());
                    player.sendMessage(ChatColor.RED + "¡Ha aparecido el Titán Zombie!");
                    return true;
                }
            }
        }
        return false;
    }
}
