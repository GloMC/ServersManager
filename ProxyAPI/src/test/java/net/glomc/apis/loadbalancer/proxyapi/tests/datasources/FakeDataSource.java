package net.glomc.apis.loadbalancer.proxyapi.tests.datasources;

import net.glomc.apis.loadbalancer.proxyapi.datasources.ServersDataSource;
import net.glomc.apis.loadbalancer.common.models.HostAndPort;

import java.util.*;

public class FakeDataSource extends ServersDataSource {

    public FakeDataSource(String groupId) {
        super(groupId);
    }

    @Override
    public List<String> getHeartBeatingServers() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 3000; i++) {
            list.add("server-" + i);
        }
        list.add(UUID.randomUUID().toString());
        return list;
    }

    @Override
    public List<String> getDeadServers() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 3000; i++) {
            list.add("server-dead-" + i);
        }
        list.add(UUID.randomUUID().toString());
        return list;
    }

    @Override
    public void cleanDeadServers(List<String> deadServersIds) {

    }

    private final Random random = new Random();

    @Override
    public Map<String, Map<String, Object>> getServersData(List<String> servers) {
        HashMap<String, Map<String, Object>> serversData = new HashMap<>();

        for (String server : servers) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("online", random.nextInt(200));
            hashMap.put("max-online", String.valueOf(200));
            serversData.put(server, hashMap);
        }
        return Collections.unmodifiableMap(serversData);
    }

    @Override
    public Map<String, Object> getServerData(String server) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", random.nextInt(200));
        hashMap.put("max-online", 200);
        return hashMap;
    }

    @Override
    public Object getSingleServerData(String serverId, String fieldName) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", String.valueOf(random.nextInt(200)));
        hashMap.put("max-online", String.valueOf(200));
        return hashMap.get(fieldName);
    }

    @Override
    public HostAndPort getServerHostAndPort(String server) {
        return new HostAndPort("192.168.0.150", random.nextInt(60000));
    }

}
