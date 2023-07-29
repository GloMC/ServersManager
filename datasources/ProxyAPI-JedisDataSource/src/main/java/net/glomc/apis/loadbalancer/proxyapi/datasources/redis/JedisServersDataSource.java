package net.glomc.apis.loadbalancer.proxyapi.datasources.redis;

import net.glomc.apis.loadbalancer.common.models.HostAndPort;
import net.glomc.apis.loadbalancer.proxyapi.datasources.ServersDataSource;
import org.json.JSONObject;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.UnifiedJedis;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class JedisServersDataSource extends ServersDataSource implements Runnable, AutoCloseable {

    private final UnifiedJedis unifiedJedis;

    private final ConcurrentHashMap<String, Long> heartBeats = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Map<String, Object>> serversData = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, HostAndPort> serversHostAndPort = new ConcurrentHashMap<>();

    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    public JedisServersDataSource(String groupId, UnifiedJedis unifiedJedis) {
        super(groupId);
        this.unifiedJedis = unifiedJedis;
    }

    @Override
    public List<String> getHeartBeatingServers() {
        ArrayList<String> aliveServers = new ArrayList<>();
        heartBeats.forEach((server, heartbeat) -> {
            // consider servers that their heartbeat after 10 seconds dead
            // servers should remove their heartbeat on shutdown automatically but this for dead servers
            // that never reached the shutdown process like exited or killed.
            if (!(Instant.now().getEpochSecond() - heartbeat > 10)) {
                aliveServers.add(server);
            }
        });
        return aliveServers;
    }


    @Override
    public List<String> getDeadServers() {
        ArrayList<String> deadServers = new ArrayList<>();
        this.heartBeats.forEach((server, heartbeat) -> {
            // see note on getHeatBeatingServers
            if (Instant.now().getEpochSecond() - heartbeat > 10) {
                deadServers.add(server);
            }
        });
        return deadServers;
    }


    @Override
    public void cleanDeadServers(List<String> deadServersIds) {
        for (String deadServersId : deadServersIds) {
            this.serversData.remove(deadServersId);
            this.serversHostAndPort.remove(deadServersId);
            this.heartBeats.remove(deadServersId);
        }
    }

    @Override
    public Map<String, Map<String, Object>> getServersData(List<String> serversIds) {
        Map<String, Map<String, Object>> data = new HashMap<>();
        for (String serversId : serversIds) {
            data.put(serversId, this.serversData.get(serversId));
        }
        return Collections.unmodifiableMap(data);
    }

    @Override
    public Map<String, Object> getServerData(String server) {
        return Collections.unmodifiableMap(this.serversData.get(server));
    }

    @Override
    public Object getSingleServerData(String serverId, String fieldName) {
        return this.serversData.get(serverId).get(fieldName);
    }

    @Override
    public HostAndPort getServerHostAndPort(String serverId) {
        return this.serversHostAndPort.get(serverId);
    }


    @Override
    public void close() throws Exception {
        this.unifiedJedis.close();
    }

    public UnifiedJedis getUnifiedJedis() {
        return unifiedJedis;
    }

    private static class PubSubHandler extends JedisPubSub {

        private final ConcurrentHashMap<String, Long> heartBeats;

        private final ConcurrentHashMap<String, Map<String, Object>> serversData;

        private final ConcurrentHashMap<String, HostAndPort> serversHostAndPort;

        public PubSubHandler(ConcurrentHashMap<String, Long> heartBeats, ConcurrentHashMap<String, Map<String, Object>> serversData, ConcurrentHashMap<String, HostAndPort> serversHostAndPort) {
            this.heartBeats = heartBeats;
            this.serversData = serversData;
            this.serversHostAndPort = serversHostAndPort;
        }

        @Override
        public void onMessage(String channel, String message) {
            JSONObject data = new JSONObject(message);
            String serverId = data.getString("server-id");
            if (data.getString("type").equals("heartbeat")) {
                heartBeats.put(serverId, Instant.now().getEpochSecond());
                serversHostAndPort.put(serverId, HostAndPort.fromMap(data.getJSONObject("host-port").toMap()));
            } else if (data.getString("type").equals("data")) {
                serversData.put(serverId, data.getJSONObject("data").toMap());
            } else if (data.getString("type").equals("death")) {
                this.serversData.remove(serverId);
                this.serversHostAndPort.remove(serverId);
                this.heartBeats.remove(serverId);
            }
        }
    }

    @Override
    public void run() {
        while (!isClosed.get()) {
            try {
                this.unifiedJedis.subscribe(new PubSubHandler(heartBeats, serversData, serversHostAndPort), (getGroupId() + "-proxies"));
            } catch (Throwable t) {
                // t.printStackTrace();
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
