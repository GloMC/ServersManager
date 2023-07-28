package net.glomc.apis.loadbalancer.proxyapi.tests.datasources;


import net.glomc.apis.loadbalancer.proxyapi.LoadBalancer;
import net.glomc.apis.loadbalancer.proxyapi.datasources.ServersDataSource;
import net.glomc.apis.loadbalancer.common.enums.DataFieldId;

import java.util.Map;

public class LowCountBalancerSystem extends LoadBalancer {

    public LowCountBalancerSystem(ServersDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String bestServer(Map<String, Map<String, String>> data) {
        String bestServer = null;
        Integer lastCount = null;

        for (Map.Entry<String, Map<String, String>> stringMapEntry : data.entrySet()) {
            String server = stringMapEntry.getKey();
            Map<String, String> serverData = stringMapEntry.getValue();
            int count = Integer.parseInt(serverData.get(DataFieldId.ONLINE.getFieldId()));
            int maxCount = Integer.parseInt(serverData.get(DataFieldId.MAX_ONLINE.getFieldId()));
            if (lastCount == null) {
                if (count != maxCount) {
                    lastCount = count;
                    bestServer = server;
                }
            } else if (count != maxCount && lastCount > count) {
                lastCount = count;
                bestServer = server;
            }
            if (count == 0) {
                break;
            }
        }

        return bestServer;
    }

}
