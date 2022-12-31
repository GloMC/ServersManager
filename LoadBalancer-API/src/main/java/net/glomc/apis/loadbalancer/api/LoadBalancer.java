package net.glomc.apis.loadbalancer.api;

import net.glomc.apis.loadbalancer.api.datasources.ServersDataSource;
import net.glomc.apis.loadbalancer.common.enums.DataFieldId;
import net.glomc.apis.loadbalancer.common.models.HostAndPort;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
     */
    public LoadBalancer(ServersDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * You will need to implement this method as is get called on each call to
     *
     * @param serverData like online: 10 etc.
     * @return Server id
     * @see LoadBalancer#bestServer()
     * @see net.glomc.apis.loadbalancer.common.enums.DataFieldId
     */
    protected abstract String bestServer(Map<String, Map<String, String>> serverData);

    /**
     * You would call this method to get the best server id
     *
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
     *
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
     * Returns Max number of total online players in this group.
     * note: Max number depends on servers capacity
     * for example:
     *  Server1 have 20 as max
     *  server2 have 15 as max
     *  output/total would be 35
     *
     * @return max number of Online players that can be online in this group
     */
    public int getMaxPlayers() {
        AtomicInteger integer = new AtomicInteger(0);
        this.dataSource.getServersData(
                this.dataSource.getHeartBeatingServers()).forEach((key, value)
                -> integer.addAndGet(Integer.parseInt(value.get(DataFieldId.MAX_ONLINE.getFieldId()))));
        return integer.get();
    }


    /**
     * Returns number of total online players in this group
     * Note: this can go over max as servers might allow override of max count.
     * @return number of Online players in this group
     */
    public int getCurrentPlayers() {
        AtomicInteger integer = new AtomicInteger(0);
        this.dataSource.getServersData(
                this.dataSource.getHeartBeatingServers()).forEach((key, value)
                -> integer.addAndGet(Integer.parseInt(value.get(DataFieldId.ONLINE.getFieldId()))));
        return integer.get();
    }


    /**
     * return the group id of this load balancer which is set by the data source
     *
     * @return groupId set by user in data source
     * @see ServersDataSource
     */
    public String getGroupId() {
        return dataSource.getGroupId();
    }
}
