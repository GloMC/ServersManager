package net.glomc.apis.impl.bukkit.loadbalancer;

import org.bukkit.plugin.java.JavaPlugin;

public class ServerAPIPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println(getServer().getIp());
    }


    @Override
    public void onDisable() {

    }

}
