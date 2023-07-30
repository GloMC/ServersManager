package net.glomc.apis.loadbalancer.proxyapi.serversmanager;

import net.glomc.apis.loadbalancer.common.enums.DataFieldId;
import net.glomc.apis.loadbalancer.common.models.HostAndPort;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * an abstract class that fetches, manages the data from the Database
 */
public abstract class ServersManager {

    private final String groupId;

    private final BiConsumer<String, HostAndPort> addServerConsumer;
    private final Consumer<String> removeServerConsumer;

    /**
     * @param groupId Group id of the servers for example "skyblock"
     * @param addServerConsumer this get called when server get added.
     * @param removeServerConsumer this get called when server get removed when death.
     */
    public ServersManager(String groupId, BiConsumer<String, HostAndPort> addServerConsumer, Consumer<String> removeServerConsumer) {
        this.groupId = groupId;
        this.addServerConsumer = addServerConsumer;
        this.removeServerConsumer = removeServerConsumer;
    }

    /**
     * Returns servers that are heart beating.
     *
     * @return A list with all servers that are alive and detected by the manager
     */
    public abstract List<String> getServers();

    /**
     * Returns servers that are dead. didn't heartbeat for some amount of time.
     *
     * @return A list with all servers that are dead and detected by the manager
     * @apiNote THIS ALWAYS WILL BE EMPTY LIST as manager automatically removes dead servers.
     */
    public abstract List<String> getDeadServers();

    /**
     * Cleans dead servers That didn't heartbeat
     *
     * @param deadServersIds dead servers id usually from {@link #getDeadServers()}
     */
    public abstract void cleanDeadServers(List<String> deadServersIds);

    /**
     * Returns all data set by the servers provided by you
     *
     * @param serversIds server ids usually from {@link #getServers()}
     * @return Returns a Map with Servers ids as key and inside it A second map which contains data.
     * @see #getServers()
     */
    public abstract Map<String, Map<String, Object>> getServersData(List<String> serversIds);

    /**
     * Returns data set by the server provided by you
     *
     * @param server a single server
     * @return Returns a Map which contains data.
     */
    public abstract Map<String, Object> getServerData(String server);

    /**
     * Returns single data instead of whole data about a server
     *
     * @param serverId    server id
     * @param dataFieldId Internal needed
     * @return Returns a value instead of a map
     * @see DataFieldId
     */
    public Object getSingleServerData(String serverId, DataFieldId dataFieldId) {
        return getSingleServerData(serverId, dataFieldId.getFieldId());
    }

    /**
     * Returns single data instead of whole data about a server
     *
     * @param serverId  server id
     * @param fieldName field name
     * @return Returns a value instead of a map
     */
    public abstract Object getSingleServerData(String serverId, String fieldName);

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
    public long getMaxPlayers() {
        return getMaxPlayers(getServersData(getServers()));
    }


    /**
     * Returns number of total online players in this group
     * Note: this can go over max as servers might allow override of max count.
     *
     * @return number of Online players in this group
     */
    public long getCurrentPlayers() {
        return getCurrentPlayers(getServersData(getServers()));
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
     * @apiNote Second note: if servers didn't publish their data, see: {@linkplain DataFieldId}, method will just it to zero.
     * @see #getServersData(List)
     * @see #getServers()
     * @see DataFieldId
     */
    public long getMaxPlayers(Map<String, Map<String, Object>> data) {
        AtomicInteger integer = new AtomicInteger(0);
        data.forEach((key, value) -> {
            Integer number = (Integer) value.get(DataFieldId.MAX_ONLINE.getFieldId());
            integer.addAndGet(number != null ? number : 0);
        });
        return integer.get();
    }


    /**
     * Returns number of total online players in this group
     *
     * @param data Servers data if you already have them.
     * @return number of Online players in this group
     * @apiNote Note: this can go over max as servers might allow override of max count.
     * @apiNote Second note: if servers didn't publish their data, see: {@linkplain DataFieldId}, method will just it to zero.
     * @see #getServersData(List)
     * @see #getServers()
     * @see DataFieldId
     */
    public long getCurrentPlayers(Map<String, Map<String, Object>> data) {
        AtomicInteger integer = new AtomicInteger(0);
        data.forEach((key, value) -> {
            Integer number = (Integer) value.get(DataFieldId.ONLINE.getFieldId());
            integer.addAndGet(number != null ? number : 0);
        });
        return integer.get();
    }

    /**
     * return the group id of this manager
     *
     * @return groupId set by user in data source
     */
    public String groupId() {
        return groupId;
    }

    protected BiConsumer<String, HostAndPort> addServerConsumer() {
        return addServerConsumer;
    }

    protected Consumer<String> removeServerConsumer() {
        return removeServerConsumer;
    }
}
