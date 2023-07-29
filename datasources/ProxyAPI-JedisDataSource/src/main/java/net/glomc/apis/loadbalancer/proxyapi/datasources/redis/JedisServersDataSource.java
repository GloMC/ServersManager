package net.glomc.apis.loadbalancer.proxyapi.datasources.redis;

import net.glomc.apis.loadbalancer.common.models.HostAndPort;
import net.glomc.apis.loadbalancer.proxyapi.datasources.ServersDataSource;
import redis.clients.jedis.*;
import redis.clients.jedis.providers.ClusterConnectionProvider;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JedisServersDataSource extends ServersDataSource implements AutoCloseable {

    private final UnifiedJedis unifiedJedis;
    private final ClusterConnectionProvider clusterConnectionProvider;

    public JedisServersDataSource(String groupId, UnifiedJedis unifiedJedis, ClusterConnectionProvider clusterConnectionProvider) {
        super(groupId);
        this.unifiedJedis = unifiedJedis;
        this.clusterConnectionProvider = clusterConnectionProvider;
    }

    public JedisServersDataSource(String groupId, UnifiedJedis unifiedJedis) {
        super(groupId);
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
    public List<String> getHeartBeatingServers() {
        Map<String, String> heartbeats = unifiedJedis.hgetAll("loadbalancer::" + groupId + "::heartbeats");
        ArrayList<String> aliveServers = new ArrayList<>();
        heartbeats.forEach((server, heartbeat) -> {
            // consider servers that their heartbeat after 10 seconds dead
            // servers should remove their heartbeat on shutdown automatically but this for dead servers
            // that never reached the shutdown process like exited or killed.
            if (!(Instant.now().getEpochSecond() - Long.parseLong(heartbeat) > 10)) {
                aliveServers.add(server);
            }
        });

        return aliveServers;
    }

    @Override
    public List<String> getDeadServers() {
        Map<String, String> heartbeats = unifiedJedis.hgetAll("loadbalancer::" + groupId + "::heartbeats");
        ArrayList<String> deadServers = new ArrayList<>();
        heartbeats.forEach((server, heartbeat) -> {
            // see note on getHeatBeatingServers
            if (Instant.now().getEpochSecond() - Long.parseLong(heartbeat) > 10) {
                deadServers.add(server);
            }
        });

        return deadServers;
    }

    @Override
    public void cleanDeadServers(List<String> deadServersIds) {
        if (unifiedJedis instanceof JedisPooled pooled) {
            try (Connection connection = pooled.getPool().getResource()) {
                Pipeline pipeline = new Pipeline(connection);
                deadServersIds.forEach((serverId) -> {
                    pipeline.hdel("loadbalancer::" + getGroupId() + "::heartbeats", serverId);
                    pipeline.del("loadbalancer::" + getGroupId() + "::data::" + serverId);
                    pipeline.del("loadbalancer::" + getGroupId() + "::intetnert_protocol::" + serverId);
                });
                pipeline.sync();
            }
        } else if (unifiedJedis instanceof JedisCluster) {
            ClusterPipeline clusterPipeline = new ClusterPipeline(clusterConnectionProvider);
            deadServersIds.forEach((serverId) -> {
                clusterPipeline.hdel("loadbalancer::" + getGroupId() + "::heartbeats", serverId);
                clusterPipeline.del("loadbalancer::" + getGroupId() + "::data::" + serverId);
                clusterPipeline.del("loadbalancer::" + getGroupId() + "::intetnert_protocol::" + serverId);
            });
            clusterPipeline.sync();
        } else {
            deadServersIds.forEach((serverId) -> {
                unifiedJedis.hdel("loadbalancer::" + getGroupId() + "::heartbeats", serverId);
                unifiedJedis.del("loadbalancer::" + getGroupId() + "::data::" + serverId);
                unifiedJedis.del("loadbalancer::" + getGroupId() + "::intetnert_protocol::" + serverId);
            });

        }
    }

    @Override
    public Map<String, Map<String, String>> getServersData(List<String> serversIds) {
        Map<String, Map<String, String>> data = new HashMap<>();
        if (unifiedJedis instanceof JedisPooled pooled) {
            try (Connection connection = pooled.getPool().getResource()) {
                Pipeline pipeline = new Pipeline(connection);
                HashMap<String, Response<Map<String, String>>> responseHashMap = new HashMap<>();
                for (String serverId : serversIds) {
                    responseHashMap.put(serverId, pipeline.hgetAll("loadbalancer::" + groupId + "::data::" + serverId));
                }
                pipeline.sync();
                responseHashMap.forEach((serverId, response) -> data.put(serverId, response.get()));
            }
        } else if (unifiedJedis instanceof JedisCluster) {
            ClusterPipeline clusterPipeline = new ClusterPipeline(clusterConnectionProvider);
            HashMap<String, Response<Map<String, String>>> responseHashMap = new HashMap<>();
            for (String serverId : serversIds) {
                responseHashMap.put(serverId, clusterPipeline.hgetAll("loadbalancer::" + groupId + "::data::" + serverId));
            }
            clusterPipeline.sync();
            responseHashMap.forEach((serverId, response) -> data.put(serverId, response.get()));
        } else {
            for (String serverId : serversIds) {
                data.put(serverId, unifiedJedis.hgetAll("loadbalancer::" + groupId + "::data::" + serverId));
            }
        }

        return data;
    }

    @Override
    public Map<String, String> getServerData(String serverId) {
        return unifiedJedis.hgetAll("loadbalancer::" + groupId + "::data::" + serverId);
    }

    @Override
    public String getSingleServerData(String serverId, String fieldName) {
        return unifiedJedis.hget("loadbalancer::" + groupId + "::data::" + serverId, fieldName);
    }

    @Override
    public HostAndPort getServerHostAndPort(String serverId) {
        Map<String, String> data = unifiedJedis.hgetAll("loadbalancer::" + groupId + "::intetnert_protocol::" + serverId);
        if (data == null || data.isEmpty()) return null;
        return HostAndPort.fromMap(data);
    }

    @Override
    public void close() throws Exception {
        this.unifiedJedis.close();
    }

    public UnifiedJedis getUnifiedJedis() {
        return unifiedJedis;
    }
}
