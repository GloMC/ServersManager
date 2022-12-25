plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":LoadBalancer-Common"))
    implementation(project(":LoadBalancer-API"))
    implementation("redis.clients:jedis:4.3.1")
}

