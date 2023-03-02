package net.glomc.apis.loadbalancer.serverapi;

import net.glomc.apis.loadbalancer.serverapi.datasources.ServersDataSource;

import java.util.Map;

public class CollectorPublisher implements Runnable {

    private final CollectorManager collectorManager;
    private final ServersDataSource dataSource;

    private Map<String, String> lastCollection;

    public CollectorPublisher(CollectorManager collectorManager, ServersDataSource dataSource) {
        this.collectorManager = collectorManager;
        this.dataSource = dataSource;
        collectorManager.testNeededFields(true);
    }

    private void collect() {
        if (lastCollection == null) {
            lastCollection = collectorManager.collect();
            this.dataSource.publishData(lastCollection);
        } else {
            Map<String, String> newCollection = collectorManager.collect();
            if (!lastCollection.equals(newCollection)) {
                this.lastCollection = newCollection;
                this.dataSource.publishData(newCollection);
            }
        }
    }

    @Override
    public void run() {
        collect();
    }


}
