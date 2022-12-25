package net.glomc.apis.loadbalancer.api;

import net.glomc.apis.loadbalancer.api.datasources.ServersDataSource;
import net.glomc.apis.loadbalancer.common.models.HostAndPort;

import java.util.Map;

public abstract class LoadBalancer {
    protected final ServersDataSource dataSource;

    public LoadBalancer(ServersDataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected abstract String bestServer(Map<String , Map<String, String>> serverData);

    public String bestServer() {
        try {
            return bestServer(dataSource.getServersData(dataSource.getHeartBeatingServers()));
        } catch (Exception e) {
            throw new RuntimeException("Problem selecting the best server", e);
        }
    }

    public HostAndPort getServerHostAndPort(String server) {
        try {
            return dataSource.getServerHostAndPort(server);
        } catch (Exception e) {
            throw new RuntimeException("Problem fetching server host and port", e);
        }

    }

    public String getGroupId() {
        return dataSource.getGroupId();
    }
}
