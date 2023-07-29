package net.glomc.apis.loadbalancer.common.models;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record HostAndPort(String host, int port) {

    private static enum FieldId {
        HOST("host"), PORT("port");

        private final String fieldId;

        FieldId(String fieldId) {
            this.fieldId = fieldId;
        }

        public String getFieldId() {
            return fieldId;
        }
    }

    /**
     *
     * @param host ip address, Domain of the server
     * @param port port of the server
     * @throws IllegalArgumentException when port is not in-range: 0-65535
     */
    public HostAndPort {
        if (!(0 < port && port < 65535)) {
            throw new IllegalArgumentException("Port number is invalid");
        }
    }


    /**
     * Converts this object into a Map
     */
    public Map<String, Object> convertIntoMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(FieldId.PORT.fieldId, port);
        map.put(FieldId.HOST.fieldId, host);
        return Collections.unmodifiableMap(map);
    }


    /**
     * Converts a Map to this Object
     *
     * @param map map to convert
     * @see #convertIntoMap()
     * @throws IllegalArgumentException if map does not contain requiered data
     */
    public static HostAndPort fromMap(Map<String, Object> map) {
        // checking if map is valid
        for (FieldId field : FieldId.values()) {
            if (!map.containsKey(field.getFieldId())) {
                throw new IllegalArgumentException("Provided map is invalid");
            }
        }
        return new HostAndPort((String) map.get(FieldId.HOST.getFieldId()), (Integer) map.get(FieldId.PORT.fieldId));
    }

    public InetSocketAddress convertIntoINetSocketAddress() {
        return InetSocketAddress.createUnresolved(host, port);
    }

    @Override
    public String toString() {
        return "host:" + host + " | port:" + port;
    }
}
