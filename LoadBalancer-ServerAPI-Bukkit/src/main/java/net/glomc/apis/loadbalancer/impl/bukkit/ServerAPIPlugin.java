package net.glomc.apis.loadbalancer.impl.bukkit;

import com.github.puregero.multilib.MultiLib;
import net.glomc.apis.loadbalancer.common.models.HostAndPort;
import net.glomc.apis.loadbalancer.common.utils.ip.IpChecker;
import net.glomc.apis.loadbalancer.config.jedis.JedisConfigLoader;
import net.glomc.apis.loadbalancer.serverapi.CollectorManager;
import net.glomc.apis.loadbalancer.serverapi.CollectorPublisher;
import net.glomc.apis.loadbalancer.serverapi.HeartbeatTask;
import net.glomc.apis.loadbalancer.serverapi.config.MainConfigLoader;
import net.glomc.apis.loadbalancer.serverapi.config.model.MainConfiguration;
import net.glomc.apis.loadbalancer.serverapi.datasources.ServersDataSource;
import net.glomc.apis.loadbalancer.serverapi.datasources.redis.RedisServersDataSource;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.providers.ClusterConnectionProvider;
import redis.clients.jedis.providers.PooledConnectionProvider;

import java.io.IOException;
import java.time.Duration;

public class ServerAPIPlugin extends JavaPlugin implements JedisConfigLoader, MainConfigLoader {

    private final CollectorManager collectorManager = new CollectorManager();
    private ServersDataSource dataSource;
    private BukkitTask heartbeatTask;
    private BukkitTask collectorPublisherTask;
    private MainConfiguration configuration;

    private final boolean isMultiPaper;


    public ServerAPIPlugin() {
        boolean isMultiPaper1;
        try {
            Player.class.getMethod("isExternalPlayer");
            isMultiPaper1 = true;
        } catch (NoSuchMethodException e) {
            isMultiPaper1 = false;
        }

        this.isMultiPaper = isMultiPaper1;
    }

    @Override
    public void onEnable() {
        try {
            // tell config object about the server port
            configuration = loadMainConfig(getDataFolder(), getServer().getPort());

        } catch (IOException e) {
            throw new RuntimeException("Main config failed to load", e);
        }
        if (isMultiPaper()) {
            getLogger().info("MultiPaper detected!");
            configuration.setServerId(MultiLib.getLocalServerName());
        }
        if (configuration.getDataSource() == MainConfiguration.DataSource.REDIS) {
            try {
                loadRedisConfig(getDataFolder());
            } catch (IOException e) {
                throw new RuntimeException("Redis config failed to load", e);
            }
        } else  {
            throw new RuntimeException("unknown database");
        }
        getLogger().info("Server id: " + configuration.getServerId());
        getLogger().info("Group id: " + configuration.getGroupId());
        if (configuration.isPublic()) {
            try {
                String ip = IpChecker.getPublicAddress();
                dataSource.publishHostAndPort(new HostAndPort(ip, getServer().getPort()));
                getSLF4JLogger().info("ip / port was published!");
            } catch (IOException e) {
                throw new RuntimeException("Unable to fetch ip address", e);
            }
        } else {
            HostAndPort hostAndPort = configuration.getHostAndPort();
            if (hostAndPort != null) {
                dataSource.publishHostAndPort(hostAndPort);
                getSLF4JLogger().info("ip / port was published!");
            } else {
                getSLF4JLogger().error("Failed to obtain host / port");
            }
        }
        BukkitCollectors.registerAll(collectorManager, this);

        HeartbeatTask publishTask = new HeartbeatTask(this.dataSource);

        heartbeatTask = getServer().getScheduler().runTaskTimerAsynchronously(this, publishTask, 0, 20);

        CollectorPublisher collectorPublisher = new CollectorPublisher(collectorManager, dataSource);
        this.collectorPublisherTask = getServer().getScheduler().runTaskTimerAsynchronously(this, collectorPublisher, 0, 1);

    }


    @Override
    public void onDisable() {
        try {
            this.heartbeatTask.cancel();
            this.collectorPublisherTask.cancel();
            dataSource.publishHeartBeatDeath();
            if (this.dataSource instanceof AutoCloseable closeable) {
                closeable.close();
            }
        } catch (Exception ignore) {
        }


    }

    @Override
    public void handleCluster(ClusterConnectionProvider clusterConnectionProvider) {
        dataSource = new RedisServersDataSource(configuration.getServerId(), configuration.getGroupId(), new JedisCluster(clusterConnectionProvider, 60, Duration.ofSeconds(60)));
    }

    @Override
    public void handlePooled(PooledConnectionProvider pooledConnectionProvider) {
        dataSource = new RedisServersDataSource(configuration.getServerId(), configuration.getGroupId(), new JedisPooled(pooledConnectionProvider));
    }

    public boolean isMultiPaper() {
        return isMultiPaper;
    }

    public ServersDataSource getDataSource() {
        return dataSource;
    }
}
