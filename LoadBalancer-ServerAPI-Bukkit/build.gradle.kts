plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.5.2"
    id("xyz.jpenilla.run-paper") version "2.0.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    id("com.github.johnrengelman.shadow") version "8.1.0"
    `java-library`
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.clojars.org/") } // for multilib
}

dependencies {
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
    implementation(project(":LoadBalancer-ServerAPI"))
    implementation("com.github.puregero:multilib:1.1.9")
} 


tasks {
    runServer {
        jvmArgs("-DgroupId=development", "-DserverHost=127.0.0.1")
    }
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
        relocate("com.github.puregero.multilib", "net.glomc.apis.impl.bukkit.libs.multilib");
    }

}

bukkit {
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "net.glomc.apis.loadbalancer.impl.bukkit.ServerAPIPlugin"
    apiVersion = "1.13"
    authors = listOf("Ham1255")
}
