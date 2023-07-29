rootProject.name = "LoadBalanacer-Parent"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}


include("common")
include("ProxyAPI")
include("ServerAPI")


// redis data source
include("datasources:ProxyAPI-JedisDataSource")
include("datasources:Jedis-Common")
include("datasources:ServerAPI-JedisDataSource")
