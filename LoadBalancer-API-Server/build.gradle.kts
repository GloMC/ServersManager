plugins {
    id("java")
}

group = "net.glomc.api"
version = parent?.version ?: "non-parent"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":LoadBalancer-Common"))

    implementation("redis.clients:jedis:4.3.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

}

tasks.test {
    useJUnitPlatform()
}