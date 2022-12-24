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
}

tasks.test {
    useJUnitPlatform()
}