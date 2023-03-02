plugins {
    id("java")
    `maven-publish`
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":LoadBalancer-Common"))
    api(project(":datasources:JedisConfig"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
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