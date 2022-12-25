package net.glomc.apis.loadbalancer.server.api.datasources.redis;

import net.glomc.apis.loadbalancer.common.models.HostAndPort;
import net.glomc.apis.loadbalancer.server.api.datasources.ServersDataSource;
import redis.clients.jedis.UnifiedJedis;

import java.time.Instant;
import java.util.Map;

public class RedisServersDataSource extends ServersDataSource {

    private final UnifiedJedis unifiedJedis;

    public RedisServersDataSource(String serverId, String groupId, UnifiedJedis unifiedJedis) {
        super(serverId, groupId);
        this.unifiedJedis = unifiedJedis;
    }

    @Override
    public void publishHeartBeat() {
        unifiedJedis.hset("loadbalancer:: " + groupId + "::heartbeats", serverId, String.valueOf(Instant.now().getEpochSecond()));
    }

    @Override
    public void publishData(Map<String, String> data) {
        unifiedJedis.hset("loadbalancer:: " + groupId + "::data::" + serverId, data);

    }

    @Override
    protected void publishHostAndPort(HostAndPort hostAndPort) {

    }
}
