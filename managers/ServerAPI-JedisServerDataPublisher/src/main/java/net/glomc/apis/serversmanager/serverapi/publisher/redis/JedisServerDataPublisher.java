package net.glomc.apis.serversmanager.serverapi.publisher.redis;

import net.glomc.apis.serversmanagers.common.models.HostAndPort;
import net.glomc.apis.serversmanager.serverapi.publisher.ServerDataPublisher;
import org.json.JSONObject;
import redis.clients.jedis.UnifiedJedis;

import java.util.Map;

public class JedisServerDataPublisher extends ServerDataPublisher implements AutoCloseable {

    private final UnifiedJedis unifiedJedis;
    private final String channelName;

    public JedisServerDataPublisher(String serverId, String groupId, HostAndPort hostAndPort, UnifiedJedis unifiedJedis) {
        super(serverId, groupId, hostAndPort);
        this.unifiedJedis = unifiedJedis;
        this.channelName = (groupId() + "-proxies");
    }


    @Override
    public void close() throws Exception {
        this.unifiedJedis.close();
    }

    private JSONObject makeJson(String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("server-id", serverId);
        jsonObject.put("type", type);
        return jsonObject;
    }

    @Override
    public void publishHeartBeat() {
        JSONObject heartbeat = makeJson("heartbeat");
        heartbeat.put("host-port", hostAndPort.convertIntoMap());
        this.unifiedJedis.publish(this.channelName, heartbeat.toString());
    }

    @Override
    public void publishDeath() {
        this.unifiedJedis.publish(this.channelName, makeJson("death").toString());
    }

    @Override
    public void publishData(Map<String, Object> data) {
        JSONObject mainJson = makeJson("data");
        mainJson.put("data", data);
        this.unifiedJedis.publish(this.channelName, mainJson.toString());
    }
}
