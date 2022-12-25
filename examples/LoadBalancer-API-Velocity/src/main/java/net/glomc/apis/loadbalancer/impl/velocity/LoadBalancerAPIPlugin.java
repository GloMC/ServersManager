package net.glomc.apis.loadbalancer.impl.velocity;


import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "loadbalancerapi", name = "LoadBalancerAPI", version = "1.0.0-SNAPSHOT")
public class LoadBalancerAPIPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    @Inject
    public LoadBalancerAPIPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitializeEvent(ProxyInitializeEvent event) {
        try {
            server.getEventManager().register(this, new RedisAutomaticDiscovery(this, "development"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

}
