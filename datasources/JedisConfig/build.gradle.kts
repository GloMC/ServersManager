plugins {
    id("java")
    `java-library`
    id("maven-publish")
}


repositories {
    mavenCentral()
}

dependencies {
    api("org.spongepowered:configurate-yaml:3.7.2")
    api("redis.clients:jedis:4.3.1")
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}