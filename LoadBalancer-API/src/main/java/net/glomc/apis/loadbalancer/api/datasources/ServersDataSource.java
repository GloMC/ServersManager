package net.glomc.apis.loadbalancer.api.datasources;

import net.glomc.apis.loadbalancer.common.models.HostAndPort;

import java.util.List;
import java.util.Map;

public abstract class ServersDataSource {

    protected final String groupId;


    public ServersDataSource(String groupId) {
        this.groupId = groupId;
    }

    public abstract List<String> getHeartBeatingServers();

    public abstract Map<String, Map<String, String>> getServersCustomData(List<String> servers);

    public abstract HostAndPort getServerHostAndPort(String server);

    public String getGroupId() {
        return groupId;
    }
}
