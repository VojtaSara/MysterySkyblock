package me.vojtasara.noodlegeneration;

import org.bukkit.plugin.java.JavaPlugin;

public final class NoodleGeneration extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("PLUGIN STARTED SUCCESFULLY");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("PLUGIN ended SUCCESFULLY");

    }
}
