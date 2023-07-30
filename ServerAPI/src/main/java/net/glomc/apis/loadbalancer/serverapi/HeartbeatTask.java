package net.glomc.apis.loadbalancer.serverapi;

import net.glomc.apis.loadbalancer.serverapi.publisher.ServerDataPublisher;

/**
 * just runnable task calling {@link ServerDataPublisher#publishHeartBeat()}
 */
public class HeartbeatTask implements Runnable {

    public static final long REPEAT = 1;

    private final ServerDataPublisher publisher;

    public HeartbeatTask(ServerDataPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void run() {
        publisher.publishHeartBeat();
    }

}
