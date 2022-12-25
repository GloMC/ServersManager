package net.glomc.apis.loadbalancer.impl.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.glomc.apis.loadbalancer.api.LoadBalancer;
import net.glomc.apis.loadbalancer.api.datasources.ServersDataSource;
import net.glomc.apis.loadbalancer.api.datasources.redis.RedisServersDataSource;
import net.glomc.apis.loadbalancer.common.config.RedisConfigLoader;
import net.glomc.apis.loadbalancer.common.models.HostAndPort;
import net.glomc.apis.loadbalancer.impl.LowCountBalancerSystem;
import net.kyori.adventure.text.Component;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.providers.ClusterConnectionProvider;
import redis.clients.jedis.providers.PooledConnectionProvider;

import java.io.IOException;
import java.time.Duration;

public class RedisAutomaticDiscovery implements RedisConfigLoader, AutoCloseable {

    private final LoadBalancerAPIPlugin plugin;

    private final String groupId;
    private ServersDataSource dataSource;
    private final LoadBalancer loadBalancer;
    public RedisAutomaticDiscovery(LoadBalancerAPIPlugin plugin, String groupId) throws IOException {
        this.plugin = plugin;
        this.groupId = groupId;
        loadRedisConfig(plugin.getDataDirectory());
        loadBalancer = new LowCountBalancerSystem(dataSource);
    }

    @Subscribe
    public void onPlayerChooseInitialServerEvent(PlayerChooseInitialServerEvent event) {
        System.out.println("evemt called");
        Player player = event.getPlayer();
        player.sendMessage(Component.text("Selecting a server......"));
        String serverId = loadBalancer.bestServer();
        if (serverId == null) {
            return;
        }
        System.out.println("1");
        HostAndPort hostAndPort = loadBalancer.getServerHostAndPort(serverId);
        System.out.println("2");
        ServerInfo serverInfo = new ServerInfo(serverId, hostAndPort.convertIntoINetSocketAddress());
        System.out.println("3");
        RegisteredServer registeredServer = plugin.getServer().createRawRegisteredServer(serverInfo);
        System.out.println("4");
        event.setInitialServer(registeredServer);
        System.out.println("5");
        System.out.println("Player was sent into " + serverId);
    }

    @Override
    public void handleCluster(ClusterConnectionProvider clusterConnectionProvider) {
        dataSource = new RedisServersDataSource(groupId, new JedisCluster(clusterConnectionProvider, 60, Duration.ofSeconds(60)));
    }

    @Override
    public void handlePooled(PooledConnectionProvider pooledConnectionProvider) {
        dataSource = new RedisServersDataSource(groupId, new JedisPooled(pooledConnectionProvider));
    }

    @Override
    public void close() throws Exception {
        if (dataSource instanceof AutoCloseable autoCloseable) {
            autoCloseable.close();
        }
    }
}
