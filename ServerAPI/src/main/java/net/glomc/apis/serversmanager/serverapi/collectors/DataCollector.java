package net.glomc.apis.serversmanager.serverapi.collectors;

/**
 * this just returns the data collected by {@link CollectorManager}
 */
public abstract class DataCollector<T> {
    /**
     * @return any data java type
     */
    public abstract T collect();

}
