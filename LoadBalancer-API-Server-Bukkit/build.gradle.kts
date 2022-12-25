import java.net.URL

plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.4.0"
    id("xyz.jpenilla.run-paper") version "2.0.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.clojars.org/") } // for multilib
}

dependencies {
    paperDevBundle("1.19.3-R0.1-SNAPSHOT")
    implementation(project(":LoadBalancer-API-Server"))
    implementation(project(":LoadBalancer-Common"))
    implementation("redis.clients:jedis:4.3.1")
    implementation("com.github.puregero:multilib:1.1.9")
    implementation("org.spongepowered:configurate-yaml:3.7.2")
}



tasks {
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    shadowJar {
        relocate("com.github.puregero.multilib", "net.glomc.apis.impl.bukkit.libs.multilib")
    }

}

bukkit {
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "net.glomc.apis.impl.bukkit.loadbalancer.ServerAPIPlugin"
    apiVersion = "1.19"
    authors = listOf("Author")
}
