package net.glomc.apis.loadbalancer.common.models;


import net.glomc.apis.loadbalancer.common.utils.ip.InetAddressUtils;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record HostAndPort(String host, int port) {

    public enum FieldId {
        INTERNET_PROTOCOL_ADDRESS("host"), PORT("port");

        private final String fieldId;

        FieldId(String fieldId) {
            this.fieldId = fieldId;
        }

        public String getFieldId() {
            return fieldId;
        }
    }

    public HostAndPort {
        if (!(0 <= port && port < 65535)) {
            throw new IllegalArgumentException("Port number is invalid");
        }
        if (!(InetAddressUtils.isIPv4Address(host) || InetAddressUtils.isIPv6Address(host))) {
            throw new IllegalArgumentException("Not an ip v4 or v6 address");
        }
    }

    public Map<String, String> convertIntoMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(FieldId.PORT.fieldId, String.valueOf(port));
        map.put(FieldId.INTERNET_PROTOCOL_ADDRESS.fieldId, host);
        return Collections.unmodifiableMap(map);
    }

    public static HostAndPort fromMap(Map<String, String> map) {
        // checking if map is valid
        for (FieldId field : FieldId.values()) {
            if (!map.containsKey(field.getFieldId())) {
                throw new IllegalArgumentException("Provided map is invalid");
            }
        }
        return new HostAndPort(map.get(FieldId.INTERNET_PROTOCOL_ADDRESS.getFieldId()), Integer.parseInt(map.get(FieldId.PORT.fieldId)));
    }

    public InetSocketAddress convertIntoINetSocketAddress() {
        return InetSocketAddress.createUnresolved(host, port);
    }

    @Override
    public String toString() {
        return "host:" + host + " | port:" + port;
    }
}
