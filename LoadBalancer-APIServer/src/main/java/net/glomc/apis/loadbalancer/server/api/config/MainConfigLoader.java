package net.glomc.apis.loadbalancer.server.api.config;

import net.glomc.apis.loadbalancer.server.api.config.model.MainConfiguration;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public interface MainConfigLoader {


    default MainConfiguration loadMainConfig(File dataFolder, int port) throws IOException {
        return loadMainConfig(dataFolder.toPath(), port);
    }
    default MainConfiguration loadMainConfig(Path dataFolder, int port) throws IOException {
        Path configFile = createMainConfigFile(dataFolder);
        final YAMLConfigurationLoader yamlConfigurationFileLoader = YAMLConfigurationLoader.builder().setPath(configFile).build();
        ConfigurationNode node = yamlConfigurationFileLoader.load();
        if (node.getNode("config-version").getInt(0) != 1) {
            handleMainOldConfig(dataFolder);
            node = yamlConfigurationFileLoader.load();
        }

        return new MainConfiguration(
                MainConfiguration.DataSource.getTypeFromConfigField(node.getNode("datasoruce").getString("redis"))
                , node.getNode("group-id").getString("test-stuff")
                , node.getNode("server-id").getString("server-1")
                , node.getNode("public").getBoolean(false)
                , port
        );
    }


    default Path createMainConfigFile(Path dataFolder) throws IOException {
        if (Files.notExists(dataFolder)) {
            Files.createDirectory(dataFolder);
        }
        Path file = dataFolder.resolve("config.yml");
        if (Files.notExists(file)) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                Files.createFile(file);
                assert in != null;
                Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return file;
    }

    default void handleMainOldConfig(Path dataFolder) throws IOException {
        Path oldConfigFolder = dataFolder.resolve("old_config");
        if (Files.notExists(oldConfigFolder)) {
            Files.createDirectory(oldConfigFolder);
        }
        Path oldConfigPath = dataFolder.resolve("config.yml");
        Files.move(oldConfigPath, oldConfigFolder.resolve(UUID.randomUUID() + "_config.yml"));
        createMainConfigFile(dataFolder);
    }

}
