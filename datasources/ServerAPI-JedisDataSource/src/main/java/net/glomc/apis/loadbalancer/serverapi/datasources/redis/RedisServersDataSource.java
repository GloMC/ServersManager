package net.glomc.apis.loadbalancer.serverapi.datasources.redis;

import net.glomc.apis.loadbalancer.common.models.HostAndPort;
import net.glomc.apis.loadbalancer.serverapi.datasources.ServersDataSource;
import redis.clients.jedis.*;
import redis.clients.jedis.providers.ClusterConnectionProvider;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Map;

public class RedisServersDataSource extends ServersDataSource implements AutoCloseable {

    private final UnifiedJedis unifiedJedis;

    private final ClusterConnectionProvider clusterConnectionProvider;

    public RedisServersDataSource(String serverId, String groupId, HostAndPort hostAndPort, UnifiedJedis unifiedJedis) {
        super(serverId, groupId, hostAndPort);
        this.unifiedJedis = unifiedJedis;
        if (unifiedJedis instanceof JedisCluster) {
            // let's try using reflection to obtain provider from unified cluster Connection provider
            try {
                Field field = UnifiedJedis.class.getDeclaredField("provider");
                clusterConnectionProvider = (ClusterConnectionProvider) field.get(unifiedJedis);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("unable to use reflection to obtain the provider", e);
            }
        } else {
            this.clusterConnectionProvider = null;
        }
    }

    @Override
    public void publishHeartBeat() {
        unifiedJedis.hset("loadbalancer::" + groupId() + "::heartbeats", serverId(), String.valueOf(Instant.now().getEpochSecond()));
    }

    @Override
    public void publishHeartBeatDeath() {
        if (unifiedJedis instanceof JedisPooled pooled) {
            try (Connection connection = pooled.getPool().getResource()) {
                Pipeline pipeline = new Pipeline(connection);
                pipeline.hdel("loadbalancer::" + groupId() + "::heartbeats", serverId());
                pipeline.del("loadbalancer::" + groupId() + "::data::" + serverId());
                pipeline.del("loadbalancer::" + groupId() + "::intetnert_protocol::" + serverId());
                pipeline.sync();
            }
        } else if (unifiedJedis instanceof JedisCluster) {
            ClusterPipeline clusterPipeline = new ClusterPipeline(clusterConnectionProvider);
            clusterPipeline.hdel("loadbalancer::" + groupId() + "::heartbeats", serverId());
            clusterPipeline.del("loadbalancer::" + groupId() + "::data::" + serverId());
            clusterPipeline.del("loadbalancer::" + groupId() + "::intetnert_protocol::" + serverId());
            clusterPipeline.sync();
        }
    }

    @Override
    public void publishData(Map<String, String> data) {
        unifiedJedis.hset("loadbalancer::" + groupId() + "::data::" + serverId(), data);
    }

    @Override
    public void publishData(String field, String data) {
        unifiedJedis.hset("loadbalancer::" + groupId() + "::data::" + serverId(), field, data);
    }

    @Override
    public void publishHostAndPort() {
        unifiedJedis.hset("loadbalancer::" + groupId() + "::intetnert_protocol::" + serverId(), hostAndPort().convertIntoMap());
    }

    @Override
    public void close() throws Exception {
        this.unifiedJedis.close();
    }
}
