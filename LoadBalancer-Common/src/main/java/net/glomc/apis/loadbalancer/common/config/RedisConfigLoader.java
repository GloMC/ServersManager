package net.glomc.apis.loadbalancer.common.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Connection;
import redis.clients.jedis.ConnectionFactory;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.providers.ClusterConnectionProvider;
import redis.clients.jedis.providers.PooledConnectionProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface RedisConfigLoader {

    default void loadRedisConfig(File dataFolder) throws IOException {
        loadRedisConfig(dataFolder.toPath());
    }

    default void loadRedisConfig(Path dataFolder) throws IOException {
        Path configFile = createRedisConfigFile(dataFolder);
        final YAMLConfigurationLoader yamlConfigurationFileLoader = YAMLConfigurationLoader.builder().setPath(configFile).build();
        ConfigurationNode node = yamlConfigurationFileLoader.load();
        if (node.getNode("config-version").getInt(0) != 1) {
            handleRedisOldConfig(dataFolder);
            node = yamlConfigurationFileLoader.load();
        }
        final boolean useSSL = node.getNode("useSSL").getBoolean(false);
        String redisPassword = node.getNode("redis-password").getString("");
        final int maxConnections = node.getNode("max-redis-connections").getInt(10);
        // check redis password
        if ((redisPassword.isEmpty() || redisPassword.equals("none"))) {
            redisPassword = null;

        }
        if (node.getNode("cluster-mode-enabled").getBoolean(false)) {
            Set<HostAndPort> hostAndPortSet = new HashSet<>();
            GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
            poolConfig.setMaxTotal(maxConnections);
            poolConfig.setBlockWhenExhausted(true);
            node.getNode("redis-cluster-servers").getChildrenList().forEach((childNode) -> {
                Map<Object, ? extends ConfigurationNode> hostAndPort = childNode.getChildrenMap();
                String host = hostAndPort.get("host").getString();
                int port = hostAndPort.get("port").getInt();
                hostAndPortSet.add(new HostAndPort(host, port));
            });
            if (hostAndPortSet.isEmpty()) {
                throw new RuntimeException("No redis cluster servers specified");
            }
            handleCluster(new ClusterConnectionProvider(hostAndPortSet, DefaultJedisClientConfig.builder().password(redisPassword).ssl(useSSL).socketTimeoutMillis(5000).timeoutMillis(10000).build(), poolConfig));
        } else {
            final String redisServer = node.getNode("redis-server").getString("127.0.0.1");
            final int redisPort = node.getNode("redis-port").getInt(6379);
            if (redisServer != null && redisServer.isEmpty()) {
                throw new RuntimeException("No redis server specified");
            }
            GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
            poolConfig.setMaxTotal(maxConnections);
            poolConfig.setBlockWhenExhausted(true);
            handlePooled(new PooledConnectionProvider(new ConnectionFactory(new HostAndPort(redisServer, redisPort), DefaultJedisClientConfig.builder().timeoutMillis(5000).ssl(useSSL).password(redisPassword).build()), poolConfig));
        }
    }


    void handleCluster(ClusterConnectionProvider clusterConnectionProvider);

    void handlePooled(PooledConnectionProvider pooledConnectionProvider);


    default Path createRedisConfigFile(Path dataFolder) throws IOException {
        if (Files.notExists(dataFolder)) {
            Files.createDirectory(dataFolder);
        }
        Path file = dataFolder.resolve("redis.yml");
        if (Files.notExists(file)) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("redis.yml")) {
                Files.createFile(file);
                assert in != null;
                Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return file;
    }

    default void handleRedisOldConfig(Path dataFolder) throws IOException {
        Path oldConfigFolder = dataFolder.resolve("old_config");
        if (Files.notExists(oldConfigFolder)) {
            Files.createDirectory(oldConfigFolder);
        }
        Path oldConfigPath = dataFolder.resolve("redis.yml");
        Files.move(oldConfigPath, oldConfigFolder.resolve(UUID.randomUUID() + "_redis.yml"));
        createRedisConfigFile(dataFolder);
    }

}