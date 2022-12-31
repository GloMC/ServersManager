plugins {
    id("java")
    id("maven-publish")
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":LoadBalancer-Common"))
    api(project(":LoadBalancer-API"))
    api("redis.clients:jedis:4.3.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

