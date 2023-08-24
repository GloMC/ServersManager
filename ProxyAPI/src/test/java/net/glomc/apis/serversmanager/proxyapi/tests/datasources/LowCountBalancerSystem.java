package net.glomc.apis.serversmanager.proxyapi.tests.datasources;


import net.glomc.apis.serversmanager.proxyapi.loadbalancer.LoadBalancer;
import net.glomc.apis.serversmanager.proxyapi.ServersManager;
import net.glomc.apis.serversmanagers.common.enums.DataFieldId;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class LowCountBalancerSystem extends LoadBalancer {

    public LowCountBalancerSystem(ServersManager dataSource) {
        super(dataSource);
    }

    @Override
    protected String bestServer(Map<String, Map<String, Object>> serversData) {
        Map<String, Integer> joinableServers = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> data : serversData.entrySet()) {
            String serverId = data.getKey();
            Map<String, Object> serverData = data.getValue();
            if (serverData == null) {
                continue;
            }
            int online = (int) serverData.get(DataFieldId.ONLINE.getFieldId());
            int maxOnline = (int) serverData.get(DataFieldId.MAX_ONLINE.getFieldId());
            if (online >= maxOnline) {
                continue;
            }
            joinableServers.put(serverId, online);
        }
        if (joinableServers.isEmpty()) return null;
        return joinableServers.entrySet().stream().min(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
    }

}
