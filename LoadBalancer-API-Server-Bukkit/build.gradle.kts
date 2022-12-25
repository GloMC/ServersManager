plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.4.0"
    id("xyz.jpenilla.run-paper") version "2.0.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

repositories {
    mavenCentral()
}

dependencies {
    paperDevBundle("1.19.3-R0.1-SNAPSHOT")

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

}

bukkit {
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "net.glomc.apis.impl.bukkit.loadbalancer.ServerAPIPlugin"
    apiVersion = "1.19"
    authors = listOf("Author")
}
