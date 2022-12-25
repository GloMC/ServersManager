package net.glomc.apis.loadbalancer.api.datasources.redis;

import net.glomc.apis.loadbalancer.api.datasources.ServersDataSource;
import net.glomc.apis.loadbalancer.common.models.HostAndPort;
import redis.clients.jedis.*;
import redis.clients.jedis.providers.ClusterConnectionProvider;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisServersDataSource extends ServersDataSource implements AutoCloseable {

    private final UnifiedJedis unifiedJedis;
    private final ClusterConnectionProvider clusterConnectionProvider;

    public RedisServersDataSource(String groupId, UnifiedJedis unifiedJedis) {
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
        ArrayList<String> arrayList = new ArrayList<>();
        heartbeats.forEach((server, heartbeat) -> {
            // consider servers that their heartbeat after 10 seconds dead
            // servers should remove their heartbeat on shutdown automaticlly but this for dead servers
            // that never reached the shutdown process like exited or killed.
            if (!(Instant.now().getEpochSecond() - Long.parseLong(heartbeat) > 10)) {
                arrayList.add(server);
            }
        });

        return arrayList;
    }

    @Override
    public Map<String, Map<String, String>> getServersData(List<String> serversIds) {
        Map<String, Map<String, String>> data = new HashMap<>();
        if (unifiedJedis instanceof JedisPooled pooled) {
            Pipeline pipeline = new Pipeline(pooled.getPool().getResource());
            HashMap<String, Response<Map<String, String>>> responseHashMap = new HashMap<>();
            for (String serverId : serversIds) {
                responseHashMap.put(serverId, pipeline.hgetAll("loadbalancer::" + groupId + "::data::" + serverId));
            }
            pipeline.sync();
            pipeline.close();
            responseHashMap.forEach((serverId, response) -> data.put(serverId, response.get()));
        } else if (unifiedJedis instanceof JedisCluster) {
            ClusterPipeline clusterPipeline = new ClusterPipeline(clusterConnectionProvider);
            HashMap<String, Response<Map<String, String>>> responseHashMap = new HashMap<>();
            for (String serverId : serversIds) {
                responseHashMap.put(serverId, clusterPipeline.hgetAll("loadbalancer::" + groupId + "::data::" + serverId));
            }
            clusterPipeline.sync();
            responseHashMap.forEach((serverId, response) -> data.put(serverId, response.get()));
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
        if ( data == null || data.isEmpty()) return null;
        return HostAndPort.fromMap(data);
    }

    @Override
    public void close() throws Exception {
        this.unifiedJedis.close();
    }
}
