package net.glomc.apis.loadbalancer.serverapi.collectors;

import net.glomc.apis.loadbalancer.serverapi.collectors.CollectorManager;
import net.glomc.apis.loadbalancer.serverapi.datasources.ServersDataSource;

import java.util.Map;

public class CollectorPublisher implements Runnable {

    private final CollectorManager collectorManager;
    private final ServersDataSource dataSource;


    public CollectorPublisher(CollectorManager collectorManager, ServersDataSource dataSource) {
        this.collectorManager = collectorManager;
        this.dataSource = dataSource;
        collectorManager.testNeededFields(true);
    }

    private void collect() {
        this.dataSource.publishData(collectorManager.collect());
    }

    @Override
    public void run() {
        collect();
    }


}
