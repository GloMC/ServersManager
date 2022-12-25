package net.glomc.apis.loadbalancer.api.datasources;

import net.glomc.apis.loadbalancer.common.enums.DataFieldId;
import net.glomc.apis.loadbalancer.common.models.HostAndPort;

import java.util.List;
import java.util.Map;

public abstract class ServersDataSource {

    protected final String groupId;


    public ServersDataSource(String groupId) {
        this.groupId = groupId;
    }

    public abstract List<String> getHeartBeatingServers();

    public abstract Map<String, Map<String, String>> getServersData(List<String> serversIds);

    public abstract Map<String , String> getServerData(String server);

    public String getSingleServerData(String serverId, DataFieldId dataFieldId) {
        return getSingleServerData(serverId, dataFieldId.getFieldId());
    }

    public abstract String getSingleServerData(String serverId, String fieldName);

    public abstract HostAndPort getServerHostAndPort(String serverId);

    public String getGroupId() {
        return groupId;
    }
}
