rootProject.name = "ServersManager-Parent"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}


include("common")
include("ProxyAPI")
include("ServerAPI")


// redis data source
include("managers:ProxyAPI-JedisServersManager")
include("managers:Jedis-Common")
include("managers:ServerAPI-JedisServerDataPublisher")
