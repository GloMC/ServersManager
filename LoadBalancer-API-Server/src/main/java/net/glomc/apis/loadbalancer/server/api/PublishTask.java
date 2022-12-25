package net.glomc.apis.loadbalancer.server.api;

import net.glomc.apis.loadbalancer.server.api.datasources.ServersDataSource;

public class PublishTask implements Runnable {

    public static final long REPEAT = 1;

    private final CollectorManager collectorManager;

    private final ServersDataSource dataSource;

    public PublishTask(ServersDataSource dataSource, CollectorManager collectorManager) {
        this.dataSource = dataSource;
        this.collectorManager = collectorManager;
    }

    @Override
    public void run() {
        dataSource.publishHeartBeat();
        dataSource.publishData(this.collectorManager.collect());
    }

}
