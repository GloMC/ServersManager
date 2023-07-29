package net.glomc.apis.loadbalancer.serverapi.datasources;

import net.glomc.apis.loadbalancer.common.models.HostAndPort;

import java.util.Map;

public abstract class ServersDataSource {

    protected final String serverId;
    protected final String groupId;
    protected final HostAndPort hostAndPort;

    public ServersDataSource(String serverId, String groupId, HostAndPort hostAndPort) {
        this.serverId = serverId;
        this.groupId = groupId;
        this.hostAndPort = hostAndPort;

    }

    public abstract void publishHeartBeat();

    public abstract void publishDeath();

    public abstract void publishData(Map<String, Object> data);

    public String serverId() {
        return serverId;
    }
    public String groupId() {
        return groupId;
    }

    public HostAndPort hostAndPort() {
        return hostAndPort;
    }
}
