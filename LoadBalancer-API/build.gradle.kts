plugins {
    id("java")
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":LoadBalancer-Common"))
    compileOnly("redis.clients:jedis:4.3.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}