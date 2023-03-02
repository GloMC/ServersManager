plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-velocity") version "2.0.1"
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") } // velocity repo
}

tasks.runVelocity {
    velocityVersion("3.2.0-SNAPSHOT")
}


dependencies {
    implementation(project(":LoadBalancer-ProxyAPI"))
    implementation(project(":datasources:LoadBalancer-ProxyAPI-JedisDataSource"))
    implementation(project(":datasources:JedisConfig"))
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")
}

