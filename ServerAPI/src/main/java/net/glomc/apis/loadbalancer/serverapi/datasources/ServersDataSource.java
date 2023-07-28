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

    public abstract void publishHeartBeatDeath();

    public abstract void publishData(Map<String, String> data);

    public abstract void publishData(String field, String data);

    public abstract void publishHostAndPort();

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
