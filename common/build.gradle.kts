plugins {
    `java-library`
    `maven-publish`
}


repositories {
    mavenCentral()
}

dependencies {

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}