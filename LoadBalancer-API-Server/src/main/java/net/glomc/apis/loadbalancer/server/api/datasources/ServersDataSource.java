package net.glomc.apis.loadbalancer.server.api.datasources;

import net.glomc.apis.loadbalancer.common.models.HostAndPort;

import java.util.Map;

public abstract class ServersDataSource {

    protected final String serverId;
    protected final String groupId;

    public ServersDataSource(String serverId, String groupId) {
        this.serverId = serverId;
        this.groupId = groupId;
    }

    public abstract void publishHeartBeat();
    public abstract void publishData(Map<String, String> data);

    protected abstract void publishHostAndPort(HostAndPort hostAndPort);


    public String getServerId() {
        return serverId;
    }

    public String getGroupId() {
        return groupId;
    }
}
