plugins {
    id("maven-publish")
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":common"))
    api(project(":ProxyAPI"))
    api(project(":datasources:Jedis-Common"))

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

