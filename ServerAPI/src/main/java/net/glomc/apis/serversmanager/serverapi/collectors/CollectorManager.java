package net.glomc.apis.serversmanager.serverapi.collectors;

import net.glomc.apis.serversmanager.serverapi.publisher.ServerDataPublisher;
import net.glomc.apis.serversmanagers.common.enums.DataFieldId;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CollectorManager {
    private final ConcurrentHashMap<String, DataCollector<?>> collectors = new ConcurrentHashMap<>();

    /**
     * @param dataFieldId   field id of the collector
     * @param dataCollector The collector
     * @return false if already contains that field
     */
    public boolean register(DataFieldId dataFieldId, DataCollector<?> dataCollector) {
        Object objectTest = dataCollector.collect();
        if (objectTest.getClass() != dataFieldId.type()) {
            throw new IllegalStateException("collector has wrong return type for " + dataFieldId);
        }
        return this.register0(dataFieldId.getFieldId(), dataCollector);
    }

    private boolean register0(String fieldId, DataCollector<?> dataCollector) {
        fieldId = fieldId.toLowerCase(Locale.ROOT);
        if (!collectors.containsKey(fieldId)) {
            collectors.put(fieldId, dataCollector);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param fieldId       field id of the collector
     * @param dataCollector The collector
     * @return false if it uses field name similar to ones in {@link DataFieldId} or already contains that field
     */
    public boolean register(String fieldId, DataCollector<?> dataCollector) {
        for (DataFieldId value : DataFieldId.values()) {
            if (fieldId.equals(value.getFieldId())) {
                return false;
            }
        }
        return this.register0(fieldId, dataCollector);
    }


    private void testNeededFields() throws IllegalStateException {
        for (DataFieldId value : DataFieldId.values()) {
            if (!this.collectors.containsKey(value.getFieldId())) {
                throw new IllegalStateException("Not all needed collectors are registered field: " + value.getFieldId());
            }
        }
    }

    /**
     * collects data from registered collectors
     *
     * @return Map<String, Object> to be used in {@link ServerDataPublisher}
     */
    public Map<String, Object> collect() {
        testNeededFields();
        HashMap<String, Object> data = new HashMap<>();
        collectors.forEach(((fieldId, dataCollector) -> data.put(fieldId, dataCollector.collect())));
        return Collections.unmodifiableMap(data);
    }


}
