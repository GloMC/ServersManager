package net.glomc.apis.impl.bukkit.loadbalancer;

import net.glomc.apis.loadbalancer.common.models.HostAndPort;
import net.glomc.apis.loadbalancer.common.utils.ip.IpChecker;
import net.glomc.apis.loadbalancer.server.api.CollectorManager;
import net.glomc.apis.loadbalancer.server.api.PublishTask;
import net.glomc.apis.loadbalancer.server.api.datasources.ServersDataSource;
import net.glomc.apis.loadbalancer.server.api.datasources.redis.RedisServersDataSource;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Connection;
import redis.clients.jedis.JedisPooled;

import java.io.IOException;

public class ServerAPIPlugin extends JavaPlugin {


    private final CollectorManager collectorManager = new CollectorManager();
    private final ServersDataSource dataSource;
    private BukkitTask task;

    public ServerAPIPlugin() {
        GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(3);
        poolConfig.setBlockWhenExhausted(true);
        dataSource = new RedisServersDataSource("test-1", "test",
                new JedisPooled(poolConfig, "127.0.0.1", 6379));
    }


    @Override
    public void onEnable() {
        try {
            String ip = IpChecker.getAddress();
            dataSource.publishHostAndPort(new HostAndPort(ip, getServer().getPort()));
        } catch (IOException e) {
            throw new RuntimeException("Unable to fetch ip address", e);
        }
        BukkitCollectors.registerAll(collectorManager);

        PublishTask publishTask = new PublishTask(this.dataSource, this.collectorManager);

        task = getServer().getScheduler().runTaskTimerAsynchronously(this, publishTask, 0, 20);

    }


    @Override
    public void onDisable() {
        try {
            this.task.cancel();
            dataSource.publishHeartBeatDeath();
            if (this.dataSource instanceof AutoCloseable closeable) {
                closeable.close();
            }
        } catch (Exception ignore) {
        }


    }

}
