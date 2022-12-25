package net.glomc.apis.loadbalancer.server.api.config.model;

import net.glomc.apis.loadbalancer.common.models.HostAndPort;

import java.util.Locale;
import java.util.Objects;

public class MainConfiguration {
    public enum DataSource {
        REDIS("redis");
        private final String configField;

        DataSource(String configField) {
            this.configField = configField;
        }

        public String getConfigField() {
            return configField;
        }

        public static DataSource getTypeFromConfigField(String field) {
            field = field.toLowerCase(Locale.ROOT);
            for (DataSource value : DataSource.values()) {
                if (Objects.equals(value.configField, field)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("unknown field: " + field);
        }

    }

    private final DataSource dataSource;

    private final String groupId;

    private String serverId;

    private final boolean isPublic;

    private final HostAndPort hostAndPort;

    public MainConfiguration(DataSource dataSource, String groupId, String serverId, boolean isPublic, int port) {
        HostAndPort hostAndPort1;
        this.dataSource = dataSource;
        this.groupId = System.getProperty("groupId", groupId);
        this.serverId = System.getProperty("serverId", serverId);

        try {
            hostAndPort1 = new HostAndPort(System.getProperty("serverHost",  null), port);
        } catch (Exception e) {
            hostAndPort1 = null;
        }

        hostAndPort = hostAndPort1;
        if (this.hostAndPort == null) {
            this.isPublic = false;
        } else {
            this.isPublic = isPublic;
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getServerId() {
        return serverId;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
    public HostAndPort getHostAndPort() {
        return hostAndPort;
    }
}
