package net.glomc.apis.loadbalancer.api.models;

import net.glomc.apis.loadbalancer.api.apacheapis.ip.InetAddressUtils;

public record HostAndPort(String host, int port) {

    public HostAndPort {
        if (!(0 <= port && port < 65535)) {
            throw new IllegalArgumentException("Port number is invalid");
        }
        if (!(InetAddressUtils.isIPv4Address(host) || InetAddressUtils.isIPv6Address(host))) {
            throw new IllegalArgumentException("Not an ip v4 or v6 address");
        }
    }

    @Override
    public String toString() {
        return "host:" + host + " | port:" + port;
    }
}
