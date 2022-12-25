plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/")} // velocity repo
}

dependencies {
    implementation(project(":LoadBalancer-Common"))
    implementation(project(":LoadBalancer-API"))
    implementation("redis.clients:jedis:4.3.1")
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")
}

