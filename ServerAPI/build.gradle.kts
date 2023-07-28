plugins {
    `maven-publish`
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":common"))
    api("org.spongepowered:configurate-yaml:3.7.2")


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