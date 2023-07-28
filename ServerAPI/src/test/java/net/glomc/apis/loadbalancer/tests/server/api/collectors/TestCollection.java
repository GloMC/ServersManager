package net.glomc.apis.loadbalancer.tests.server.api.collectors;

import net.glomc.apis.loadbalancer.common.enums.DataFieldId;
import net.glomc.apis.loadbalancer.serverapi.collectors.CollectorManager;
import net.glomc.apis.loadbalancer.serverapi.collectors.DataCollector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestCollection {

    private final CollectorManager collectorManager = new CollectorManager();


    @Test
    public void testCollection() {
        Assertions.assertThrowsExactly(IllegalStateException.class, () -> collectorManager.testNeededFields(true));
        collectorManager.register(DataFieldId.ONLINE.getFieldId(), new DataCollector() {
            @Override
            public String collect() {
                return "15";
            }
        });
        collectorManager.register(DataFieldId.MAX_ONLINE.getFieldId(), new DataCollector() {
            @Override
            public String collect() {
                return "30";
            }
        });
        collectorManager.testNeededFields(true);
        System.out.println("COLLECTING.....");
        collectorManager.collect().forEach((fieldId, data) -> System.out.println(fieldId + ": " + data));
        for (DataFieldId value : DataFieldId.values()) {
            collectorManager.unRegister(value.getFieldId());
        }
        Assertions.assertThrowsExactly(IllegalStateException.class, () -> collectorManager.testNeededFields(true));
    }



}
