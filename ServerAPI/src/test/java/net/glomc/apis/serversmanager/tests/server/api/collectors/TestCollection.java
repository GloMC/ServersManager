package net.glomc.apis.serversmanager.tests.server.api.collectors;

import net.glomc.apis.serversmanagers.common.enums.DataFieldId;
import net.glomc.apis.serversmanager.serverapi.collectors.CollectorManager;
import net.glomc.apis.serversmanager.serverapi.collectors.DataCollector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestCollection {

    private final CollectorManager collectorManager = new CollectorManager();


    @Test
    public void testCollection() {
        Assertions.assertThrowsExactly(IllegalStateException.class, collectorManager::collect);
        collectorManager.register(DataFieldId.ONLINE, new DataCollector<Integer>() {
            @Override
            public Integer collect() {
                return 15;
            }
        });
        Assertions.assertThrowsExactly(IllegalStateException.class, collectorManager::collect);
        collectorManager.register(DataFieldId.MAX_ONLINE, new DataCollector<Integer>() {
            @Override
            public Integer collect() {
                return 15;
            }
        });
        System.out.println("COLLECTING.....");
        collectorManager.collect().forEach((fieldId, data) -> System.out.println(fieldId + ": " + data));
    }


}
