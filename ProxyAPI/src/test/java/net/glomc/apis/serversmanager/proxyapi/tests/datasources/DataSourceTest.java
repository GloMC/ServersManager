package net.glomc.apis.serversmanager.proxyapi.tests.datasources;

import net.glomc.apis.serversmanager.proxyapi.loadbalancer.LoadBalancer;
import net.glomc.apis.serversmanagers.common.models.HostAndPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataSourceTest {
    private final LoadBalancer balancer = new LowCountBalancerSystem(new FakeDataSource("test"));
    @Test
    public void testBalancer() {

        System.out.println("Testing 50 requests");
        for (int i = 0; i < 50; i++) {
            System.out.println(balancer.bestServer());
        }
    }

    @Test
    public void testHostAndPort() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> new HostAndPort("2", 3000));
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> new HostAndPort("192.168.0.150", -3020));
        System.out.println(balancer.manager().getServerHostAndPort("server-1"));
    }




}
