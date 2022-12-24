package net.glomc.apis.loadbalancer.server.api;

import net.glomc.apis.loadbalancer.server.api.collectors.DataCollector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CollectorManager {
    private final ConcurrentHashMap<String, DataCollector> collectors = new ConcurrentHashMap<>();

    public boolean register(String fieldId, DataCollector dataCollector) {
        fieldId = fieldId.toLowerCase(Locale.ROOT);
        if (!collectors.containsKey(fieldId)) {
            collectors.put(fieldId, dataCollector);
            return true;
        } else  {
            return false;
        }
    }
    public boolean unRegister(String fieldId) {
        fieldId = fieldId.toLowerCase(Locale.ROOT);
        if (collectors.containsKey(fieldId)) {
            collectors.remove(fieldId);
            return true;
        } else  {
            return false;
        }
    }

    public Map<String, String> collect() {
        HashMap<String, String> data = new HashMap<>();
        collectors.forEach(((fieldId, dataCollector) -> data.put(fieldId, dataCollector.collect())));
        return Collections.unmodifiableMap(data);
    }


}
