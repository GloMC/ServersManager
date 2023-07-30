package net.glomc.apis.loadbalancer.proxyapi;

import net.glomc.apis.loadbalancer.proxyapi.serversmanager.ServersManager;
import net.glomc.apis.loadbalancer.common.models.HostAndPort;

import java.util.Map;

/**
 * You need to extend this class to create Load balancers
 *
 * @since 1.0.0
 */
public abstract class LoadBalancer {
    protected final ServersManager manager;

    /**
     * @param manager The manager
     * @see ServersManager
     */
    public LoadBalancer(ServersManager manager) {
        this.manager = manager;
    }


    /**
     * You will need to implement this method as is get called on each call to
     *
     * @param serverData like online: 10 etc.
     * @return Server id
     * @see LoadBalancer#bestServer()
     * @see net.glomc.apis.loadbalancer.common.enums.DataFieldId
     */
    protected abstract String bestServer(Map<String, Map<String, Object>> serverData);

    /**
     * You would call this method to get the best server id
     *
     * @return serverId can be null if no servers were found.
     * @throws RuntimeException if there is a problem fetching
     */
    public String bestServer() {
        try {
            return bestServer(manager.getServersData(manager.getServers()));
        } catch (Exception e) {
            throw new RuntimeException("Problem selecting the best server", e);
        }
    }


    /**
     * return the group id of this load balancer which is set by the data source
     *
     * @return groupId set by user in data source
     * @see ServersManager
     */
    public String groupId() {
        return manager.groupId();
    }
}
