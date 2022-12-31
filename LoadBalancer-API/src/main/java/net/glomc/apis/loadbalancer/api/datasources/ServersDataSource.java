package net.glomc.apis.loadbalancer.api.datasources;

import net.glomc.apis.loadbalancer.common.enums.DataFieldId;
import net.glomc.apis.loadbalancer.common.models.HostAndPort;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * an abstract class that fetches the data from the Database
 *
 * @see net.glomc.apis.loadbalancer.api.datasources.redis.RedisServersDataSource is an implementation
 */
public abstract class ServersDataSource {

    protected final String groupId;

    /**
     * Group id of servers
     *
     * @param groupId Group id of the servers for example "skyblock"
     */
    public ServersDataSource(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Returns servers that are heart beating.
     *
     * @return A list with all servers that are alive and detected by the datasource
     */
    public abstract List<String> getHeartBeatingServers();

    /**
     * Returns all data set by the servers provided by you
     *
     * @param serversIds server ids usually from
     * @return Returns a Map with Servers ids as key and inside it A second map which contains data.
     * @see #getHeartBeatingServers()
     */
    public abstract Map<String, Map<String, String>> getServersData(List<String> serversIds);

    /**
     * Returns data set by the server provided by you
     *
     * @param server a single server
     * @return Returns a Map which contains data.
     */
    public abstract Map<String, String> getServerData(String server);

    /**
     * Returns single data instead of whole data about a server
     *
     * @param serverId    server id
     * @param dataFieldId Internal needed
     * @return Returns a value instead of a map
     * @see DataFieldId
     */
    public String getSingleServerData(String serverId, DataFieldId dataFieldId) {
        return getSingleServerData(serverId, dataFieldId.getFieldId());
    }

    /**
     * Returns single data instead of whole data about a server
     *
     * @param serverId  server id
     * @param fieldName field name
     * @return Returns a value instead of a map
     */
    public abstract String getSingleServerData(String serverId, String fieldName);

    /**
     * return the server host
     *
     * @return HostAndPort can be null
     * @see HostAndPort
     */
    public abstract HostAndPort getServerHostAndPort(String serverId);

    /**
     * Returns Max number of total online players in this group.
     * note: Max number depends on servers capacity
     * for example:
     * Server1 have 20 as max
     * server2 have 15 as max
     * output/total would be 35
     *
     * @return max number of Online players that can be online in this group
     */
    public int getMaxPlayers() {
        return getMaxPlayers(getServersData(getHeartBeatingServers()));
    }


    /**
     * Returns number of total online players in this group
     * Note: this can go over max as servers might allow override of max count.
     *
     * @return number of Online players in this group
     */
    public int getCurrentPlayers() {
        return getCurrentPlayers(getServersData(getHeartBeatingServers()));
    }


    /**
     * Returns Max number of total online players in this group.
     * note: Max number depends on servers capacity
     * for example:
     * Server1 have 20 as max
     * server2 have 15 as max
     * output/total would be 35
     *
     * @param data Servers data if you already have them.
     * @return max number of Online players that can be online in this group
     * @see #getServersData(List)
     * @see #getHeartBeatingServers()
     */
    public int getMaxPlayers(Map<String, Map<String, String>> data) {
        AtomicInteger integer = new AtomicInteger(0);
        data.forEach((key, value)
                -> integer.addAndGet(Integer.parseInt(value.get(DataFieldId.MAX_ONLINE.getFieldId()))));
        return integer.get();
    }


    /**
     * Returns number of total online players in this group
     * Note: this can go over max as servers might allow override of max count.
     *
     * @param data Servers data if you already have them.
     * @return number of Online players in this group
     * @see #getServersData(List)
     * @see #getHeartBeatingServers()
     */
    public int getCurrentPlayers(Map<String, Map<String, String>> data) {
        AtomicInteger integer = new AtomicInteger(0);
        data.forEach((key, value)
                -> integer.addAndGet(Integer.parseInt(value.get(DataFieldId.ONLINE.getFieldId()))));
        return integer.get();
    }

    /**
     * return the group id of this datasource
     *
     * @return groupId set by user in data source
     */
    public String getGroupId() {
        return groupId;
    }
}
