plugins {
    id("java")
    `maven-publish`
}


repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.spongepowered:configurate-yaml:3.7.2")
    compileOnly("redis.clients:jedis:4.3.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}