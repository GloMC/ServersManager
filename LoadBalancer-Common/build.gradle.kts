plugins {
    id("java")
}


repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.spongepowered:configurate-yaml:3.7.2")
    compileOnly("redis.clients:jedis:4.3.1")
}
