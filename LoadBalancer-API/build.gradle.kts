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


    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.test {
    useJUnitPlatform()
}
