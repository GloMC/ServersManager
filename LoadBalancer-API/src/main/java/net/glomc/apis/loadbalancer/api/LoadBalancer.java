package net.glomc.apis.loadbalancer.api;

import net.glomc.apis.loadbalancer.api.datasources.ServersDataSource;
import net.glomc.apis.loadbalancer.common.models.HostAndPort;

import java.util.Map;

/**
 * You need to extend this class to create Load balancers
 *
 * @since 1.0.0
 */
public abstract class LoadBalancer {
    protected final ServersDataSource dataSource;

    /**
     * @param dataSource The Datasource
     * @see ServersDataSource
     * @see net.glomc.apis.loadbalancer.api.datasources.redis.RedisServersDataSource
     */
    public LoadBalancer(ServersDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * You will need to implement this method as is get called on each call to
     * @see LoadBalancer#bestServer()
     * @param serverData like online: 10 etc.
     * @see net.glomc.apis.loadbalancer.common.enums.DataFieldId
     * @return Server id
     */
    protected abstract String bestServer(Map<String , Map<String, String>> serverData);

    /**
     * You would call this method to get the best server id
     * @return serverId can be null if no servers were found.
     * @throws RuntimeException if there is a problem fetching
     */
    public String bestServer() {
        try {
            return bestServer(dataSource.getServersData(dataSource.getHeartBeatingServers()));
        } catch (Exception e) {
            throw new RuntimeException("Problem selecting the best server", e);
        }
    }

    /**
     * return the server host
     * @return HostAndPort can be null
     * @see HostAndPort
     */
    public HostAndPort getServerHostAndPort(String server) {
        try {
            return dataSource.getServerHostAndPort(server);
        } catch (Exception e) {
            throw new RuntimeException("Problem fetching server host and port", e);
        }

    }

    /**
     * return the group id of this load balancer which is set by the data source
     * @return groupId set by user in data source
     * @see ServersDataSource
     */
    public String getGroupId() {
        return dataSource.getGroupId();
    }
}
