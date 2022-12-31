plugins {
    id("java")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":LoadBalancer-Common"))
    implementation(project(":LoadBalancer-API"))
    implementation("redis.clients:jedis:4.3.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

