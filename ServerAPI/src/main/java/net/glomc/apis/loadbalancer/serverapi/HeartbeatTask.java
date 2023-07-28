package net.glomc.apis.loadbalancer.serverapi;

import net.glomc.apis.loadbalancer.serverapi.datasources.ServersDataSource;

public class HeartbeatTask implements Runnable {

    public static final long REPEAT = 1;


    private final ServersDataSource dataSource;

    public HeartbeatTask(ServersDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run() {
        dataSource.publishHeartBeat();
    }

}
