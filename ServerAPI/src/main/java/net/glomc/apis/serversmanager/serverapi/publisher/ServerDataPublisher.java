package net.glomc.apis.serversmanager.serverapi.publisher;

import net.glomc.apis.serversmanager.serverapi.HeartbeatTask;
import net.glomc.apis.serversmanagers.common.models.HostAndPort;
import net.glomc.apis.serversmanager.serverapi.collectors.CollectorManager;

import java.util.Map;

/**
 * abstract class responsible for publishing heartbeat, HostAndPort, data
 */
public abstract class ServerDataPublisher {

    protected final String serverId;
    protected final String groupId;
    protected final HostAndPort hostAndPort;


    /**
     * @param serverId    id of the server
     * @param groupId     group id of the server
     * @param hostAndPort host and port of the server
     * @see HostAndPort
     */
    public ServerDataPublisher(String serverId, String groupId, HostAndPort hostAndPort) {
        this.serverId = serverId;
        this.groupId = groupId;
        this.hostAndPort = hostAndPort;

    }

    /**
     * publish heartbeat
     *
     * @apiNote Data sent in the heartbeat is host and port
     * @implNote should be only called every 1 second
     * @see HeartbeatTask
     */
    public abstract void publishHeartBeat();

    /**
     * when server is shutting down announce the death
     */
    public abstract void publishDeath();

    /**
     * publish data based of the collector api
     *
     * @param manager {@link CollectorManager}
     * @implNote can be called at any interval
     * @see CollectorManager
     */
    public void publishData(CollectorManager manager) {
        this.publishData(manager.collect());
    }

    /**
     * @param data is Map<String, Object>
     * @implNote can be called at any interval
     */
    public abstract void publishData(Map<String, Object> data);

    /**
     * @return server id
     */

    public String serverId() {
        return serverId;
    }

    /**
     * @return group id
     */
    public String groupId() {
        return groupId;
    }

    /**
     * @return host and port
     */
    public HostAndPort hostAndPort() {
        return hostAndPort;
    }
}
