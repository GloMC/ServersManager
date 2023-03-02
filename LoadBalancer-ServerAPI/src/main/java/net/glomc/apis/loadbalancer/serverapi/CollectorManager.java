package net.glomc.apis.loadbalancer.serverapi;

import net.glomc.apis.loadbalancer.common.enums.DataFieldId;
import net.glomc.apis.loadbalancer.serverapi.collectors.DataCollector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class CollectorManager {
    private final ConcurrentHashMap<String, DataCollector> collectors = new ConcurrentHashMap<>();
    private final Logger logger = Logger.getLogger("CollectorManager");

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

    public void testNeededFields(boolean shouldThrow) {
        boolean notFullySetup = false;
        for (DataFieldId value : DataFieldId.values()) {
            if (!this.collectors.containsKey(value.getFieldId())) {
                notFullySetup = true;
                logger.warning("Collector for " + value.getFieldId() + " is not registered!");
            }
        }
        if (notFullySetup && shouldThrow) {
            throw new IllegalStateException("Not all needed collectors are registered");
        }
    }

    public Map<String, String> collect() {
        HashMap<String, String> data = new HashMap<>();
        collectors.forEach(((fieldId, dataCollector) -> data.put(fieldId, dataCollector.collect())));
        return Collections.unmodifiableMap(data);
    }


}
