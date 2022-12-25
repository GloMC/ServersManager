plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":LoadBalancer-Common"))
    compileOnly("redis.clients:jedis:4.3.1")
    compileOnly("org.spongepowered:configurate-yaml:3.7.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

}

tasks.test {
    useJUnitPlatform()
}