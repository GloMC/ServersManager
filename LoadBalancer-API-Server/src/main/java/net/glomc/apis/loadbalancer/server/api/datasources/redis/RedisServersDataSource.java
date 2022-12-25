package net.glomc.apis.loadbalancer.server.api.datasources.redis;

import net.glomc.apis.loadbalancer.common.models.HostAndPort;
import net.glomc.apis.loadbalancer.server.api.datasources.ServersDataSource;
import redis.clients.jedis.UnifiedJedis;

import java.time.Instant;
import java.util.Map;

public class RedisServersDataSource extends ServersDataSource implements AutoCloseable {

    private final UnifiedJedis unifiedJedis;

    public RedisServersDataSource(String serverId, String groupId, UnifiedJedis unifiedJedis) {
        super(serverId, groupId);
        this.unifiedJedis = unifiedJedis;
    }

    @Override
    public void publishHeartBeat() {
        unifiedJedis.hset("loadbalancer::" + groupId + "::heartbeats", serverId, String.valueOf(Instant.now().getEpochSecond()));
        // expire data of server after 10 seconds incase death never get called
        unifiedJedis.expire("loadbalancer::" + groupId + "::data::" + serverId, 10);
    }

    @Override
    public void publishHeartBeatDeath() {
        unifiedJedis.hdel("loadbalancer::" + groupId + "::heartbeats", serverId);
        unifiedJedis.del("loadbalancer::" + groupId + "::data::" + serverId);
    }

    @Override
    public void publishData(Map<String, String> data) {
        unifiedJedis.hset("loadbalancer::" + groupId + "::data::" + serverId, data);
    }

    @Override
    public void publishData(String field, String data) {
        unifiedJedis.hset("loadbalancer::" + groupId + "::data::" + serverId, field, data);
    }

    @Override
    public void publishHostAndPort(HostAndPort hostAndPort) {
        unifiedJedis.hset("loadbalancer::" + groupId + "::intetnert_protocol::" + serverId, hostAndPort.convertIntoMap());
    }

    @Override
    public void close() throws Exception {
        this.unifiedJedis.close();
    }
}
